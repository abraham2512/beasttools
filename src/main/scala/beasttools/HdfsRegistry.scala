package beasttools

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import org.apache.spark.sql.SparkSession

import java.util.concurrent.TimeUnit

object HdfsRegistry {
  sealed trait Command
  case class WriteToHDFS(replyTo: ActorRef[HDFSActionPerformed]) extends Command
  final case class HDFSActionPerformed(description:String)

  def apply(): Behavior[Command] = {
    println("Hdfs Actor Born!")
    hdfsRegistry
  }

  private def hdfsRegistry: Behavior[Command] = {

    Behaviors.receiveMessage {
      case WriteToHDFS(replyTo) => {
        try {
          println("STARTED SPARK JOB")
          val fileURI = "/Users/abraham/Projects/Riverside_IntersectionPoints.geojson"
          val spark = SparkSession
            .builder
            //        .appName("HdfsTest")
            .getOrCreate()
          val file = spark.read.json(fileURI).rdd
          val mapped = file.map(s => s.length).cache()
          for (iter <- 1 to 10) {
            val startTimeNs = System.nanoTime()
            for (x <- mapped) {
              x + 2
            }
            val durationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTimeNs)
            println(s"Iteration $iter took $durationMs ms")
          }
          println(s"File contents: ${file.map(_.toString).take(1).mkString(",").slice(0, 10)}")
          println(s"Returned length(s) of: ${file.map(_.length).sum().toString}")
          spark.stop()
        } catch {
          case e: NoClassDefFoundError =>
            println("Could not get spark session")
        } finally {
          println("Good Bye :(")

        }
        replyTo ! HDFSActionPerformed("FILE UPLOAD STARTED")
        Behaviors.same
      }
    }
  }
}


