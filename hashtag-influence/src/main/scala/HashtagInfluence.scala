package com.revature.project2.group4.hashtaginfluence

import org.apache.spark.sql.{Dataset, SparkSession}
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions.row_number

object HashtagInfluence {
  /** Returns the total number of users in the dataset.*/
  def userCount(tweets: Dataset[Tweet], spark: SparkSession): Long = {
    import spark.implicits._

    tweets
      .map(_.username)
      .distinct
      .count
  }

  /** Returns the total combined follower count of all users in the dataset.*/
  def totalFollowers(tweets: Dataset[Tweet], spark: SparkSession): Long = {
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
  def medianFollowers(tweets: Dataset[Tweet], spark: SparkSession): Long = {
    import spark.implicits._

    val users = this.userCount(tweets, spark)
    val median = {
      if (users % 2 == 0)
        (users / 2).toLong
      else
        ((users + 1) / 2).toLong
    }
    val uniqueUsers = tweets.dropDuplicates("username")

    val window = Window.orderBy("followers")
    val indexed = uniqueUsers.toDF()
      .withColumn("index", row_number.over(window))
      .as[IndexedTweet]

    indexed.filter(_.index == median)
      .head()
      .followers
  }

  case class IndexedTweet(text: String, username: String, followers: Long, index: Long);
}