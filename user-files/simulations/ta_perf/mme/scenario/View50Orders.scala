package mme.scenario

import io.gatling.core.Predef._
import mme.Constant._
import mme.step.user.CurrentUserStep
import mme.step.login._
import mme.step.user._

class ViewOrderList extends Simulation {

  val scn = scenario("single user get 50 orders list")
    .exec(CiamLoginStep.login, ViewOderStep.viewOrder)

  setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}
