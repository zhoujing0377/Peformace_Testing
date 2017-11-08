package mme.scenario

import io.gatling.core.Predef._
import mme.Constant._
import mme.step.user.CurrentUserStep
import mme.step.login._
import io.gatling.core.Predef._
import io.gatling.http.Predef._

class DataQuantity extends Simulation {

    val dcpApiHeader = Map(
    "Accept" -> "application/json, text/plain, */*",
    "Accept-Encoding" -> "gzip, deflate, sdch, br",
    "Accept-Language" -> "zh-CN,zh;q=0.8,en;q=0.6",
    "Connection" -> "keep-alive",
    "FRONT-END-HTTPS" -> "on",
    "If-None-Match" -> "0ccf5f311a98b009bdd76a1bbdfcc2c9c",
    "User-Agent" -> "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1",
    "X-Requested-With" -> "XMLHttpRequest",
    "Content-Type" -> "application/json",
    "lang" -> "zh_CN")

    val headers_with_token = Map(
        "MME-TOKEN" -> "${accessToken}",
        "CID" -> "${CID}"
        )
val scn = scenario("single user comes to ecommerce for data quantity cases")

    .exec(http("homepage_campaign_GET")
	    	.get(mmeHost + "api/ecommerce/customers/contents/home-campaigns")
	    	.headers(dcpApiHeader)
	    	)

	.exec(http("classes_GET")
            .get(mmeHost + "api/ecommerce/customers/vehicles/classes")
            .headers(dcpApiHeader)
            )

    .exec(http("PDP_E300sports_PRODUCTS_GET")
            .get(mmeHost +"api/ecommerce/customers/products?query=%3Aprice-asc%3Acity%3ABeijing%3Amodel%3A21304810-CBA")
            .headers(dcpApiHeader)
            )

    .exec(http("PDP_E300sports_ATTRIBUTES_GET")
            .get(mmeHost + "api/ecommerce/customers/vehicles/spus?model=21304810-CBA")
            .headers(dcpApiHeader)
            )

    .exec(CiamLoginStep.login)

    .exec(http("get current user")
            .get(mmeHost + "api/ecommerce/customers/current")
            .headers(dcpApiHeader)
            .headers(headers_with_token)
            )

    .exec(http("50_DEALERS_GET")
            .get(mmeHost + "api/ecommerce/customers/dealers?city=Beijing&sku=1ea348b8-571f-45bd-b2b7-ffd5016c36b7")
            .headers(dcpApiHeader)
            .headers(headers_with_token)
            )

    .exec(http("Add_favourite")
            .post(mmeHost + "api/ecommerce/customers/${CID}/favourites")            
            .body(StringBody("""{ "city":"Beijing","classId":"213","model":"21304210-CBA","modelCode":"21304210-CBA","modelName":"E 200 运动版","optionalPackage":[],"preview":"/dcpmedia/?context=bWFzdGVyfGltYWdlc3wxNDQ5MTF8aW1hZ2UvcG5nfGltYWdlcy9oYWEvaDZhLzg3OTY3Mjc3MDU2MzAucG5nfGJiYjg5YmRlNTRhMjQ5OTJhN2I0YWRhMTU2MWJiNzM3NmU3NjBjNDY3YjhjODhhMjg4MTAwMmMwM2UyNzlmMGY","selection":{"exteriorColour":null,"interiorColour":null,"wheel":null,"optionalPackage":[],"singleOption":[]},"singleOption":[],"style":"e","styleName":"标准轴距E级车运动版" }"""))
            .headers(dcpApiHeader)
            .headers(headers_with_token)
            .resources()
            )

    .exec(http("Get_100_favourite_items")
            .get(mmeHost + "api/ecommerce/customers/${CID}/favourites?pageIndex=0&pageSize=10")
            .headers(dcpApiHeader)
            .headers(headers_with_token)
            )

    .exec(http("Get_100_favourite_items_detail")
            .get(mmeHost + "api/ecommerce/customers/vehicles/classes/1")
            .headers(dcpApiHeader)
            .headers(headers_with_token)
            )

  setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}
