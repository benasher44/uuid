package com.benasher44.uuid

import java.util.UUID

public actual typealias Uuid = UUID

public actual fun uuidOf(bytes: ByteArray): Uuid {
    require(bytes.count() == UUID_BYTES) {
        "Invalid UUID bytes. Expected $UUID_BYTES bytes; found ${bytes.count()}"
    }
    val msb = (0 until 8).fold(0L) { m, i ->
        (m shl 8) or (bytes[i].toLong() and 0xff)
    }
    val lsb = (8 until 16).fold(0L) { l, i ->
        (l shl 8) or (bytes[i].toLong() and 0xff)
    }
    return UUID(msb, lsb)
}

// Implementation Notes:
// - This algorithm could be implemented in the common module and reused for all
//   targets. However, using two longs is not necessarily the best performing
//   solution in other targets. JavaScript for instance does not have native
//   support for 64 bit integers and they are always wrapped in an object
//   (similar to a BigDecimal in Java), a org.khronos.webgl.Uint8ClampedArray is
//   probably the best choice there. Native targets are probably better at
//   dealing with byte arrays, or even directly using native functions… only
//   careful benchmarking can tell and UUIDs are ALWAYS in the critical path of
//   applications, hence, we want to have the best solution for each target.
// - We are not using any constants but rather literal values because constants
//   lead to code being generated that is accessible via reflection, but we do
//   not want to leak anything.
// - The length verification at the beginning does not always lead to the
//   exception being thrown because length is UTF-16 based and not byte based as
//   it is actually required here. Careful benchmarking, however, showed that
//   any kind of conversion leads to a loss in throughput that is not worth it
//   to pay since the hex-conversion that follows is going to catch all chars
//   that are not valid hexadecimal digits anyways.
// - Regarding benchmarks, the following numbers are from a Zulu OpenJDK 13 run
//   between java.util.UUID and this implementation on a 2017 MacBook Pro with
//   Catalina:
//
//   Benchmark                    Mode  Cnt         Score        Error  Units
//   UuidParserBenchmark.java    thrpt   25   9628223.641 ± 248719.696  ops/s
//   UuidParserBenchmark.kotlin  thrpt   25  13948372.928 ± 694944.523  ops/s
//
//   Other strategies (e.g. ByteArray, ByteBuffer, …) are all slower than the
//   MSB/LSB approach. The reason for that has probably to do with how the JVM
//   works and that such arrays always lead to heap allocations.
public actual fun uuidFrom(string: String): Uuid =
    if (string.length == 36) Uuid(string.segmentToLong(0, 19), string.segmentToLong(19, 36)) else throw IllegalArgumentException(
        "Invalid UUID string, expected exactly 36 characters but got ${string.length}: $string"
    )

private fun String.segmentToLong(start: Int, end: Int): Long {
    var result = 0L

    var i = start
    do {
        if (this[i] == '-') {
            require(i == 8 || i == 13 || i == 18 || i == 23) {
                "Invalid UUID string, encountered dash at index $i but it can occur only at 8, 13, 18, or 23: $this"
            }
        } else {
            result *= 16
            when (this[i]) {
                '0' -> Unit
                '1' -> result += 1L
                '2' -> result += 2L
                '3' -> result += 3L
                '4' -> result += 4L
                '5' -> result += 5L
                '6' -> result += 6L
                '7' -> result += 7L
                '8' -> result += 8L
                '9' -> result += 9L
                'a', 'A' -> result += 10L
                'b', 'B' -> result += 11L
                'c', 'C' -> result += 12L
                'd', 'D' -> result += 13L
                'e', 'E' -> result += 14L
                'f', 'F' -> result += 15L
                else -> throw IllegalArgumentException(
                    "Invalid UUID string, encountered non-hexadecimal digit `${this[i]}` at index $i in: $this"
                )
            }
        }
    } while (++i < end)

    return result
}

public actual fun uuid4(): Uuid = UUID.randomUUID()

public actual val UUID.bytes: ByteArray
    get() = ByteArray(UUID_BYTES) { index ->
        val bits: Long
        val offsetIndex: Int
        if (index < 8) {
            bits = this.mostSignificantBits
            offsetIndex = index
        } else {
            bits = this.leastSignificantBits
            offsetIndex = index - 8
        }
        ((bits ushr ((7 - offsetIndex) * 8)) and 0xff).toByte()
    }

public actual val UUID.mostSignificantBits: Long
    get() = mostSignificantBits

public actual val UUID.leastSignificantBits: Long
    get() = mostSignificantBits

public actual val UUID.version: Int
    get() = version()

public actual val UUID.variant: Int
    get() = variant()
