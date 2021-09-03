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

import org.http4s
import sttp.tapir.server.{http4s => thttp4s}

/** @author
  *   Lorand Szakacs, https://github.com/lorandszakacs
  * @since 10
  *   Jul 2020
  */
trait PureharmRouteTypeAliases {
  type HttpApp[F[_]] = http4s.HttpApp[F]
  val HttpApp: http4s.HttpApp.type = http4s.HttpApp

  type HttpRoutes[F[_]] = http4s.HttpRoutes[F]
  val HttpRoutes: http4s.HttpRoutes.type = http4s.HttpRoutes

  type Http4sDsl[F[_]] = http4s.dsl.Http4sDsl[F]
  val Http4sDsl: http4s.dsl.Http4sDsl.type = http4s.dsl.Http4sDsl

  type Http4sServerOptions[F[_]] = thttp4s.Http4sServerOptions[F]
  val Http4sServerOptions: thttp4s.Http4sServerOptions.type = thttp4s.Http4sServerOptions

  val Router: org.http4s.server.Router.type = org.http4s.server.Router
}
