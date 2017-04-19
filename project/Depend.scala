import sbt._

object Depend {

  lazy val scalazVersion   = "7.2.6"
  lazy val http4sVersion   = "0.15.2a"
  lazy val argonautVersion = "6.2-RC2"

  lazy val scalaz = Seq(
    "org.scalaz" %% "scalaz-core",
    "org.scalaz" %% "scalaz-concurrent"
  ).map(_ % scalazVersion)

  lazy val http4s = Seq(
    "org.http4s" %% "http4s-dsl",
    "org.http4s" %% "http4s-argonaut",
    "org.http4s" %% "http4s-blaze-server",
    "org.http4s" %% "http4s-blaze-client"
  ).map(_ % http4sVersion)

  lazy val fs2 = Seq(
    "co.fs2" %% "fs2-core" % "0.9.4",
    "co.fs2" %% "fs2-scalaz" % "0.2.0"
  )

  lazy val argonaut = Seq(
    "io.argonaut" %% "argonaut" % argonautVersion
  )

  lazy val pureconfig = Seq(
    "com.github.melrief" %% "pureconfig" % "0.3.3"
  )

  lazy val logging = Seq(
    "org.log4s" %% "log4s"  % "1.3.0",
    "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.7",
    "org.apache.logging.log4j" % "log4j-core" % "2.7",
    "org.apache.logging.log4j" % "log4j-api" % "2.7"
  )

  lazy val scalaTestCheck = Seq(
    "org.scalatest"   %% "scalatest" % "2.2.4",
    "org.scalacheck"  %% "scalacheck" % "1.12.1"
  ).map(_.withSources).map(_ % "test")

  lazy val depResolvers = Seq(
    "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases",
    Resolver.sonatypeRepo("releases")
  )
}
