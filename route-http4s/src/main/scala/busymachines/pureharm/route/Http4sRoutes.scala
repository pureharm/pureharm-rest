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

package busymachines.pureharm.route

import busymachines.pureharm.effects.{BlockingShifter, Concurrent, ContextShift, Timer}
import org.http4s.HttpRoutes
import sttp.capabilities.WebSockets
import sttp.capabilities.fs2.Fs2Streams
import sttp.tapir.Endpoint
import sttp.tapir.server.http4s.{Http4sServerInterpreter, Http4sServerOptions}

trait Http4sRoutes[F[_], ET <: Concurrent[F], RT <: Http4sRuntime[F, ET]] {

  protected def http4sRuntime: RT
  implicit protected def F:               ET                 = http4sRuntime.F
  implicit protected def blockingShifter: BlockingShifter[F] = http4sRuntime.blockingShifter
  implicit protected def contextShift:    ContextShift[F]    = http4sRuntime.contextShift
  implicit protected def timer:           Timer[F]           = http4sRuntime.timer

  implicit protected def tapirHttp4Ops: Http4sServerOptions[F]  = http4sRuntime.http4sServerOptions
  implicit protected val http4sServer:  Http4sServerInterpreter = Http4sServerInterpreter

  protected def fromEndpoint[I, O](
    e: Endpoint[I, Throwable, O, Fs2Streams[F] with WebSockets]
  )(f: I => F[O]): HttpRoutes[F] =
    http4sServer.toRouteRecoverErrors[I, Throwable, O, F](e)(f)
}
