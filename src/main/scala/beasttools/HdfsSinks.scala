package beasttools


import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route

import scala.concurrent.Future
import akka.actor.typed.scaladsl.AskPattern.{Askable, schedulerFromActorSystem}
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.util.Timeout
import beasttools.HdfsRegistry._
class HdfsSinks(hdfsRegistry: ActorRef[HdfsRegistry.Command])(implicit val system: ActorSystem[_]) {

  private implicit val timeout: Timeout = Timeout.create(system.settings.config.getDuration("my-app.routes.ask-timeout"))

  //TEMP ask to hdfs actor
    def writeToHDFS: Future[HDFSActionPerformed] =
      hdfsRegistry.ask(WriteToHDFS)

}
