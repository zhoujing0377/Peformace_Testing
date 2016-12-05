
import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class concurrent50Users extends Simulation {

	val httpProtocol = http
		.baseURL("https://estore-dev-bce.mercedes-benz.com.cn")
		.inferHtmlResources(BlackList(""".*\.js""", """.*\.css""", """.*\.gif""", """.*\.jpeg""", """.*\.jpg""", """.*\.ico""", """.*\.woff""", """.*\.(t|o)tf""", """.*\.png"""), WhiteList())
		.acceptHeader("application/json, text/plain, */*")
		.acceptEncodingHeader("gzip, deflate, sdch")
		.acceptLanguageHeader("en-US,en;q=0.8,zh-CN;q=0.6,zh;q=0.4")
		.userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.98 Safari/537.36")

	val headers_login = Map(
		"Content-Type" -> "application/json")

	val headers_with_token = Map(
		"MME-TOKEN" -> "${token}")

	val feeder = csv("accounts.csv").random
	val e_class_data = csv("e_class_data_dev.csv")

	val scn = scenario("50 concurrent users to customer site")
		.feed(e_class_data)
	    .exec(http("homepage_campaign_GET")
	        .get("/api/ecommerce/customers/contents/home-campaigns")
	        .headers(headers_login)
	        )

	    .exec(http("homepage_footer_GET")
	        .get("/api/ecommerce/customers/contents/home-buttons")
	        .headers(headers_login)
	        )

	    .exec(http("classes_GET")
	    	.get("/api/ecommerce/customers/vehicles/classes")
	    	.headers(headers_login)
	        )

	    .exec(http("E_class_GET")
	    	.get("/api/ecommerce/customers/vehicles/classes/${class_id}")
	    	.headers(headers_login)
	        )

	    .exec(http("E_IPD_GET")
	    	.get("/api/ecommerce/customers/vehicles/classes/${class_id}/demonstration")
	    	.headers(headers_login)
	    	)        

	   .exec(http("E_PDP_CONTENT_GET")
	    	.get("/api/ecommerce/customers/contents/pdp?classId=${class_id}")
	    	.headers(headers_login)
	    	)

	   .exec(http("E_MBFS_GET")
	    	.get("/api/ecommerce/customers/mbfs?line=${class_id}")
	    	.headers(headers_login)
	    	)

	   .exec(http("FOOTER_BOTTONS_GET")
	    	.get("/api/ecommerce/customers/contents/home-buttons")
	    	.headers(headers_login)
	    	)

	   .exec(http("E_MODEL_COMPARE_GET")
	    	.get("/api/ecommerce/customers/vehicles/model-specs?models=${model1_id},${model2_id}")
	    	.headers(headers_login)
	    	)

		.exec(http("CITIES_GET")
	    	.get("/api/ecommerce/cities")
	    	.headers(headers_login)
	    	)

	    .exec(http("E_class_GET")
	    	.get("/api/ecommerce/customers/vehicles/classes/${class_id}")
	    	.headers(headers_login)
	        )

		.exec(http("PDP_MBFS_GET")
	    	.get("/api/ecommerce/customers/mbfs")
	    	.headers(headers_login)
	    	)

		.exec(http("PDP_E200L_PRODUCT_GET")
	    	.get("/api/ecommerce/customers/products?query=%3Aprice-asc%3Acity%3A${city}%3Amodel%3A${model_nst_code}")
	    	.headers(headers_login)
	    	)

		.exec(http("PDP_E200L_ATTRIBUTES_GET")
	    	.get("/api/ecommerce/customers/vehicles/spus?model=${model1_id}")
	    	.headers(headers_login)
	    	)

		.exec(http("PDP_E200L_ATTRIBUTES_GET")
	    	.get("/api/ecommerce/customers/vehicles/spus?model=${model1_id}")
	    	.headers(headers_login)
	    	)
		
		.exec(http("PRODUCT_GET")
	    	.get("/api/ecommerce/customers/products/${product_id}")
	    	.headers(headers_login))
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
	    	.get("/dealers?city=${city}&exteriorColours=${exterior_id}&interiorColours=${interior_id}&model=${model1_id}&optionalPackages%5B%5D=${optional_pkgs_id}&price=${product_price}&singleOptions%5B%5D=${single_pkg_id}&sku=${product_id}&styleName=${style_name}&wheels=${wheel_id}")
	    	.headers(headers_login)
	    	.headers(headers_with_token)
	    	)

		.exec(http("DEALER_GET")
	    	.get("/api/ecommerce/customers/dealers/${dealer_id}")
	    	.headers(headers_login)
	    	)

		.exec(http("DEALER_COMMENTS_GET")
	    	.get("/api/ecommerce/customers/dealers/${dealer_id}/comments?pageIndex=0&pageSize=10")
	    	.headers(headers_login)
	    	.headers(headers_with_token)
	    	)

		.exec(http("CREATE_Order")
			.post("/api/ecommerce/customers/${user_id}/orders")
			.headers(headers_login)
			.body(StringBody("""{ "contactName":"${username}","contactMobile":"${mobile}","productId":${product_id},"dealerId":${dealer_id},"giftId":${gift_id} }"""))
			.headers(headers_with_token)
			.check(regex(""""id":"([^"]*)""").find(0).saveAs("reservation_id"))
			.resources()
		)		

		.exec(http("GET_ORDER_LIST")
			.get("/api/ecommerce/customers/${user_id}/orders")
			.headers(headers_with_token)
		)

		.exec(http("GET_ORDER_DETAILS")
			.get("/api/ecommerce/customers/${user_id}/orders/${reservation_id}")
			.headers(headers_with_token)
		)	

		.exec(http("CANCEL_ORDER")
			.get("/api/ecommerce/customers/${user_id}/orders/${reservation_id}/cancel")
			.headers(headers_with_token)
		)

		// .exec(http("Add_favourite_items")
		// 	.post("/api/ecommerce/customers/${user_id}/favourites")
		// 	.headers(headers_with_token)
		// 	.headers(headers_login)
		// 	.body(StringBody()
		// 	.resources()
		// )

		.exec(http("Get_favourite_list_pagination")
			.get("/api/ecommerce/customers/${user_id}/favourites?pageIndex=0&pageSize=10")
			.headers(headers_with_token)
		)

		.exec(http("Get_favourite_items")
			.get("/api/ecommerce/customers/products?query=%3Aprice-asc%3Acity%3A${city}%3Amodel%3A${model_nst_code}%3Awheel%3A${wheel_id}%3AinteriorColour%3A${interior_id}%3AexteriorColour%3A${exterior_id}%3AsingleOption%3A${single_pkg_id}%3AoptionalPackage%3A${optional_pkgs_id}")
			.headers(headers_with_token)
		)

	setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}
