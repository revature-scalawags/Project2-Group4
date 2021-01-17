import com.danielasfregola.twitter4s.TwitterRestClient
import com.danielasfregola.twitter4s.entities.{HashTag, Tweet}
import scala.concurrent.ExecutionContext.Implicits.global

object RestExample extends App {

  // Make sure to define consumer and access tokens
  val client = TwitterRestClient()
  val user = "therastal"
  val limit = 10

  val results = client
    .userTimelineForUser(screen_name = user, count = limit)
    .map { ratedData =>
      val tweets = ratedData.data
      println(s"${user.toUpperCase}'S LATEST TWEETS:")
      println(tweets.mkString("\n-----------\n"))
  }
}
