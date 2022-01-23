package beasttools

//#file-registry-actor
import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import scala.collection.immutable

//#case classes
final case class File(filename:String, filetype:String, source:String)
final case class Files(files: immutable.Seq[File])

object FileRegistry {
  //#actor protocol
  sealed trait Command
  final case class GetFiles(replyTo: ActorRef[Files]) extends Command
  final case class CreateFile(file: File, replyTo: ActorRef[FileActionPerformed]) extends Command
  //final case class GetFileResponse(maybeFile: Option[File])
  final case class FileActionPerformed(description:String)

  def apply(): Behavior[Command] = registry(Set.empty)

  private def registry(files: Set[File]): Behavior[Command] =
    Behaviors.receiveMessage {
      case GetFiles(replyTo) =>
        replyTo ! Files(files.toSeq)
        Behaviors.same
      case CreateFile(file,replyTo) =>
        replyTo ! FileActionPerformed(s"File ${file.filename} created!")
        registry(files + file)

  }

}
