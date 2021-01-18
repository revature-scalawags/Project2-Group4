import com.danielasfregola.twitter4s.entities.{HashTag, Tweet}
import com.danielasfregola.twitter4s.entities.enums.ResultType
import com.danielasfregola.twitter4s.TwitterRestClient
import scala.concurrent.ExecutionContext.Implicits.global

object HistoricalScraper extends App {

  // Make sure to define consumer and access tokens
  val client = TwitterRestClient()
  val hashtag = "savemelee"
  val limit = 10
  val recentOrPopular = ResultType.Recent

  for (i <- 0 until args.length) {
    val results = client
      .searchTweet(s"#$hashtag", count = limit, result_type = recentOrPopular)
      .map { ratedData => 
        val tweets = ratedData.data.statuses
        tweets.foreach(tweet => println(s"\n${tweet.text}"))
      }
  }

  Thread.sleep(3000)
  client.shutdown()
}
