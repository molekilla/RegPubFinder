import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "RegPubFinder"
    val appVersion      = "1.0"

    val appDependencies = Seq(
      "org.elasticsearch" % "elasticsearch" % "0.19.0",
      "com.mongodb.casbah" % "casbah_2.9.0-1" % "2.1.5.0",
      "org.scalaj" %% "scalaj-collection" % "1.2",
      "org.jsoup"  % "jsoup" % "1.6.1",
      "net.databinder" %% "dispatch-http" % "0.8.8"
    )
    
    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      // Add your own project settings here      
    )

}
