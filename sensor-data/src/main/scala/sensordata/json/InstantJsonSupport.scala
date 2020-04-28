package sensordata.json

import java.time.Instant

import spray.json.{DefaultJsonProtocol, JsNumber, JsValue, JsonFormat, deserializationError}

trait InstantJsonSupport extends DefaultJsonProtocol {
  implicit object InstantFormat extends JsonFormat[Instant] {
    def write(instant: Instant): JsValue = JsNumber(instant.toEpochMilli)

    def read(json: JsValue): Instant = json match {
      case JsNumber(value) => Instant.ofEpochMilli(value.toLong)
      case other => deserializationError(s"Expected Instant as JsNumber, but got: $other")
    }
  }
}