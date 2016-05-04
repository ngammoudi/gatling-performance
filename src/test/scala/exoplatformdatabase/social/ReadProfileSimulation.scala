package exoplatformdatabase.social

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import java.util.concurrent.ThreadLocalRandom
class ReadProfileSimulation extends Simulation {

	def getRand(i: Int) : String = ThreadLocalRandom.current.nextInt(i).toString
    val userDataset = csv("users.csv")
	val httpProtocol = http
		.baseURL("http://localhost:8080")
		




	val scn = scenario("ReadProfileSimulation")
			
			.feed(userDataset)
			.exec(http("Login")
				.post("/portal/login")
				.formParam("initialURI", "/portal/intranet/")
				.formParam("username", "${userName}")
				.formParam("password", "${userPassword}")
				.formParam("rememberme", "true")
		        .check(status.is(200)))

                .exec(http("Goto my profile")
				.get("/portal/intranet/profile/${userName}")
				.check(status.is(200)))


	setUp(scn.inject(rampUsers(10) over(5 seconds))).protocols(httpProtocol)
}
