package exoplatformdatabase.forum
 
import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
class ForumReadTopicSimulation extends Simulation {
    val forumDataset = csv("forum.csv")
	val httpProtocol = http
		.baseURL("http://localhost:8080")
		
		.userAgentHeader("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.109 Safari/537.36")




	val scn = scenario("ForumReadTopicSimulation")
			.feed(forumDataset)
			.exec(http("Login")
				.post("/portal/login")
				.formParam("initialURI", "/portal/intranet/")
				.formParam("username", "${userName}")
				.formParam("password", "${userPassword}")
				.formParam("rememberme", "true")
		        	.check(status.is(200)))

						.pause(2)
			.exec(http("Goto Forum Topic")
				.get("/portal/intranet/${forumTopicUrl}/${forumTopicId}")
				.check(status.is(200)))

.pause(2)

.exec(session => {
  // print the Session for debugging, don't do that on real Simulations
  println(session)
  session
})

	setUp(scn.inject(atOnceUsers(10))).protocols(httpProtocol)
}
