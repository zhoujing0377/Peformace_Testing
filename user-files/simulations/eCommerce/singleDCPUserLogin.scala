
import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import io.gatling.core.feeder._

class DCP1ConcurrentUsersLogin extends Simulation {
	val httpProtocol = http
		// .baseURL("https://shop.mercedes-benz.com")
		.baseURL("https://login.secure.mercedes-benz.com")
		.inferHtmlResources(BlackList(""".*\.js""", """.*\.css""", """.*\.gif""", """.*\.jpeg""", """.*\.jpg""", """.*\.ico""", """.*\.woff""", """.*\.(t|o)tf""", """.*\.png"""), WhiteList())
		.acceptHeader("text/plain, */*")
		.acceptEncodingHeader("gzip, deflate, br")
		.acceptLanguageHeader("en-US,en;q=0.8,zh-CN;q=0.6,zh;q=0.4")
		.userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2420.98 Safari/537.36")
		// .proxy(Proxy("http://53.90.130.51", 3128))

	val headers_login = Map(
		"Content-Type" -> "application/x-www-form-urlencoded")

	val feeder = csv("ciam_accounts.csv").random

	val scn = scenario("1 concurrent user login to dcp/ciam site")

		.feed(feeder)
		.exec(http("Login")
			// .post("/dcpstorefront/ciam/redirect?site=dcp-gb&lang=en&cntxt=login")
			// .post("/iap/b2c-pwd.fcc")
			.post("/wl/login")
			.headers(headers_login)
				.body(StringBody("""{ "SMAUTHREASON=0&target=%2Fwl%2Flevel-15%3Ft%3DaHR0cHM6Ly9sb2dpbi5zZWN1cmUubWVyY2VkZXMtYmVuei5jb20vd2wvbGV2ZWwtMTA_YXBwLWlkPWRjcC5wcm9kJmxhbmc9ZW5fR0I%26rm%3D1&acr_values=&t=&app-id=DCP.PROD&lang=en_GB&username=${mobile}&password=1234567A%21" }"""))
			.resources()
			// .body(StringBody("""{ "smauthreason=0&target=%2Fwl%2Flevel-15%3Ft%3DaHR0cHM6Ly9sb2dpbi5zZWN1cmUubWVyY2VkZXMtYmVuei5jb20vd2wvbGV2ZWwtMTA_YXBwLWlkPWRjcC5wcm9kJmxhbmc9ZW5fR0I%26rm%3D1&username=${mobile}&password=1234567A%21" }"""))
			// .resources()
	  	)

	setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
	
}
