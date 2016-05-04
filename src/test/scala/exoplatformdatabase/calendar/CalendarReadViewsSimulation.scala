package exoplatformdatabase.calendar

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
class CalendarReadViewsSimulation extends Simulation {
       val calendarDataset = csv("users.csv")
	   val httpProtocol = http
		.baseURL("http://localhost:8080")
		
	

	val scn = scenario("CalendarReadViewsSimulation")

			.feed(calendarDataset)
			.exec(http("Login")
				.post("/portal/login")
				.formParam("initialURI", "/portal/intranet/")
				.formParam("username", "${userName}")
				.formParam("password", "${userPassword}")
				.formParam("rememberme", "true")
		        .check(status.is(200)))
			.exec(http("Goto Calendar Home")
				.get("/portal/intranet/calendar")
				.check(status.is(200)))
	
	setUp(scn.inject(atOnceUsers(3))).protocols(httpProtocol)
}