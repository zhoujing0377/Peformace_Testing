import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import io.gatling.core.feeder._
import com.pay.api.PayClient
import com.pay.api.bean.request.PayMentBean
import com.pay.api.bean.request.ProrateBean
import com.pay.api.bean.request.RefundBean
import com.pay.api.bean.response.PayMentResponse
import com.pay.api.bean.response.ProrateResponse
import com.pay.api.bean.response.RefundResponse
import com.fasterxml.jackson.databind.ObjectMapper

class concurrent50UsersPay extends Simulation {

    val httpProtocol = http
    .baseURL("https://estore-preprod.mercedes-benz.com.cn")
    .inferHtmlResources(BlackList(""".*\.js""", """.*\.css""", """.*\.gif""", """.*\.jpeg""", """.*\.jpg""", """.*\.ico""", """.*\.woff""", """.*\.(t|o)tf""", """.*\.png"""), WhiteList())
    .acceptHeader("application/json, text/plain, */*")
    .acceptEncodingHeader("gzip, deflate, sdch")
    .acceptLanguageHeader("en-US,en;q=0.8,zh-CN;q=0.6,zh;q=0.4")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2940.98 Safari/537.36")

  val headers_login = Map(
    "Content-Type" -> "application/json")

  val headers_with_token = Map(
    "MME-TOKEN" -> "${token}")


  val payClient = new PayClient(
    "https://pay.beautyyan.cn/pay/gateway.do",
    "2488033942878246",
    "56YNALLQ3fY3hakHHG8P5oNiXIvqnXtk",
    "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDeHwHS1HlC0JUxgE/Qqjj7v8xkKTE9Z5s1dStJH9STiwZS1WXNm630Ye2nehyoLATA/+cxTRk7wB67Ho2l/8NBRhFeTQHzge5I028M0x3yy9/NUcQUgJT4LhgpQFSlluzrCYzRRBje72Noyc8My0jzB2MRUq1OKqtpNZXC+U4guwIDAQAB")

  val targetHost = "https://estore-preprod.mercedes-benz.com.cn"

  val feeder = csv("accounts.csv")
  val product = csv("products.csv")
  /**
    *
    * @param reservationId order reservationId
    * @param limitPay      alipay or wechat
    * @param dealerCode    dealerCode for payment mapping
    * @param intentionFee
    * @return pay request url
    */
 def payApply(reservationId: String, limitPay: String, dealerCode: String, intentionFee: Double): String = {
    // println(reservationId)
    val orderPayRequest: PayMentBean = new PayMentBean
    orderPayRequest.setService("pay.auth.pay.apply")
    orderPayRequest.setOut_trade_no(reservationId)
    orderPayRequest.setLimit_pay(limitPay)
    orderPayRequest.setSubject("mme subject")
    orderPayRequest.setSeller_user_id(dealerCode)
    val totalFee: java.math.BigDecimal = java.math.BigDecimal.valueOf(intentionFee)
    orderPayRequest.setTotal_fee(totalFee)
    orderPayRequest.setNotify_url(targetHost + "/api/ecommerce/customers/payment/callback")
    orderPayRequest.setReturn_url(targetHost + "/api/ecommerce/customers/payment/callback")
    orderPayRequest.setBody("mme body")
    val url=payClient.payment(orderPayRequest)
    // println(url)
    url
  }

  def refund(reservationId: String): Boolean = {
    val refundRequest: RefundBean = new RefundBean
    refundRequest.setService("pay.auth.refund.apply")
    refundRequest.setOut_trade_no(reservationId)
    refundRequest.setNotify_url(targetHost + "/api/ecommerce/admins/refund/callback")

    val refundResponse: RefundResponse = payClient.refund(refundRequest)
    val res = refundResponse.getCode == "200"
    if (!res) {
      println(refundResponse.getErr_msg)
    }
    res
  }

  def prorate(reservationId: String): Boolean = {
    val prorateBean: ProrateBean = new ProrateBean
    prorateBean.setService("pay.auth.prorate.apply")
    prorateBean.setOut_trade_no(reservationId)
    prorateBean.setNotify_url(targetHost + "/api/ecommerce/payment/prorate/callback")

    val prorateResponse: ProrateResponse = payClient.prorate(prorateBean)

    val res = prorateResponse.getCode == "200"
    if (!res) {
      println(prorateResponse.getErr_msg)
    }
    res
  }

  val payAndRefundScn = scenario("50 concurrent users request payment")

    .feed(feeder)
    .feed(product)
    .exec(http("Login")
      .post("/api/ecommerce/user/login")
      .headers(headers_login)
      .body(StringBody("""{ "mobile":"${mobile}","password":"${password}" }"""))
      .check(header("MME-TOKEN").saveAs("token"))
      .check(regex(""""id":"([^"]*)""").saveAs("user_id"))
      .resources()
    )
    .pause(3)

    .exec(http("CREATE_Order")
      .post("/api/ecommerce/customers/${user_id}/orders")
      .headers(headers_login)
      .body(StringBody("""{ "contactName":"${username}","contactMobile":"${mobile}","productId":${product_id},"dealerId":${dealer_id},"giftId":${gift_id},"financialPlanTerm":null,"financialPlanDownpayment":null,"financialPlanBalloonpayment":null,"financialPlanMonthlyPayment":null }"""))
      .headers(headers_with_token)
      .resources()
    )
    .pause(3)
    .exec(http("GET_ORDER_LIST")
      .get("/api/ecommerce/customers/${user_id}/orders")
      .headers(headers_with_token)
      .check(regex(""""reservationId":"([^"]*)""").saveAs("reservation_id"))
      .resources()
    )   
    .pause(3)
    .exec(http("Payment")
      .post("/api/ecommerce/customers/${user_id}/orders/${reservation_id}/payment")
      .headers(headers_login)
      .headers(headers_with_token)
      .body(StringBody("""{ "subject":"支付意向金","body":"E 200 L 轿车-1","limit_pay":"alipay","total_fee":4999,"dealer_code":"Admin","return_url": "https://estore-preprod.mercedes-benz.com.cn/pay-result" }"""))
      .resources()
    )
    .pause(1)
    .exec(session => {
      val payURL = payApply(session("reservation_id").as[String],"alipay", "Admin", 0.01)
      session.set("payURL", payURL)
    })
    .exec(http("YY Payment redirect request")
      .get("${payURL}")
    )
   .pause(3)
   .exec(http("Pay callback")
      .post("https://pay.beautyyan.cn/paytest/payment/notifyH5Test_url.do?payer_user_id=zzz2134d546234sd&out_trade_no=${reservation_id}&trade_no=2014112400001000340011111118&trade_status=TRADE_SUCCESS&total_fee=4999")
      )
   setUp(payAndRefundScn.inject(atOnceUsers(50))).protocols(httpProtocol)
}
