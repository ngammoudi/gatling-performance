package exoplatformdatabase.gatein

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import java.util.concurrent.ThreadLocalRandom
class NavigationPageSimulation extends Simulation {
       val adminDataset = csv("admin.csv")
	   val httpProtocol = http
		.baseURL("http://localhost:8080")

	val scn = scenario("NavigationPageSimulation")

			.feed(adminDataset)
				.exec(http("Login")
					.post("/portal/login")
						.formParam("initialURI", "/portal/intranet/")
						.formParam("username", "${userName}")
						.formParam("password", "${userPassword}")
						.formParam("rememberme", "true")
				        .check(status.is(200)))
            	.exec(http("Page Form")
					.get("/portal/intranet/?portal:componentId=UIWorkingWorkspace&portal:action=PageCreationWizard&ajaxRequest=true")
						.check(regex("""gtn:csrf.*value="([^"]*)"""").saveAs("gtncsrf")))

				.exec(http("ChangeNode")
					.post("/portal/intranet/?portal:componentId=UIWizardPageSetInfo&subComponentId=TreePageSelector&objectId=&ajaxRequest=true")
						.formParam("formOp", "ChangeNode")
						.formParam("gtn:csrf", "${gtncsrf}")
						.formParam("pageName", "")
						.formParam("switchmode", "on")
						.formParam("languages", "en")
						.formParam("i18nizedLabel", "")
						.formParam("visible", "on"))

				.exec(http("ViewStep2")
					.post("/portal/intranet/?portal:componentId=UIWizardPageSetInfo&ajaxRequest=true")
						.formParam("formOp", "ViewStep2")
						.formParam("gtn:csrf", "${gtncsrf}")
						.formParam("pageName", "${pageName}")
						.formParam("switchmode", "on")
						.formParam("languages", "en")
						.formParam("i18nizedLabel", "")
						.formParam("visible", "on"))

				.exec(http("ViewStep3")
					.post("/portal/intranet/?portal:componentId=UIWizardPageSelectLayoutForm&ajaxRequest=true")
						.formParam("formOp", "ViewStep3")
						.formParam("gtn:csrf", "${gtncsrf}")
						.formParam("UIPageTemplateOptions", "")
						.check(regex("""UIPage-([^"]*)""").saveAs("uiPageId")))

				.exec(http("Choose Portlet")
					.get("/portal/intranet/")
						.queryParam("portal:componentId", "UIPortal")
					    .queryParam("portal:action", "MoveChild")
					    .queryParam("srcID", "Administration/ApplicationRegistryPortlet")
					    .queryParam("targetID", "${uiPageId}")
					    .queryParam("insertPosition", "-1")
					    .queryParam("isAddingNewly", "true")
					    .queryParam("ajaxRequest", "true"))
				.exec(http("Save Page")
					.get("/portal/intranet/")
						.queryParam("portal:componentId", "UIPageEditor")
					    .queryParam("portal:action", "Finish"))
		.pause(1)
		.exec(session => {
  // print the Session for debugging, don't do that on real Simulations
  println(session)
  session
})	
	setUp(scn.inject(atOnceUsers(3))).protocols(httpProtocol)
}