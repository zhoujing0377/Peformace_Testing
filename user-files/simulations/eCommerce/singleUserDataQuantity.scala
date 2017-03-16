
import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import io.gatling.core.feeder._

class singleUserDataQuantity extends Simulation {
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

	val scn = scenario("single user comes to ecommerce for data quantity cases")

	    // .exec(http("homepage_5_campaigns_GET")
	    //     .get("/api/ecommerce/customers/contents/home-campaigns")
	    //     .headers(headers_login)
	    //     )

	    // .exec(http("7_classes_GET")
	    // 	.get("/api/ecommerce/customers/vehicles/classes")
	    // 	.headers(headers_login)
	    //     )

		.exec(http("PDP_600_PRODUCTS_GET")
	    	.get("/api/ecommerce/customers/products?query=%3Aprice-asc%3Acity%3A131%3Amodel%3A21314210-XXX")
	    	.headers(headers_login)
	    	)

		.exec(http("PDP_E200L_ATTRIBUTES_GET")
	    	.get("/api/ecommerce/customers/vehicles/spus?model=164")
	    	.headers(headers_login)
	    	)

		// .exec(http("PRODUCT_GET")
	 //    	.get("/api/ecommerce/customers/products/2855")
	 //    	.headers(headers_login))
		// .feed(feeder)
		// .exec(http("Login")
		// 	.post("/api/ecommerce/user/login")
		// 	.headers(headers_login)
		// 	.body(StringBody("""{ "mobile":"${mobile}","password":"${password}" }"""))
		// 	.check(header("MME-TOKEN").saveAs("token"))
		// 	.check(regex(""""id":"([^"]*)""").saveAs("user_id"))
		// 	.resources()
	 //  	)

		// .exec(http("Get customer profile")
		// 	.get("/api/ecommerce/customers/current")
		// 	.header("MME-TOKEN", "${token}")
		// )

		// .exec(http("50_DEALERS_GET")
	 //    	.get("/dealers?city=131&exteriorColours=101&interiorColours=201&model=164&optionalPackages%5B%5D=401&price=436800&singleOptions%5B%5D=501&sku=2855&styleName=%E9%95%BF%E8%BD%B4%E8%B7%9DE%E7%BA%A7%E8%BD%BF%E8%BD%A6-1&wheels=301")
	 //    	.headers(headers_login)
	 //    	.headers(headers_with_token)
	 //    	)

		// .exec(http("DEALER_100_COMMENTS_GET")
	 //    	.get("/api/ecommerce/customers/dealers/257/comments?pageIndex=0&pageSize=10")
	 //    	.headers(headers_login)
	 //    	.headers(headers_with_token)
	 //    	)

		// .exec(http("Get_u100_favourite_list_pagination")
		// 	.get("/api/ecommerce/customers/7d7b71ff-9ce3-4173-bad8-c6d9da5589c7/favourites?pageIndex=0&pageSize=10")
		// 	.headers(headers_with_token)
		// )

		// .exec(http("Get_100_favourite_items")
		// 	.get("/api/ecommerce/customers/products?query=%3Aprice-asc%3Acity%3A131%3Amodel%3A21314210-XXX%3Awheel%3A301%3AinteriorColour%3A201%3AexteriorColour%3A101%3AsingleOption%3A501%3AoptionalPackage%3A401")
		// 	.headers(headers_with_token)
		// )
	
	setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)	
	
	
}
