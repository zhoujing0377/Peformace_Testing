import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import io.gatling.core.feeder._

class singleUsersGet50orders extends Simulation {
	val httpProtocol = http
		.baseURL("https://estore-preprod.mercedes-benz.com.cn")
		.inferHtmlResources(BlackList(""".*\.js""", """.*\.css""", """.*\.gif""", """.*\.jpeg""", """.*\.jpg""", """.*\.ico""", """.*\.woff""", """.*\.(t|o)tf""", """.*\.png"""), WhiteList())
		.acceptHeader("application/json, text/plain, */*")
		.acceptEncodingHeader("gzip, deflate, sdch")
		.acceptLanguageHeader("en-US,en;q=0.8,zh-CN;q=0.6,zh;q=0.4")
		.userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2940.98 Safari/537.36")

	val headers_login = Map(
		"Content-Type" -> "application/json")

	val headers_with_token = Map(
		"MME-TOKEN" -> "${token}")


	val scn = scenario("single users get 50 orders")

		.exec(http("Login")
			.post("/api/ecommerce/user/login")
			.headers(headers_login)
			.body(StringBody("""{ "mobile":"90000000198","password":"1234567A!" }"""))
			.check(header("MME-TOKEN").saveAs("token"))
			.check(regex(""""id":"([^"]*)""").saveAs("user_id"))
			.resources()
	  	)

		.exec(http("Get customer profile")
			.get("/api/ecommerce/customers/current")
			.header("MME-TOKEN", "${token}")
		)
		
		.exec(http("GET_ORDER_LIST")
			.get("/api/ecommerce/customers/${user_id}/orders")
			.headers(headers_with_token)
			.check(regex(""""reservationId":"([^"]*)""").saveAs("reservation_id"))
			.resources()
		)		

	setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)	
}
