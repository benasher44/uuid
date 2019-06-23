# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/), and this
project adheres to [Semantic Versioning](https://semver.org/).

## [Unreleased]
### Added
- `uuid4` function (#42)
### Deprecated
- no-args constructor in favor of `uuid4` (#42)

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

[Unreleased]: https://github.com/benasher44/uuid/compare/0.0.3...HEAD
[0.0.3]: https://github.com/benasher44/uuid/compare/0.0.2...0.0.3
[0.0.2]: https://github.com/benasher44/uuid/compare/0.0.1...0.0.2
[0.0.1]: https://github.com/benasher44/uuid/releases/tag/0.0.1
