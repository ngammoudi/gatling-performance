package exoplatformdatabase.ecms

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import java.util.concurrent.ThreadLocalRandom
class CreateNewContentSimulation extends Simulation {

	def getRand(i: Int) : String = ThreadLocalRandom.current.nextInt(i).toString
    val userDataset = csv("users.csv")
	val httpProtocol = http
		.baseURL("http://localhost:8080")
		
		.userAgentHeader("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.109 Safari/537.36")




	val scn = scenario("CreateNewContentSimulation")
			
			.feed(userDataset)
			.exec(http("Login")
				.post("/portal/login")
				.formParam("initialURI", "/portal/intranet/")
				.formParam("username", "${userName}")
				.formParam("password", "${userPassword}")
				.formParam("rememberme", "true")
		        .check(status.is(200)))

                .exec(http("Go to Documents")
				.get("/portal/intranet/documents")
				.check(regex("""/Private/Documents.*\n.*portal:componentId=([a-zA-Z0-9\-\_]+)&[amp;]*interactionstate=([a-zA-Z0-9\_\*]+)&[amp;]*portal:type=action&[amp;]*ajaxRequest=true""").saveAs("document_componentId"))
				.check(regex("""/Private/Documents.*\n.*interactionstate=([a-zA-Z0-9\_\*]+)&[amp;]*portal:type=action&[amp;]*ajaxRequest=true""").saveAs("document_interactionstate")))
				
				.exec(http("Click on New Content button")
				.get("/portal/intranet/documents")
				.queryParam("portal:componentId", "${document_componentId}")
				.queryParam("interactionstate", "${document_interactionstate}")
				.queryParam("portal:type", "action")
				.queryParam("ajaxRequest", "true")
				.check(regex("""a.*interactionstate=([a-zA-Z0-9\_\*]+)&[amp;]*portal:type=action&[amp;]*ajaxRequest=true.*\n.*i.*New Content""").saveAs("content_interactionstate")))

				.exec(http("Choose HTML File")
				.get("/portal/intranet/documents")
				.queryParam("portal:componentId", "${document_componentId}")
				.queryParam("interactionstate", "${content_interactionstate}")
				.queryParam("portal:type", "action")
				.queryParam("ajaxRequest", "true")
				.check(regex("""div.*interactionstate=([a-zA-Z0-9\_\*]+)&[amp;]*portal:type=action&[amp;]*ajaxRequest=true.*\n.*div.*\n.*i.*title="File".*""").saveAs("file_interactionstate")))

				.exec(http("Open new document form")
				.get("/portal/intranet/documents")
				.queryParam("portal:componentId", "${document_componentId}")
				.queryParam("interactionstate", "${file_interactionstate}")
				.queryParam("portal:type", "action")
				.queryParam("ajaxRequest", "true")
				.check(regex("""form .*id="UIDocumentForm".*interactionstate=([a-zA-Z0-9\_\*]+)&[amp;]*portal:type=action""").saveAs("documentForm_interactionstate")))
                .exec(session => session.set("documentID", getRand(10000000)))
				.exec(http("Save and close form")
				.post("/portal/intranet/documents")
				.formParam("portal:componentId", "${document_componentId}")
				.formParam("interactionstate", "${documentForm_interactionstate}")
				.formParam("portal:type", "action")
				.formParam("ajaxRequest", "true")
				.formParam("formOp", "SaveAndClose")
				.formParam("name", "DOC_${documentID}")
				.formParam("content-lang", "en") 
				.formParam("contentHtml", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa") 
				.formParam("mimetype", "text/html") 
		        .check(status.is(200)))


	setUp(scn.inject(atOnceUsers(3))).protocols(httpProtocol)
}
