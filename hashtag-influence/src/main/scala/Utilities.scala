package com.revature.project2.group4.hashtaginfluence

import scala.collection.immutable.ListMap

object Utilities {
  /** Returns the total number of users in the dataset.*/
  def calculateUserCount(tweets: Seq[Tweet]): Int = {
    tweets
      .map(_.username)
      .distinct
      .size
  }

  /** Returns the combined follower count of all users in the dataset.*/
  def calculateTotalFollowers(tweets: Seq[Tweet]): Long = {
    val usersFollowers = tweets
      .reverse
      .map(x => (x.username, x.followers))
      .distinct
    val followers = usersFollowers
      .map(_._2)
      .reduce(_ + _)
    
    followers
  }

  /** Returns the median follower count of all users in the dataset.*/
  def calculateMedianFollowers(tweets: Seq[Tweet]): Long = {
    val usersFollowers = tweets
      .reverse
      .map(x => (x.username, x.followers))
      .distinct
    val sortedFollowers = usersFollowers
      .map(_._2)
      .sortWith((x, y) => x > y)
    val users = calculateUserCount(tweets)
    val median = {
      if (users % 2 == 0) 
        (users / 2).toInt - 1
      else 
        ((users + 1) / 2).toInt - 1
    }

    sortedFollowers(median)
  }
}