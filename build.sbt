name := "swaddle"

version := "0.1.0"

organization := "org.conbere"

licenses := Seq("BSD-style" -> url("http://www.opensource.org/licenses/bsd-license.php"))

homepage := Some(url("http://github.com/aconbere/scala-irc"))

scalaVersion := "2.10.0"

scalacOptions ++= Seq(
  "-deprecation",
  "-feature"
)

fork in run := true

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

testOptions in Test += Tests.Argument("-oDF")

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.10" % "1.9.1" % "test",
  "junit" % "junit" % "4.10" % "test",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.2.0-rc1",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.1.3"
)
