package mme.step.login

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import mme.Constant._
import mme.step.login.CiamLoginStep._

object CiamLogoutStep {
  val logout = exec(http("Dcp Logout")
    .get(mmeHost + "/dcp-api/v2/auth/ciam/redirect?site=dcp-ovs-cn&lang=zh-CN&cntxt=logout&mode=in-place")
    .headers(dcpLoginHeader))
}