package beasttools

import beasttools.UserRegistry.ActionPerformed
import beasttools.FileRegistry.FileActionPerformed
//#json-formats
import spray.json.DefaultJsonProtocol

object JsonFormats  {
  // import the default encoders for primitive types (Int, String, Lists etc)
  import DefaultJsonProtocol._

  implicit val userJsonFormat = jsonFormat3(User)
  implicit val usersJsonFormat = jsonFormat1(Users)
  implicit val actionPerformedJsonFormat = jsonFormat1(ActionPerformed)

  implicit val fileJsonFormat = jsonFormat3(File)
  implicit val filesJsonFormat = jsonFormat1(Files)
  implicit val fileActionPerformedJsonFormat = jsonFormat1(FileActionPerformed)
}
//#json-formats
