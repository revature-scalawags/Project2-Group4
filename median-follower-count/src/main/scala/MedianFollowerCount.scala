import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.types.{LongType, StringType}

object MedianFollowerCount {

  case class Tweet(tweet: String, username: String, followers: Long)

  def main(args: Array[String]) {
    val spark = SparkSession
      .builder
      .master("local[1]")
      .appName("median-follower-count")
      .getOrCreate()

    import spark.implicits._  

    val schema = new StructType()
      .add("tweet", StringType)
      .add("username", StringType)
      .add("followers", LongType)

    val tweets = spark.read
      .format("csv")
      .schema(schema)
      .option("delimiter", "\t")
      .load("./tweet.tsv")
      .as[Tweet]
      .cache()
    
    val userCount = tweets
      .map(_.username)
      .distinct()
      .count()
    val totalFollowers = tweets
      .map(_.followers)
      .reduce(_ + _)
    val avgFollowers = totalFollowers / userCount
    
    val unsortedUsersWithFollowers = tweets
      .map(x => (x.username, x.followers))
      .distinct()
      .collect()
    val sortedFollowers = unsortedUsersWithFollowers
      .toSeq
      .sortWith((x, y) => x._2 > y._2)
      .map(_._2)
      .toArray
    val median = {
      if (userCount % 2 == 0) 
        (userCount / 2).toInt 
      else 
        ((userCount + 1) / 2).toInt
    }
    val medianFollowers = sortedFollowers(median)
    
    println("\n")
    println(s"TOTAL FOLLOWERS: $totalFollowers")
    println(s"USER COUNT: $userCount")
    println(s"AVG FOLLOWERS: $avgFollowers")
    println(s"MEDIAN FOLLOWERS: $medianFollowers")
    println("\n")
    spark.stop()
  }
}