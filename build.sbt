name := "kafka-experiments"
version := "1.0.0"

scalaVersion := "2.12.3"

lazy val KafkaVersion = "0.11.0.1"
libraryDependencies ++= Seq(
  "com.whisk" %% "docker-testkit-scalatest" % "0.9.5" % "test",
  "com.whisk" %% "docker-testkit-impl-spotify" % "0.9.5" % "test",
  "org.apache.kafka" % "kafka-clients" % KafkaVersion % "test",
  "org.apache.kafka" % "kafka-streams" % KafkaVersion % "test",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "net.manub" %% "scalatest-embedded-kafka" % "0.15.1" % "test"
)