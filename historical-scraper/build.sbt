name := "historical-scraper"

version := "1.0"

scalaVersion := "2.12.12"

resolvers += Resolver.sonatypeRepo("releases")

libraryDependencies ++= Seq(
  "com.danielasfregola" %% "twitter4s" % "6.2",
  "ch.qos.logback" % "logback-classic" % "1.2.3"
)
