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

package busymachines.phx

import busymachines.phx.docs.PhxDocs
import busymachines.pureharm.effects._

object PhxMain extends ResourceApp.Forever {

  override def run(args: List[String]): Resource[IO, Unit] = serverResource[IO]

  private def serverResource[F[_]: Async]: Resource[F, Unit] =
    for {
      ecology <- PhxEcology.ecology[F]
      _       <- Resource.eval(PhxDocs.printDocs[F])
      _       <- ecology.bindHttp4sServer
    } yield ()

}
