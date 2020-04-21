package sensordata.json

import java.util.UUID

import spray.json.{DefaultJsonProtocol, JsString, JsValue, JsonFormat, deserializationError}

import scala.util.Try

trait UUIDJsonSupport extends DefaultJsonProtocol {
  implicit object UUIDFormat extends JsonFormat[UUID] {
    def write(uuid: UUID): JsValue = JsString(uuid.toString)

    def read(json: JsValue): UUID = json match {
      case JsString(uuid) ⇒ Try(UUID.fromString(uuid)).getOrElse(deserializationError(s"Expected valid UUID but got '$uuid'."))
      case other          ⇒ deserializationError(s"Expected UUID as JsString, but got: $other")
    }
  }
}