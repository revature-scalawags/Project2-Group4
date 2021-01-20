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
    tweets
      .map(_.followers)
      .reduce(_ + _)
  }

  /** Returns the median follower count of all users in the dataset.*/
  def calculateMedianFollowers(tweets: Seq[Tweet]): Long = {
    val unsortedUsersWithFollowers = 
      Map(tweets.reverse.map(x => x.username -> x.followers):_*)
    val sortedFollowers = unsortedUsersWithFollowers
      .map(_._2)
      .toSeq
      .sortWith((x, y) => x > y)
    val users = calculateUserCount(tweets)
    val median = {
      if (users % 2 == 0) 
        (users / 2).toInt 
      else 
        ((users + 1) / 2).toInt
    }

    sortedFollowers(median)
  }
}