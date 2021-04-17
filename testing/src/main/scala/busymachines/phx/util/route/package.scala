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

import busymachines.pureharm.route
import org.http4s.syntax

package object route
  extends route.PureharmRestHttp4sTypeAliases with syntax.AllSyntax with syntax.KleisliSyntaxBinCompat0
  with syntax.KleisliSyntaxBinCompat1 {

  // TODO: move to aliases
  val Router: org.http4s.server.Router.type = org.http4s.server.Router
}
