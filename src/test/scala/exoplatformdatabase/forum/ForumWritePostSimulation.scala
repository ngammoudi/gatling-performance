package exoplatformdatabase.forum
 
import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import java.util.concurrent.ThreadLocalRandom
class ForumWritePostSimulation extends Simulation {
    def getRand(i: Int) : String = ThreadLocalRandom.current.nextInt(i).toString
    val forumDataset = csv("forum.csv")
	val httpProtocol = http
		.baseURL("http://localhost:8080")
		
		.userAgentHeader("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.109 Safari/537.36")




	val scn = scenario("ForumWritePostSimulation")
			.feed(forumDataset)
			.exec(http("Login")
				.post("/portal/login")
				.formParam("initialURI", "/portal/intranet/")
				.formParam("username", "${userName}")
				.formParam("password", "${userPassword}")
				.formParam("rememberme", "true")
		        	.check(status.is(200)))
						.exec(session => session.set("random", getRand(10000000)))

						.pause(2)
			.exec(http("Goto TopicId")
				.get("/portal/intranet/${forumTopicUrl}/${forumTopicId}")
				.check(regex("""form class="UIForm" id="UITopicDetail" .*portal:componentId=([^&]*)(&amp;|&)interactionstate=([^&]+)(&amp;|&).*""").saveAs("quickreply_componentId"))
				.check(regex("""form class="UIForm" id="UITopicDetail" .*/portal/intranet/forum/topic.*interactionstate=([^&]+)(&amp;|&).*gtn:csrf" value="([0-9A-Z]+)""").saveAs("quickreply_interactionstate"))
				.check(regex("""gtn:csrf.*value="([^"]*)"""").saveAs("gtncsrf"))
				.check(regex("""/exo:applications.*topic[0-9a-z]+""").saveAs("objectId"))
	)
.pause(2)
			.exec(http("Quick reply in TopicId")
				.post("/portal/intranet/${forumTopicUrl}/${forumTopicId}?portal:componentId=${quickreply_componentId}&interactionstate=${quickreply_interactionstate}&portal:type=action&objectId=${objectId}&ajaxRequest=true")
				.formParam("formOp", "QuickReply")
				.formParam("gtn:csrf", "${gtncsrf}")
				.formParam("AddTag", "")
				.formParam("SearchForm", "")
				.formParam("UITopicDetail.label.Message", "${userName} reply to ${forumTopicUrl}/${forumTopicId}.${random}"))
.pause(2)
.exec(session => {
  // print the Session for debugging, don't do that on real Simulations
  println(session)
  session
})

	setUp(scn.inject(atOnceUsers(3))).protocols(httpProtocol)
}
