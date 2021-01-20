name := "Streamer"

version := "0.1"

scalaVersion := "2.12.13"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "2.4.7",
  "org.apache.spark" %% "spark-streaming" % "2.4.7",
  "org.apache.bahir" %% "spark-streaming-twitter" % "2.4.0"
)