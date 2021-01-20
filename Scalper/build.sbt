name := "Scalper"

version := "0.1"

scalaVersion := "2.11.12"

libraryDependencies ++= Seq(
  "org.twitter4j" % "twitter4j-stream" % "4.0.7",
  "org.apache.spark" %% "spark-core" % "2.4.7",
  "org.apache.spark" %% "spark-mllib" % "2.4.7",
  "com.johnsnowlabs.nlp" %% "spark-nlp" % "2.7.1"
)