


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
    * @param reservationId  order reservationId
    * @param limitPay alipay or wechat
    * @param dealerCode dealerCode for payment mapping
    * @param intentionFee
    * @return
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

    refundResponse.getCode == "200"
  }

  def prorate(reservationId: String): Boolean = {
    val prorateBean: ProrateBean = new ProrateBean
    prorateBean.setService("pay.auth.prorate.apply")
    prorateBean.setOut_trade_no(reservationId)
    prorateBean.setNotify_url(targetHost + "/api/ecommerce/payment/prorate/callback")

    val prorateResponse: ProrateResponse = payClient.prorate(prorateBean)
    prorateResponse.getCode == "200"
  }


  val httpProtocol = http
    .baseURL(targetHost)
    // .baseURL("https://estore-int01.mercedes-benz.com.cn")
    .inferHtmlResources(BlackList(""".*\.js""", """.*\.css""", """.*\.gif""", """.*\.jpeg""", """.*\.jpg""", """.*\.ico""", """.*\.woff""", """.*\.(t|o)tf""", """.*\.png"""), WhiteList())
    .acceptHeader("application/json, text/plain, */*")
    .acceptEncodingHeader("gzip, deflate, sdch")
    .acceptLanguageHeader("en-US,en;q=0.8,zh-CN;q=0.6,zh;q=0.4")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2940.98 Safari/537.36")


  val headers_login = Map(
    "Content-Type" -> "application/json")

  val headers_with_token = Map(
    "MME-TOKEN" -> "${token}")

  val feeder = csv("accounts.csv").random

  val scn = scenario("single user to customer site")
    // val scn = scenario("Injects 100 users with a linear ramp over 5s.")

    .exec(http("homepage_campaign_GET")
    .get("/api/ecommerce/customers/contents/home-campaigns")
    .headers(headers_login)
  )

    .exec(http("homepage_footer_GET")
      .get("/api/ecommerce/customers/contents/home-buttons")
      .headers(headers_login)
    )

    .exec(http("classes_GET")
      .get("/api/ecommerce/customers/vehicles/classes")
      .headers(headers_login)
    )

    .exec(http("E_class_GET")
      .get("/api/ecommerce/customers/vehicles/classes/94")
      .headers(headers_login)
    )

    .exec(http("E_IPD_GET")
      .get("/api/ecommerce/customers/vehicles/classes/94/demonstration")
      .headers(headers_login)
    )

    .exec(http("E_PDP_CONTENT_GET")
      .get("/api/ecommerce/customers/contents/pdp?classId=94")
      .headers(headers_login)
    )

    .exec(http("E_MBFS_GET")
      .get("/api/ecommerce/customers/mbfs?line=94")
      .headers(headers_login)
    )

    .exec(http("FOOTER_BOTTONS_GET")
      .get("/api/ecommerce/customers/contents/home-buttons")
      .headers(headers_login)
    )

    .exec(http("E_MODEL_COMPARE_GET")
      .get("/api/ecommerce/customers/vehicles/model-specs?models=173,172")
      .headers(headers_login)
    )

    .exec(http("CITIES_GET")
      .get("/api/ecommerce/cities")
      .headers(headers_login)
    )

    // .exec(http("E_class_GET")
    // 	.get("/api/ecommerce/customers/vehicles/classes/94")
    // 	.headers(headers_login)
    //     )

    .exec(http("PDP_MBFS_GET")
    .get("/api/ecommerce/customers/mbfs")
    .headers(headers_login)
  )

    .exec(http("PDP_E200L_PRODUCT_GET")
      .get("/api/ecommerce/customers/products?query=%3Aprice-asc%3Acity%3A131%3Amodel%3A21314210-CBA")
      .headers(headers_login)
    )

    .exec(http("PDP_E200L_ATTRIBUTES_GET")
      .get("/api/ecommerce/customers/vehicles/spus?model=173")
      .headers(headers_login)
    )

    .exec(http("PDP_E300L_ATTRIBUTES_GET")
      .get("/api/ecommerce/customers/vehicles/spus?model=172")
      .headers(headers_login)
    )

    .exec(http("PRODUCT_GET")
      .get("/api/ecommerce/customers/products/2172")
      .headers(headers_login))
    .feed(feeder)
    .exec(http("Login")
      .post("/api/ecommerce/user/login")
      .headers(headers_login)
      .body(StringBody("""{ "mobile":"${mobile}","password":"${password}" }"""))
      .check(header("MME-TOKEN").saveAs("token"))
      .check(regex(""""id":"([^"]*)""").saveAs("user_id"))
      .resources()
    )

    .exec(http("Get customer profile")
      .get("/api/ecommerce/customers/current")
      .header("MME-TOKEN", "${token}")
    )

    .exec(http("DEALERS_GET")
      .get("/dealers?city=131&exteriorColours=775&interiorColours=115&model=176&optionalPackages%5B%5D=DB1&price=474800&singleOptions%5B%5D=873&sku=2172&styleName=%E9%95%BF%E8%BD%B4%E8%B7%9DE%E7%BA%A7%E8%BF%90%E5%8A%A8%E8%BD%BF%E8%BD%A6&wheels=R47")
      .headers(headers_login)
      .headers(headers_with_token)
    )

    .exec(http("DEALER_GET")
      .get("/api/ecommerce/customers/dealers/245")
      .headers(headers_login)
    )

    .exec(http("DEALER_COMMENTS_GET")
      .get("/api/ecommerce/customers/dealers/245/comments?pageIndex=0&pageSize=10")
      .headers(headers_login)
      .headers(headers_with_token)
    )

    // .exec(http("CREATE_Order")
    // 	.post("/api/ecommerce/customers/${user_id}/orders")
    // 	.headers(headers_login)
    // 	.body(StringBody("""{ "contactName":"${username}","contactMobile":"${mobile}","productId":2172,"dealerId":245,"giftId":161,"financialPlanTerm":null,"financialPlanDownpayment":null,"financialPlanBalloonpayment":null,"financialPlanMonthlyPayment":null }"""))
    // 	.headers(headers_with_token)
    // 	// .check(regex(""""id":"([^"]*)""").saveAs("reservation_id"))
    // 	.resources()
    // )

    .exec(http("GET_ORDER_LIST")
    .get("/api/ecommerce/customers/${user_id}/orders")
    .headers(headers_with_token)
    // .check(regex(""""reservationId":"([^"]*)""").saveAs("reservation_id"))
    .resources()
  )

    // .exec(http("GET_ORDER_DETAILS")
    // 	.get("/api/ecommerce/customers/${user_id}/orders/${reservation_id}")
    // 	.headers(headers_with_token)
    // )

    // .exec(http("CANCEL_ORDER")
    // 	.post("/api/ecommerce/customers/${user_id}/orders/${reservation_id}/cancel")
    // 	.headers(headers_with_token)
    // )

    .exec(http("Add_favourite_items")
    .post("/api/ecommerce/customers/${user_id}/favourites")
    .headers(headers_with_token)
    .headers(headers_login)
    .body(StringBody("""{ "city":"131","classId":"94","style":97,"styleName":"长轴距E级运动轿车","model":176,"modelCode":"21314810-CBB","modelName":"E 300 L 时尚型运动轿车","selection":{"exteriorColour":"775","interiorColour":"115","wheel":"R47","optionalPackage":["DB1"],"singleOption":["873"]},"preview":"https://estore-dev-bce.mercedes-benz.com.cn/images/pop/vehicle/ess/775_90.png","exteriorColour":{"code":"775","name":"北极白色","picture":"https://estore-dev-bce.mercedes-benz.com.cn/images/pop/exteriors/775.jpg","color":"#DADFD2","price":0,"description":null,"preview":"https://estore-dev-bce.mercedes-benz.com.cn/images/pop/vehicle/ess/775_90.png","disabled":false},"interiorColour":{"code":"115","name":"玛奇朵米色/黑色115","picture":"https://estore-dev-bce.mercedes-benz.com.cn/images/pop/interiors/115.jpg","color":"#7B6D62","price":0,"description":null,"preview":null,"disabled":false},"wheel":{"code":"R47","name":"48.3 厘米（19 英寸）6 辐轻合金车轮","picture":"https://estore-dev-bce.mercedes-benz.com.cn/images/pop/wheels/R47.jpg","color":null,"price":null,"description":null,"preview":null,"disabled":false},"optionalPackage":[{"code":"DB1","name":"智驾套装","picture":"https://estore-dev-bce.mercedes-benz.com.cn/images/pop/optional_packages/DB1.jpg","color":null,"price":null,"description":null,"preview":null,"disabled":false}],"singleOption":[{"code":"873","name":"可加热前排座椅","picture":"https://estore-dev-bce.mercedes-benz.com.cn/images/pop/single_options/873.jpg","color":null,"price":null,"description":null,"preview":null,"disabled":false}] }"""))
    .resources()
  )

    .exec(http("Get_favourite_list_pagination")
      .get("/api/ecommerce/customers/${user_id}/favourites?pageIndex=0&pageSize=10")
      .headers(headers_with_token)
    )

    .exec(http("Get_favourite_items")
      .get("/api/ecommerce/customers/products?query=%3Aprice-asc%3Acity%3A131%3Amodel%3A21314210-CBA%3Awheel%3A66R%3AinteriorColour%3A144%3AexteriorColour%3A197%3AsingleOption%3Anull%3AoptionalPackage%3Anull")
      .headers(headers_with_token)
    )
  setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
  // setUp(scn.inject(rampUsers(100) over (5 seconds))).protocols(httpProtocol)
  // setUp(scn.inject(constantUsersPerSec(20) during(15 seconds) randomized).protocols(httpProtocol)
  // setUp(scn.inject(splitUsers(100) into(rampUsers(10) over(10 seconds)) separatedBy(10 seconds)).protocols(httpProtocol)

}
