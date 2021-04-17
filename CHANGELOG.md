# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

# unreleased

### :warning: breaking changes :warning

#### remodularize everything

Mostly source compatible, depending on all modules should allow you to use
the library as before.

- `"com.busymachines" %% "pureharm-endpoint-tapir"`
  - now contains everything `tapir` related needed to define `Endpoint`s, sans dependency on `http4s`
- `"com.busymachines" %% "pureharm-endpoint-docs-tapir"`
  - contains a simple helper method to print out YAML representation of OpenAPI
- `"com.busymachines" %% "pureharm-route-http4s-tapir"`
  - contains everything `http4s` related, and required to transform `tapir` endpoints into `http4s` routes
- `"com.busymachines" %% "pureharm-server-http4s"`
  - depends on `http4s-blaze-server`, contains helpers to run the server and bind ports, etc.

#### source changes

- remove `PureharmHttp4sCirceInstances.pureharmHttps4sEntityJsonDecoder`, and drop dependency on `http4s-circe` because it was essentially unused.
- remove `Http4sRoutes`, `RestDefs` from `PureharmRouteTypeAliases` (formerly `PureharmRestHttp4sTypeAliases`), see deprecations section

#### deprecations

- deprecate `PureharmRestHttp4sTypeAliases` in favor of `PureharmRouteTypeAliases`
- deprecate `RestDefs` in favor of `Http4sRoutes`, they are source compatible.

### new features

- add `PureharmRouteAllImplicits` trait to `pureharm-route-http4s-tapir`
- add `fromEnpoint` helper function in `RestDefs`
- add `PureharmEndpointAllTypeAliases`, `PureharmEndpointAllImplicits` mixin traits to `pureharm-endpoint-tapir`

# 0.4.0

### dependency upgrades

- [pureharm-testkit](https://github.com/busymachines/pureharm-testkit/releases) `0.3.0`
- [pureharm-effects-cats](https://github.com/busymachines/pureharm-effects-cats/releases) `0.4.0`

# 0.3.0

### dependency upgrades

- [http4s](https://github.com/http4s/http4s/releases) `0.21.21`
- [pureharm-core-anomaly](https://github.com/busymachines/pureharm-core/releases) `0.2.0`
- [pureharm-core-sprout](https://github.com/busymachines/pureharm-core/releases) `0.2.0`
- [pureharm-effects-cats](https://github.com/busymachines/pureharm-effects-cats/releases) `0.2.0`
- [pureharm-json-circe](https://github.com/busymachines/pureharm-json-circe/releases) `0.2.0`
- [pureharm-testkit](https://github.com/busymachines/pureharm-testkit/releases) `0.2.0`

# 0.2.0

- add implicit resolution for tapir `PlainCodec`s for `Sprout`, and `SproutRefinedThrow` types.
  previously these have been removed because compilation was exponentially bad, and inference bad
  as well. Turns out, the trick was to put the `NewType` instance first in the implicit parameter list,
  not second to greatly improve type inference. Which makes sense, there is only one such instance,
  while there could be many other `PlainCodec` ones.

  ```scala
    implicit def pureharmSproutTypeGenericPlainCodec[Old, New](implicit
    p: NewType[Old, New], // here
    c: tapir.Codec.PlainCodec[Old], //not here
  ): tapir.Codec.PlainCodec[New] = c.map[New](p.newType _)(p.oldType)
  ```

Version upgrades:

- bump `pureharm-json-circe` to `0.1.1`

# 0.1.0

Split out from [pureharm](https://github.com/busymachines/pureharm) as of version `0.0.7`.

### dependency upgrades

- upgrade tapir to `0.17.19`
- upgrade to pureharm `0.1.0` series + drop phantom type support.
