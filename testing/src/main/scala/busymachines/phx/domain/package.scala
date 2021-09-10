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

import java.util.UUID

import busymachines.pureharm.sprout._
import busymachines.pureharm.effects._
import busymachines.pureharm.effects.implicits._

/** @author
  *   Lorand Szakacs, https://github.com/lorandszakacs
  * @since 10
  *   Jul 2020
  */
package object domain {

  type PHString = PHString.Type

  object PHString extends Sprout[String] {
    def unsafeGenerate: PHString = this.apply(scala.util.Random.nextString(10))

    def generate[F[_]: Applicative](implicit random: CERandom[F]): F[PHString] =
      random.nextString(10).map(this.apply)
  }

  type PHLong = PHLong.Type

  object PHLong extends Sprout[Long] {
    def unsafeGenerate: PHLong = this.apply(scala.util.Random.between(1000L, 9000L))

    def generate[F[_]: Applicative](implicit random: CERandom[F]): F[PHLong] =
      random.betweenLong(100L, 9000L).map(this.apply)
  }

  type PHInt = PHInt.Type

  object PHInt extends Sprout[Int] {
    def unsafeGenerate: PHInt = this.apply(scala.util.Random.between(100, 900))

    def generate[F[_]: Applicative](implicit random: CERandom[F]): F[PHInt] =
      random.betweenInt(100, 900).map(this.apply)
  }

  type PHUUID = PHUUID.Type

  object PHUUID extends SproutSub[UUID] {
    def unsafeGenerate: PHUUID = this.apply(UUID.randomUUID())
    def generate[F[_]: Applicative](implicit uuid: UUIDGen[F]): F[PHUUID] = uuid.randomUUID.map(this.apply)
  }

  type MyAuthToken = MyAuthToken.Type

  object MyAuthToken extends Sprout[Long] {
    def unsafeGenerate: MyAuthToken = this.apply(scala.util.Random.between(1000L, 9000L))

    def generate[F[_]: Applicative](implicit random: CERandom[F]): F[MyAuthToken] =
      random.betweenLong(100L, 9000L).map(this.apply)
  }

  type PHHeader = PHHeader.Type

  object PHHeader extends Sprout[Long] {
    def unsafeGenerate: PHHeader = this.apply((scala.util.Random.nextLong() % 9000L) + 1000L)

    def generate[F[_]: Applicative](implicit random: CERandom[F]): F[PHHeader] =
      random.betweenLong(100L, 9000L).map(this.apply)
  }

  type SafePHUUIDThr = SafePHUUIDThr.Type

  object SafePHUUIDThr extends SproutRefinedThrow[UUID] {
    override def refine[F[_]](o: UUID)(implicit m: MonadThrow[F]): F[UUID] = o.pure[F]

    def generate[F[_]: MonadThrow](implicit uuid: UUIDGen[F]): F[SafePHUUIDThr] =
      uuid.randomUUID.flatMap(this.apply[F])
  }
}
