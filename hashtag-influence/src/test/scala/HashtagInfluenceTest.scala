package com.revature.project2.group4.hashtaginfluence

import org.apache.spark.sql.{Dataset, SparkSession}
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.types.{LongType, StringType}
import org.scalatest.funsuite.AnyFunSuite

class HashtagInfluenceTest extends AnyFunSuite {
  val spark = SparkSession
    .builder
    .master("local[1]")
    .appName("hashtag-influence-analysis-test")
    .getOrCreate()
  spark
    .sparkContext
    .setLogLevel("WARN")
  import spark.implicits._

  val tweetSeq = Seq(
    new Tweet("bongos #savemelee", "donkey", 1000),
    new Tweet("tweet #testing", "pickles", 24),
    new Tweet("besker #oinkers", "dunkey", 1000000),
    new Tweet("testing testing 123 #456", "lesmoonvest", 20),
    new Tweet("this is a tweet #tweeeeet", "picklesandcheese", 4),
    new Tweet("this is another tweet", "donkey", 1000)
  )
  val tweetDS = tweetSeq.toDS().cache()
  
  test("calculateUserCount should return 5 users for test dataset") {
    val count = HashtagInfluence.userCount(tweetDS, spark)
    assert(count == 5)
  }

  test("calculateTotalFollowers should return 1001048 for test dataset") {
    val followers = HashtagInfluence.totalFollowers(tweetDS, spark)
    assert(followers == 1001048)
  }

  test("calculateMedianFollowers should return 24 for test dataset") {
    val median = HashtagInfluence.medianFollowers(tweetDS, spark)
    assert(median == 24)
  }
}
