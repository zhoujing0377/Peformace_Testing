
import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import io.gatling.core.feeder._

class singleUser extends Simulation {
	val httpProtocol = http
		.baseURL("https://estore-preprod.mercedes-benz.com.cn")
		.inferHtmlResources(BlackList(""".*\.js""", """.*\.css""", """.*\.gif""", """.*\.jpeg""", """.*\.jpg""", """.*\.ico""", """.*\.woff""", """.*\.(t|o)tf""", """.*\.png"""), WhiteList())
		.acceptHeader("application/json, text/plain, */*")
		.acceptEncodingHeader("gzip, deflate, sdch")
		.acceptLanguageHeader("en-US,en;q=0.8,zh-CN;q=0.6,zh;q=0.4")
		.userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2420.98 Safari/537.36")

	val headers_login = Map(
		"Content-Type" -> "application/json")

	val headers_with_token = Map(
		"MME-TOKEN" -> "${token}")

	val feeder = csv("accounts.csv").random

	val scn = scenario("single user comes to customer site")

	    .exec(http("homepage_campaign_GET")
	        .get("/api/ecommerce/customers/contents/home-campaigns")
	        .headers(headers_login)
	        )
	    .pause(3)

	    .exec(http("classes_GET")
	    	.get("/api/ecommerce/customers/vehicles/classes")
	    	.headers(headers_login)
	        )
	    .pause(3)

	    .exec(http("E_class_GET")
	    	.get("/api/ecommerce/customers/vehicles/classes/42")
	    	.headers(headers_login)
	        )

	    .exec(http("E_IPD_GET")
	    	.get("/api/ecommerce/customers/vehicles/classes/42/demonstration")
	    	.headers(headers_login)
	    	)    

	   .exec(http("E_PDP_CONTENT_GET")
	    	.get("/api/ecommerce/customers/contents/pdp?classId=42")
	    	.headers(headers_login)
	    	)
	   .pause(5)

	   .exec(http("E_MODEL_COMPARE_GET")
	    	.get("/api/ecommerce/customers/vehicles/model-specs?models=155,150")
	    	.headers(headers_login)
	    	)
	   .pause(5)

		.exec(http("CITIES_GET")
	    	.get("/api/ecommerce/cities")
	    	.headers(headers_login)
	    	)
		.pause(1)

		.exec(http("MBFS_GET")
	    	.get("/api/ecommerce/customers/mbfs")
	    	.headers(headers_login)
	    	)

		.exec(http("PDP_MODEL_PRODUCT_GET")
	    	.get("/api/ecommerce/customers/products?query=%3Aprice-asc%3Acity%3A131%3Amodel%3A21314210-CBA")
	    	.headers(headers_login)
	    	)

		.exec(http("PDP_MODEL_ATTRIBUTES_GET")
	    	.get("/api/ecommerce/customers/vehicles/spus?model=148")
	    	.headers(headers_login)
	    	)

		.exec(http("ONE_PRODUCT_GET")
	    	.get("/api/ecommerce/customers/products/2735")
	    	.headers(headers_login))
		.pause(8)
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

		.exec(http("DEALERS_GET")
	    	.get("/api/ecommerce/customers/dealers?city=131&exteriorColours=149&interiorColours=114&model=154&optionalPackages%5B%5D=DB1&price=474800&singleOptions%5B%5D=873&sku=2735&styleName=%E9%95%BF%E8%BD%B4%E8%B7%9DE%E7%BA%A7%E8%BF%90%E5%8A%A8%E8%BD%BF%E8%BD%A6&wheels=R47&lat=39.999412&lng=116.489074")
	    	.headers(headers_login)
	    	.headers(headers_with_token)
	    	)

		.exec(http("DEALER_GET")
	    	.get("/api/ecommerce/customers/dealers/256")
	    	.headers(headers_login)
	    	)
		.pause(3)

		.exec(http("DEALER_COMMENTS_GET")
	    	.get("/api/ecommerce/customers/dealers/256/comments?pageIndex=0&pageSize=10")
	    	.headers(headers_login)
	    	.headers(headers_with_token)
	    	)
		.pause(3)
		.exec(http("PDP_MODEL_GET")
	    	.get("/api/ecommerce/customers/vehicles/models/154")
	    	.headers(headers_login)
	    	.headers(headers_with_token)
	    	)		
		.pause(3)
		// .exec(http("GET_ORDER_LIST")
		// 	.get("/api/ecommerce/customers/${user_id}/orders")
		// 	.headers(headers_with_token)
		// 	.check(regex(""""reservationId":"([^"]*)""").saveAs("reservation_id"))
		// 	.resources()
		// )		

