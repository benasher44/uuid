package com.benasher44.uuid

import kotlin.experimental.and
import kotlin.experimental.or

val UUID_BYTES = 16

class UUID(val uuid: ByteArray) {
    constructor(): this(genUuid())

    companion object {
        private fun genUuid(): ByteArray {
            val bytes = getRandomUUIDBytes()
            bytes[7] = (bytes[6] and 0x0F.toByte()) or 0x40.toByte()
            bytes[8] = (bytes[8] and 0b00111111.toByte()) or 0b10000000.toByte()
            return bytes
        }

        private val uuidCharRanges: List<IntRange> = listOf(
            0 until 8,
            9 until 13,
            14 until 18,
            19 until 23,
            24 until 36
        )

        private val hyphenIndices = listOf(8, 13, 18, 23)

        private fun halfByteFromChar(char: Char): Int? {
            return when (char) {
                in CharRange('0', '9') -> char.toInt() - 48
                in CharRange('a', 'f') -> char.toInt() - 87
                in CharRange('A', 'F') -> char.toInt() - 55
                else -> null
            }
        }

        fun parse(from: String): UUID? {
            if (from.length != 36) return null
            if (hyphenIndices.find { from[it] != '-' } != null) return null
            val bytes = ByteArray(16)
            var byte = 0
            for (range in uuidCharRanges) {
               var i = range.start
                while (i < range.endInclusive) {
                    val left = halfByteFromChar(from[i])
                    val right = halfByteFromChar(from[i + 1])
                    if (left == null || right == null) return null
                    bytes[byte] = (left.shl(4) or right).toByte()
                    i += 2
                    byte += 1
                }
            }
            return UUID(bytes)
        }

        private val uuidByteRanges: List<IntRange> = listOf(
            0 until 4,
            4 until 6,
            6 until 8,
            8 until 10,
            10 until 16
        )

        private val uuidChars = CharRange('0', '9').toList() + CharRange('a', 'f').toList()

        private fun octetPairToString(octetPair: Byte): String {
            val left = (octetPair.toInt().shr(4).toByte() and 0b00001111).toUByte()
            val right = (octetPair and 0b00001111).toUByte()
            return "${uuidChars[left.toInt()]}${uuidChars[right.toInt()]}"
        }
    }

    override fun toString() = uuidByteRanges.map { range -> String
        range.map { octetPairToString(uuid[it]) }.joinToString("")
    }.joinToString("-")

    override fun equals(other: Any?): Boolean {
        if (other !is UUID) return false
        return other.uuid.contentEquals(uuid)
    }

    override fun hashCode() = uuid.hashCode()
}