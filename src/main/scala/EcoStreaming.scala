
import kafka.serializer.StringDecoder
import org.apache.spark.SparkContext
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
/*
import com.github.nscala_time.time.Imports._
import kafka.serializer.StringDecoder
import org.apache.commons.io.Charsets
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import reactivemongo.api._
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson._
import MongoConversions._
import scala.concurrent.ExecutionContext.Implicits.global
 */

/**
 * Created by ecomotto on 3/6/15.
 */
//import com.twitter.algebird.HyperLogLogMonoid
//import MongoConversions._

object EcoStreaming extends App{

  val BatchDuration = Seconds(10)
  val sparkContext = new SparkContext("local[4]", "logAggregator")
  val streamingContext = new StreamingContext(sparkContext, BatchDuration)
  val kafkaParams = Map(
    "zookeeper.connect" -> "localhost:2181",
    "zookeeper.connection.timeout.ms" -> "10000",
    "group.id" -> "myGroup"
  )

  val topics = Map(
    Constants.KafkaTopic -> 1
  )

  while(true) {
    // stream of (topic, ImpressionLog)
    val messages = KafkaUtils.createStream[String, ImpressionLog, StringDecoder, ImpressionLogDecoder](streamingContext, kafkaParams, topics, StorageLevel.MEMORY_AND_DISK)
    println("Received Messages => " + messages.map(_._2).map(meter => println(meter.usage_date.toString())))
  }

}
