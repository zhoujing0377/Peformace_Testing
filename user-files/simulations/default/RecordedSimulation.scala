package default

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class RecordedSimulation extends Simulation {

	val httpProtocol = http
		.baseURL("http://computer-database.gatling.io")
		.inferHtmlResources()
		.acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
		.acceptEncodingHeader("gzip, deflate")
		.acceptLanguageHeader("en-us")
		.userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/601.6.17 (KHTML, like Gecko) Version/9.1.1 Safari/601.6.17")

	val headers_1 = Map("Accept" -> "*/*")

	val headers_10 = Map("Origin" -> "http://computer-database.gatling.io")

	val users = scenario("Users").exec(Search.search, Browse.browse)
	val admin = scenario("Admin").exec(Search.search, Browse.browse, Edit.edit)

		// search
		object Search {
			 val feeder = csv("search.csv").random
  		val search = exec(http("request_0")
			.get("/")
			.resources(http("request_1")
			.get("/favicon.ico")
			.headers(headers_1)
			.check(status.is(404))))
			.pause(2)
			.feed(feeder)
			.exec(http("request_2")
			.get("/computers?f=${searchCriterion}") // 4
    		.check(css("a:contains('${searchComputerName}')", "href").saveAs("computerURL"))) // 5
			.pause(3)
			.exec(http("request_3")
			.get("${computerURL}"))
			.pause(5)
		}

		// browser
		object Browse {

  		val browse = exec(http("request_4")
			.get("/"))
			.pause(3)
			.exec(http("request_5")
				.get("/computers?p=1"))
			.pause(1)
			.exec(http("request_6")
				.get("/computers?p=2"))
			.pause(2)
			.exec(http("request_7")
				.get("/computers?p=3"))
			.pause(2)
			.exec(http("request_8")
				.get("/computers?p=4"))
			.pause(5)
		}

		// edit
		object Edit {

  		val edit = exec(http("request_9")
				.get("/computers/new"))
			.pause(3)
			.exec(http("request_10")
			.post("/computers")
			.headers(headers_10)
			.formParam("name", "mytestcomputer")
			.formParam("introduced", "2002-01-01")
			.formParam("discontinued", "2016-12-01")
			.formParam("company", "1"))
		}

	setUp(users.inject(rampUsers(10) over (10 seconds)),admin.inject(rampUsers(2) over (10 seconds))).protocols(httpProtocol)
}