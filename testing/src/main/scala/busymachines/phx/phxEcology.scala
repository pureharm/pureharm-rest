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

import busymachines.pureharm.effects._
import busymachines.pureharm.effects.implicits._
import busymachines.phx.util.route._
import busymachines.phx.api.PhxRoutes
import busymachines.phx.domain.auth.PhxAuthStack
import busymachines.phx.domain.organizer.PhxOrganizer
import busymachines.pureharm.effects.pools.Pools
import org.http4s.server.Router

final case class PhxEcology[F[_]](
  capabilities:           PhxCapabilities[F],
  domainLogic:            PhxDomainLogic[F],
  restAPIs:               PhxRestAPI[F],
  http4sApp:              HttpApp[F],
)(implicit private val F: Async[F]) {

  def bindHttp4sServer: Resource[F, Unit] = {
    import org.http4s.blaze.server._

    BlazeServerBuilder[F](capabilities.blazeEC)
      .bindHttp(12345, "0.0.0.0")
      .withHttpApp(http4sApp)
      .resource
      .void

  }
}

final class PhxCapabilities[F[_]](
  val blazeEC: ExecutionContext
)(implicit
  val routes:  Routes[F],
  val console: Console[F],
  val random:  CERandom[F],
  val uuidGen: UUIDGen[F],
)

final case class PhxDomainLogic[F[_]](
  authStack:    PhxAuthStack[F],
  phxOrganizer: PhxOrganizer[F],
)

final case class PhxRestAPI[F[_]](
  phxRoutes: PhxRoutes[F]
)

object PhxEcology {

  def ecology[F[_]: Async]: Resource[F, PhxEcology[F]] =
    this.capabilities[F].flatMap { capabilities =>
      import capabilities._
      for {
        domainLogic <- this.domainLogic[F]
        restAPIs    <- this.restAPIs[F](domainLogic)
        http4sApp   <- this.http4sApp[F](restAPIs)
      } yield PhxEcology[F](
        capabilities = capabilities,
        domainLogic  = domainLogic,
        restAPIs     = restAPIs,
        http4sApp    = http4sApp,
      )
    }

  def capabilities[F[_]: Async]: Resource[F, PhxCapabilities[F]] =
    for {
      routes  <- Routes[F].pure[Resource[F, *]]
      blazeEC <- Pools.cached[F](threadNamePrefix = "blaze-pool")
    } yield new PhxCapabilities[F](blazeEC = blazeEC)(
      routes                               = routes,
      console                              = Console.make[F],
      random                               = CERandom.javaUtilConcurrentThreadLocalRandom[F],
      uuidGen                              = UUIDGen.fromSync[F],
    )

  def domainLogic[F[_]: MonadThrow: CERandom: UUIDGen: Console]: Resource[F, PhxDomainLogic[F]] =
    for {
      phxAuth <- PhxAuthStack.resource[F]
      phxOrg  <- PhxOrganizer.resource[F](phxAuth)
    } yield PhxDomainLogic(
      authStack    = phxAuth,
      phxOrganizer = phxOrg,
    )

  private def restAPIs[F[_]: MonadThrow: CERandom: UUIDGen: Routes: Console](
    domain: PhxDomainLogic[F]
  ): Resource[F, PhxRestAPI[F]] =
    for {
      phxRoutes <- PhxRoutes.resource[F](domain.phxOrganizer)
    } yield PhxRestAPI[F](
      phxRoutes = phxRoutes
    )

  private def http4sApp[F[_]: Monad](apis: PhxRestAPI[F]): Resource[F, HttpApp[F]] = {
    val routes: HttpRoutes[F] = NEList
      .of(
        apis.phxRoutes.http4sRoutes
      )
      .reduceK

    Router("/api" -> routes).orNotFound.pure[Resource[F, *]]
  }
}
