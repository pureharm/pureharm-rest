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

package busymachines.pureharm.endpoint

import sttp.tapir.openapi

package object docs {
  type Server = openapi.Server
  val Server: openapi.Server.type = openapi.Server

  type Info = openapi.Info
  val Info: openapi.Info.type = openapi.Info

  type Contact = openapi.Contact
  val Contact: openapi.Contact.type = openapi.Contact

  type License = openapi.License
  val License: openapi.License.type = openapi.License

  type ServerVariable = openapi.ServerVariable
  val ServerVariable: openapi.ServerVariable.type = openapi.ServerVariable

  type OpenAPI = openapi.OpenAPI
  val OpenAPI: openapi.OpenAPI.type = openapi.OpenAPI
}