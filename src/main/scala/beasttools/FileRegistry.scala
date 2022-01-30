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

  def apply(): Behavior[Command] = registry(Set.empty)
  //def apply(): Behavior[Command] =


  private def registry(files: Set[File]): Behavior[Command] =
    Behaviors.receiveMessage {
      case GetFiles(replyTo) =>
        replyTo ! Files(files.toSeq)
        Behaviors.same
      case CreateFile(file,replyTo) =>
        try {
          //#create
          val f = {
            val h2db = Database.forConfig("h2")
            RunDBIO.run(new DAO(H2Profile), h2db).andThen { case _ => h2db.close }
          }
          //#create
          Await.result(f, Duration.Inf)
        } finally
          println("Database operation complete!")
          replyTo ! FileActionPerformed(s"File ${file.filename} created!")
          registry(files + file)


  }
}


object RunDBIO {
  //#run
  def run(dao: DAO, db: Database): Future[Unit] = {
    val h = new DAOHelper(dao)
    println("Using profile " + dao.profile)
    //#run
    db.run(DBIO.seq(
      dao.create,
      dao.insert("foo", "bar"),
      dao.get("foo").map(r => println("- Value for key 'foo': " + r)),
      dao.get("baz").map(r => println("- Value for key 'baz': " + r)),
      h.dao.getFirst(h.restrictKey("foo", dao.props)).map(r => println("- Using the helper: " + r))
    ).withPinnedSession)
  }
}



