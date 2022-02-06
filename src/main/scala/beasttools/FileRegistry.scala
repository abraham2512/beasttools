package beasttools

//#file-registry-actor
import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

import scala.collection.immutable
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.{Duration, DurationInt}
import scala.util.{Failure, Success}


//#case classes
case class File(filename:String, filetype:String, filesource:String, filestatus:String) {
  def apply(filename:String,filetype:String,filesource:String,filestatus:String): File = { File(filename,filetype,filesource,filestatus)}
}

final case class Files(files: immutable.Seq[File])

object FileRegistry {
  //#actor protocol
  sealed trait Command

  final case class GetFiles(replyTo: ActorRef[Files]) extends Command
  final case class GetFile(filename: String,replyTo: ActorRef[File]) extends Command

  final case class CreateFile(file: File, replyTo: ActorRef[FileActionPerformed]) extends Command

  final case class FileActionPerformed(description:String)

  //implicit  val ec = ExecutionContext.global
  def apply(): Behavior[Command] = {
    DAL()
    registry(Set.empty)
  }


  private def registry(files: Set[File]): Behavior[Command] = {
    Behaviors.receiveMessage {
      case GetFile(filename,replyTo)  => {
        val f = DAL.get(filename)
        //println(f)
        //val ft = File("test", "test", "test", "test")
        replyTo ! f
        Behaviors.same
        }
      case GetFiles(replyTo) =>
        try {
          val f: Future[Unit] = DAL.get_all()
          //f.onComplete { case _ => print(_)}
          Await.result(f, Duration.Inf)
//          print("here")

        } finally
          println("Database get all complete!")

        replyTo ! Files(files.toSeq)
        Behaviors.same
      case CreateFile(file,replyTo) =>
        try {
          val f = DAL.insert(file)
          Await.result(f, Duration.Inf)

        } finally
          println("Database insert complete!")
          replyTo ! FileActionPerformed(s"File ${file.filename} created!")
          registry(files + file)

    }
  }
}






