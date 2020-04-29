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
import cloudflow.akkastream.scaladsl.{FlowWithCommittableContext, RunnableGraphStreamletLogic}
import cloudflow.akkastream.util.scaladsl.Splitter
import cloudflow.akkastream.{AkkaStreamlet, AkkaStreamletLogic}
import cloudflow.streamlets.avro.{AvroInlet, AvroOutlet}
import cloudflow.streamlets.{RoundRobinPartitioner, StreamletShape}

class MetricsValidation extends AkkaStreamlet {
  val in: AvroInlet[Metric] = AvroInlet[Metric]("in")
  val invalid: AvroOutlet[InvalidMetric] = AvroOutlet[InvalidMetric]("invalid").withPartitioner(metric => metric.metric.deviceId.toString)
  val valid: AvroOutlet[Metric] = AvroOutlet[Metric]("valid").withPartitioner(RoundRobinPartitioner)
  val shape: StreamletShape = StreamletShape(in).withOutlets(invalid, valid)

  override def createLogic: AkkaStreamletLogic = new RunnableGraphStreamletLogic() {
    private val flow = FlowWithCommittableContext[Metric].map { metric =>
      if (!SensorDataUtils.isValidMetric(metric)) {
        Left(InvalidMetric(metric, "All measurements must be positive numbers!"))
      } else {
        Right(metric)
      }
    }

    def runnableGraph: RunnableGraph[_] = sourceWithOffsetContext(in).to(Splitter.sink(flow, invalid, valid))
  }
}