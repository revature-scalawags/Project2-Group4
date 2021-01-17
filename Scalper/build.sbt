name := "Scalper"

version := "0.1"

scalaVersion := "2.11.12"

val sparkVersion = "2.4.7"
val sparkNLPVersion = "2.7.1"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-streaming" % sparkVersion,
  "org.apache.spark" %% "spark-streaming-twitter" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "org.apache.spark" %% "spark-mllib" % sparkVersion,
  "com.johnsnowlabs.nlp" %% "spark-nlp" % sparkNLPVersion
)