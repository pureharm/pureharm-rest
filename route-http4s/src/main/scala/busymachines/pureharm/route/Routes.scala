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

import busymachines.pureharm.anomaly.AnomalyLike
import busymachines.pureharm.effects._
import org.http4s.HttpRoutes
import sttp.capabilities.WebSockets
import sttp.capabilities.fs2.Fs2Streams
import sttp.tapir.Endpoint
import sttp.tapir.server.http4s.Http4sServerOptions.Log
import sttp.tapir.server.http4s.{Http4sServerInterpreter, Http4sServerOptions}
import sttp.tapir.server.interceptor.decodefailure.{DecodeFailureHandler, DefaultDecodeFailureHandler}
import sttp.tapir.server.interceptor.exception.{DefaultExceptionHandler, ExceptionHandler}
import sttp.tapir.server.interceptor.log.ServerLog

/** Extend this capability trait to convert tapir endpoints to HttpRoutes instead of propagating the Async constraint
  * everywhere.
  *
  * Define your own in your own app, with the desired ``Http4sServerOptions``
  */
trait Routes[F[_]] {
  val dsl: org.http4s.dsl.Http4sDsl[F] = org.http4s.dsl.Http4sDsl[F]

  implicit protected def F: Async[F]

  protected def http4sServerOptions: Http4sServerOptions[F, F] = _defaultServerOps
  private lazy val _defaultServerOps = customServerOpsHelper()

  /** Convenience method to easily plug in a logger, mostly.
    *
    * Just inline and use tapir's full customization capabilities if needed
    */
  protected def customServerOpsHelper(
    exceptionHandler:     Option[ExceptionHandler]   = Option(DefaultExceptionHandler.handler),
    serverLog:            Option[ServerLog[F[Unit]]] = Option(Log.defaultServerLog[F]),
    decodeFailureHandler: DecodeFailureHandler       = DefaultDecodeFailureHandler.handler,
  ): Http4sServerOptions[F, F] =
    Http4sServerOptions
      .customInterceptors[F, F]
      .serverLog(serverLog)
      .exceptionHandler(exceptionHandler)
      .decodeFailureHandler(decodeFailureHandler)
      .options

  protected def http4sServer: Http4sServerInterpreter[F] = Http4sServerInterpreter[F](http4sServerOptions)

  def fromEndpoint[I, O](
    e: Endpoint[I, AnomalyLike, O, Fs2Streams[F] with WebSockets]
  )(f: I => F[O]): HttpRoutes[F] =
    http4sServer.toRouteRecoverErrors[I, AnomalyLike, O](e)(f)
}
