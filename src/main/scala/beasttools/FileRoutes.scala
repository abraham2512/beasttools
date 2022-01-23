package beasttools

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route

import scala.concurrent.Future
import akka.actor.typed.scaladsl.AskPattern.{Askable, schedulerFromActorSystem}
import akka.actor.typed.{ActorRef, ActorSystem}
//import akka.http.scaladsl.server.Directives.{as, complete, concat, entity, get, onSuccess, pathEnd, pathPrefix, post}
import akka.util.Timeout
import beasttools.FileRegistry._


class FileRoutes(fileRegistry: ActorRef[FileRegistry.Command])(implicit val system: ActorSystem[_]) {

  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import JsonFormats._
  //#file-route-class

  //#implicit default timeout value for all requests
  private implicit val timeout = Timeout.create(system.settings.config.getDuration("my-app.routes.ask-timeout"))

  def getFiles(): Future[Files] =
    fileRegistry.ask(GetFiles)
  def createFile(file: File) : Future[FileActionPerformed] =
    fileRegistry.ask(CreateFile(file,_))

  //#all-routes

  val fileRoutes: Route =
    pathPrefix("files"){
      concat(
        //#files-create-and-list
      pathEnd {
        concat(
          get {
            complete(getFiles())
          },
          post {
            entity(as[File]){ file =>
              onSuccess(createFile(file)) { performed =>
                complete((StatusCodes.Created, performed))
              }

            }
          }
        )
      }
      )
    }
}
