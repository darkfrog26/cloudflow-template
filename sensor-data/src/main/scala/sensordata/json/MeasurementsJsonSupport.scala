package sensordata.json

import sensordata.Measurements
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

object MeasurementsJsonSupport extends DefaultJsonProtocol {
  implicit val measurementFormat: RootJsonFormat[Measurements] = jsonFormat3(Measurements.apply)
}