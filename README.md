# A Cross Platform UUID for K/N

[![Build Status](https://dev.azure.com/benasher44/benasher44.uuid/_apis/build/status/benasher44.uuid?branchName=master)](https://dev.azure.com/benasher44/benasher44.uuid/_build/latest?definitionId=1&branchName=master)

K/N doesn't have a UUID yet. This brings a random (v4) UUID that matches UUIDs on various platforms:

- iOS/Mac: `NSUUID`
- Java: `java.util.UUID` (specifically `randomUUID`)

### This `UUID` is:

- Frozen
- Thread-safe (thread-safe randomness in native)
- Adheres to RFC4122 (version 4 UUID)
- Tested
- Tested against macOS/iOS UUID to verify correctness
