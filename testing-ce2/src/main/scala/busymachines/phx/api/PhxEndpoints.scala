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

import busymachines.phx.domain._
import busymachines.phx.util.endpoint._

object PhxEndpoints {

  val nonAuthedGetEndpoint: SimpleEndpoint[Unit, MyOutputType] =
    testEndpoint.get
      .in("no_auth")
      .out(jsonBody[MyOutputType])
      .out(statusCode(StatusCode.Ok))

  val nonAuthedPostEndpoint: SimpleEndpoint[MyInputType, MyOutputType] =
    testEndpoint.post
      .in("no_auth")
      .in(jsonBody[MyInputType])
      .out(jsonBody[MyOutputType])
      .out(statusCode(StatusCode.Created))

  val testGetEndpoint: SimpleEndpoint[(MyAuthToken, PHUUID), MyOutputType] =
    authedEndpoint.get
      .in("test" / path[PHUUID])
      .out(jsonBody[MyOutputType])
      .out(statusCode(StatusCode.Ok))

  val testGetEndpointSafePHUUIDThr: SimpleEndpoint[(MyAuthToken, SafePHUUIDThr), MyOutputType] =
    authedEndpoint.get
      .in("test_safe_thr" / path[SafePHUUIDThr])
      .out(jsonBody[MyOutputType])
      .out(statusCode(StatusCode.Ok))

  val testPostEndpoint: SimpleEndpoint[(MyAuthToken, MyInputType), MyOutputType] =
    authedEndpoint.post
      .in("test")
      .in(jsonBody[MyInputType])
      .out(jsonBody[MyOutputType])
      .out(statusCode(StatusCode.Created))

  val testGetEndpointQueryParams: SimpleEndpoint[(MyAuthToken, PHUUID, PHLong, Option[PHInt]), MyOutputType] =
    authedEndpoint.get
      .in("test_q" / path[PHUUID])
      .in(query[PHLong]("long"))
      .in(query[Option[PHInt]]("opt_int"))
      .out(jsonBody[MyOutputType])
      .out(statusCode(StatusCode.Ok))

  val testGetWithHeaderEndpoint: SimpleEndpoint[(MyAuthToken, PHUUID, PHHeader), MyOutputType] =
    authedEndpoint.get
      .in("test_h" / path[PHUUID])
      .in(header[PHHeader]("X-TEST-HEADER"))
      .out(jsonBody[MyOutputType])
      .out(statusCode(StatusCode.Ok))

  val endpoints: List[Endpoint[_, _, _, _]] = List(
    nonAuthedGetEndpoint,
    nonAuthedPostEndpoint,
    testGetEndpoint,
    testGetEndpointSafePHUUIDThr,
    testPostEndpoint,
    testGetEndpointQueryParams,
    testGetWithHeaderEndpoint,
  )
}
