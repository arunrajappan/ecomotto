/**
 * Created by ecomotto on 3/5/15.
 */
//import com.twitter.algebird.HLL
//import com.twitter.algebird.HLL
import org.joda.time.DateTime

case class ImpressionLog(essid: String,usage_date: String,usage_start_time: String,usage_end_time: String,usage_kwh: Double,estimated_actual: String,consumption_generation: String)

// intermediate result used in reducer
case class AggregationLog(timestamp: Long, sumBids: Double, imps: Int = 1)

// result to be stored in MongoDB
case class AggregationResult(date: DateTime, publisher: String, geo: String, imps: Int, uniques: Int, avgBids: Double)

case class PublisherGeoKey(publisher: String, geo: String)
