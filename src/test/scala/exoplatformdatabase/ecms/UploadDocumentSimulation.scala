package exoplatformdatabase.ecms

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import java.util.concurrent.ThreadLocalRandom
class UploadDocumentSimulation extends Simulation {
    def getRand(i: Int) : String = ThreadLocalRandom.current.nextInt(i).toString
    val fileDataset = csv("documents.csv")
	val httpProtocol = http
		.baseURL("http://localhost:8080")
		

val headers_1 = Map(
		"Accept-Encoding" -> "gzip, deflate",
		"Keep-Alive" -> "115",
        "Content-Type" -> "multipart/form-data")


	val scn = scenario("UploadDocumentSimulation")
			
			.feed(fileDataset)
			.exec(http("Login")
				.post("/portal/login")
				.formParam("initialURI", "/portal/intranet/")
				.formParam("username", "${userName}")
				.formParam("password", "${userPassword}")
				.formParam("rememberme", "true")
		        .check(status.is(200)))
                .exec(session => session.set("getRand", getRand(10000000)))

            .exec(http("Go To Documents Folder")
				.get("/portal/intranet/documents"))
				
			.repeat(5)
				{
					exec(http("Upload file")
						.post("http://localhost:8080/portal/rest/wcmDriver/uploadFile/upload?uploadId=${getRand}")
						.headers(headers_1)
						.bodyPart(RawFileBodyPart("${fileName}", "${filePath}/${fileName}")))

		        	.exec(http("Save Document After Upload ")
		        		.get("/portal/rest/wcmDriver/uploadFile/control?repositoryName=repository&workspaceName=collaboration&driverName=Personal%20Documents&currentFolder=/Documents&currentPortal=intranet&userId=${userName}&uploadId=${getRand}&fileName=${fileName}&language=en&existenceAction=keep&action=save"))
				}
			

.exec(session => {
  // print the Session for debugging, don't do that on real Simulations
  println(session)
  session
})
	setUp(scn.inject( atOnceUsers(5))).protocols(httpProtocol)
}
