
import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class CancelOrderScenario extends Simulation {

	val httpProtocol = http
		.baseURL("https://estore-dev-bce.mercedes-benz.com.cn")
		.inferHtmlResources(BlackList(""".*\.js""", """.*\.css""", """.*\.gif""", """.*\.jpeg""", """.*\.jpg""", """.*\.ico""", """.*\.woff""", """.*\.(t|o)tf""", """.*\.png"""), WhiteList())
		.acceptEncodingHeader("gzip, deflate, sdch, br")
		.acceptLanguageHeader("en-US,en;q=0.8,zh-CN;q=0.6,zh;q=0.4")
		.userAgentHeader("Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1")

	val headers_0 = Map(
		"Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8",
		"Cache-Control" -> "no-cache",
		"Pragma" -> "no-cache",
		"Upgrade-Insecure-Requests" -> "1")

	val headers_1 = Map(
		"Accept" -> "application/json, text/plain, */*",
		"Cache-Control" -> "no-cache",
		"MME-TOKEN" -> "${token}",
		"Pragma" -> "no-cache",
		"X-Requested-With" -> "XMLHttpRequest")

	val headers_4 = Map("Cache-Control" -> "no-cache")

	val headers_28 = Map(
		"Accept" -> "application/json, text/plain, */*",
		"Accept-Encoding" -> "gzip, deflate, br",
		"Cache-Control" -> "no-cache",
		"Content-Type" -> "application/json;charset=UTF-8",
		"MME-TOKEN" -> "${token}",
		"Origin" -> "https://estore-dev-bce.mercedes-benz.com.cn",
		"Pragma" -> "no-cache",
		"X-Requested-With" -> "XMLHttpRequest")

	val headers_33 = Map(
		"Accept" -> "image/webp,image/*,*/*;q=0.8",
		"Cache-Control" -> "no-cache",
		"Pragma" -> "no-cache")

    val uri1 = "https://pay.beautyyan.cn:443/pay/gateway.do"
    val uri2 = "https://estore-dev-bce.mercedes-benz.com.cn:443"

	val scn = scenario("OrderScenario")
		.exec(http("request_0")
			.get("/")
			.headers(headers_0)
			.resources(http("request_1")
			.get("/api/ecommerce/customers/contents/home-campaigns")
			.headers(headers_1)
			.check(bodyBytes.is(RawFileBody("OrderScenario_0001_response.txt"))),
            http("request_2")
			.get("/api/ecommerce/customers/contents/home-buttons")
			.headers(headers_1)
			.check(bodyBytes.is(RawFileBody("OrderScenario_0002_response.txt"))))
			.check(bodyBytes.is(RawFileBody("OrderScenario_0000_response.txt"))))
		.pause(2)
		.exec(http("request_3")
			.get("/product?classId=20")
			.headers(headers_0)
			.resources(http("request_4")
			.get("/static/js/manifest.de576dffbb2a59dcd0fb.js.map")
			.headers(headers_4)
			.check(bodyBytes.is(RawFileBody("OrderScenario_0004_response.txt"))),
            http("request_5")
			.get("/static/css/app.75ddf3655027d3d1089c8f3e43c085b1.css.map")
			.headers(headers_4)
			.check(bodyBytes.is(RawFileBody("OrderScenario_0005_response.txt"))),
            http("request_6")
			.get("/api/ecommerce/vehicles/classes/20")
			.headers(headers_1)
			.check(bodyBytes.is(RawFileBody("OrderScenario_0006_response.txt"))),
            http("request_7")
			.get("/api/ecommerce/vehicles/classes/20/demonstration")
			.headers(headers_1)
			.check(bodyBytes.is(RawFileBody("OrderScenario_0007_response.txt"))),
            http("request_8")
			.get("/api/ecommerce/customers/mbfs")
			.headers(headers_1)
			.check(bodyBytes.is(RawFileBody("OrderScenario_0008_response.txt"))),
            http("request_9")
			.get("/static/js/vendor.a99b29d5fb95407ef20a.js.map")
			.headers(headers_4)
			.check(bodyBytes.is(RawFileBody("OrderScenario_0009_response.txt"))),
            http("request_10")
			.get("/api/ecommerce/customers/contents/pdp?classId=20")
			.headers(headers_1)
			.check(bodyBytes.is(RawFileBody("OrderScenario_0010_response.txt"))),
            http("request_11")
			.get("/api/ecommerce/customers/contents/home-buttons")
			.headers(headers_1)
			.check(bodyBytes.is(RawFileBody("OrderScenario_0011_response.txt"))),
            http("request_12")
			.get("/static/js/app.7cffba0af26426d8fed7.js.map")
			.headers(headers_4)
			.check(bodyBytes.is(RawFileBody("OrderScenario_0012_response.txt"))),
            http("request_13")
			.get("/static/js/vendor.a99b29d5fb95407ef20a.js.map")
			.headers(headers_4)
			.check(bodyBytes.is(RawFileBody("OrderScenario_0013_response.txt"))))
			.check(bodyBytes.is(RawFileBody("OrderScenario_0003_response.txt"))))
		.pause(12)
		.exec(http("request_14")
			.get("/api/ecommerce/cities")
			.headers(headers_1)
			.check(bodyBytes.is(RawFileBody("OrderScenario_0014_response.txt"))))
		.pause(4)
		.exec(http("request_15")
			.get("/api/ecommerce/vehicles/classes/20")
			.headers(headers_1)
			.resources(http("request_16")
			.get("/api/ecommerce/customers/mbfs")
			.headers(headers_1)
			.check(bodyBytes.is(RawFileBody("OrderScenario_0016_response.txt"))),
            http("request_17")
			.get("/api/ecommerce/vehicles/spus?model=89")
			.headers(headers_1)
			.check(bodyBytes.is(RawFileBody("OrderScenario_0017_response.txt"))),
            http("request_18")
			.get("/api/ecommerce/products?query=%3Aprice-asc%3Acity%3A131%3Amodel%3A21314210-CBA")
			.headers(headers_1)
			.check(bodyBytes.is(RawFileBody("OrderScenario_0018_response.txt"))))
			.check(bodyBytes.is(RawFileBody("OrderScenario_0015_response.txt"))))
		.pause(4)
		.exec(http("request_19")
			.get("/api/ecommerce/vehicles/spus?model=92")
			.headers(headers_1)
			.resources(http("request_20")
			.get("/api/ecommerce/products?query=%3Aprice-asc%3Acity%3A131%3Amodel%3A21314210-CBB")
			.headers(headers_1)
			.check(bodyBytes.is(RawFileBody("OrderScenario_0020_response.txt"))))
			.check(bodyBytes.is(RawFileBody("OrderScenario_0019_response.txt"))))
		.pause(1)
		.exec(http("request_21")
			.get("/api/ecommerce/vehicles/spus?model=93")
			.headers(headers_1)
			.resources(http("request_22")
			.get("/api/ecommerce/products?query=%3Aprice-asc%3Acity%3A131%3Amodel%3A21314810-CBB")
			.headers(headers_1)
			.check(bodyBytes.is(RawFileBody("OrderScenario_0022_response.txt"))))
			.check(bodyBytes.is(RawFileBody("OrderScenario_0021_response.txt"))))
		.pause(14)
		.exec(http("request_23")
			.get("/api/ecommerce/products/1257")
			.headers(headers_1)
			.check(bodyBytes.is(RawFileBody("OrderScenario_0023_response.txt"))))
		.pause(1)
		.exec(http("request_24")
			.get("/api/ecommerce/customers/dealers?balloon=0&campaignName=12%E8%87%B348%E4%B8%AA%E6%9C%886.99%25-8.99%25%E4%BD%8E%E9%A6%96%E4%BB%98%E6%96%B9%E6%A1%88&city=131&downpayment=0&exteriorColours=197&interiorColours=114&model=93&monthlyPayment=10413&optionalPackages%5B%5D=DB1&price=474800&singleOptions%5B%5D=873&sku=1257&styleName=%E9%95%BF%E8%BD%B4%E8%B7%9DE%E7%BA%A7%E8%BF%90%E5%8A%A8%E8%BD%BF%E8%BD%A6&term=36&wheels=R47")
			.headers(headers_1)
			.check(bodyBytes.is(RawFileBody("OrderScenario_0024_response.txt"))))
		.pause(6)
		.exec(http("request_25")
			.get("/api/ecommerce/products/1257")
			.headers(headers_1)
			.resources(http("request_26")
			.get("/api/ecommerce/customers/current")
			.headers(headers_1)
			.check(bodyBytes.is(RawFileBody("OrderScenario_0026_response.txt"))),
            http("request_27")
			.get("/api/ecommerce/vehicles/models/93")
			.headers(headers_1)
			.check(bodyBytes.is(RawFileBody("OrderScenario_0027_response.txt"))))
			.check(bodyBytes.is(RawFileBody("OrderScenario_0025_response.txt"))))
		.pause(4)
		.exec(http("request_28")
			.post("/api/ecommerce/customers/470eae1c-cab3-42a3-b3fb-efcb2d18ee98/orders")
			.headers(headers_28)
			.body(RawFileBody("OrderScenario_0028_request.txt"))
			.resources(http("request_29")
			.get("/api/ecommerce/customers/470eae1c-cab3-42a3-b3fb-efcb2d18ee98/orders/1479910802511")
			.headers(headers_1)
			.check(bodyBytes.is(RawFileBody("OrderScenario_0029_response.txt"))))
			.check(bodyBytes.is(RawFileBody("OrderScenario_0028_response.txt"))))
		.pause(3)
		.exec(http("request_30")
			.post("/api/ecommerce/customers/470eae1c-cab3-42a3-b3fb-efcb2d18ee98/orders/1479910802511/payment")
			.headers(headers_28)
			.body(RawFileBody("OrderScenario_0030_request.txt"))
			.check(bodyBytes.is(RawFileBody("OrderScenario_0030_response.txt"))))
		.pause(12)
		.exec(http("request_31")
			.get(uri1 + "?info=jkPdDl0gXFSk1Q33lTTB7H0UmZFGRbC1Fg%2BbOYef2bDdS0zxTFr62XNNLaZO%2BK0mIIHkxsDRAC4s%0AmitmfAUPDcn9w6A8sCyPzU04Dw18aBfUHSeWrNQd0di%2BOUayyn%2BpivAOHdc8fupC8DWa3whDOIaU%0Ae4Td8KGfS%2Bica3V%2F3Hth%2FZzWBYGBGeML%2BY2XcTRmVh%2BT2KA7gy0yqHbKwkcufX%2Bkr2UD8ZGtxtt4%0AG4uO4aK17MMb%2F6xGR6vcgkl%2FdCuJX4%2B4HRAVjcS6Km5iCKyG5LfuWPMSZNkE3a530O0aHfsLG5GY%0A5UL9g7y01IDOPwUboYucMoZwf%2FyMbPMw1nHzvZsGqBqnS5fDaVTd6h%2BiKyAcwvlktx%2BQ4mEa%2Bmif%0Agbgt7wUI8XnUWKwfivZX5zluQtOKxYhhq5LwDvIbJpXSZaflCs0E20wDADT%2Fm%2BVuTyLxHwzMr1QT%0AjWg2eSEbvc89%2B7%2FyJBiKFi9DVNoHD1PSpx5tRfM6WXnvphDINoAVWb%2B82mPykSf%2F1lOhCIYr0HjM%0AJtIr95Ig1%2BeTJKBG320FAGohTPbkEgEjVUlGtV5M%2BXge%2BtzED02Y0DMEwKfmwuUsNWLJIsuaULS3%0AtwKl5Pl1LI6uCWRuob77UO2Z%2FiEjtCjc6GlRKKNWgQt9S6mfdOwCCRWHVz%2FT%2F27Bqwaj7ZGS87EJ%0AIMRngIiw%2BrKH4Y%2B8RFXPgD8Aw9mlmejVWxoFwGUmMgGpY0C6uWzhNjaqRwqkQRJVJtx3nIymmzYM%0AOK00%2FXykoLq6YvI4NzY3ujXL91em09Oli5S%2BWwN7uIEurmBLHgPKqrUioYNiScE9XldcyGg5ozYN%0AR%2FnzgI%2B3rzJLpsTLHUZi9eZHSqNS1LN4oBtGwyuWOvloh1j%2Bi%2BUMed7SCXj49R3eaV1FOXy2nqfI%0ABj5XR%2Fyu87OSi8%2BklN9l%2BF4oV9cMLA6NRPMb5nOCYjtj%2BRJcjcJJPKlgOVPDrrvvjxPPs%2ByuY06i%0APsdWrL6ksPGfNHTV38xbYtY%2FQT%2F6KsN%2FWIhggtt4saQHoBcXZABJQRDsK5muDhbxSwaBlytyA5It%0ANIojyrJgncYDsqa3DXzGl1Z4BJiEezPyGsuF1cQIDOoee9ioTEHOT2fRGH0f3zuExuPuLBLtZl4r%0A%2Fan5IJw8sMm2BdFcvhQ96JqlI8iOPTiX5hDhRCVH%2FWn2pEmAsq5xSvM%3D")
			.headers(headers_0)
			.resources(http("request_32")
			.get("/api/ecommerce/customers/470eae1c-cab3-42a3-b3fb-efcb2d18ee98/orders")
			.headers(headers_1)
			.check(bodyBytes.is(RawFileBody("OrderScenario_0032_response.txt")))))
		.pause(6)
		.exec(http("request_33")
			.get("/undefined")
			.headers(headers_33)
			.resources(http("request_34")
			.get("/api/ecommerce/customers/470eae1c-cab3-42a3-b3fb-efcb2d18ee98/orders/1479910802511")
			.headers(headers_1)
			.check(bodyBytes.is(RawFileBody("OrderScenario_0034_response.txt"))))
			.check(bodyBytes.is(RawFileBody("OrderScenario_0033_response.txt"))))
		.pause(11)
		.exec(http("request_35")
			.post("/api/ecommerce/customers/470eae1c-cab3-42a3-b3fb-efcb2d18ee98/orders/1479910802511/cancel")
			.headers(headers_28)
			.body(RawFileBody("OrderScenario_0035_request.txt"))
			.check(bodyBytes.is(RawFileBody("OrderScenario_0035_response.txt"))))

	setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}