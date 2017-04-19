import sbt._
import Depend._

lazy val commonSettings = Seq(
  scalaVersion := "2.11.8",
  logBuffered in Test := false,
  resolvers := Depend.depResolvers,
  scalacOptions ++= Seq(
    "-deprecation",
    "-encoding", "UTF-8",
    "-feature",
    "-language:existentials",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-unchecked",
    "-Xfatal-warnings",
    "-Xlint",
    "-Yno-adapted-args",
    "-Ywarn-numeric-widen",
    "-Xfuture",
    "-Yrangepos"
  )
)

lazy val buildSettings = Seq(
  name := "syd-fp",
  mainClass in Compile := Some("com.sebmuellermath.Boot"),
  libraryDependencies :=
    scalaz ++
    http4s ++
    argonaut ++
    fs2 ++
    logging ++
    scalaTestCheck
) ++ commonSettings

lazy val root = (project in file("."))
  .settings(buildSettings)

