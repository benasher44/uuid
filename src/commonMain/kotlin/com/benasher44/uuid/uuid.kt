package com.benasher44.uuid

import kotlin.experimental.and
import kotlin.experimental.or

val UUID_BYTES = 16

inline class UUID(val uuid: ByteArray) {
    constructor(): this(genUuid())

    companion object {
        private fun genUuid(): ByteArray {
            val bytes = getRandomUUIDBytes()
            bytes[7] = (bytes[6] and 0x0F.toByte()) or 0x40.toByte()
            bytes[8] = (bytes[8] and 0b00111111.toByte()) or 0b10000000.toByte()
            return bytes
        }
    }
}