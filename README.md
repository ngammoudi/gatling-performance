# qa-gatling-performance
 eXo QA - Gatling Performance Tests Referential

Configuration
-----------------------

 - Install Gatling Plugin (via Manage Jenkins -> Manage Plugins)
 - Configure this project to execute Gatling simulations, for example using the Maven plugin (see Maven plugin documentation).
 - The execution is attached to the test phase, the simulation will be executed when running mvn test.
 Configure your job 
 - Add "test" in the "Goals and options" build property.
 - Add "Track a Gatling load simulation" as a new post-build action.

Gatling plugin usage
-----------------------
 - Install HTML Publisher plugin,This plugin publishes the generated gatling HTML reports 
 - Configure the Content Security Policy https://wiki.jenkins-ci.org/display/JENKINS/Configuring+Content+Security+Policy
 - Start building the project will execute the default simulation UploadDocumentSimulation
 - A the end of the test a graph displaying the mean response time will appear
