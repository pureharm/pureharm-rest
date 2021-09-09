/*
 * Copyright 2021 BusyMachines
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

package busymachines.pureharm.endpoint

import busymachines.pureharm.anomaly._
import sttp.tapir

/** @author
  *   Lorand Szakacs, https://github.com/lorandszakacs
  * @since 14
  *   Jul 2020
  */
object PureharmTapirSchemas {

  private[this] val idFN       = tapir.FieldName("id")
  private[this] val messageFN  = tapir.FieldName("message")
  private[this] val messagesFN = tapir.FieldName("messages")
  private[this] val paramsFN   = tapir.FieldName("parameters")

  private[this] val schemaAnomalyID: tapir.Schema[AnomalyID] = tapir.Schema
    .string[AnomalyID]
    .name(tapir.Schema.SName("AnomalyID"))
    .description("ID of the error message. These are defined per app and they will vary.")

  private[this] val schemaParameters: tapir.Schema[Anomaly.Parameters] =
    tapir.Schema(
      schemaType     = tapir.SchemaType.SProduct[Anomaly.Parameters](List.empty),
      name           = Option(tapir.Schema.SName("AnomalyParameters")),
      description    = Option(
        """|
           |This is a dynamically typed object.
           |
           |Its form will depend on the specific anomaly, identified through the .id field.
           |But what you can expect is that if it _does_ contain values, they will be of type 'string', or array of 'string'.
           |
           |Example Value:
           |{
           |    "id" : "some-id",
           |    "message" : "example message",
           |    "parameters" : {
           |      "one" : "one",
           |      "two" : ["one", "two" ]
           |    }
           | }
           |
           |
           |""".stripMargin
      ),
      encodedExample = Option(Anomaly.Parameters.empty),
    )

  private[this] val anomalyIDField = tapir.SchemaType.SProductField[AnomalyBase, AnomalyID](
    _name   = idFN,
    _schema = schemaAnomalyID,
    _get    = anomaly => Option(anomaly.id),
  )

  private[this] val messageField = tapir.SchemaType.SProductField[AnomalyBase, String](
    _name   = messageFN,
    _schema = tapir.Schema.schemaForString,
    _get    = anomaly => Option(anomaly.message),
  )

  private[this] val parametersField = tapir.SchemaType.SProductField[AnomalyBase, Anomaly.Parameters](
    _name   = paramsFN,
    _schema = schemaParameters,
    _get = anomaly => if (anomaly.parameters.isEmpty) Option.empty[Anomaly.Parameters] else Option(anomaly.parameters),
  )

  private[this] val simpleAnomalyBase: tapir.Schema[AnomalyBase] = {
    tapir.Schema(
      schemaType  = tapir.SchemaType.SProduct[AnomalyBase](List(anomalyIDField, messageField, parametersField)),
      name        = Option(tapir.Schema.SName("AnomalyLike")),
      description = Option("Simple form of any failure. Please use .id field to make decisions specific to an anomaly"),
    )
  }

  private[this] val anomaliesField = tapir.SchemaType.SProductField[AnomalyBase, Seq[AnomalyBase]](
    _name   = messagesFN,
    _schema = simpleAnomalyBase.asIterable[Seq],
    _get    = {
      case anomalies: AnomaliesBase => Option(anomalies.messages)
      case _ => Option.empty
    },
  )

  private[this] def anomalyToThrowable(anomaly: AnomalyBase): Option[Throwable] = anomaly match {
    case a: AnomalyLike => Option(a: Throwable)
    case _ => Option(Anomaly(anomaly): Throwable)
  }

  private[this] def throwableToAnomaly(t: Throwable): AnomalyBase = t match {
    case a: AnomalyLike => a: AnomalyBase
    case _ => UnhandledCatastrophe(t): AnomalyBase
  }

  implicit val pureharmAnomalyTapirSchema: tapir.Schema[AnomalyBase] = {
    tapir.Schema(
      schemaType  = tapir.SchemaType
        .SProduct[AnomalyBase](List(anomalyIDField, messageField, parametersField, anomaliesField)),
      name        = Option(tapir.Schema.SName("Anomaly")),
      description = Option(
        "Complex form of failure, containing multiple failures at once in the .messages field if present. This is usually returned for form validations"
      ),
    )
  }

  implicit val pureharmThrowableTapirSchema: tapir.Schema[Throwable] =
    pureharmAnomalyTapirSchema.map[Throwable](anomalyToThrowable)(throwableToAnomaly)

}
