val baseName = "cloudflow-template"

name := baseName
organization in ThisBuild := "com.hbc"
version in ThisBuild := "1.0.0-SNAPSHOT"
scalaVersion in ThisBuild := "2.12.11"
scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation", "-feature")

val akkaStreamAlpakkaFileVersion = "1.1.2"
val akkaVersion = "10.1.11"
val logbackVersion = "1.2.3"
val youiVersion = "0.13.9"

lazy val root = project.in(file("."))
  .aggregate(sensorData, app, client)

lazy val sensorData = project.in(file("sensor-data"))
  .enablePlugins(CloudflowAkkaStreamsLibraryPlugin)
  .settings(
    name := s"$baseName-sensor-data",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaVersion,
      "ch.qos.logback" %  "logback-classic" % logbackVersion,
    )
  )

lazy val app = project.in(file("app"))
  .enablePlugins(CloudflowAkkaStreamsApplicationPlugin)
  .settings(
    name := s"$baseName-app"
  )
  .dependsOn(sensorData)

lazy val client = project.in(file("client"))
  .settings(
    name := s"$baseName-client",
    libraryDependencies ++= Seq(
      "io.youi" %% "youi-client" % youiVersion
    )
  )