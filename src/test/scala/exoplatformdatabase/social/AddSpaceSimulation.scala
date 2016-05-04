package exoplatformdatabase.social

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
class AddSpaceSimulation extends Simulation {

    val spaceDataset = csv("addSpaces.csv")
	val httpProtocol = http
		.baseURL("http://localhost:7070")

	val scn = scenario("AddSpaceSimulation")
			.feed(spaceDataset)
				.exec(http("Login")
					.post("/portal/login")
						.formParam("initialURI", "/portal/intranet/")
						.formParam("username", "${userName}")
						.formParam("password", "${userPassword}")
						.formParam("rememberme", "true")
		        		.check(status.is(200)))

                .exec(http("Load Spaces")
					.get("/portal/intranet/spaces")
						.check(status.is(200))
						.check(regex("""gtn:csrf.*value="([^"]*)"""").saveAs("gtncsrf"))
						.check(regex("""UISpaceSearch.*\?portal:componentId=([^&]+)(&amp;|&)interactionstate=([^&]+)(&amp;|&).*""").saveAs("loadSpace_componentId"))
						.check(regex("""UISpaceSearch.*interactionstate=([^&]+)(&amp;|&).*""").saveAs("loadSpace_interactionstate")))

				.exec(http("Add Space")
					.post("/portal/intranet/spaces?portal:componentId=${loadSpace_componentId}&interactionstate=${loadSpace_interactionstate}&portal:type=action&ajaxRequest=true")
						.formParam("formOp", "AddSpace")
						.formParam("gtn:csrf", "${gtncsrf}")
						.formParam("SpaceSearch", "")	
						.check(regex("""UISpaceAddForm.*interactionstate=([^&]+)(&amp;|&).*""").saveAs("addSpace_interactionstate")))

				.exec(http("Save Space")
					.post("/portal/intranet/spaces?portal:componentId=${loadSpace_componentId}&interactionstate=${addSpace_interactionstate}&portal:type=action&objectId=UISpaceVisibility&ajaxRequest=true")
						.formParam("formOp", "Create")
						.formParam("gtn:csrf", "${gtncsrf}")
						.formParam("currentSelectedTab", "UISpaceVisibility")	
						.formParam("displayName", "${spaceName}")	
						.formParam("description", "${spaceDescription}")
						.formParam("UIVisibility", "${spaceVisibility}")	
						.formParam("UIRegistration", "${spaceMode}"))


	setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}
