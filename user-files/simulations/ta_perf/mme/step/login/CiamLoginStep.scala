package mme.step.login

import java.nio.charset.StandardCharsets.UTF_8

import com.fasterxml.jackson.databind.ObjectMapper
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.response.{ResponseWrapper, StringResponseBody}

import scala.collection.JavaConverters._
import mme.Constant._

object CiamLoginStep {

  val ciamConsentUrl = "https://api-test.secure.mercedes-benz.com/oidc10/auth/oauth/v2/authorize/consent"
  val loginUrl = "https://login-test.secure.mercedes-benz.com/wl/login"


  val dcpLoginHeader = Map(
    "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8",
    "Accept-Encoding" -> "gzip, deflate, sdch, br",
    "Accept-Language" -> "zh-CN,zh;q=0.8,en;q=0.6",
    "Connection" -> "keep-alive",
    "Upgrade-Insecure-Requests" -> "1",
    "User-Agent" -> "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1")

  val login =
    exec(http("DCP Login Request")
      .get(mmeHost + "/dcp-api/v2/auth/ciam/redirect?site=dcp-ovs-cn&lang=zh-CN&cntxt=login&mode=in-place")
      .headers(dcpLoginHeader)
      .check(currentLocationRegex("""sessionID=([^&]*)""").saveAs("sessionID"))
      .check(currentLocationRegex("""sessionData=([^&]*)""").saveAs("sessionData"))
      .check(currentLocationRegex("""app-id=([^&]*)""").saveAs("appId"))
    )
      .pause(1)
      .feed(csv("users.csv"))
      .exec(http("Login Post")
        .post(loginUrl)
        .headers(dcpLoginHeader)
        .formParam("SMAUTHREASON", "")
        .formParam("target", "")
        .formParam("acr_values", "")
        .formParam("sessionID", "${sessionID}")
        .formParam("sessionData", "${sessionData}")
        .formParam("t", "")
        .formParam("app-id", "${appId}")
        .formParam("lang", "zh_CN")
        .formParam("username", "${userName}")
        .formParam("password", "${password}")
        .check(
          css("input[name='sessionID']", "value").saveAs("sessionID"),
          css("input[name='sessionData']", "value").saveAs("sessionData"))
      )
      .exec(http("consent")
        .post(ciamConsentUrl)
        .headers(dcpLoginHeader)
        .formParam("action", "Grant")
        .formParam("sessionID", "${sessionID}")
        .formParam("sessionData", "${sessionData}")
        .transformResponse { //will be run in every redirection response
          case response if response.request.getUrl.contains("/ciamanswer") =>
            val cookies = response.request.getCookies.asScala.toList
            val ciamAnswer = cookies.filter(_.getName == "CIAM_RESPONSE_DATA").map(_.getValue).head.replaceAll("\\\\", "")
            val maper = new ObjectMapper
            val node = maper.readTree(ciamAnswer)
            val cid = node.findValue("uid").asText()
            val accessToken = node.findValue("access_token").asText()
            new ResponseWrapper(response) {
              override val body = new StringResponseBody(
                s"""{
                  |"accessToken":"bearer $accessToken",
                  |"cid":"$cid"
                  |}
                """.stripMargin, UTF_8)
            }
        }
        .check(jsonPath("$.cid").saveAs("CID"),jsonPath("$.accessToken").saveAs("accessToken"))
      )
}
