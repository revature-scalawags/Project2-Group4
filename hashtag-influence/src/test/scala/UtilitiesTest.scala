package com.revature.project2.group4.hashtaginfluence

// TODO(rastal) actually implement testing for the utilities functions
class UtilitiesTest extends org.scalatest.funsuite.AnyFunSuite {
  val tweets = Seq(
    new Tweet("bongos #savemelee", "donkey", 1000),
    new Tweet("tweet #testing", "pickles", 24),
    new Tweet("besker #oinkers", "dunkey", 1000000),
    new Tweet("testing testing 123 #456", "lesmoonvest", 20),
    new Tweet("this is a tweet #tweeeeet", "picklesandcheese", 4),
    new Tweet("this is another tweet", "donkey", 1000)
  )
  
  test("calculate user count") {
    val count = Utilities.calculateUserCount(tweets)
    assert(count == 5)
  }

  test("calculate total follower count") {
    val followers = Utilities.calculateTotalFollowers(tweets)
    assert(followers == 1001048)
  }

  test("calculate median follower count") {
    val median = Utilities.calculateMedianFollowers(tweets)
    assert(median == 24)
  }
}
