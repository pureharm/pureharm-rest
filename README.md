# pureharm-rest

See [changelog](./CHANGELOG.md).

We do not even pretend to support anything other than Postgresql.

## modules

- `"com.busymachines" %% s"pureharm-rest-http4s-tapir" % "0.3.0"`. Which has these as its main dependencies:
  - [http4s](https://github.com/http4s/http4s/releases) `0.21.21`
  - [tapir](https://github.com/softwaremill/tapir/releases) `0.17.19`
  - [pureharm-core-anomaly](https://github.com/busymachines/pureharm-core/releases) `0.2.0`
  - [pureharm-core-sprout](https://github.com/busymachines/pureharm-core/releases) `0.2.0`
  - [pureharm-effects-cats](https://github.com/busymachines/pureharm-effects-cats/releases) `0.2.0`
  - [pureharm-json-circe](https://github.com/busymachines/pureharm-json-circe/releases) `0.2.0`
- `"com.busymachines" %% s"pureharm-rest-http4s-tapir-testkit" % "0.3.0"`. Which has these as its main dependencies:
  - [pureharm-testkit](https://github.com/busymachines/pureharm-testkit/releases) `0.2.0`

## usage

Under construction. See [release notes](https://github.com/busymachines/pureharm-db-flyway/releases) and tests for examples.

## Copyright and License

All code is available to you under the Apache 2.0 license, available
at [http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0) and also in
the [LICENSE](./LICENSE) file.
