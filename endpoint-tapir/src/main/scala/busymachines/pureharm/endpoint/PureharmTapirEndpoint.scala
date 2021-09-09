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
import busymachines.pureharm.internals.json.AnomalyJsonCodec
import sttp.tapir
import sttp.tapir._
import sttp.model.StatusCode

/** @author
  *   Lorand Szakacs, https://github.com/lorandszakacs
  * @since 14
  *   Jul 2020
  */
object PureharmTapirEndpoint {
  import busymachines.pureharm.json

  /** This should serve as your basis for most endpoints in your app. It provides grade A interpretation of all Anomaly
    * types, plus the good old mapping to status codes. You can easily glance the mapping from the implementation. But
    * here it is for easy glancing:
    *
    * {{{
    *   NotFoundAnomaly              StatusCode.NotFound
    *   UnauthorizedAnomaly          StatusCode.Unauthorized
    *   ForbiddenAnomaly             StatusCode.Forbidden
    *   DeniedAnomaly                StatusCode.Forbidden
    *   InvalidInputAnomaly          StatusCode.BadRequest
    *   Anomalies                    StatusCode.BadRequest
    *   ConflictAnomaly              StatusCode.Conflict
    *   NotImplementedCatastrophe    StatusCode.NotImplemented
    *   Catastrophe                  StatusCode.InternalServerError
    *   Throwable                    StatusCode.InternalServerError
    * }}}
    */
  def phEndpoint: Endpoint[Unit, AnomalyLike, Unit, Any] = {
    implicit val anomalyLikeEnc: json.Encoder[AnomalyLike] = AnomalyJsonCodec.pureharmThrowableCodec.contramap(identity)
    implicit val anomalyLikeDev: json.Decoder[AnomalyLike] = AnomalyJsonCodec.pureharmThrowableCodec.map {
      case e:   AnomalyLike => e
      case thr: Throwable   =>
        UnhandledCatastrophe(s"Unhandled Throwable. This usually signals a bug. ", thr): AnomalyLike
    }

    implicit val anomalyScheme: Schema[AnomalyLike] = PureharmTapirSchemas.pureharmAnomalyTapirSchema.as[AnomalyLike]
    val anomalyBody = tapir.json.circe.jsonBody[AnomalyLike]

    val badRequestMapping = sttp.tapir
      .oneOfMappingValueMatcher(
        statusCode = StatusCode.BadRequest,
        output     = anomalyBody.description(
          "Signals that there's something wrong with user input. Either display in error messages, or ensure that client cannot send bad data. This is the only case where we can have the .messages field in the error."
        ),
      ) {
        case _: InvalidInputAnomaly => true
        case _: Anomalies           => true
        case _ => false
      }

    val conflictMapping = tapir
      .oneOfMappingValueMatcher(
        statusCode = StatusCode.Conflict,
        output     = anomalyBody.description(
          "Signals that user input conflicts w/ current state of the application."
        ),
      ) {
        case _: ConflictAnomaly => true
        case _ => false
      }

    val unauthorizedMapping = tapir
      .oneOfMappingValueMatcher(
        statusCode = StatusCode.Unauthorized,
        output     = anomalyBody.description(
          "Authentication missing, or invalid."
        ),
      ) {
        case _: UnauthorizedAnomaly => true
        case _ => false
      }

    val deniedAndForbiddenMapping = tapir
      .oneOfMappingValueMatcher(
        statusCode = StatusCode.Forbidden,
        output     = anomalyBody.description(
          "User is successfully authenticated, but they do not have specific permission to do this action."
        ),
      ) {
        case _: DeniedAnomaly    => true
        case _: ForbiddenAnomaly => true
        case _ => false
      }

    val notFoundMapping = tapir
      .oneOfMappingValueMatcher(
        statusCode = StatusCode.NotFound,
        output     = anomalyBody.description(
          "Resource or endpoint does not exist."
        ),
      ) {
        case _: NotFoundAnomaly => true
        case _ => false
      }

    val internalServerError = tapir
      .oneOfMappingValueMatcher(
        statusCode = StatusCode.InternalServerError,
        output     = anomalyBody.description(
          "Something went wrong. This always signals a bug. Please report. There's nothing you can do except maybe retry."
        ),
      ) {
        case _: Catastrophe => true
        case _ => false
      }

    val notImplementedError = tapir
      .oneOfMappingValueMatcher(
        statusCode = StatusCode.NotImplemented,
        output     = anomalyBody.description(
          "Feature not yet implemented, please report and or check back in later."
        ),
      ) {
        case _: NotImplementedCatastrophe                                                            => true
        case u: UnhandledCatastrophe if u.causedBy.exists(_.isInstanceOf[scala.NotImplementedError]) => true
        case _ => false
      }

    sttp.tapir.endpoint.errorOut(
      tapir.oneOf[AnomalyLike](
        badRequestMapping,
        unauthorizedMapping,
        deniedAndForbiddenMapping,
        notFoundMapping,
        conflictMapping,
        internalServerError,
        notImplementedError,
      )
    )
  }

}
