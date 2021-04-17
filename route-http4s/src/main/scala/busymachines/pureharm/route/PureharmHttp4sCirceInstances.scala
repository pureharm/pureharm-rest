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

package busymachines.pureharm.route

import busymachines.pureharm.effects._
import busymachines.pureharm.json._
import io.circe.Printer
import org.http4s._
import org.http4s.headers.`Content-Type`

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 26 Jun 2018
  */
trait PureharmHttp4sCirceInstances {
  import PureharmHttp4sCirceInstances._

  implicit def pureharmHttps4sEntityJsonEncoder[F[_], T: Encoder]: EntityEncoder[F, T] =
    EntityEncoder[F, Chunk[Byte]]
      .contramap[Json] { json =>
        val bytes = printer.printToByteBuffer(json)
        Chunk.byteBuffer(bytes)
      }
      .withContentType(`Content-Type`(org.http4s.MediaType.application.json))
      .contramap(t => Encoder[T].apply(t))
}

object PureharmHttp4sCirceInstances {
  private val printer: Printer = Printer.noSpaces.copy(dropNullValues = true)

}
