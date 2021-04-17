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

package busymachines.phx.docs

import busymachines.pureharm.effects._
import busymachines.pureharm.effects.implicits._
import busymachines.pureharm.endpoint.docs._
import busymachines.phx.api.PhxEndpoints

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 13 Jul 2020
  */
object PhxDocs {

  def printDocs[F[_]: Sync]: F[Unit] = {
    val docs = OpenAPIDocs.fromEndpoints(
      PhxEndpoints.endpoints,
      title       = "PHX API w/ tapir and http4s",
      version     = "0.4",
      description = "Just a test API we use to ensure the library works properly",
      Server("http://localhost:12345/api").description("localhost server"),
    )
    Sync[F].delay(
      println(
        s"""|-------------
            |PHX api docs:
            |
            |$docs
            |
            |-------------
            |""".stripMargin
      )
    )

  }
}
