package mme.scenario

import io.gatling.core.Predef._
import mme.Constant._
import mme.step.user.CurrentUserStep
import mme.step.login._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class Concurrent38UsersCreateOrder extends Simulation {

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
val scn = scenario("38 concurrent users create order")
    
    .exec(CiamLoginStep.login)
    .feed(csv("TA_products.csv"))
    .exec(http("get current user")
            .get(mmeHost + "api/ecommerce/customers/current")
            .headers(dcpApiHeader)
            .headers(headers_with_token)
            .check(regex(""""realName":"([^"]*)""").saveAs("username"))
            .check(regex(""""mobile":"([^"]*)""").saveAs("mobile"))
            .resources()
            )

    .exec(http("create order") 
            .post(mmeHost + "api/ecommerce/customers/${CID}/orders")
            .body(StringBody("""{ "contactName":"${username}","contactMobile":"${mobile}","productId":"${productId}","dealerId":"${dealerId}","giftId":"gift17","giftName":"个性化钥匙扣" }"""))
            .headers(dcpApiHeader)
            .headers(headers_with_token)
            .resources()
            ) 

    .exec(http("get order list")
            .get(mmeHost + "api/ecommerce/customers/${CID}/orders")
            .headers(dcpApiHeader) 
            .headers(headers_with_token)
            .check(regex(""""reservationId":"([^"]*)""").saveAs("reservation_id"))
            .resources()   
            )

    .exec(http("view order detail")
            .get(mmeHost + "api/ecommerce/customers/${CID}/orders/${reservation_id}")
            .headers(dcpApiHeader) 
            .headers(headers_with_token)  
            .resources() 
            )
/*
    .exec(http("cancel the order")
        .post(mmeHost + "/api/ecommerce/customers/${CID}/orders/${reservation_id}/cancel")
        .headers(dcpApiHeader) 
        .headers(headers_with_token)
        .resources()  
        )
*/
  setUp(scn.inject(atOnceUsers(38))).protocols(httpProtocol)
}
