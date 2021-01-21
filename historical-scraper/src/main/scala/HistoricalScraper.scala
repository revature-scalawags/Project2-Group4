import com.danielasfregola.twitter4s.entities.{HashTag, Tweet}
import com.danielasfregola.twitter4s.entities.enums.ResultType
import com.danielasfregola.twitter4s.TwitterRestClient

import com.github.tototoshi.csv._

import java.io.File

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object HistoricalScraper extends App {

  // Make sure to define consumer and access tokens
  val client = TwitterRestClient()
  val hashtag = "savemelee"
  val limit = 100
  val recentOrPopular = ResultType.Recent
  val filename = s"${hashtag}.tsv"

  implicit object TweetFormat extends DefaultCSVFormat {
    override val delimiter = '\t'
  }
  val file = new File(filename)
  val writer = CSVWriter.open(file)
  writer.writeRow(List("text", "user", "followers"))

  def searchTweets(query: String, max_id: Option[Long] = None): Future[Seq[Tweet]] = {
    def extractNextMaxId(params: Option[String]): Option[Long] = {
      //example: "?max_id=658200158442790911&q=%23scala&include_entities=1&result_type=mixed"
      params.getOrElse("").split("&").find(_.contains("max_id")).map(_.split("=")(1).toLong)
    }

    client.searchTweet(query, count = limit, result_type = recentOrPopular, max_id = max_id).flatMap { ratedData =>
      val result    = ratedData.data
      val nextMaxId = extractNextMaxId(result.search_metadata.next_results)
      val tweets    = result.statuses
      if (tweets.nonEmpty) searchTweets(query, nextMaxId).map(_ ++ tweets)
      else Future(tweets.sortBy(_.created_at))
    } recover { case _ => Seq.empty }
  }

  val results = searchTweets(s"#$hashtag").map { tweets =>
    println(s"Downloaded ${tweets.size} tweets")
    for (tweet <- tweets) {
      var username = ""
      var followers = 0
      try {
        val user = tweet.user.get
        username = user.screen_name
        followers = user.followers_count
      } catch {
        case e: Exception =>
          println(s"[ERROR]: failed to retrieve user of tweet ${tweet.id_str}. " +
            s"Defaulting to blank name and follower count of 0.")
      }
      writer.writeRow(List(tweet.text, username, followers))
    }
    println(s"Tweets saved to file $filename")
  }
  /*.searchTweet(s"#$hashtag", count = 100, result_type = recentOrPopular)
    .flatMap { ratedData => 
      val tweets = ratedData.data.statuses
      for (tweet <- tweets) {
        var username = ""
        var followers = 0
        try {
          val user = tweet.user.get
          username = user.screen_name
          followers = user.followers_count
        } catch {
          case e: Exception =>
            println(s"[ERROR]: failed to retrieve user of tweet ${tweet.id_str}. " +
              s"Defaulting to blank name and follower count of 0.")
        }
        writer.writeRow(List(tweet.text, username, followers))
      }
      println(s"Limit: ${ratedData.rate_limit.limit}, Remaining: ${ratedData.rate_limit.remaining}")
    } */

  Await.result(results, 45 seconds)
  writer.close()
  client.shutdown()
}
