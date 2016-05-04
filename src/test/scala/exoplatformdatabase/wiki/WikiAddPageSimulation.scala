package exoplatformdatabase.wiki

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import java.util.concurrent.ThreadLocalRandom
class WikiAddPageSimulation extends Simulation {
	   def getRand(i: Int) : String = ThreadLocalRandom.current.nextInt(i).toString
       val wikiDataset = csv("wikis.csv")
	   val httpProtocol = http
		.baseURL("http://localhost:8080")
		
	

	val scn = scenario("WikiAddPageSimulation")

			.feed(wikiDataset)
			.exec(http("Login")
				.post("/portal/login")
				.formParam("initialURI", "/portal/intranet/")
				.formParam("username", "${userName}")
				.formParam("password", "${userPassword}")
				.formParam("rememberme", "true")
		        .check(status.is(200)))

			.exec(http("Goto Wiki Path")
				.get("${WikiPath}/${WikiPage}")
				.check(regex("""\?portal:componentId=([^&]+)(&amp;|&)interactionstate=([^&]+)(&amp;|&).*UIWikiPortlet_AddPage""").saveAs("wikiAddPage_componentId"))
				.check(regex("""interactionstate=([^&]+)(&amp;|&).*UIWikiPortlet_AddPage""").saveAs("wikiAddPage_interactionstate"))
				.check(regex("""class="uiInputInfo">([^<]+)""").saveAs("wiki_page_title")))

			.exec(http("Add page in Wiki Path")
				.get("${WikiPath}/${WikiPage}")
				.queryParam("portal:componentId", "${wikiAddPage_componentId}")
				.queryParam("interactionstate", "${wikiAddPage_interactionstate}")
				.queryParam("portal:type", "action")
				.queryParam("ajaxRequest", "true")
				.check(regex("""gtn:csrf.*value="([^"]*)"""").saveAs("gtncsrf"))
				.check(regex("""UIWikiPageEditForm.*\?portal:componentId=([^&]+)(&amp;|&)interactionstate=([^&]+)(&amp;|&).*""").saveAs("wikiSavePage_componentId"))
				.check(regex("""UIWikiPageEditForm.*interactionstate=([^&]+)(&amp;|&).*""").saveAs("wikiSavePage_interactionstate")))
			.exec(session => session.set("random", getRand(10000000)))
				
		.exec(session => {
  // print the Session for debugging, don't do that on real Simulations
  println(session)
  session
})
		.exec(http("Submit the new page")
			.post("${WikiPath}/${WikiPage}?portal:componentId=${wikiSavePage_componentId}&interactionstate=${wikiSavePage_interactionstate}&portal:type=action&subComponentId=UISubmitToolBarUpper_SavePage_&objectId=SavePage")
			.formParam("formOp", "SavePage")
			.formParam("gtn:csrf", "${gtncsrf}")
			.formParam("titleInput", "${wiki_page_title}.${userName}.${random}")
			.formParam("RequiresHTMLConversion", "UIWikiRichTextArea_TextArea")
			.formParam("UIWikiRichTextArea_TextArea_syntax", "xwiki/2.0")
			.formParam("UIWikiRichTextArea_TextArea", "Add ${wiki_page_title}.${userName}.${random}"))


	setUp(scn.inject(atOnceUsers(3))).protocols(httpProtocol)
}