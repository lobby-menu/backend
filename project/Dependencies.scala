import play.sbt.PlayImport._
import sbt._

object Dependencies{
  val globalExcludes = Seq()

  val playVersion = play.core.PlayVersion.current
  val playJsonVersion = "2.6.7"
  val slf4jVersion = "1.7.12"
  val macwireVersion = "2.3.0"

  val play2ReactiveMongoVersion = "0.12.7-play26"

  val mongoStoreDependencies = Seq(
    "org.reactivemongo" %% "play2-reactivemongo" % play2ReactiveMongoVersion,
    "com.typesafe.play" %% "play-json" % playJsonVersion
  )

  val endpointDependencies = Seq(
    jdbc , ehcache , ws , specs2 % Test, guice,
    "com.softwaremill.macwire" %% "macros" % macwireVersion % "provided"
  )
}