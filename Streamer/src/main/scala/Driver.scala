
import scala.io.Source
import org.apache.spark._
import org.apache.spark.SparkContext._
import org.apache.spark.streaming._
import org.apache.spark.streaming.twitter._
import org.apache.spark.streaming.StreamingContext._

object Driver {

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

    val ssc = new StreamingContext("local[*]", "Hashtags", Seconds(1))

    val tweets = TwitterUtils.createStream(ssc, None)

    val text = tweets.map(x => x.getText())
    val words = text.flatMap(x => x.split(" "))
    val hashtags = words.filter(x => x.startsWith("#"))

    val hashtagsValues = hashtags.map(x => (x, 1))
    val hashtagsCount = hashtags_values.reduceByKeyAndWindow((x, y) => x + y, (x, y) => x - y, Seconds(300), Seconds(1))

    val results = hashtags_count.transform(x => x.sortBy(x => x._2, false))

    ssc.checkpoint("Checkpoint")
    ssc.start()
    ssc.awaitTermination()
  }
}
