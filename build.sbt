import Dependencies._
import Common._

organization in ThisBuild := "org.tubit"
name := "products"
lazy val resolversSettings = resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

lazy val root = project.in(file("."))
    .settings(commonSettings: _*)
    .settings(
      name := "products",
      version := "0.1"
    )
    .aggregate(common, mongodbStore, endpoint)

lazy val common = project
    .settings(commonSettings: _*)
    .settings(
      name := "common",
      scalaSource in Compile := baseDirectory.value / "src"
    )

lazy val mongodbStore = project
    .settings(commonSettings: _*)
    .settings(libraryDependencies ++= mongoStoreDependencies)
    .settings(
      name := "mongodbStore",
      scalaSource in Compile := baseDirectory.value / "src"
    )
    .dependsOn(common)

lazy val endpoint = project
    .enablePlugins(PlayScala)
    .settings(commonSettings: _*)
    .settings(
      routesGenerator := InjectedRoutesGenerator
    )
    .settings(
      name := "endpoint",
      version := "1.0"
    )
    .settings(resolversSettings)
    .settings(libraryDependencies ++= endpointDependencies)
    .dependsOn(common, mongodbStore)
