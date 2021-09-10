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

package busymachines.pureharm

import busymachines.pureharm.effects.Concurrent

package object route {

  @scala.deprecated("Use Http4sRoutes instead. Otherwise it's source compatible", "0.5.0")
  type RestDefs[F[_], ET <: Concurrent[F], RT <: Http4sRuntime[F, ET]] =
    busymachines.pureharm.route.Http4sRoutes[F, ET, RT]

  @scala.deprecated("Use PureharmRouteTypeAliases instead", "0.5.0")
  type PureharmRestHttp4sTypeAliases = PureharmRouteTypeAliases
}
