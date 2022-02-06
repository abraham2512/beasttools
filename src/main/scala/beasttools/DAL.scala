package beasttools

import beasttools.FileRegistry.registry
import slick.dbio.DBIO
import slick.jdbc.H2Profile
import slick.jdbc.JdbcBackend.Database

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}

object DAL {
    //sealed trait QueryResult
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
//    def get(k:String): Future[Unit] = {
//      db.run(DBIO.seq(
//          dao.get(k)
//      ))
//    }
  def get(k:String): File = {
  try{
    val f : Future[Option[(String,String,String,String)]] = db.run(dao.get(k))
    val result = Await.result(f,Duration.Inf)
    val file_data = result.get
    val returnFile = File(file_data._1,file_data._2,file_data._3,file_data._4)

    //    f.onComplete {
//      case Success(value) => {
//        println("success" + value)
//        val file = value.get
//        }
//
//      }
    returnFile
    }
  }
//  val returnFile:File = File("","","","")
//
//  returnFile
}
