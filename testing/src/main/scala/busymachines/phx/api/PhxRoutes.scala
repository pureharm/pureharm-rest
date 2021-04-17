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

package busymachines.phx.api

import busymachines.pureharm.effects._
import busymachines.pureharm.effects.implicits._
import busymachines.phx.util.route._

import busymachines.phx.domain._
import busymachines.phx.domain.organizer.PhxOrganizer

final class PhxRoutes[F[_]](phxOrganizer: PhxOrganizer[F])(implicit rt: PhxHttp4sRuntime[F])
  extends PhxHttp4sRoutes[F] {

  val nonAuthedGetRoute: HttpRoutes[F] = fromEndpoint(PhxEndpoints.nonAuthedGetEndpoint) { _ =>
    for {
      rid    <- PHUUID.generate[F]
      rtoken <- MyAuthToken.generate[F]
      result <- phxOrganizer.getLogic(rid)(rtoken)
    } yield result
  }

  val nonAuthedPostRoute: HttpRoutes[F] = fromEndpoint(PhxEndpoints.nonAuthedPostEndpoint) { myInputType: MyInputType =>
    for {
      rtoken <- MyAuthToken.generate[F]
      result <- phxOrganizer.postLogic(myInputType)(rtoken)
    } yield result
  }

  val testGetRoute: HttpRoutes[F] = fromEndpoint(PhxEndpoints.testGetEndpoint) { t: (MyAuthToken, PHUUID) =>
    val (auth, ph) = t
    for {
      result <- phxOrganizer.getLogic(ph)(auth)
    } yield result
  }

  val testGetEndpointQueryParamsRoute: HttpRoutes[F] =
    fromEndpoint(PhxEndpoints.testGetEndpointQueryParams) { t: (MyAuthToken, PHUUID, PHLong, Option[PHInt]) =>
      val (auth, ph, longParam, intOpt) = t
      for {
        _      <- F.delay[Unit](println(s"params: $longParam --- $intOpt"))
        result <- phxOrganizer.getLogic(ph)(auth)
      } yield result
    }

  val testGetWithHeaderRoute: HttpRoutes[F] =
    fromEndpoint(PhxEndpoints.testGetWithHeaderEndpoint) { t: (MyAuthToken, PHUUID, PHHeader) =>
      val (auth, ph, header) = t
      for {
        _      <- F.delay[Unit](println(s"header: $header"))
        result <- phxOrganizer.getLogic(ph)(auth)
      } yield result
    }

  val testPostRoute: HttpRoutes[F] = fromEndpoint(PhxEndpoints.testPostEndpoint) { t: (MyAuthToken, MyInputType) =>
    val (auth, myInputType) = t
    for {
      _      <- F.delay[Unit](println(s"testPostRoute"))
      result <- phxOrganizer.postLogic(myInputType)(auth)
    } yield result

  }

  val routes: HttpRoutes[F] = NEList
    .of[HttpRoutes[F]](
      nonAuthedGetRoute,
      nonAuthedPostRoute,
      testGetRoute,
      testGetEndpointQueryParamsRoute,
      testGetWithHeaderRoute,
      testPostRoute,
    )
    .reduceK

}

object PhxRoutes {

  def resource[F[_]](
    domain:     PhxOrganizer[F]
  )(implicit F: Monad[F], rt: PhxHttp4sRuntime[F]): Resource[F, PhxRoutes[F]] =
    new PhxRoutes[F](domain).pure[Resource[F, *]]
}
