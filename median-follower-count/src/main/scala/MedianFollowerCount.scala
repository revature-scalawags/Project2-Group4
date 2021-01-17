import org.apache.spark.sql.functions
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.
  {LongType, StringType, StructField, StructType}

object MedianFollowerCount {

  case class Tweet(tweet: String, username: String, followers: Long)

  def main(args: Array[String]) {
    val spark = SparkSession
      .builder
      .master("local[1]")
      .appName("median-follower-count")
      .getOrCreate()

    import spark.implicits._  

    val tweets = spark.read.csv("./tweet.tsv").as[Tweet]
    // tweets.reduce(_.followers + _.followers)   // this is meant to be a 
    //   mapreduce style function to output the total follower count of the entire
    //   dataset
    
    println(s"\nTOTAL FOLLOWERS:\n")
    spark.stop()
  }
}