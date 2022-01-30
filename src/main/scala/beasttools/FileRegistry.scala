package beasttools

//#file-registry-actor
import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import slick.dbio.DBIO
import slick.jdbc.H2Profile
import slick.jdbc.JdbcBackend.Database

import scala.collection.immutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration


//#case classes
final case class File(filename:String, filetype:String, filesource:String, filestatus:String)
final case class Files(files: immutable.Seq[File])

object FileRegistry {
  //#actor protocol
  sealed trait Command
  final case class GetFiles(replyTo: ActorRef[Files]) extends Command
  final case class CreateFile(file: File, replyTo: ActorRef[FileActionPerformed]) extends Command
  //final case class GetFileResponse(maybeFile: Option[File])
  final case class FileActionPerformed(description:String)
//  val dao = new DAO(H2Profile)
//  val h2db = Database.forConfig("h2")
//  def apply(): Unit = {
//    println("DAO Object created!")
//    registry(Set.empty)
//    val f = RunDBIO.create_db(dao,h2db)
//    Await.result(f, Duration.Inf)
//  }

  def apply(): Behavior[Command] = {
    DAL()
    registry(Set.empty)
  }


  private def registry(files: Set[File]): Behavior[Command] = {
    sealed trait Query
    Behaviors.receiveMessage {
      case GetFiles(replyTo) =>
        try {
          val f = DAL.get_all()
          Await.result(f, Duration.Inf)
          print(f)
        } finally
          println("Database operation complete!")

        replyTo ! Files(files.toSeq)
        Behaviors.same
      case CreateFile(file,replyTo) =>
        try {
          val f = DAL.insert(file)
          Await.result(f, Duration.Inf)
        } finally
          println("Database operation complete!")
          replyTo ! FileActionPerformed(s"File ${file.filename} created!")
          registry(files + file)
  }
  }
}






