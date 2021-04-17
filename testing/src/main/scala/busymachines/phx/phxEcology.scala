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

package busymachines.phx

import busymachines.pureharm.effects._
import busymachines.pureharm.effects.implicits._
import busymachines.phx.util.route._
import busymachines.phx.api.PhxRoutes
import busymachines.phx.domain.auth.PhxAuthStack
import busymachines.phx.domain.organizer.PhxOrganizer
import busymachines.pureharm.effects.pools.Pools
import org.http4s.server.Router

final case class PhxEcology[F[_]](
  capabilities: PhxCapabilities[F],
  domainLogic:  PhxDomainLogic[F],
  restAPIs:     PhxRestAPI[F],
  http4sApp:    HttpApp[F],
)

final class PhxCapabilities[F[_]](
  implicit val concurrentEffect: ConcurrentEffect[F],
  implicit val timer:            Timer[F],
  implicit val contextShift:     ContextShift[F],
  implicit val blockingShifter:  BlockingShifter[F],
  implicit val http4sRuntime:    PhxHttp4sRuntime[F],
) {
  val blocker: Blocker = blockingShifter.blocker
}

final case class PhxDomainLogic[F[_]](
  authStack:    PhxAuthStack[F],
  phxOrganizer: PhxOrganizer[F],
)

final case class PhxRestAPI[F[_]](
  phxRoutes: PhxRoutes[F]
)

object PhxEcology {

  @scala.annotation.nowarn
  def ecology[F[_]: ConcurrentEffect: ContextShift: Timer]: Resource[F, PhxEcology[F]] =
    for {
      capabilities <- this.capabilities[F]
      implicit0(rt: PhxHttp4sRuntime[F]) = capabilities.http4sRuntime
      domainLogic  <- this.domainLogic[F]

      restAPIs  <- this.restAPIs[F](domainLogic)
      http4sApp <- this.http4sApp[F](restAPIs)
    } yield PhxEcology[F](
      capabilities = capabilities,
      domainLogic  = domainLogic,
      restAPIs     = restAPIs,
      http4sApp    = http4sApp,
    )

  def capabilities[F[_]: ConcurrentEffect: ContextShift: Timer]: Resource[F, PhxCapabilities[F]] =
    for {
      blockingPool <- Pools.cached("phx-blocking")
      blockingShifter = BlockingShifter.fromExecutionContext[F](blockingPool)
      phxHttp4s       = PhxHttp4sRuntime[F](blockingShifter)
    } yield new PhxCapabilities[F]()(
      concurrentEffect = ConcurrentEffect[F],
      timer            = Timer[F],
      contextShift     = ContextShift[F],
      blockingShifter  = blockingShifter,
      http4sRuntime    = phxHttp4s,
    )

  def domainLogic[F[_]: Sync]: Resource[F, PhxDomainLogic[F]] =
    for {
      phxAuth <- PhxAuthStack.resource[F]
      phxOrg  <- PhxOrganizer.resource[F](phxAuth)
    } yield PhxDomainLogic(
      authStack    = phxAuth,
      phxOrganizer = phxOrg,
    )

  def restAPIs[F[_]: Monad: PhxHttp4sRuntime](domain: PhxDomainLogic[F]): Resource[F, PhxRestAPI[F]] =
    for {
      phxRoutes <- PhxRoutes.resource[F](domain.phxOrganizer)
    } yield PhxRestAPI[F](
      phxRoutes = phxRoutes
    )

  def http4sApp[F[_]: Monad](apis: PhxRestAPI[F]): Resource[F, HttpApp[F]] = {
    val routes: HttpRoutes[F] = NEList
      .of(
        apis.phxRoutes.routes
      )
      .reduceK

    Router("/api" -> routes).orNotFound.pure[Resource[F, *]]
  }
}
