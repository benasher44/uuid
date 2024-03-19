# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/), and this
project adheres to [Semantic Versioning](https://semver.org/).

## [0.8.4] - 2024-03-19
### Changed
- Fix Kotlin Metadata (#157)

## [0.8.3] - 2024-03-16
### Changed
- Bump kotlin to 1.9.23 (#154)
- Add wasmWasi support (#154)

## [0.8.2] - 2023-11-10
### Changed
- Bump kotlin to 1.9.20
- Rename "wasm" target to "wasmJs" (required by kotlin 1.9.20)

## [0.8.1] - 2023-08-25
### Changed
- Set apiVersion and languageVersion on the JVM for better compatibility (#147)

## [0.8.0] - 2023-07-22
### Changed
- Bump Kotlin to 1.9.0 and add wasm target (#140)
- Add kotlin-js-store to source control (#141)
- Do not try to sign if no signing key is configured (#141)
- Bump Gradle and Dokka (#143)
- Compile for Java8 by default (#144)
- Removed deprecated K/N targets (#145)

## [0.7.1] - 2023-06-01
### Changed
- Add support for watchosDeviceArm64 (#137)
- Upgrade Kotlin to 1.8.21 (#139)

## [0.7.0] - 2023-02-25
### Changed
- Add support for linuxArm64 (#134)
- Upgrade Kotlin to 1.8.10 (#135)

## [0.6.0] - 2022-11-08
### Changed
- Rework source sets to form a rich hierarchy (#130)
- Upgrade to Kotlin 1.7.20 (#131)

## [0.5.0] - 2022-07-13
### Changed
- Upgrade to Kotlin 1.7.10 (#126)

## [0.4.1] - 2022-06-04
### Changed
- Add `watchosX64` target (#123)
- Upgrade to Kotlin 1.6.21 (#125)

## [0.4.0] - 2022-01-13
### Changed
- Upgrade to Kotlin 1.6.10 (#122)

## [0.3.1] - 2021-09-07
### Changed
- Upgrade to Kotlin 1.5.30 (#118)
- Add Apple Silicon targets: `iosSimulatorArm64`, `macosArm64`, `tvosSimulatorArm64`, `watchosSimulatorArm64` (#118) 

## [0.3.0] - 2021-04-26
### Changed
- Upgrade to Kotlin 1.5.0 (#113)
## [0.2.4] - 2021-04-08
### Added
- Support HMPP (#110)
### Changed
- Upgrade to Kotlin 1.4.32 (#110)

## [0.2.3] - 2020-11-23
### Changed
- Improve error handling on nix systems (#108)

## [0.2.2] - 2020-08-27
### Added
- Support for watchOS and tvOS (#98)

## [0.2.1] - 2020-08-18
### Added
- Support for JS IR (#96)

## [0.2.0] - 2020-08-14
### Added
- Upgradee to 1.4.0 (#92)

## [0.1.1] - 2020-07-12
### Added
- Add namespaced UUIDs v3 and v5 (#87)

## [0.1.0] - 2020-03-03
### Added
- Comparable support for `Uuid` (#72)
### Changed
- Upgrade to Kotlin 1.3.70
- Removed `Uuid.parse` (#71)
- Use `java.util.UUID` on JVM (#71)

## [0.0.7] - 2019-12-29
### Added
- `uuidOf(bytes)` to construct a `Uuid` from a `ByteArray` (#67)
- `uuidFrom(from)` to construct a `Uuid` from a `String` (#67)
### Changed
- Deprecate `Uuid.parse()` in favor of ~`Uuid.fromString()`~ (now `uuidFrom()`, which returns a non-null Uuid or throws an error for an invalid string, in line with Java's `UUID.fromString()`. (#59)
- `Uuid(msb: Long, lsb: Long)` is now a constructor in stead of a free function (#66)
- Removed empty `Uuid()` constructor (#66)
- Deprecate `Uuid(bytes)`, which will eventually become `internal` (#67)
- Deprecate `.uuid` in favor of `.bytes` (#69)

## [0.0.6] - 2019-11-21
### Changed
- Upgrade to Kotlin 1.3.60 (#56)

## [0.0.5] - 2019-09-03
### Changed
- Fix mingw and linux builds (#55)

## [0.0.4] - 2019-08-22
### Added
- `uuid4` function (#42)
### Changed
- Kotlin version to 1.3.50 (#54)
### Deprecated
- no-args constructor in favor of `uuid4()` (#42)

## [0.0.3] - 2019-06-20
### Added
- `variant` and `version` (#16)
### Changed
- Kotlin version to 1.3.40 (#38)
- Java compatibility from 1.8 to 1.6 (#31)
- `UUID` class name to `Uuid`, with a `typealias` for backward compatibility (#36)
### Deprecated
- `UUID` class name in favor of `Uuid` (#36)

## [0.0.2] - 2019-06-02
### Changed
- Use SecureRandom on JVM (#16)
- Fix incorrect version bit (#16)
- Improve JVM test coverage (#16)

## [0.0.1] - 2019-05-13
- initial release
