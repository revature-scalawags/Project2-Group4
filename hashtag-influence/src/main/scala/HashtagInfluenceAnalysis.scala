package com.revature.project2.group4.hashtaginfluence

import org.apache.spark.sql.{Dataset, SparkSession}
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.types.{LongType, StringType}
import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.services.s3.{AmazonS3ClientBuilder}
import com.amazonaws.services.s3.AmazonS3

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

    val key = System.getenv("AWS_ACCESS_KEY_ID")
    val secret = System.getenv("AWS_SECRET_ACCESS_KEY")
    val s3client = getS3Client(key, secret)
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
      .appName("hashtag-influence-analysis")
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
      .collectAsList()

    val userCount = Utilities.calculateUserCount(tweetDS, spark)
    val totalFollowers = Utilities.calculateTotalFollowers(tweetDS, spark)
    val avgFollowers = totalFollowers / userCount
    val medianFollowers = Utilities.calculateMedianFollowers(tweetDS, spark)

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

    val s3Path = "jeroen-twitter-demo-bucket/output"
    s3client.putObject(s3Path,output.getName,output)
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

  def getS3Client(key: String, secret: String): AmazonS3 = {
    val credentials = new BasicAWSCredentials(key, secret)
    val client = AmazonS3ClientBuilder
      .standard()
      .withCredentials(new AWSStaticCredentialsProvider(credentials))
      .withRegion("us-east-2")
      .build();
    client
  }
}