//import sbtassembly.AssemblyKeys
//import AssemblyKeys._

name := "EcoStreaming"

version := "1.0"

scalaVersion := "2.10.4"

val sparkVersion = "1.2.0"

val kafkaVersion = "0.8.2.0"

net.virtualvoid.sbt.graph.Plugin.graphSettings

libraryDependencies <<= scalaVersion {
  scala_version => Seq(
  // Spark and Spark Streaming
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-streaming" % sparkVersion,
  "org.apache.spark" %% "spark-streaming-kafka" % sparkVersion,
  "org.apache.spark" %% "spark-mllib" % sparkVersion,
  // Kafka
  "org.apache.kafka" %% "kafka" % kafkaVersion,
  // for serialization of case class
  "com.novus" %% "salat" % "1.9.8",
  // MongoDB
  "org.reactivemongo" %% "reactivemongo" % "0.10.5.0.akka23",//"org.reactivemongo" %% "reactivemongo" % "0.11.0-SNAPSHOT",
  // Joda dates for Scala
  "com.github.nscala-time" %% "nscala-time" % "1.2.0",
  "com.google.code.gson" % "gson" % "2.3",
  "commons-cli" % "commons-cli" % "1.2"
)
}

resolvers += "typesafe repo" at " http://repo.typesafe.com/typesafe/releases/"

resolvers += "Akka Repository" at "http://repo.akka.io/releases/"

resolvers += "opennlp sourceforge repo" at "http://opennlp.sourceforge.net/maven2"

resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/releases/"

resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

///*
//assemblySettings

//mergeStrategy in assembly := {
  //case m if m.toLowerCase.endsWith("manifest.mf") => MergeStrategy.discard
  //case m if m.toLowerCase.matches("meta-inf.*\\.sf$") => MergeStrategy.discard
  //case "log4j.properties" => MergeStrategy.discard
  //case m if m.toLowerCase.startsWith("meta-inf/services/") => MergeStrategy.filterDistinctLines
 // case "reference.conf" => MergeStrategy.concat
 // case _ => MergeStrategy.first
//}
//*/

