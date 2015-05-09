name := "dynamicRuleFSM"

organization := "com.glassbeam.fsm"

version := "0.0.1"

scalaVersion := "2.11.5"

libraryDependencies ++= Seq(
	"org.scala-lang" % "scala-compiler" % "2.11.1",
	"com.typesafe.akka" % "akka-actor_2.11" % "2.3.10",
	"com.typesafe.akka" % "akka-testkit_2.11" % "2.3.10" % "test",
	"org.scalatest" % "scalatest_2.11" % "2.2.2" % "test"
)