		// .exec(http("CANCEL_ORDERS")
		// 	.post("/api/ecommerce/customers/${user_id}/orders/${reservation_id}/cancel")
		// 	.headers(headers_with_token)
		// )

		.exec(http("CREATE_ORDERS")
			.post("/api/ecommerce/customers/${user_id}/orders")
			.headers(headers_login)
			.body(StringBody("""{ "contactName":"${username}","contactMobile":"${mobile}","productId":2855,"dealerId":257,"giftId":299,"financialPlanTerm":null,"financialPlanDownpayment":null,"financialPlanBalloonpayment":null,"financialPlanMonthlyPayment":null }"""))
			.headers(headers_with_token)
			.resources()
		)	

		.pause(2)
		.exec(http("GET_ORDER_LIST")
			.get("/api/ecommerce/customers/${user_id}/orders")
			.headers(headers_with_token)
			.check(regex(""""reservationId":"([^"]*)""").saveAs("reservation_id"))
			.resources()
		)
		.pause(2)
		.exec(http("GET_ORDER_DETAILS")
			.get("/api/ecommerce/customers/${user_id}/orders/${reservation_id}")
			.headers(headers_with_token)
		)
		// .pause(2)
	 //    .exec(http("Request_Payment_POST")
	 //    .post("/api/ecommerce/customers/${user_id}/orders/${reservation_id}/payment")
	 //    .headers(headers_login)
	 //    .headers(headers_with_token)
	 //    .body(StringBody("""{ "subject":"支付意向金","body":"E 300 L 时尚型运动轿车","limit_pay":"alipay","total_fee":5299,"dealer_code":"Admin","return_url":"https://estore-preprod.mercedes-benz.com.cn/pay-result" }"""))
	 //    .resources()
	 //    )		
		.pause(2)
		.exec(http("Add_favourite_items")
			.post("/api/ecommerce/customers/${user_id}/favourites")
			.headers(headers_with_token)
			.headers(headers_login)
			.body(StringBody("""{ "city":"131","classId":"42","style":42,"styleName":"长轴距E级运动轿车","model":154,"modelCode":"21314810-CBB","modelName":"E 300 L 时尚型运动轿车","selection":{"exteriorColour":"149","interiorColour":"114","wheel":"R47","optionalPackage":["DB1"],"singleOption":["873"]},"preview":"https://estore-preprod.mercedes-benz.com.cn/images/pop/vehicle/ess/149_90.png","exteriorColour":{"code":"149","name":"北极白色","picture":"https://estore-preprod.mercedes-benz.com.cn/images/pop/exteriors/149.jpg","color":"#DADFD2","price":0,"description":null,"preview":"https://estore-preprod.mercedes-benz.com.cn/images/pop/vehicle/ess/149_90.png","disabled":false},"interiorColour":{"code":"114","name":"栗棕色/黑色114","picture":"https://estore-preprod.mercedes-benz.com.cn/images/pop/interiors/114.jpg","color":"#392719","price":0,"description":null,"preview":null,"disabled":false},"wheel":{"code":"R47","name":"48.3 厘米（19 英寸）6 辐轻合金车轮","picture":"https://estore-preprod.mercedes-benz.com.cn/images/pop/wheels/R47.jpg","color":null,"price":null,"description":null,"preview":null,"disabled":false},"optionalPackage":[{"code":"DB1","name":"智驾套装","picture":"https://estore-preprod.mercedes-benz.com.cn/images/pop/optional_packages/DB1.jpg","color":null,"price":null,"description":null,"preview":null,"disabled":false}],"singleOption":[{"code":"873","name":"可加热前排座椅","picture":"https://estore-preprod.mercedes-benz.com.cn/images/pop/single_options/873.jpg","color":null,"price":null,"description":null,"preview":null,"disabled":false}] }"""))
			.resources()
		)
		.pause(2)
		.exec(http("Get_favourite_list_pagination")
			.get("/api/ecommerce/customers/${user_id}/favourites?pageIndex=0&pageSize=10")
			.headers(headers_with_token)
		)

		.exec(http("Get_favourite_items")
			.get("/api/ecommerce/customers/products?query=%3Aprice-asc%3Acity%3A131%3Amodel%3A21314810-CBB%3Awheel%3AR47%3AinteriorColour%3A114%3AexteriorColour%3A149%3AsingleOption%3A873%3AoptionalPackage%3ADB1")
			.headers(headers_with_token)
		)

	setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
	
}
