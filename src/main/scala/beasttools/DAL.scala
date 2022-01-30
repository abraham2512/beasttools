package beasttools

import beasttools.FileRegistry.registry
import slick.dbio.DBIO
import slick.jdbc.H2Profile
import slick.jdbc.JdbcBackend.Database

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration

object DAL {
    private val dao = new DAO(H2Profile)
    private val db = Database.forConfig("h2")
    def apply(): Unit = {
      val f = create_db()
      Await.result(f, Duration.Inf)
      println("DAO Object created!")

    }
    def create_db() : Future[Unit] = {
      db.run(DBIO.seq(
        dao.create
      ).withPinnedSession)
    }
    def insert(file: File): Future[Unit] = {
      db.run(DBIO.seq(
        dao.insert(file.filename,file.filetype,file.filesource,file.filestatus)
      ).withPinnedSession)
    }
    def get_all(): Future[Unit] = {
      db.run(DBIO.seq(
        dao.get_all().map(r => println("Values " + r))
      ).withPinnedSession)
    }
}
