package sensordata.json

import sensordata.SensorData
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

object SensorDataJsonSupport extends DefaultJsonProtocol with UUIDJsonSupport with InstantJsonSupport {
  import sensordata.json.MeasurementsJsonSupport._
  implicit val sensorDataFormat: RootJsonFormat[SensorData] = jsonFormat3(SensorData.apply)
}
