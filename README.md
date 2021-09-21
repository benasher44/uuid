# A Kotlin Multiplatform UUID

[![Maven Central](https://img.shields.io/maven-central/v/com.benasher44/uuid.svg?label=mavenCentral%28%29)](https://search.maven.org/artifact/com.benasher44/uuid)
[![Build Status](https://github.com/benasher44/uuid/workflows/master/badge.svg)](https://github.com/benasher44/uuid/actions?query=workflow%3Amaster)
[![Contributor Covenant](https://img.shields.io/badge/Contributor%20Covenant-v1.4%20adopted-ff69b4.svg)](CODE_OF_CONDUCT.md)

K/N doesn't have a UUID yet. This brings a UUID that matches UUIDs on various platforms:

- iOS/Mac: `NSUUID`
- Java: `java.util.UUID`

### `UUID`

- Frozen
- Thread-safe (thread-safe randomness in native)
- Adheres to RFC4122
- Tested
- Tested against macOS/iOS UUID to verify correctness

### Setup

In your build.gradle(.kts):

- Add `mavenCentral()` to your repositories
- Add `implementation "com.benasher44:uuid:<version>"` as a dependency in your `commonMain` `sourceSets`.

This library publishes gradle module metadata. If you're using Gradle prior to version 6, you should have `enableFeaturePreview("GRADLE_METADATA")` in your settings.gradle(.kts).

### Future Goals

- Develop UUID functionality that can be contributed back to the Kotlin stdlib (see latest issues, PRs, and CHANGELOG.md for updates)
