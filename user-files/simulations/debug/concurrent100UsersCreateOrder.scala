import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import io.gatling.core.feeder._

class concurrent100UsersCreateOrder extends Simulation {
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

	val feeder = csv("accounts.csv").random
	// val product = csv("products.csv").random
	val gift = Array(
	  Map("gift_id" -> "299"),
	  Map("gift_id" -> "300")).random


	val scn = scenario("100 concurrent users create order")
		.feed(gift)
		.feed(feeder)
		.exec(http("Login")
			.post("/api/ecommerce/user/login")
			.headers(headers_login)
			.body(StringBody("""{ "mobile":"${mobile}","password":"${password}" }"""))
			.check(header("MME-TOKEN").saveAs("token"))
			.check(regex(""""id":"([^"]*)""").saveAs("user_id"))
			.resources()
	  	)

		.exec(http("Get customer profile")
			.get("/api/ecommerce/customers/current")
			.header("MME-TOKEN", "${token}")
		)
	
		// .exec(http("CREATE_100_ORDERS")
		// 	.post("/api/ecommerce/customers/${user_id}/orders")
		// 	.headers(headers_login)
		// 	.body(StringBody("""{ "contactName":"${username}","contactMobile":"${mobile}","productId":2855,"dealerId":257,"giftId":299,"financialPlanTerm":null,"financialPlanDownpayment":null,"financialPlanBalloonpayment":null,"financialPlanMonthlyPayment":null }"""))
		// 	.headers(headers_with_token)
		// 	.resources()
		// )		

		.exec(http("GET_ORDER_LIST")
			.get("/api/ecommerce/customers/${user_id}/orders")
			.headers(headers_with_token)
			.check(regex(""""reservationId":"([^"]*)""").saveAs("reservation_id"))
			.resources()
		)		

		.exec(http("CANCEL_100_ORDERS")
			.post("/api/ecommerce/customers/${user_id}/orders/${reservation_id}/cancel")
			.headers(headers_with_token)
		)
	setUp(scn.inject(atOnceUsers(100))).protocols(httpProtocol)	
}
