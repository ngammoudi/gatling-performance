package exoplatformdatabase.wiki

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
class WikiReadPageSimulation extends Simulation {
       val wikiDataset = csv("wikis.csv")
	   val httpProtocol = http
		.baseURL("http://localhost:8080")
		
	

	val scn = scenario("WikiReadPageSimulation")

			.feed(wikiDataset)
			.exec(http("Login")
				.post("/portal/login")
				.formParam("initialURI", "/portal/intranet/")
				.formParam("username", "${userName}")
				.formParam("password", "${userPassword}")
				.formParam("rememberme", "true")
		        .check(status.is(200)))
			.exec(http("Goto Wiki Page")
				.get("${WikiPath}/${WikiPage}")
				.check(status.is(200)))
	
	setUp(scn.inject(atOnceUsers(3))).protocols(httpProtocol)
}