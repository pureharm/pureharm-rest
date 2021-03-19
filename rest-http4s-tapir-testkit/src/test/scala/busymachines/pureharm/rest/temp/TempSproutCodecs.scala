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

package busymachines.pureharm.rest.temp

//Unfortunately, providing implicitly such codecs can sometimes degrade compilation by one order of magnitude.
//so it's better to be conservative, and just define them explicitly
object TempSproutCodecs {
  import busymachines.pureharm.rest._

  implicit val safePhuuidThrCodec: TapirPlainCodec[SafePHUUIDThr] = TapirCodec.uuid.sproutRefined[SafePHUUIDThr]
  implicit val phuuidCodec:        TapirPlainCodec[PHUUID]        = TapirCodec.uuid.sprout[PHUUID]
  implicit val myAuthTokenCodec:   TapirPlainCodec[MyAuthToken]   = TapirCodec.long.sprout[MyAuthToken]
  implicit val PHLongCodec:        TapirPlainCodec[PHLong]        = TapirCodec.long.sprout[PHLong]
  implicit val PHHeaderCodec:      TapirPlainCodec[PHHeader]      = TapirCodec.long.sprout[PHHeader]
  implicit val PHIntCodec:         TapirPlainCodec[PHInt]         = TapirCodec.int.sprout[PHInt]

}
