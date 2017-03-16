
import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import io.gatling.core.feeder._

class singleMmeUserLogin extends Simulation {
	val httpProtocol = http
		.baseURL("https://estore.mercedes-benz.com.cn")
		.inferHtmlResources(BlackList(""".*\.js""", """.*\.css""", """.*\.gif""", """.*\.jpeg""", """.*\.jpg""", """.*\.ico""", """.*\.woff""", """.*\.(t|o)tf""", """.*\.png"""), WhiteList())
		.acceptHeader("application/json, text/plain, */*")
		.acceptEncodingHeader("gzip, deflate, sdch")
		.acceptLanguageHeader("en-US,en;q=0.8,zh-CN;q=0.6,zh;q=0.4")
		.userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2420.98 Safari/537.36")

	val headers_login = Map(
		"Content-Type" -> "application/json")

	// val headers_with_token = Map(
	// 	"MME-TOKEN" -> "${token}")
	// val feeder = csv("ciam_accounts.csv").random

	val scn = scenario("single user login to ciam site")

		// .feed(feeder)
		.exec(http("Login")
			.post("/api/ecommerce/user/login")
			.headers(headers_login)
			.body(StringBody("""{ "mobile":"15910877655","password":"1234567A!" }"""))
			// .check(header("MME-TOKEN").saveAs("token"))
			// .resources()
	  	)

	setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
	
}
