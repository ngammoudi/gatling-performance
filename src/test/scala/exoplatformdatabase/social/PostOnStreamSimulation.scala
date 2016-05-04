package exoplatformdatabase.social

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class PostOnStreamSimulation extends Simulation {
    val userDataset = csv("users.csv")
	val httpProtocol = http
		.baseURL("http://localhost:8080")
		




	val scn = scenario("PostOnStreamSimulation")
			.feed(userDataset)
			.exec(http("Login to intranet")
				.post("/portal/login")
				.formParam("initialURI", "/portal/intranet/")
				.formParam("username", "${userName}")
				.formParam("password", "${userPassword}")
				.formParam("rememberme", "true")
		        .check(status.is(200))	
				.check(regex("""form class="UIForm" id="UIComposer".*portal:componentId=([^&]+)(&amp;|&).*""").saveAs("post_componentId"))
				.check(regex("""form class="UIForm" id="UIComposer".*interactionstate=([^&]+)(&amp;|&).*""").saveAs("interactionstate"))
				.check(regex("""gtn:csrf.*value="([^"]*)"""").saveAs("gtncsrf")))

.repeat(5) {exec(http("Post On Stream")
				.post("http://localhost:8080/portal/intranet/?portal:componentId=${post_componentId}&interactionstate=${interactionstate}&portal:type=action&ajaxRequest=true")
				.formParam("formOp", "PostMessage")
				.formParam("gtn:csrf", "${gtncsrf}")
				.formParam("composerInput", "HELLO GATLING")
				.formParam("InputLink", ""))}   

.exec(session => {
  // print the Session for debugging, don't do that on real Simulations
  println(session)
  session
})
	setUp(scn.inject(atOnceUsers(10))).protocols(httpProtocol)
}
