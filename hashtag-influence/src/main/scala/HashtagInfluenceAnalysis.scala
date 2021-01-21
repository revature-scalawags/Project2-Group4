package com.revature.project2.group4.hashtaginfluence

import org.apache.spark.sql.{Dataset, SparkSession}
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.types.{LongType, StringType}

import com.github.tototoshi.csv._

import java.io.File
import java.time.LocalDateTime

object HashtagInfluenceAnalysis {
  def main(args: Array[String]) {
    if (args.length != 1) {
      println("[ERROR]: Please specify a tsv file as input with sbt \"run [filepath]\". " +
        "Aborting execution.")
      sys.exit(-1)
    }
    val input = args(0)
    val pathNodes = input match {
      case _ if input.contains("/") => input.split('/')
      case _ if input.contains("\\") => input.split('\\')
      case _ => Array(input)
    }
    val hashtag = pathNodes.last.split('.')(0)

    val spark = SparkSession
      .builder
      .master("local[1]")
      .appName("median-follower-count")
      .getOrCreate()

    import spark.implicits._  

    val schema = new StructType()
      .add("text", StringType)
      .add("username", StringType)
      .add("followers", LongType)
    val tweetDS = spark.read
      .format("csv")
      .schema(schema)
      .option("delimiter", "\t")
      .option("header", true)
      .load(input)
      .as[Tweet]
      .cache()
    val tweetList = tweetDS
      .collect()
      .toList

    val userCount = Utilities.calculateUserCount(tweetList)
    val totalFollowers = Utilities.calculateTotalFollowers(tweetList)
    val avgFollowers = totalFollowers / userCount
    val medianFollowers = Utilities.calculateMedianFollowers(tweetList)

    implicit object TweetFormat extends DefaultCSVFormat {
      override val delimiter = '\t'
    }

    val output = new File("influence-analysis.tsv")
    val writer = CSVWriter.open(output, true)
    try {
      CSVReader.open(output).readNext().get
    } catch {
      case e: Exception => writeHeaderRow(writer)
    }
    writer.writeRow(Seq(
      LocalDateTime.now(),
      hashtag, 
      userCount, 
      totalFollowers,
      avgFollowers,
      medianFollowers))
    writer.close()
    spark.stop()
  }

  def writeHeaderRow(writer: CSVWriter): Unit = {
    writer.writeRow(Seq(
      "timestamp",
      "hashtag",
      "userCount",
      "totalFollowers",
      "avgFollowers",
      "medianFollowers"))
  }
}