package mytest1

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
// import assertions._

class PDPScenario extends Simulation {

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

	val scn = scenario("50 concurrent users to customer site")

	    .exec(http("homepage_campaign_GET")
	        .get("/api/ecommerce/customers/contents/home-campaigns")
	        .headers(headers_login)
	        )

	    .exec(http("homepage_footer_GET")
	        .get("/api/ecommerce/customers/contents/home-buttons")
	        .headers(headers_login)
	        )

	    .exec(http("classes_GET")
	    	.get("/api/ecommerce/vehicles/classes")
	    	.headers(headers_login)
	        )

	    .exec(http("E_class_GET")
	    	.get("/api/ecommerce/vehicles/classes/20")
	    	.headers(headers_login)
	        )

	    .exec(http("E_IPD_GET")
	    	.get("/api/ecommerce/vehicles/classes/20/demonstration")
	    	.headers(headers_login)
	    	)        

	   .exec(http("E_PDP_CONTENT_GET")
	    	.get("/api/ecommerce/customers/contents/pdp?lineId=20")
	    	.headers(headers_login)
	    	)

	   .exec(http("E_MBFS_GET")
	    	.get("/api/ecommerce/customers/mbfs?line=20")
	    	.headers(headers_login)
	    	)

	   .exec(http("FOOTER_BOTTONS_GET")
	    	.get("/api/ecommerce/customers/contents/home-buttons")
	    	.headers(headers_login)
	    	)

	   .exec(http("E_MODEL_COMPARE_GET")
	    	.get("/api/ecommerce/vehicles/model-specs?models=89,90")
	    	.headers(headers_login)
	    	)

		.exec(http("CITIES_GET")
	    	.get("/api/ecommerce/cities")
	    	.headers(headers_login)
	    	)

	    .exec(http("E_class_GET")
	    	.get("/api/ecommerce/vehicles/classes/20")
	    	.headers(headers_login)
	        )

		.exec(http("PDP_MBFS_GET")
	    	.get("/api/ecommerce/customers/mbfs")
	    	.headers(headers_login)
	    	)

		.exec(http("PDP_E200L_PRODUCT_GET")
	    	.get("/api/ecommerce/products?query=%3Aprice-asc%3Acity%3A131%3Amodel%3A21314210-CBA")
	    	.headers(headers_login)
	    	)

		.exec(http("PDP_E200L_ATTRIBUTES_GET")
	    	.get("/api/ecommerce/vehicles/spus?model=89")
	    	.headers(headers_login)
	    	)

		.exec(http("PDP_E200L_ATTRIBUTES_GET")
	    	.get("/api/ecommerce/vehicles/spus?model=89")
	    	.headers(headers_login)
	    	)

		.exec(http("PRODUCT_GET")
	    	.get("/api/ecommerce/products/1245")
	    	.headers(headers_login)
	    	)

		.exec(http("Login")
			.post("/api/ecommerce/user/login")
			.headers(headers_login)
			.body(StringBody("""{ "mobile":"15910877655","password":"1234567A!" }"""))
			.check(header("MME-TOKEN").saveAs("token"))
			.resources()
	  	)
		.exec(http("Get customer profile")
			.get("/api/ecommerce/customers/current")
			.header("MME-TOKEN", "${token}")
		)

		.exec(http("DEALERS_GET")
	    	.get("/dealers?city=131&exteriorColours=197&interiorColours=144&model=89&optionalPackages%5B%5D=null&price=436800&singleOptions%5B%5D=null&sku=1245&styleName=%E9%95%BF%E8%BD%B4%E8%B7%9DE%E7%BA%A7%E8%BD%BF%E8%BD%A6&wheels=66R")
	    	.headers(headers_login)
	    	.headers(headers_with_token)
	    	)

		.exec(http("DEALER_GET")
	    	.get("/api/ecommerce/customers/dealers/120")
	    	.headers(headers_login)
	    	)

		.exec(http("DEALER_COMMENTS_GET")
	    	.get("/api/ecommerce/customers/dealers/120/comments?pageIndex=0&pageSize=10")
	    	.headers(headers_login)
	    	.headers(headers_with_token)
	    	)

		.exec(http("Get_order_list")
			.get("/api/ecommerce/customers/470eae1c-cab3-42a3-b3fb-efcb2d18ee98/orders")
			.headers(headers_with_token)
		)

		.exec(http("Get_order_detail_paid")
			.get("/api/ecommerce/customers/470eae1c-cab3-42a3-b3fb-efcb2d18ee98/orders/1479899289100")
			.headers(headers_with_token)
		)		

		.exec(http("Add_favourite_items")
			.post("/api/ecommerce/customers/470eae1c-cab3-42a3-b3fb-efcb2d18ee98/favourites")
			.headers(headers_with_token)
			.headers(headers_login)
			.body(StringBody("""{ "city":"131","classId":"20","style":22,"styleName":"长轴距E级轿车","model":90,"modelCode":"21314810-CBA","modelName":"E 300 L 时尚型轿车","selection":{"exteriorColour":"890","interiorColour":"144","wheel":"66R","optionalPackage":["DB1"],"singleOption":["873"]},"preview":"/images/pop/213148_P23_890_BE270.png","exteriorColour":{"code":"890","name":"水硅钒钙石蓝色","picture":"/images/pop/890.jpg","color":null,"price":0,"description":null,"preview":"/images/pop/213148_P23_890_BE270.png","disabled":false},"interiorColour":{"code":"144","name":"栗棕色/咖啡棕色","picture":"/images/pop/144.jpg","color":null,"price":0,"description":null,"preview":null,"disabled":false},"wheel":{"code":"66R","name":"45.7 厘米（18 英寸）双 5 辐轻合金车轮","picture":"http://cdn-emb-ccore.mercedes-benz.com/images/2414/d/c2/d1a5ec14adb23f62d93c75fca361c7305a0e9.jpg","color":null,"price":null,"description":null,"preview":null,"disabled":false},"optionalPackage":[{"code":"DB1","name":"智驾套装","picture":"/images/pop/DB1.jpg","color":null,"price":null,"description":null,"preview":null,"disabled":false}],"singleOption":[{"code":"873","name":"可加热前排座椅","picture":"/images/pop/873.jpg","color":null,"price":null,"description":null,"preview":null,"disabled":false}] }"""))
			.resources()
		)

		.exec(http("Get_favourite_list_pagination")
			.get("/api/ecommerce/customers/470eae1c-cab3-42a3-b3fb-efcb2d18ee98/favourites?pageIndex=0&pageSize=10")
			.headers(headers_with_token)
		)

		.exec(http("Get_favourite_items")
			.get("/api/ecommerce/products?query=%3Aprice-asc%3Acity%3A131%3Amodel%3A21314210-CBA%3Awheel%3A66R%3AinteriorColour%3A144%3AexteriorColour%3A197%3AsingleOption%3Anull%3AoptionalPackage%3Anull")
			.headers(headers_with_token)
		)

	
    // setUp(scn.inject(atOnceUsers(10))).protocols(httpConf)
    setUp(scn.inject(rampUsers(2) over (3 seconds))).protocols(httpConf)
  }