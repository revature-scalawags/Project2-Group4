// IMPORTS
import scala.io.Source
import java.time.format.DateTimeFormatter
import collection.JavaConverters._

import org.apache.spark._
import twitter4j.{Query, Status, Twitter, TwitterFactory}
import twitter4j.conf.ConfigurationBuilder
import com.johnsnowlabs.nlp.pretrained.PretrainedPipeline

object Driver {

  var numDaysPrior = 7 // Twitter API only lets you go back a week
  var numTweetsPerDay = 200
  var queryTerm = ""

  def setupTwitterConfig(): ConfigurationBuilder = {
    val cb = new ConfigurationBuilder

    cb.setDebugEnabled(true)

    for (line <- Source.fromFile("secret.txt").getLines()) {
      val words = line.split(" ")

      if (words(0) == "consumerKey") {
        cb.setOAuthConsumerKey(words(1))
      }
      else if (words(0) == "consumerSecret") {
        cb.setOAuthConsumerSecret(words(1))
      }
      else if (words(0) == "accessToken") {
        cb.setOAuthAccessToken(words(1))
      }
      else if (words(0) == "accessTokenSecret") {
        cb.setOAuthAccessTokenSecret(words(1))
      }
    }

    return cb
  }

  def getTwitter(configBuilder: ConfigurationBuilder): Twitter = {
    val twitterFactory = new TwitterFactory(configBuilder.build)

    return twitterFactory.getInstance()
  }

  def performQueries(twitter: Twitter, sentimentDetector: PretrainedPipeline): Unit = {
    val query = new Query(queryTerm)

    query.setCount(numTweetsPerDay)
    query.setLang("en")

    val today = java.time.LocalDate.now()
    val dtFormat = DateTimeFormatter.ofPattern("[MM-dd]")

    for (i <- 1 to numDaysPrior) {
      Thread.sleep(1000)

      val curDay = today.minusDays(i)

      // Set time frame
      query.setSince(curDay.toString)
      query.setUntil(curDay.plusDays(1).toString)

      val results = twitter.search(query)

      var positiveCount = 0
      var negativeCount = 0

      for (result <- results.getTweets.asScala) {
        val annotations = sentimentDetector.annotate(result.getText)
        val sentiments = annotations("sentiment")

        for (sentiment <- sentiments) {
          sentiment match {
            case "positive" => positiveCount += 1
            case "negative" => negativeCount += 1
            case _ =>
          }
        }
      }

      println(s"${curDay.format(dtFormat)} $positiveCount $negativeCount")
    }
  }

  def main(args: Array[String]): Unit = {
    // Set up config
    val configBuilder = setupTwitterConfig()

    // Set up spark context
    val conf = new SparkConf().setAppName("Sentiment Analyzer").setMaster("local")
    val sc = new SparkContext(conf)

    // Set up sentiment detector
    val sentimentDetector = PretrainedPipeline("analyze_sentiment", lang="en")

    // Initialize Twitter
    val twitter = getTwitter(configBuilder)

    // Set vars
    queryTerm = args(0)

    // Perform queries
    performQueries(twitter, sentimentDetector)

    sc.stop()
  }
}
