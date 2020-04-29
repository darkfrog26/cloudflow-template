/*
 * Copyright (C) 2016-2020 Lightbend Inc. <https://www.lightbend.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sensordata

import akka.stream.scaladsl.RunnableGraph
import cloudflow.akkastream.AkkaStreamlet
import cloudflow.akkastream.scaladsl.{FlowWithCommittableContext, RunnableGraphStreamletLogic}
import cloudflow.streamlets.avro.AvroInlet
import cloudflow.streamlets.{RegExpConfigParameter, StreamletShape, StringConfigParameter}

class ValidMetricLogger extends AkkaStreamlet {

  val inlet: AvroInlet[Metric] = AvroInlet[Metric]("in")
  val shape: StreamletShape = StreamletShape.withInlets(inlet)

  val LogLevel: RegExpConfigParameter = RegExpConfigParameter(
    key = "log-level",
    description = "Provide one of the following log levels, debug, info, warning or error",
    pattern = "^debug|info|warning|error$",
    defaultValue = Some("debug")
  )
  private val MsgPrefix: StringConfigParameter = StringConfigParameter("msg-prefix", "Provide a prefix for the log lines", Some("valid-logger"))

  override def configParameters = Vector(LogLevel, MsgPrefix)

  override def createLogic = new RunnableGraphStreamletLogic() {

    private val logF: String => Unit = streamletConfig.getString(LogLevel.key).toLowerCase match {
      case "debug" => system.log.debug _
      case "info" => system.log.info _
      case "warning" => system.log.warning _
      case "error" => system.log.error _
    }

    private val msgPrefix: String = streamletConfig.getString(MsgPrefix.key)

    private val flow = FlowWithCommittableContext[Metric].map { validMetric =>
      log(validMetric)
      validMetric
    }

    def runnableGraph: RunnableGraph[_] = sourceWithOffsetContext(inlet).via(flow).to(committableSink)

    def log(metric: Metric): Unit = logF(s"$msgPrefix $metric")
  }
}