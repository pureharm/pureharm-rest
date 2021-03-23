# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

# unreleased

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

Upgrades:

- upgrade tapir to `0.17.19`
- upgrade to pureharm `0.1.0` series + drop phantom type support.
