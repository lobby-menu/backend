import Dependencies._
import Common._

lazy val resolversSettings = resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

lazy val root = project.in(file("."))
    .settings(commonSettings)
    .settings(
      name := "products",
      version := "0.1"
    )
    .aggregate(core, mongoStore, endpoint)

lazy val core = project.in(file("core"))
    .settings(commonSettings)

lazy val mongoStore = project.in(file("mongodb-store"))
    .settings(commonSettings)
    .settings(libraryDependencies ++= mongoStoreDependencies)
    .dependsOn(core)

lazy val endpoint = project.in(file("endpoint"))
    .enablePlugins(PlayScala)
    .settings(commonSettings)
    .settings(
      routesGenerator := InjectedRoutesGenerator
    )
    .settings(
      name := "endpoint",
      version := "1.0"
    )
    .settings(resolversSettings)
    .settings(libraryDependencies ++= endpointDependencies)
    .dependsOn(core, mongoStore)
