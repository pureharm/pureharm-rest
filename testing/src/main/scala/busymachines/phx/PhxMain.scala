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
import busymachines.pureharm.effects.implicits._

object PhxMain extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = runF[IO]

  private def runF[F[_]: ConcurrentEffect: Timer: ContextShift]: F[ExitCode] =
    this.serverResource[F].use(serverStream => serverStream.compile.toList.map(_.headOption.getOrElse(ExitCode.Error)))

  private def serverResource[F[_]: ConcurrentEffect: Timer: ContextShift]: Resource[F, Stream[F, ExitCode]] =
    for {
      ecology <- PhxEcology.ecology[F]
      _       <- Resource.eval(PhxDocs.printDocs[F])
      serverStream = runServer[F](ecology)
    } yield serverStream

  private def runServer[F[_]: ConcurrentEffect: Timer](ecology: PhxEcology[F]): Stream[F, ExitCode] = {
    import org.http4s.server.blaze._

    BlazeServerBuilder[F](ecology.capabilities.blocker.blockingContext)
      .bindHttp(12345, "0.0.0.0")
      .withHttpApp(ecology.http4sApp)
      .serve
  }
}
