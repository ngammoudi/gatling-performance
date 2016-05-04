package exoplatformdatabase.social

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class LoginSimulation extends Simulation {
    val userDataset = csv("users.csv")
	val httpProtocol = http
		.baseURL("http://localhost:8080")
		
		.userAgentHeader("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.109 Safari/537.36")




	val scn = scenario("LoginSimulation")
			.feed(userDataset)
			.exec(http("Login")
				.post("/portal/login")
				.formParam("initialURI", "/portal/intranet/")
				.formParam("username", "${userName}")
				.formParam("password", "${userPassword}")
				.formParam("rememberme", "true")
		        	.check(status.is(200)))


	setUp(scn.inject(rampUsers(10) over(5 seconds)).protocols(httpProtocol))
}
