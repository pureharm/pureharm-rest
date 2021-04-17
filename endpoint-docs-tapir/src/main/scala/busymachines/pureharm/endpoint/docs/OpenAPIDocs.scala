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

package busymachines.pureharm.endpoint.docs

import cats.Foldable
import sttp.tapir.Endpoint

object OpenAPIDocs {

  def fromEndpoints[F[_]](
    es:           F[Endpoint[_, _, _, _]],
    info:         Info,
    servers:      Server*
  )(implicit Fld: Foldable[F]): String = {
    import sttp.tapir.docs.openapi._
    import sttp.tapir.openapi.circe.yaml._

    OpenAPIDocsInterpreter
      .toOpenAPI(Fld.toList(es), info)
      .servers(servers.toList)
      .toYaml
  }

  def fromEndpoints[F[_]](
    es:           F[Endpoint[_, _, _, _]],
    title:        String,
    version:      String,
    description:  String,
    servers:      Server*
  )(implicit Fld: Foldable[F]): String =
    this.fromEndpoints[F](
      es,
      info = Info(
        title       = title,
        version     = version,
        description = Option(description),
      ),
      servers: _*
    )
}
