val baseName = "cloudflow-template"

name := baseName
organization in ThisBuild := "com.hbc"
version in ThisBuild := "1.0.0-SNAPSHOT"
scalaVersion in ThisBuild := "2.12.11"
scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation", "-feature")

val akkaStreamAlpakkaFileVersion = "1.1.2"
val akkaVersion = "10.1.11"
val logbackVersion = "1.2.3"

lazy val root = project.in(file("."))
  .aggregate(core, sensorData, app)
  .settings(
    publish := {},
    publishLocal := {}
  )

lazy val core = project.in(file("core"))
lazy val sensorData = project.in(file("sensor-data"))
  .enablePlugins(CloudflowAkkaStreamsLibraryPlugin)
  .settings(
    name := s"$baseName-core",
    libraryDependencies ++= Seq(
      "com.lightbend.akka" %% "akka-stream-alpakka-file" % akkaStreamAlpakkaFileVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaVersion,
      "ch.qos.logback" %  "logback-classic" % logbackVersion,
      "com.typesafe.akka" %% "akka-http-testkit" % akkaVersion % "test"
    )
  )

lazy val sensorData = project.in(file("sensor-data"))
  .enablePlugins(CloudflowAkkaStreamsApplicationPlugin)
  .settings(
    name := s"$baseName-sensor-data"
  )
  .dependsOn(core)

lazy val app = project.in(file("app"))
  .enablePlugins(CloudflowAkkaStreamsApplicationPlugin)
  .settings(
    name := s"$baseName-app"
  )
  .dependsOn(sensorData)