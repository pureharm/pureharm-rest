/*
 * Copyright 2019 BusyMachines
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

package busymachines.pureharm.internals.rest

import busymachines.pureharm.sprout._
import sttp.tapir

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 10 Jul 2020
  */
trait PureharmSproutTypeRestTapirImplicits extends sttp.tapir.json.circe.TapirJsonCirce {

  implicit def pureharmSproutTypeGenericSchema[Underlying, New: OldType[Underlying, *]](implicit
    sc: tapir.Schema[Underlying]
  ): tapir.Schema[New] =
    sc.copy(description = sc.description match {
      case None           => Option(OldType[Underlying, New].symbolicName)
      case Some(original) => Option(s"$original — type name: ${OldType[Underlying, New].symbolicName}")
    }).asInstanceOf[tapir.Schema[New]]

  //---------------------------------------------------------------------------

  implicit def pureharmSproutTypeGenericValidator[Underlying, PT: OldType[Underlying, *]](implicit
    sc: tapir.Validator[Underlying]
  ): tapir.Validator[PT] = sc.contramap(OldType[Underlying, PT].oldType)

  /** Basically, it's union of the schema of AnomalyBase and AnomaliesBase,
    * + any non-anomaly throwable is being wrapped in an UnhandledAnomaly
    */
  implicit val tapirSchemaThrowableAnomaly: tapir.Schema[Throwable] = PureharmTapirSchemas.tapirSchemaAnomalies

  implicit def pureharmTapirAuthOps(o: tapir.TapirAuth.type): TapirOps.AuthOps = new TapirOps.AuthOps(o)

  implicit def pureharmTapirCodecOps[Old](c: sttp.tapir.Codec.PlainCodec[Old]): TapirOps.CodecOps[Old] =
    new TapirOps.CodecOps[Old](c)
}
