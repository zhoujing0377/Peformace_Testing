package mme.scenario

import io.gatling.core.Predef._
import mme.Constant._
import mme.step.user.CurrentUserStep
import mme.step.login._

class Concurrent38UsersLogin extends Simulation {

  val scn = scenario("38 concurrent users login")
    .exec(CiamLoginStep.login)

  setUp(scn.inject(atOnceUsers(38))).protocols(httpProtocol)
}
