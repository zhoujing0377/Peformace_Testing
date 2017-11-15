package mme.scenario

import io.gatling.core.Predef._
import mme.Constant._
import mme.step.user.CurrentUserStep
import mme.step.login._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class Split500UsersPer10sMainAPIs extends Simulation {

    val dcpApiHeader = Map(
    "Accept" -> "application/json, text/plain, */*",
    "Accept-Encoding" -> "gzip, deflate, sdch, br",
    "Accept-Language" -> "zh-CN,zh;q=0.8,en;q=0.6",
    "Connection" -> "keep-alive",
    "FRONT-END-HTTPS" -> "on",
    "If-None-Match" -> "0ccf5f311a98b009bdd76a1bbdfcc2c9c",
    "User-Agent" -> "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1",
    "X-Requested-With" -> "XMLHttpRequest",
    "lang" -> "zh_CN")

    val headers_with_token = Map(
        "MME-TOKEN" -> "${accessToken}",
        "CID" -> "${CID}"
        )

val baseURL = "https://mbafcecom-uat.mercedes-benz.com.cn/"
val scn = scenario("500 users comes to customer site")

.exec(http("homepage_campaign_GET")
            .get(mmeHost + "api/ecommerce/customers/contents/home-campaigns")
            .headers(dcpApiHeader)
            )

    .exec(http("classes_GET")
            .get(mmeHost + "api/ecommerce/customers/vehicles/classes")
            .headers(dcpApiHeader)
            )

    .exec(http("E_class_GET")
            .get(mmeHost +"api/ecommerce/customers/vehicles/classes/1")
            .headers(dcpApiHeader)
            )

    .exec(http("E_IPD_GET")
            .get(mmeHost + "api/ecommerce/customers/vehicles/classes/1/demonstration")
            .headers(dcpApiHeader)
            )

    .exec(http("E_PDP_CONTENT_GET")
            .get(mmeHost + "api/ecommerce/customers/contents/pdp?classId=1")
            .headers(dcpApiHeader)
            )
    .exec(http("E_MODEL_COMPARE_GET")
            .get(mmeHost + "api/ecommerce/customers/vehicles/model-specs?models=21314210-CBB,21304810-CBA")
            .headers(dcpApiHeader)
            )
    .exec(http("CITIES_GET")
            .get(mmeHost + "api/ecommerce/cities?lat=0&lng=0")
            .headers(dcpApiHeader)
            )

    .exec(http("PDP_MODEL_PRODUCT_GET")
            .get(mmeHost + "api/ecommerce/customers/products?query=%3Aprice-asc%3Acity%3ABeijing%3Amodel%3A21314210-CBA")
            .headers(dcpApiHeader)
            )
    .exec(http("PDP_MODEL_ATTRIBUTES_GET")
            .get(mmeHost + "api/ecommerce/customers/vehicles/spus?model=21314210-CBA")
            .headers(dcpApiHeader)
            )
/*
    .exec(http("encryptionWithoutToken")
            .post(mmeHost + "api/ecommerce/financial/encryptionWithoutToken")
            .body(StringBody("""{ "nstCode":"21737810-CN1","productId":"1ea348b8-571f-45bd-b2b7-ffd5016c36b7","redirectTo":0,"regCity":"Beijing" }"""))
            .headers(dcpApiHeader)
            .resources()
            )

    .exec(http("GetMonthlyPayment")
            .get(baseURL + "api/bmbs/GetMonthlyPaymentByCode?nstCode=21737810-CN1&msrps=485800%2C485800%2C485800%2C501519%2C485800%2C485800%2C485800%2C485800%2C485800%2C485800%2C500000")
            .headers(dcpApiHeader)
            )

    .exec(CiamLoginStep.login)
    
    .exec(http("survery")
            .get(mmeHost + "api/ecommerce/customers/survey/dealers?city=Beijing")
            .headers(dcpApiHeader)
            .headers(headers_with_token)
            )

    .exec(http("get class")
            .get(mmeHost + "api/ecommerce/customers/vehicles/classes")
            .headers(dcpApiHeader)
            .headers(headers_with_token)
            )

    .exec(http("get security code")
            .get(mmeHost + "api/ecommerce/customers/captcha-pic")
            .headers(dcpApiHeader)
            .headers(headers_with_token)
            )
/*
    .exec(http("submit suvery")
            .post(mmeHost + "api/ecommerce/customers/surveys")
            .body(StringBody("""{ "captcha":"gmcd","captchaKey":"38c2ab1e-dd49-4620-b0bf-1a2d7a2f46a3","cityCode":"131","dealerId":"testdealer13,"expectedPurchaseTime":"4到6个月","mobile":"13809884395","name":"test","title":"LADY","vehicleModelId":"21314210-CBA" }"""))
            .headers(dcpApiHeader)
            .headers(headers_with_token)
            )
*/
    .exec(http("getHtmlContentConfigByCode")
            .get(mmeHost + "bmi/api/application/getHtmlContentConfigByCode?code=LegalDisclaimerMBE")
            .headers(dcpApiHeader)
            .headers(headers_with_token)
            )

    .exec(http("getSystemConfigList")
            .get(mmeHost + "bmi/api/websetting/getSystemConfigList")
            .headers(dcpApiHeader)
            .headers(headers_with_token)
            )

    .exec(http("getquotedata")
            .get(mmeHost + "bmi/api/finance/getquotedata?assetmodelid=884")
            .headers(dcpApiHeader)
            .headers(headers_with_token)
            )

    .exec(http("get_customers_products")
            .get(mmeHost + "api/ecommerce/customers/products/1ea348b8-571f-45bd-b2b7-ffd5016c36b7")
            .headers(dcpApiHeader)
            .headers(headers_with_token)
            )
    
    .exec(http("is_white_art")
            .get(mmeHost + "api/ecommerce/customers/vehicles/models/21314210-CBA/is-white-art")
            .headers(dcpApiHeader)
            .headers(headers_with_token)
            )
    .exec(http("get product")
            .get(mmeHost + "api/ecommerce/customers/products?query=%3Aprice-asc%3Acity%3ABeijing%3Amodel%3A21314210-CBB%3Awheel%3AR31%3AinteriorColour%3A115%3AexteriorColour%3A197%3AsingleOption%3Anull%3AoptionalPackage%3Anull")
            .headers(dcpApiHeader)
            .headers(headers_with_token)
            )
    .exec(http("view all dealers")
            .get(mmeHost + "api/ecommerce/customers/dealers?city=Beijing&sku=fb00433e-aa6b-43e3-b12b-3f24be9d9640")
            .headers(dcpApiHeader)
            .headers(headers_with_token)
            )
    .exec(http("get vehicles models")
            .get(mmeHost + "api/ecommerce/customers/vehicles/models/21304810-CBA")
            .headers(dcpApiHeader)
            .headers(headers_with_token)
            )
    .exec(http("get models gifts")
            .get(mmeHost + "api/ecommerce/customers/gifts?modelId=21304810-CBA")
            .headers(dcpApiHeader)
            .headers(headers_with_token)
            )

    .exec(http("get current user")
            .get(mmeHost + "api/ecommerce/customers/current")
            .headers(dcpApiHeader)
            .headers(headers_with_token)
            )
    .exec(http("create order")
            .get(mmeHost + "api/ecommerce/customers/${CID}/orders")
            .headers(dcpApiHeader)
            .headers(headers_with_token)
            .check(regex(""""reservationId":"([^"]*)""").saveAs("reservation_id"))
            .resources()
            )

    .exec(http("get order detail")
            .get(mmeHost + "api/ecommerce/customers/${CID}/orders/${reservation_id}")
            .headers(dcpApiHeader) 
            .headers(headers_with_token)   
            )
*/
    //setUp(scn.inject(atOnceUsers(500))).protocols(httpProtocol)
  setUp(scn.inject(splitUsers(500) into(rampUsers(10) over(5 seconds)) separatedBy(10 seconds))).protocols(httpProtocol)
}
