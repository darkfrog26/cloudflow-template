
import sbt._
import sbt.Keys._

lazy val root =
  Project(id = "root", base = file("."))
    .enablePlugins(ScalafmtPlugin)
    .settings(
      name := "root",
      scalafmtOnCompile := true,
      skip in publish := true
    )
    .withId("root")
    .settings(commonSettings)
    .aggregate(
      sensorDataApp,
      sensorDataModel,
      sensorDataIngress,
      sensorDataMetrics,
      sensorDataValidation,
      sensorDataLogging
    )

lazy val sensorDataApp = appModule("sensor-data-app")
  .enablePlugins(CloudflowApplicationPlugin)
  .settings(commonSettings)
  .settings(
    name := "sensor-data-app"
  )
  .dependsOn(sensorDataIngress, sensorDataMetrics, sensorDataValidation, sensorDataLogging)

lazy val sensorDataIngress = appModule("sensor-data-ingress")
  .enablePlugins(CloudflowAkkaStreamsLibraryPlugin)
  .settings(
    commonSettings,
    libraryDependencies ++= commonDependencies
  )
  .dependsOn(sensorDataModel)

lazy val sensorDataMetrics = appModule("sensor-data-metrics")
  .enablePlugins(CloudflowAkkaStreamsLibraryPlugin)
  .settings(
    commonSettings,
    libraryDependencies ++= commonDependencies
  )
  .dependsOn(sensorDataModel)

lazy val sensorDataValidation = appModule("sensor-data-validation")
  .enablePlugins(CloudflowAkkaStreamsLibraryPlugin)
  .settings(
    commonSettings,
    libraryDependencies ++= commonDependencies
  )
  .dependsOn(sensorDataModel)

lazy val sensorDataLogging = appModule("sensor-data-logging")
  .enablePlugins(CloudflowAkkaStreamsLibraryPlugin)
  .settings(
    commonSettings,
    libraryDependencies ++= commonDependencies
  )
  .dependsOn(sensorDataModel)

lazy val sensorDataModel = appModule("sensor-data-model")
  .enablePlugins(CloudflowLibraryPlugin)

def appModule(moduleID: String): Project = {
  Project(id = moduleID, base = file(moduleID))
    .settings(
      name := moduleID
    )
    .withId(moduleID)
    .settings(commonSettings)
}

lazy val commonDependencies = Seq(
  "com.typesafe.akka"         %% "akka-http-spray-json"   % "10.1.10",
  "ch.qos.logback"            %  "logback-classic"        % "1.2.3",
  "org.scalatest"             %% "scalatest"              % "3.0.8"    % "test"
)

lazy val commonSettings = Seq(
  organization := "com.hbc",
  scalaVersion := "2.12.11",
  javacOptions += "-Xlint:deprecation",
  scalacOptions ++= Seq(
    "-encoding", "UTF-8",
    "-target:jvm-1.8",
    "-Xlog-reflective-calls",
    "-Xlint",
    "-Ywarn-unused",
    "-Ywarn-unused-import",
    "-deprecation",
    "-feature",
    "-language:_",
    "-unchecked"
  ),

  scalacOptions in (Compile, console) --= Seq("-Ywarn-unused", "-Ywarn-unused-import"),
  scalacOptions in (Test, console) := (scalacOptions in (Compile, console)).value

)