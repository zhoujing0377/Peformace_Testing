package mme

import io.gatling.core.Predef._
import io.gatling.http.Predef.http


object Constant {
  val httpProtocol = http
    .extraInfoExtractor(extraInfo => List(extraInfo.response.statusCode.get)) //put status code to simulation.log
    .inferHtmlResources(BlackList(""".*\.js\?*.*""", """.*\.css\?*.*""", """.*\.gif""", """.*\.jpeg""", """.*\.jpg""", """.*\.ico""", """.*\.woff""", """.*\.(t|o)tf""", """.*\.png"""), WhiteList())
    .disableWarmUp
    .disableCaching

  val mmeHost = "https://estore-preprod.mercedes-benz.com.cn/"
  //val mmeHost = "https://estore.mercedes-benz.com.cn/"
}
