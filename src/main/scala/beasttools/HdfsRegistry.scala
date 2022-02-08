package beasttools

import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import org.apache.spark.sql.SparkSession

import java.util.concurrent.TimeUnit

object HdfsRegistry {
  sealed trait HdfsCommand
  //final case class WriteToHDFS(replyTo: ActorRef[HDFSActionPerformed]) extends HdfsCommand
//  case class Request(query: String, replyTo: ActorRef[Response])
//  case class Response(result: String)

  final case class HDFSActionPerformed(description:String) extends HdfsCommand
  final case class WriteToHdfs(replyTo: ActorRef[HDFSActionPerformed]) extends  HdfsCommand

  //case val spark:SparkSession = SparkSession.builder().appName("HdfsTest").master("local[*]").getOrCreate()


  def apply(): Behavior[HDFSActionPerformed] = {
    println("Hdfs Actor Born!")
    //hdfs_registry(HDFSActionPerformed("WriteToHdfs"))
    //Behaviors.same
    //hdfs_registry
    //Behaviors.same
    //val spark = SparkSession.builder().appName("HdfsTest").master("local[*]")
    //spark.getOrCreate()
    HDFSActionPerformed("START SUCCESS")
    Behaviors.empty
  }
  def startHDFS(fileURI:String):HDFSActionPerformed = {
    hdfs_registry(fileURI)
    HDFSActionPerformed("Starting Spark Job")
  }

  private def hdfs_registry(fileURI:String):HDFSActionPerformed = {

//    Behaviors.receiveMessage {

        //case WriteToHdfs(replyTo) => {
        try {
          println("STARTED SPARK JOB")
          //val fileURI = "/Users/abraham/Downloads/Riverside_WaterDistrict2.csv"
          val spark = SparkSession
            .builder
            .appName("HdfsTest")
            .master("local[*]").getOrCreate()
          val df = spark.read.csv(fileURI)
          df.show()
          spark.stop()
          HDFSActionPerformed("Success")
        } catch {
          case e: NoClassDefFoundError =>
            println("Could not get spark session" + e.toString)
            HDFSActionPerformed("Failure")
        } finally {
          println("Good Bye!")
          HDFSActionPerformed("Exit")
        }
        //replyTo ! HDFSActionPerformed(s"FILE UPLOAD STARTED")
        //Behaviors.same
      }
    //}

  //}

}


