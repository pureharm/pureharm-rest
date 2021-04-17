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

package busymachines.phx.util.route

import busymachines.pureharm.route
import busymachines.phx.util.endpoint._
import busymachines.pureharm.effects._

//----- Create one of these for your app
final class PhxHttp4sRuntime[F[_]](
  override val blockingShifter: BlockingShifter[F]
)(implicit
  override val F:               Concurrent[F],
  override val timer:           Timer[F],
) extends route.Http4sRuntime[F, Concurrent[F]] {

  override val http4sServerOptions: Http4sServerOptions[F] =
    super.http4sServerOptions.withCustomHeaderAuthValidation(CustomHeaders.`X-AUTH-TOKEN`.toString)
}

object PhxHttp4sRuntime {

  def apply[F[_]](
    blockingShifter: BlockingShifter[F]
  )(implicit f:      Concurrent[F], t: Timer[F]): PhxHttp4sRuntime[F] =
    new PhxHttp4sRuntime(blockingShifter)
}
