package mme.step.user

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import mme.Constant._

object ViewOderStep {

  val dcpApiHeader = Map(
    "Accept" -> "application/json, text/plain, */*",
    "Accept-Encoding" -> "gzip, deflate, sdch, br",
    "Accept-Language" -> "zh-CN,zh;q=0.8,en;q=0.6",
    "Connection" -> "keep-alive",
    "FRONT-END-HTTPS" -> "on",
    "If-None-Match" -> "0ccf5f311a98b009bdd76a1bbdfcc2c9c",
    "User-Agent" -> "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1",
    "X-Requested-With" -> "XMLHttpRequest",
    "MME-TOKEN" -> "${accessToken}",
    "CID" ->"${CID}",
    "lang" -> "zh_CN")

  val viewOrder =
    exec(http("get order list")
      .get(mmeHost + "/api/ecommerce/customers/${CID}/orders")
      .headers(dcpApiHeader)
      //.check(regex(""""reservationId":"([^"]*)""").saveAs("reservation_id")) 
    )
      .exec(http("Get customer profile")
        .get(mmeHost + "/api/ecommerce/customers/current")
        .headers(dcpApiHeader))
}