val projectName        = "exploring-ce3"
val projectDescription = "Exploring Cats Effect 3"

ThisBuild / scalaVersion := Versions.scala2Version
ThisBuild / fork := true
ThisBuild / turbo := true                  // default: false
Global / onChangedBuildSource := ReloadOnSourceChanges
ThisBuild / includePluginResolvers := true // default: false

lazy val root = (project in file(".")).settings(
  name := projectName,
  description := projectDescription,
  libraryDependencies ++= Dependencies.dependencies,
  testFrameworks += new TestFramework("weaver.framework.CatsEffect")
)
