package mme.scenario

import io.gatling.core.Predef._
import mme.Constant._
import mme.step.user.CurrentUserStep
import mme.step.login._

class CiamLogin extends Simulation {

  val scn = scenario("Login and GetCurrentUser and Logout")
    .exec(CiamLoginStep.login, CurrentUserStep.currentUser,CiamLogoutStep.logout)

  setUp(scn.inject(atOnceUsers(15))).protocols(httpProtocol)
}
