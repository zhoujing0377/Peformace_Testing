package mme.step.user

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import mme.Constant._

object CurrentUserStep {

  val dcpApiHeader = Map(
    "Accept" -> "application/json, text/plain, */*",
    "Accept-Encoding" -> "gzip, deflate, sdch, br",
    "Accept-Language" -> "zh-CN,zh;q=0.8,en;q=0.6",
    "Connection" -> "keep-alive",
    "FRONT-END-HTTPS" -> "on",
    "If-None-Match" -> "0ccf5f311a98b009bdd76a1bbdfcc2c9c",
    "User-Agent" -> "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1",
    "X-Requested-With" -> "XMLHttpRequest",
    "authorization" -> "bearer ${accessToken}",
    "lang" -> "zh_CN")

  val currentUser =
    exec(http("get current")
      .get(mmeHost + "/dcp-api/v2/dcp-ovs-cn/users/current")
      .headers(dcpApiHeader)
      .check(jsonPath("$.uid").saveAs("currentUser")) // syntax refer to http://goessner.net/articles/JsonPath/
    )
      .exec(http("get payment")
        .get(mmeHost + "/dcp-api/v2/dcp-ovs-cn/users/${currentUser}/addresses/payment?fields=FULL")
        .headers(dcpApiHeader))
}