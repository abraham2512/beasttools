package beasttools

import slick.dbio.DBIO
import slick.jdbc.JdbcProfile
import com.byteslounge.slickrepo.repository.Repository
import scala.concurrent.Future
import beasttools.{FileRegistry}

trait Profile {
  val profile: JdbcProfile
}
trait DbModule extends Profile {
  val db: JdbcProfile#Backend#Database
  implicit def executeOperation[T](databaseOperation: DBIO[T]): Future[T] = {
    db.run(databaseOperation)
  }
}
trait PersistanceTraits {
}
