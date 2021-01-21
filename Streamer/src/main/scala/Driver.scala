
import scala.io.Source

import org.apache.spark.streaming._
import org.apache.spark.streaming.twitter._

object Driver {
  val batchInterval = 1

  def setupTwitterConfig(): Unit = {
    for (line <- Source.fromFile("secret.txt").getLines()) {
      val words = line.split(" ")

      if (words(0) == "consumerKey") {
        System.setProperty("twitter4j.oauth.consumerKey", words(1))
      }
      else if (words(0) == "consumerSecret") {
        System.setProperty("twitter4j.oauth.consumerSecret", words(1))
      }
      else if (words(0) == "accessToken") {
        System.setProperty("twitter4j.oauth.accessToken", words(1))
      }
      else if (words(0) == "accessTokenSecret") {
        System.setProperty("twitter4j.oauth.accessTokenSecret", words(1))
      }
    }
  }

  def main(args: Array[String]): Unit = {

    setupTwitterConfig()

    val ssc = new StreamingContext("local[*]", "Trending Hashtags", Seconds(1)

    val tweets = TwitterUtils.createStream(ssc, None)

    val text = tweets.map(x => x.getText())
    val words = text.flatMap(x => x.split(" "))
    val hashtags = words.filter(x => x.startsWith("#"))

    val hashtagsValues = hashtags.map(x => (x, 1))
    val hashtagsCount = hashtagsValues.reduceByKeyAndWindow((x, y) => x + y, (x, y) => x - y, Seconds(args(0).toInt), Seconds(1))

    val results = hashtagsCount.transform(x => x.sortBy(x => x._2, false))

    results.print

    ssc.checkpoint("Checkpoint")
    ssc.start()
    ssc.awaitTermination()
  }
}
