
import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import io.gatling.core.feeder._

class DCP50ConcurrentUsersRegister extends Simulation {
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

	val feeder = csv("ciam_register_accounts.csv")

	val scn = scenario("50 Concurrent users register to dcp/ciam site")

		.feed(feeder)
		.exec(http("Register")
			// .post("/dcpstorefront/ciam/redirect?site=dcp-gb&lang=en&cntxt=login")
			// .post("/iap/b2c-pwd.fcc")
			.post("/profile/register")
			.headers(headers_login)
				.body(StringBody("""{ "s=1&app-id=DCP.PROD&t=&lang=en_GB&sex_input=f&firstName_input=dcp&lastName_input=test&email_input=${mobile}&r_email_input=${mobile}&captcha=T4TC&signature=fn8KOkRDEqG1lA0eRH5MzVIZ7CUEHxxqqvdt10knmc%2FoHAATmSZISxsdWR2IiJJ8zdM%2BeeGvzgflP%2FCUUstFeqkFOiGbHhem4j699SHuiGNtVUczRVYn%2B96WeAujwRyU9lZkqY02f%2FmFNJFiGB1lRZ31rf5%2BkI9etHQYxuEyw5KvhQf9HQnPYbLxzpZecgiD7e6uJEhQ0B0w5SW1%2B%2BdpPkkOmVeDDLgPUY%2Bxs2xIqB4Mr1BwlwoNQed7iwk3Fq4d46iqMHzsz1GX%2FEtkDv6Dk1hA0i4B7L%2FqHCBNp6xz%2FeXTcnzFx%2BbT%2B6woIX9jnAZeo9D12%2Be3WbduN6CFa6yJlg%3D%3D170306063910Z&agb_input=on" }"""))
			.resources()
			// .body(StringBody("""{ "smauthreason=0&target=%2Fwl%2Flevel-15%3Ft%3DaHR0cHM6Ly9sb2dpbi5zZWN1cmUubWVyY2VkZXMtYmVuei5jb20vd2wvbGV2ZWwtMTA_YXBwLWlkPWRjcC5wcm9kJmxhbmc9ZW5fR0I%26rm%3D1&username=${mobile}&password=1234567A%21" }"""))
			// .resources()
	  	)

	setUp(scn.inject(atOnceUsers(50))).protocols(httpProtocol)
	
}
