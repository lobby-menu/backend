import Dependencies.globalExcludes
import sbt.Keys._
import sbt._

object Common {
  val commonSettings = Seq(
    organization := "org.tubit.lobby",
    scalaVersion := "2.12.4",
    resolvers += Resolver.mavenLocal,
    scalacOptions in Test ++= Seq("-Yrangepos"),
    unmanagedClasspath in Runtime += baseDirectory.value / ".." / "conf",
    excludeDependencies ++= globalExcludes
  )
}
