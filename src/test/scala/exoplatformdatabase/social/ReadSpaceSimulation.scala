package exoplatformdatabase.social

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
class ReadSpaceSimulation extends Simulation {

    val spaceDataset = csv("spaces.csv")
	val httpProtocol = http
		.baseURL("http://localhost:8080")





	val scn = scenario("SocialReadSpaceSimulation")
			
			.feed(spaceDataset)
			.exec(http("Login")
				.post("/portal/login")
				.formParam("initialURI", "/portal/intranet/")
				.formParam("username", "${userName}")
				.formParam("password", "${userPassword}")
				.formParam("rememberme", "true")
		        .check(status.is(200))

	)

                exec(http("Goto SpaceId")
				.get("/portal/g/:spaces:${spaceId}/${spaceId}")
				.check(status.is(200)))



	setUp(scn.inject(rampUsers(10) over(5 seconds))).protocols(httpProtocol)
}
