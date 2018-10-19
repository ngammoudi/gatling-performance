# qa-gatling-performance
 eXo QA - Gatling Performance Tests Referential

Configuration
===============

 - Install Gatling Plugin (via Manage Jenkins -> Manage Plugins).
 - Configure this project to execute Gatling simulations, for example using the Maven plugin (see [Maven plugin documentation](https://github.com/excilys/gatling/wiki/Maven-plugin)).
 - The execution is attached to the test phase, the simulation will be executed when running mvn test.
 
 Configure your job 
-----------------------

 - Add "test" in the "Goals and options" build property.
 - Add "Track a Gatling load simulation" as a new post-build action.

Gatling plugin usage
===============
 - Install HTML Publisher plugin,This plugin publishes the generated gatling HTML reports.
 - Configure the [Content Security Policy] (https://wiki.jenkins-ci.org/display/JENKINS/Configuring+Content+Security+Policy).
 - Start building the project will execute the default simulation UploadDocumentSimulation.
 - A the end of the test a graph displaying the mean response time will appear.
 ![Mean Response Time](https://github.com/exoplatform/qa-gatling-performance/blob/master/docs/images/gatlingMRT.png)
 - If you are on the project dashboard, clicking on Gatling will get you to a more detailed performance trend, displaying for your last 30 builds :

  * Mean response time trend.

  * 95th percentiles response time trend.

  * Percentage of KO requests.

  
- This page will also provides links to detailed reports for all your builds, at the bottom of the page .

This is an example of a genrated report for UploadDocumentSimulation with 5VU 
*****

![UploadDocumentSimulation](https://github.com/ngammoudi/gatling-performance/blob/master/docs/images/GatlingReport.png)

Gatling Realtime monitoring
===============

Requirements
-----------------------
  *  Install [InfluxDB] (https://docs.influxdata.com/influxdb/v0.13/introduction/getting_started/).
  *  Install [Grafana] (http://docs.grafana.org/installation/).
  *  Download [Collectd] (https://collectd.org/download.shtml) and compile its source packages.

Configuration
-----------------------
- Create two datasources with influxdb "gatling_db" and "collectd_db" (gatling_db is a db for simulation metrics and collect_db for CPU and Memory ).

- Edit **/etc/influxdb/influxdb.conf** in order to enable collectd and graphite,graphite and collectd sections should be like :

*****
       [[graphite]]
         enabled = true
         database = "gatling"
         bind-address = ":2003"
         protocol = "tcp"
  
*****
      [[collectd]]
         enabled = true
         port = 25826
         database = "collectd_db"
         typesdb = "/opt/collectd/share/collectd/types.db"
  
- Edit **/opt/collectd/etc/collectd.conf** in order to enable graphite-write and network protocols.


-----------------------------------------------


      LoadPlugin network
      LoadPlugin write_graphite
          <Plugin write_graphite>
             <Node "example">
               Host "localhost"
               Port "2003"
               Protocol "tcp"
               LogSendErrors true
               Prefix "collectd"
               Postfix "collectd"
               StoreRates true
               AlwaysAppendDS false
               EscapeCharacter "_"
             </Node>
           </Plugin>
         <Plugin network>
              <Server "127.0.0.1" "25826">
                 ... 
              </Server>
         </Plugin>


- Go to Graphana web server and add the influxdb datasources "gatling_db" and "collect_db" then save.
- Enable graphite data writer and graphite properties under **gatling.conf** file like:

 
****
          
     data {
        writers = [console, file, graphite]
        ...
         graphite {
               host = "localhost"
               bind-address = ":2003"
               protocol = "tcp"
               rootPathPrefix = "gatling"
               bucketWidth = 100
             }

- Restart influxdb,grafana and collectd.
- Run the gatling simulation then go to influxdb datasources you will find that there's some metrics to the both datasources "gatling_db" and "collectd_db".
- Go to grafana web server and add a dashboard for each datasource in order to display these metrics in live time.

Below a screenshot for CPU, memory behavior and UploadDocumentSimulation metrics when running this scala script. 


**UploadDocumentSimulation Live Response Time Metrics**
*****

![UploadDocumentSimulation Live Response Time Metrics](https://github.com/exoplatform/qa-gatling-performance/blob/master/docs/images/UploadDocument_Metrics.png)


**CPU & Memory Live Time Monitoring**
*****

![ CPU & Memory Live Time Monitoring](https://github.com/exoplatform/qa-gatling-performance/blob/master/docs/images/Collectd_CPU.png)


