name := "Streamer"

version := "0.1"

scalaVersion := "2.11.12"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "2.4.7",
  "org.apache.spark" %% "spark-streaming" % "2.4.7",
  "org.apache.bahir" %% "spark-streaming-twitter" % "2.0.0"
)