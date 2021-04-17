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

package busymachines.phx.util

import busymachines.pureharm.{endpoint => phend}

package object endpoint extends phend.PureharmEndpointAllTypeAliases with phend.PureharmEndpointAllImplicits {
  /** The base endpoint in all our apps
    */
  val testEndpoint: SimpleEndpoint[Unit, Unit] = phend.PureharmTapirEndpoint.phEndpoint

  import busymachines.phx.domain.MyAuthToken

  //----- Create a unified way of doing auth, you can also add in common logic. See tapir docs for that
  //using this over Authentication to double as CSRF token as well
  val authedEndpoint: SimpleEndpoint[MyAuthToken, Unit] = {
    testEndpoint.in(auth.xCustomAuthHeader[MyAuthToken](CustomHeaders.`X-AUTH-TOKEN`.toString))
  }
}
