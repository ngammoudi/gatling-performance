package exoplatformdatabase.calendar

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
class CalendarUpdateEvent extends Simulation {
       val calendarDataset = csv("calendarUpdate.csv")
	   val httpProtocol = http
		.baseURL("http://localhost:7070")
		
	

	val scn = scenario("CalendarUpdateEvent")

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
				.check(status.is(200))
				.check(regex("""gtn:csrf.*value="([^"]*)"""").saveAs("gtncsrf"))
				.check(regex("""UIWeekView.*\?portal:componentId=([^&]+)(&amp;|&)interactionstate=([^&]+)(&amp;|&).*""").saveAs("updateEvent_componentId"))
				.check(regex("""UIWeekView.*interactionstate=([^&]+)(&amp;|&).*""").saveAs("updateEvent_interactionstate")))
	.exec(http("Load Event") 
			.post("/portal/intranet/calendar?portal:componentId=${updateEvent_componentId}&interactionstate=${updateEvent_interactionstate}&portal:type=action&subComponentId=UIWeekView&objectId=${eventId}&calendarId=${calendarGroup}&calType=${calType}&isOccur=false&recurId=&ajaxRequest=true")
			.formParam("formOp", "Edit")
			.formParam("gtn:csrf", "${gtncsrf}")
			.formParam("eventCategories", "defaultEventCategoryIdAll")
			.check(regex("""UIEventForm.*portal:componentId=([^&]+)(&amp;|&)interactionstate=([^&]+)(&amp;|&).*""").saveAs("saveEvent_componentId"))
			.check(regex("""UIEventForm.*interactionstate=([^&]+)(&amp;|&).*""").saveAs("saveEvent_interactionstate")))
	.exec(http("Update Event")
			.post("/portal/intranet/calendar?portal:componentId=${saveEvent_componentId}&interactionstate=${saveEvent_interactionstate}&portal:type=action&ajaxRequest=true")
			.formParam("formOp", "Save")
			.formParam("gtn:csrf", "${gtncsrf}")
			.formParam("currentSelectedTab", "eventDetail")
			.formParam("eventName", "${eventName}")
			.formParam("description", "${description}")
			.formParam("from", "${fromDate}")
			.formParam("fromTime", "${fromTime}")
			.formParam("to", "${toDate}")
			.formParam("toTime", "${toTime}")
			.formParam("calendar", "${calType}:${calendarGroup}")
			.formParam("category", "${eventCategory}"))
	.exec(session => {
  // print the Session for debugging, don't do that on real Simulations
  println(session)
  session
})

	setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}

