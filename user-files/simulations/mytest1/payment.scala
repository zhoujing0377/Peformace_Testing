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

//import com.fasterxml.jackson.databind.ObjectMapper

// rampTo100Users5s
// concurrent50Users
class payUser extends Simulation {

  val payClient = new PayClient(
    "https://pay.beautyyan.cn/pay/gateway.do",
    "2488033942878246",
    "56YNALLQ3fY3hakHHG8P5oNiXIvqnXtk",
    "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDeHwHS1HlC0JUxgE/Qqjj7v8xkKTE9Z5s1dStJH9STiwZS1WXNm630Ye2nehyoLATA/+cxTRk7wB67Ho2l/8NBRhFeTQHzge5I028M0x3yy9/NUcQUgJT4LhgpQFSlluzrCYzRRBje72Noyc8My0jzB2MRUq1OKqtpNZXC+U4guwIDAQAB")

  val targetHost = "https://estore-dev-bce.mercedes-benz.com.cn/"

  /**
    *
    * @param reservationId order reservationId
    * @param limitPay      alipay or wechat
    * @param dealerCode    dealerCode for payment mapping
    * @param intentionFee
    * @return pay request url
    */
  def payApply(reservationId: String, limitPay: String, dealerCode: String, intentionFee: Double): String = {
    val orderPayRequest: PayMentBean = new PayMentBean
    orderPayRequest.setService("pay.auth.pay.apply")
    orderPayRequest.setOut_trade_no(reservationId)
    orderPayRequest.setLimit_pay(limitPay)
    orderPayRequest.setSubject("mme subject")
    orderPayRequest.setSeller_user_id(dealerCode)
    val totalFee: java.math.BigDecimal = java.math.BigDecimal.valueOf(intentionFee)
    orderPayRequest.setTotal_fee(totalFee)
    orderPayRequest.setNotify_url(targetHost + "api/ecommerce/customers/payment/callback")
    orderPayRequest.setReturn_url("")
    orderPayRequest.setBody("mme body")
    payClient.payment(orderPayRequest)
  }

  def refund(reservationId: String): Boolean = {
    val refundRequest: RefundBean = new RefundBean
    refundRequest.setService("pay.auth.refund.apply")
    refundRequest.setOut_trade_no(reservationId)
    refundRequest.setNotify_url(targetHost + "api/ecommerce/admins/refund/callback")

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


  val payAndRefundScn = scenario("payment pay and refund test")
    .exec(http("payment payApply")
      .get(payApply("1234566744332", "alipay", "Admin", 0.01))
    )
    .pause(1)
    .exec(session => {
      val refundRes = refund("1234566744332")
      session.set("refundRes", refundRes)
    })

  val payAndProrateScn = scenario("payment pay and prorate test")
    .exec(http("payment payApply")
      .get(payApply("1234566744332", "alipay", "Admin", 0.01))
    )
    .pause(1)
    .exec(session => {
      val prorateRes = prorate("1234566744332")
      session.set("prorateRes", prorateRes)
    })

  //  setUp(scn.inject(atOnceUsers(1))).protocols(http)
  setUp(payAndRefundScn.inject(rampUsers(100) over (5 seconds)),
        payAndProrateScn.inject(rampUsers(100) over (5 seconds))
      ).protocols(http)
  // setUp(scn.inject(rampUsers(100) over (5 seconds))).protocols(httpProtocol)
  // setUp(scn.inject(constantUsersPerSec(20) during(15 seconds) randomized).protocols(httpProtocol)
  // setUp(scn.inject(splitUsers(100) into(rampUsers(10) over(10 seconds)) separatedBy(10 seconds)).protocols(httpProtocol)

}
