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

public actual fun uuidFrom(string: String): Uuid = UUID.fromString(string)
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