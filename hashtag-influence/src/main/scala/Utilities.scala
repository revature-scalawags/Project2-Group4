package com.revature.project2.group4.hashtaginfluence

import org.apache.spark.sql.{Dataset, SparkSession}
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.types.{LongType, StringType}

import scala.collection.immutable.ListMap

object Utilities {
  /** Returns the total number of users in the dataset.*/
  def calculateUserCount(tweets: Dataset[Tweet], spark: SparkSession): Long = {
    import spark.implicits._

    tweets
      .map(_.username)
      .distinct
      .count
  }

  /** Returns the combined follower count of all users in the dataset.*/
  def calculateTotalFollowers(tweets: Dataset[Tweet], spark: SparkSession): Long = {
    import spark.implicits._

    val usersFollowers = tweets
      .map(x => (x.username, x.followers))
      .distinct
    val followers = usersFollowers
      .map(_._2)
      .reduce(_ + _)
    
    followers
  }

  /** Returns the median follower count of all users in the dataset.*/
  def calculateMedianFollowers(tweets: Dataset[Tweet], spark: SparkSession): Long = {
    import spark.implicits._
    
    val users = calculateUserCount(tweets, spark)
    val median = {
      if (users % 2 == 0) 
        (users / 2).toLong - 1
      else 
        ((users + 1) / 2).toLong - 1
    }
    val uniqueUsers = tweets.dropDuplicates("username")
    val sorted = uniqueUsers
      .rdd
      .sortBy(_.followers)
      .zipWithIndex()
    val medianFollowers = sorted
      .map(x => (x._2, x._1))
      .lookup(median)
      .head
      .followers

    medianFollowers
  }
}