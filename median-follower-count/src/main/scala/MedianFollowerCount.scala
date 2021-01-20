import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.types.{LongType, StringType}

import com.github.tototoshi.csv._

import java.io.File
import java.time.LocalDateTime
import org.apache.spark.sql.Dataset
import javax.xml.crypto.Data

object MedianFollowerCount {

  case class Tweet(text: String, username: String, followers: Long)

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

    val tweets = spark.read
      .format("csv")
      .schema(schema)
      .option("delimiter", "\t")
      .option("header", true)
      .load(input)
      .as[Tweet]
      .cache()
    
    /** Returns the total number of users in the dataset.*/
    def calculateUserCount(t: Dataset[Tweet]): Long = {
      t.map(_.username)
        .distinct()
        .count()
    }

    /** Returns the combined follower count of all users in the dataset.*/
    def calculateTotalFollowers(t: Dataset[Tweet]): Long = {
      t.map(_.followers)
        .reduce(_ + _)
    }

    /** Returns the median follower count of all users in the dataset.*/
    def calculateMedianFollowers(t: Dataset[Tweet]): Long = {
      val unsortedUsersWithFollowers = t
        .map(x => (x.username, x.followers))
        .distinct()
        .collect()
      val sortedFollowers = unsortedUsersWithFollowers
        .toSeq
        .sortWith((x, y) => x._2 > y._2)
        .map(_._2)
        .toArray
      val users = calculateUserCount(tweets)
      val median = {
        if (users % 2 == 0) 
          (users / 2).toInt 
        else 
          ((users + 1) / 2).toInt
      }

      sortedFollowers(median)
    }
    
    val userCount = calculateUserCount(tweets)
    val totalFollowers = calculateTotalFollowers(tweets)
    val avgFollowers = totalFollowers / userCount
    val medianFollowers = calculateMedianFollowers(tweets)

    implicit object TweetFormat extends DefaultCSVFormat {
      override val delimiter = '\t'
    }
    val file = new File("influence-analysis.tsv")
    val writer = CSVWriter.open(file)
    writer.writeRow(Seq(
      "timestamp",
      "hashtag",
      "userCount",
      "totalFollowers",
      "medianFollowers"))
    writer.writeRow(Seq(
      LocalDateTime.now(),
      hashtag, 
      userCount, 
      totalFollowers,
      medianFollowers))
    writer.close()
    spark.stop()
  }
}