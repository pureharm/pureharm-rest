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

package busymachines.pureharm.endpoint.aliases

/** @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 16 Jul 2020
  */
trait PureharmTapirModelAliases {
  type HasHeaders = sttp.model.HasHeaders

  type Header = sttp.model.Header
  val Header: sttp.model.Header.type = sttp.model.Header

  type HeaderNames = sttp.model.HeaderNames
  val HeaderNames: sttp.model.HeaderNames.type = sttp.model.HeaderNames

  type Headers = sttp.model.Headers
  val Headers: sttp.model.Headers.type = sttp.model.Headers

  type StatusCode = sttp.model.StatusCode
  val StatusCode: sttp.model.StatusCode.type = sttp.model.StatusCode

  type MediaType = sttp.model.MediaType
  val MediaType: sttp.model.MediaType.type = sttp.model.MediaType

  type Part[+T] = sttp.model.Part[T]
  val Part: sttp.model.Part.type = sttp.model.Part

  type Validator[T] = sttp.tapir.Validator[T]
  val Validator: sttp.tapir.Validator.type = sttp.tapir.Validator

  type TapirURI = sttp.model.Uri
  val TapirURI: sttp.model.Uri.type = sttp.model.Uri

  @scala.deprecated("Use TapirUri instead", "0.5.0")
  type Uri = sttp.model.Uri

  @scala.deprecated("Use TapirUri instead", "0.5.0")
  val Uri: sttp.model.Uri.type = sttp.model.Uri

}
