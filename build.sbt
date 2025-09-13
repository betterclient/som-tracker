ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.6"

lazy val root = (project in file("."))
  .settings(
    name := "SOMTracker",
    idePackagePrefix := Some("io.github.betterclient.tracker")
  )

libraryDependencies += "com.lihaoyi" %% "upickle" % "4.3.2"
libraryDependencies += "org.jsoup" % "jsoup" % "1.21.2"

libraryDependencies += "com.slack.api" % "bolt-socket-mode" % "1.45.2"
libraryDependencies += "com.slack.api" % "bolt" % "1.45.2"

libraryDependencies += "javax.websocket" % "javax.websocket-api" % "1.1"
libraryDependencies += "org.glassfish.tyrus.bundles" % "tyrus-standalone-client" % "1.20"