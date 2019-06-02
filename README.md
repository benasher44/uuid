# A Cross Platform UUID for K/N

![Maven Central](https://img.shields.io/maven-central/v/com.benasher44/uuid.svg?label=mavenCentral%28%29)
[![Build Status](https://dev.azure.com/benasher44/benasher44.uuid/_apis/build/status/benasher44.uuid?branchName=master)](https://dev.azure.com/benasher44/benasher44.uuid/_build/latest?definitionId=1&branchName=master)
[![Contributor Covenant](https://img.shields.io/badge/Contributor%20Covenant-v1.4%20adopted-ff69b4.svg)](CODE_OF_CONDUCT.md)

K/N doesn't have a UUID yet. This brings a random (v4) UUID that matches UUIDs on various platforms:

- iOS/Mac: `NSUUID`
- Java: `java.util.UUID` (specifically `randomUUID`)

### `UUID`

- Frozen
- Thread-safe (thread-safe randomness in native)
- Adheres to RFC4122 (version 4 UUID)
- Tested
- Tested against macOS/iOS UUID to verify correctness


### Setup

In your build.gradle(.kts):

- Add `mavenCentral()` to your repositories
- Add `implementation "com.benasher44:uuid:<version>"` as a dependency in your `commonMain` `sourceSets`.

This library publishes gradle module metadata, so you should have `enableFeaturePreview("GRADLE_METADATA")` in your settings.gradle(.kts).
