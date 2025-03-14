// See README.md for license details.

ThisBuild / scalaVersion     := "2.13.12"
ThisBuild / version          := "0.0.1"
ThisBuild / organization     := "SDDX"

val chiselVersion = "6.2.0"

lazy val root = (project in file("."))
  .settings(
    name := "Mantis",
    libraryDependencies ++= Seq(
      "org.chipsalliance" %% "chisel" % chiselVersion,
      "org.scalatest" %% "scalatest" % "3.2.16" % "test",
      "com.lihaoyi" %% "os-lib" % "0.11.4" ,
      "org.scala-lang.modules" %% "scala-parser-combinators" % "2.4.0",
      "edu.berkeley.cs" %% "chiseltest" % "6.0.0" % "test",
    ),
    scalacOptions ++= Seq(
      "-language:reflectiveCalls",
      "-deprecation",
      "-feature",
      "-Xcheckinit",
      "-Ymacro-annotations",
    ),
    addCompilerPlugin("org.chipsalliance" % "chisel-plugin" % chiselVersion cross CrossVersion.full),
  )
