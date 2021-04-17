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

package busymachines.phx.domain.organizer

import busymachines.pureharm.effects._
import busymachines.pureharm.effects.implicits._

import busymachines.phx.domain._
import busymachines.phx.domain.auth._

final class PhxOrganizer[F[_]] private (authStack: PhxAuthStack[F])(implicit F: Sync[F]) {

  private def withAuth[T](f: PhxAuthCtx => F[T])(implicit token: MyAuthToken): F[T] =
    for {
      ctx    <- authStack.authenticate(token)
      result <- f(ctx)
    } yield result

  def getLogic(id: PHUUID)(implicit auth: MyAuthToken): F[MyOutputType] = withAuth { ctx: PhxAuthCtx =>
    for {
      _   <- F.delay(println(s"I AM $ctx"))
      _   <- F.delay(println(s"GET LOGIC HERE — $id"))
      sf6 <- SafePHUUIDThr.generate[F]
    } yield MyOutputType(
      id  = id,
      f1  = PHString.unsafeGenerate,
      f2  = PHInt.unsafeGenerate,
      f3  = PHLong.unsafeGenerate,
      fl  = List(PHLong.unsafeGenerate),
      f4  = List(PHUUID.unsafeGenerate, PHUUID.unsafeGenerate, PHUUID.unsafeGenerate),
      f5  = Option(PHString.unsafeGenerate),
      sf6 = sf6,
    )
  }

  def postLogic(input: MyInputType)(implicit auth: MyAuthToken): F[MyOutputType] =
    for {
      ctx <- authStack.authenticate(auth)
      _   <- F.delay(println(s"I AM $ctx"))
      _   <- Sync[F].delay(println(s"POST LOGIC HERE — $input"))
      id  <- PHUUID.generate[F]
    } yield MyOutputType(
      id  = id,
      f1  = input.f1,
      f2  = input.f2,
      f3  = input.f3,
      fl  = input.fl,
      f4  = input.f4,
      f5  = input.f5,
      sf6 = input.sf6,
    )
}

object PhxOrganizer {

  def resource[F[_]](authStack: PhxAuthStack[F])(implicit F: Sync[F]): Resource[F, PhxOrganizer[F]] =
    new PhxOrganizer[F](authStack).pure[Resource[F, *]]

}
