package com.benasher44.uuid

internal val UUID_BYTES = 16

class UUID(
    /** The UUID bytes */
    val uuid: ByteArray = genUuid()
) {
    companion object {

        /** Helper that generates the random UUID */
        private fun genUuid(): ByteArray {
            val bytes = getRandomUUIDBytes()
            // Set the version bit
            bytes[7] = ((bytes[6].toInt() and 0x0F) or 0x40).toByte()

            // Set the 0 and 1 bits
            bytes[8] = ((bytes[8].toInt() and 0b00111111) or 0b10000000).toByte()
            return bytes
        }

        // Ranges of non-hyphen characters in a UUID string
        internal val uuidCharRanges: List<IntRange> = listOf(
            0 until 8,
            9 until 13,
            14 until 18,
            19 until 23,
            24 until 36
        )

        // Indices of the hyphen characters in a UUID string
        internal val hyphenIndices = listOf(8, 13, 18, 23)

        /** @returns the Int representation of a given UUID character */
        private fun halfByteFromChar(char: Char): Int? {
            return when (char) {
                in CharRange('0', '9') -> char.toInt() - 48
                in CharRange('a', 'f') -> char.toInt() - 87
                in CharRange('A', 'F') -> char.toInt() - 55
                else -> null
            }
        }

        /**
         * Parses a UUID from a String
         *
         * @param from The String, from which to deserialize the UUID
         * @return a UUID, if the string is a valid UUID string
         */
        fun parse(from: String): UUID? {
            if (from.length != 36) return null
            if (hyphenIndices.find { from[it] != '-' } != null) return null
            val bytes = ByteArray(16)
            var byte = 0
            for (range in uuidCharRanges) {
               var i = range.start
                while (i < range.endInclusive) {
                    // Collect each pair of UUID chars and their int representations
                    val left = halfByteFromChar(from[i])
                    val right = halfByteFromChar(from[i + 1])
                    if (left == null || right == null) return null

                    // smash them together into a single byte
                    bytes[byte] = (left.shl(4) or right).toByte()
                    i += 2
                    byte += 1
                }
            }
            return UUID(bytes)
        }

        /** The ranges of sections of UUID bytes, to be separated by hyphens */
        private val uuidByteRanges: List<IntRange> = listOf(
            0 until 4,
            4 until 6,
            6 until 8,
            8 until 10,
            10 until 16
        )

        /** The UUID chars arranged from smallest to largest, so they can be indexed by their byte representations */
        internal val uuidChars = CharRange('0', '9').toList() + CharRange('a', 'f').toList()

        /** Converts an octet pair (in a Byte) into its pair of characters */
        private fun octetPairToString(octetPair: Byte): String {
            val left = octetPair.toInt().shr(4) and 0b00001111
            val right = octetPair.toInt() and 0b00001111
            return "${uuidChars[left]}${uuidChars[right]}"
        }
    }

    override fun toString() = uuidByteRanges.map { range -> String
        range.map { octetPairToString(uuid[it]) }.joinToString("")
    }.joinToString("-")

    override fun equals(other: Any?): Boolean {
        if (other !is UUID) return false
        return other.uuid.contentEquals(uuid)
    }

    override fun hashCode() = uuid.contentHashCode()
}