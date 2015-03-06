/**
 * Created by ecomotto on 3/3/15.
 */

import java.io.{ObjectOutputStream, FileOutputStream}

import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.rdd.RDD
import org.apache.spark.SparkContext._
import scala.math.random
import org.apache.spark._
import org.apache.spark.storage.StorageLevel
import org.apache.spark.mllib.classification.SVMWithSGD
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.regression.LinearRegressionWithSGD
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.mllib.regression.LinearRegressionModel
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.mllib.feature.StandardScaler
import org.joda.time._
import org.apache.spark.rdd.RDD

object ecoMain {

  def processData(pDate:String, pTime:String): Vector = {
    val inDate = new DateTime(pDate.trim())
    val iDay = inDate.getDayOfWeek()
    val vect = Vectors.zeros(24).toArray
    if(iDay > 5){
      vect(23)=1;
    }else{
      vect(23)=0;
    }
    val iHour = pTime.split(":")(0).trim().toInt
    if(iHour != 23){
      vect(iHour) = 1;
    }
    //println(pDate + "::" + pTime)
    //println("vect("+iHour+" ==> "+ vect(iHour))
//    println("isWeekEnd ==> "+ vect(23))
    //println()
    Vectors.dense(vect)
  }

  def main(args: Array[String]) ={
    val conf = new SparkConf()
    .setMaster("local[2]")
    .setAppName("EcoStreaming")
    .set("spark.executor.memory","1g")
    .set("spark.rdd.compress","true")
    .set("spark.storage.memoryFraction","1")

    val sc = new SparkContext(conf)
    //val data = sc.parallelize(1 to 10000000).collect.filter(_<1000)
    //data.foreach(println)
    val data = sc.textFile("/home/ecomotto/Downloads/Data/IntervalMeterUsage.CSV" )

    val headers = data.mapPartitions(_.filter(i => (i>"2")))

    val parsedData = data.mapPartitions(_.drop(1)).map { line =>
      val parts = line.split(',')
      LabeledPoint(parts(4).toDouble,processData(parts(1), parts(2)))
    }.cache()

    // Scale the features
    val scaler = new StandardScaler(withMean = true, withStd = true).fit(parsedData.map(x => x.features))
    val scaledData = parsedData.map(x => LabeledPoint(x.label,scaler.transform(Vectors.dense(x.features.toArray))))

    // Building the model
    val numIterations = 500
    val step = 0.8
    val algorithm = new LinearRegressionWithSGD()
    algorithm.setIntercept(true)
    algorithm.optimizer.setNumIterations(numIterations).setStepSize(step)
    //algorithm.optimizer.setGradient(null)
    val model = algorithm.run(parsedData)
    var model0 = LinearRegressionWithSGD.train(parsedData,numIterations,step)
    val model2 = algorithm.run(scaledData)

    val ii="zz"
    val jj="yy"
    //
    val algorithm2 = new LinearRegressionWithSGD()
    algorithm2.setIntercept(false)
    algorithm2.optimizer.setNumIterations(400)
    val model1 = algorithm2.run(parsedData)
    val valuesAndPreds1 = parsedData.map { point =>
      val prediction = model1.predict(point.features)
      (point.label, prediction)
    }

    //valuesAndPreds1.saveAsTextFile("/home/ecomotto/Documents/output-"+ii+"/predictions")

    // Evaluate model on training examples and compute training error
    val valuesAndPreds = parsedData.map { point =>
      val prediction = model.predict(point.features)
      (point.label, prediction)
    }

    val valuesAndPreds0 = parsedData.map { point =>
      val prediction = model0.predict(point.features)
      (point.label, prediction)
    }


    valuesAndPreds.saveAsTextFile("/home/ecomotto/Documents/output-"+ii+"/predictions")
    var fos = new FileOutputStream("/home/ecomotto/Documents/output-" + ii + "/model")
    var oos = new ObjectOutputStream(fos)
    oos.writeObject(model)
    oos.close()
    //model.saveAsTextFile("/home/ecomotto/Documents/output-"+ii+"/model")
    parsedData.saveAsTextFile("/home/ecomotto/Documents/output-"+ii+"/parsedData")

    val valuesAndPreds2 = scaledData.map { point =>
      val prediction = model2.predict(point.features)
      (point.label, prediction)
    }
    valuesAndPreds2.saveAsTextFile("/home/ecomotto/Documents/output-"+jj+"/predictions")
    fos = new FileOutputStream("/home/ecomotto/Documents/output-"+jj+"/model2")
    oos = new ObjectOutputStream(fos)
    oos.writeObject(model2)
    oos.close()
    //model2.saveAsTextFile("/home/ecomotto/Documents/output-"+jj+"/model2")
    scaledData.saveAsTextFile("/home/ecomotto/Documents/output-"+jj+"/scaledData")

    val MSE = valuesAndPreds.map{case(v, p) => math.pow((v - p), 2)}.mean()
    val MSE2 = valuesAndPreds2.map{case(v, p) => math.pow((v - p), 2)}.mean()
//
    println("training Mean Squared Error = " + MSE)
    println("training Mean Squared Error (for Scaled data) = " + MSE2)

  }
}
