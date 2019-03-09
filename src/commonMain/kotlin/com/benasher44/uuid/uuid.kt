package com.benasher44.uuid

internal val UUID_BYTES = 16
internal val UUID_STRING_LENGTH = 36

/**
 * A v4 RFC4122 UUID
 *
 * @param uuid The underlying UUID bytes
 * */
class UUID(val uuid: ByteArray = genUuid()) {
    companion object {

        /** Generates a random UUID */
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
        private fun halfByteFromChar(char: Char) = when (char) {
            in '0'..'9' -> char.toInt() - 48
            in 'a'..'f' -> char.toInt() - 87
            in 'A'..'F' -> char.toInt() - 55
            else -> null
        }

        /**
         * Parses a UUID from a String
         *
         * @param from The String, from which to deserialize the UUID
         * @return a UUID, if the string is a valid UUID string
         */
        fun parse(from: String): UUID? {
            if (from.length != UUID_STRING_LENGTH) return null
            if (hyphenIndices.find { from[it] != '-' } != null) return null
            val bytes = ByteArray(UUID_BYTES)
            var byte = 0
            for (range in uuidCharRanges) {
                var i = range.start
                while (i < range.endInclusive) {
                    // Collect each pair of UUID chars and their int representations
                    val left = halfByteFromChar(from[i++])
                    val right = halfByteFromChar(from[i++])
                    if (left == null || right == null) return null

                    // smash them together into a single byte
                    bytes[byte++] = (left.shl(4) or right).toByte()
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
        internal val uuidChars = ('0'..'9') + ('a'..'f')
    }

    override fun toString(): String {
        val characters = CharArray(UUID_STRING_LENGTH)
        var charIndex = 0
        for (range in uuidByteRanges) {
            for (i in range) {
                val octetPair = uuid[i]
                // convert the octet pair in this byte into 2 characters
                val left = octetPair.toInt().shr(4) and 0b00001111
                val right = octetPair.toInt() and 0b00001111
                characters[charIndex++] = uuidChars[left]
                characters[charIndex++] = uuidChars[right]
            }
            if (charIndex < UUID_STRING_LENGTH) {
                characters[charIndex++] = '-'
            }
        }
        return String(characters)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is UUID) return false
        return other.uuid.contentEquals(uuid)
    }

    override fun hashCode() = uuid.contentHashCode()
}