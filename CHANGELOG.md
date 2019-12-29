# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/), and this
project adheres to [Semantic Versioning](https://semver.org/).

## [0.0.8] - TBD
### Changed
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
