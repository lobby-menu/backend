import Dependencies.globalExcludes
import sbt.Keys._
import sbt._

object Common {
  val commonSettings = Seq(
    organization := "org.tubit.lobby",
    scalaVersion := "2.12.4",
    scalacOptions ++= Seq(
      //"-target:jvm-1.8",
      //"-Xfatal-warnings",
      "-feature",
      "-deprecation",
      "-unchecked",
      "-encoding", "UTF-8",       // yes, this is 2 args
      "-language:existentials",
      "-language:higherKinds",
      "-language:implicitConversions",
      "-unchecked",
      //"-Xfatal-warnings",
      "-Xlint",
      "-Yno-adapted-args",
      //"-Ywarn-dead-code",        // N.B. doesn't work well with the ??? hole and specs2 mockito
      //"-Ywarn-numeric-widen",
      //"-Ywarn-value-discard",
      "-Xfuture"//,
      //"-Ywarn-unused-import"     // 2.11 only <- play router shows too many of these
    ),
    javacOptions ++= Seq(
      "-source", "1.8",
      "-target", "1.8",
      "-Xlint:deprecation",
      "-Xlint:unchecked"
    ),
    fork in Test := true,
    sources in (Compile,doc) := Seq.empty,
    resolvers += Resolver.mavenLocal,
    scalacOptions in Test ++= Seq("-Yrangepos"),
    unmanagedClasspath in Runtime += baseDirectory.value / ".." / "conf",
    excludeDependencies ++= globalExcludes
  )
}
