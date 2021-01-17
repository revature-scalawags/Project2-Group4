
import org.apache.spark._
import org.apache.spark.ml._
import com.johnsnowlabs.nlp.pretrained.PretrainedPipeline

import org.apache.spark.streaming._
import org.apache.spark.streaming.twitter._
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}

object Driver {

  val sample = "I can't believe it! #Avocado #Microwave"

  def main(args: Array[String]): Unit = {

    // SET UP TWITTER
    

    val conf = new SparkConf().setAppName("test").setMaster("local")
    val sc = new SparkContext(conf)

    println(sample)

    // ADD ANNOTATIONS TO RDD

    // REFORM RDD INTO HASHTAG + SENTIMENT

    val sentimentDetectorPipeline = PretrainedPipeline("analyze_sentiment", lang="en")

    val annotations = sentimentDetectorPipeline.annotate(sample)

    println(annotations)

    sc.stop()
  }
}
