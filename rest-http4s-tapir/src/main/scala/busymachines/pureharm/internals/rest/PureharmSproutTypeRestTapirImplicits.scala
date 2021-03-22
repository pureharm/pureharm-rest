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

  implicit def pureharmSproutTypeGenericPlainCodec[Old, New](implicit
    p: NewType[Old, New],
    c: tapir.Codec.PlainCodec[Old],
  ): tapir.Codec.PlainCodec[New] = c.map[New](p.newType _)(p.oldType)

  implicit def pureharmSproutRefinedTypePlainCodec[Old, New](implicit
    p: RefinedTypeThrow[Old, New],
    c: tapir.Codec.PlainCodec[Old],
  ): tapir.Codec.PlainCodec[New] = c.sproutRefined[New]

  implicit def pureharmSproutTypeGenericSchema[Old, New: OldType[Old, *]](implicit
    sc: tapir.Schema[Old]
  ): tapir.Schema[New] =
    sc.copy(description = sc.description match {
      case None           => Option(OldType[Old, New].symbolicName)
      case Some(original) => Option(s"$original — type name: ${OldType[Old, New].symbolicName}")
    }).asInstanceOf[tapir.Schema[New]]

  //---------------------------------------------------------------------------

  implicit def pureharmSproutTypeGenericValidator[Old, PT: OldType[Old, *]](implicit
    sc: tapir.Validator[Old]
  ): tapir.Validator[PT] = sc.contramap(OldType[Old, PT].oldType)

  /** Basically, it's union of the schema of AnomalyBase and AnomaliesBase,
    * + any non-anomaly throwable is being wrapped in an UnhandledAnomaly
    */
  implicit val tapirSchemaThrowableAnomaly: tapir.Schema[Throwable] = PureharmTapirSchemas.tapirSchemaAnomalies

  implicit def pureharmTapirAuthOps(o: tapir.TapirAuth.type): TapirOps.AuthOps = new TapirOps.AuthOps(o)

  implicit def pureharmTapirCodecOps[Old](c: sttp.tapir.Codec.PlainCodec[Old]): TapirOps.CodecOps[Old] =
    new TapirOps.CodecOps[Old](c)
}
