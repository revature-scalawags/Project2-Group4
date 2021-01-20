lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.revature.scalawags.group4",
      scalaVersion := "2.12.12"
    )),
    name := "median-follower-count"
  )

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.2" % Test
libraryDependencies += "org.apache.spark" %% "spark-core" % "3.1.0"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "3.1.0"
libraryDependencies += "com.github.tototoshi" %% "scala-csv" % "1.3.6"
