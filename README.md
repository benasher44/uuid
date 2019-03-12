# A Cross Platform UUID for K/N

K/N doesn't have a UUID yet. This brings a random (v4) UUID that matches UUIDs on various platforms:

- iOS/Mac: `NSUUID`
- Java: `java.util.UUID` (specifically `randomUUID`)

### This `UUID` is:

- Frozen
- Thread-safe (thread-safe randomness in native)
- Adheres to RFC4122 (version 4 UUID)
- Tested
- Tested against macOS/iOS UUID to verify correctness
