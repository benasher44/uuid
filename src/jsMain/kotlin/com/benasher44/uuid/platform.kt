package com.benasher44.uuid

import kotlin.js.Math

internal actual fun getRandomUUIDBytes(): ByteArray {
    val bytes = ByteArray(UUID_BYTES)
    shift(Math.random().toBits(), bytes, 0)
    shift(Math.random().toBits(), bytes, 8)
    return bytes
}

private fun shift(bytes: Long, onto: ByteArray, startingAt: Int) {
    for (i in 0..7) {
        val byte = (bytes.shr(8 * i) and Byte.MAX_VALUE.toLong()).toByte()
        onto[startingAt + i] = byte
    }
}