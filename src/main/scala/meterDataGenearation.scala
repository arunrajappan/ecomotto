/**
 * Created by ecomotto on 3/5/15.
 */

//import java.util.{Properties, Random}
//
//import org.apache.kafka.clients.producer.{Producer, ProducerConfig}

import java.util.Properties

import kafka.javaapi.producer.Producer
import kafka.producer.{KeyedMessage, ProducerConfig}
import org.joda.time.DateTime

import scala.collection.JavaConversions._
import scala.util.Random

object meterDataGenearation extends App {
  val random = new Random()

  val props = new Properties()
  props ++= Map(
    "serializer.class" -> "ImpressionLogEncoder",
    "metadata.broker.list" -> "127.0.0.1:9093"
  )

  val config = new ProducerConfig(props)
  val producer = new Producer[String, ImpressionLog](config)

  println("Sending messages...")
  var i = 0
  var start_datetime = new DateTime(2015,1,1,0,0,0)
  var end_datetime = new DateTime(2015,1,1,0,15,0)
  var weekDayCoeffs = Array(0.194623,0.159835,0.140935,0.133503,0.128276,0.127739,0.133832,0.173162,0.179421,0.282233,0.36031,0.476329,0.578705,0.700062,0.655169,0.573716,0.525581,0.523331,0.446176,0.41516,0.416861,0.468611,0.328002,0.226882)
  var weekEndCoeffs = Array(0.19684,0.162052,0.143152,0.13572,0.130493,0.129956,0.136049,0.175379,0.181638,0.28445,0.362527,0.478546,0.580922,0.702279,0.657386,0.575933,0.527798,0.525548,0.448393,0.417377,0.419078,0.470828,0.330219,0.231316)
  //var usage_kwh = 0.0
  // infinite loop
  while(true) {
    val essid = "10443720006582232"
    val usage_date = start_datetime.toString("YYYY-MM-DD");
    start_datetime = start_datetime.plusMinutes(15)
    end_datetime = end_datetime.plusMinutes(15)
    val usage_start_time = start_datetime.toString("HH:mm")
    val usage_end_time = end_datetime.toString("HH:mm")

    val iDay = start_datetime.getDayOfWeek()
    var usage_kwh = 0.0
    val iHr = Integer.parseInt(start_datetime.toString("HH"))

    if(iDay > 5){
      usage_kwh = weekEndCoeffs(iHr)
    }else{
      usage_kwh = weekDayCoeffs(iHr)
    }

    val estimated_actual = s"A"
    val consumption_generation = s"Consumption"
    val bid = math.abs(random.nextDouble()) % 1
    val log = ImpressionLog(essid,usage_date,usage_start_time,usage_end_time,usage_kwh,estimated_actual,consumption_generation)
    println(log.toString())
    producer.send(new KeyedMessage[String, ImpressionLog](Constants.KafkaTopic, log))
    i = i + 1
    if (i % 10000 == 0) {
      println(s"Sent $i messages!")
    }
  }
}