@file:kotlin.jvm.JvmName("UuidUtil")
@file:Suppress("RedundantVisibilityModifier", "MemberVisibilityCanBePrivate")

package com.benasher44.uuid

import kotlin.DeprecationLevel.WARNING

internal const val UUID_BYTES = 16
internal const val UUID_STRING_LENGTH = 36

@Deprecated("Use `Uuid` instead.", ReplaceWith("Uuid"))
public typealias UUID = Uuid

/**
 * Construct new [Uuid] instance using the given byte data.
 *
 * @param uuid The UUID bytes.
 * @throws IllegalArgumentException, if uuid.count() is not 16
 */
// @SinceKotlin("1.x")
@Suppress("FunctionName")
public fun Uuid(uuid: ByteArray): Uuid {
    require(uuid.count() == UUID_BYTES) {
        "Invalid UUID bytes. Expected $UUID_BYTES bytes; found ${uuid.count()}"
    }
    val msb = (0 until 8).fold(0L) { m, i ->
        (m shl 8) or uuid[i].toLong()
    }
    val lsb = (8 until 16).fold(0L) { l, i ->
        (l shl 8) or uuid[i].toLong()
    }
    return Uuid(msb, lsb)
}

/**
 * A v4 RFC4122 UUID
 *
 * @constructor Construct new [Uuid] instance from the most ([msb]) and least ([lsb]) significant bits
 */
public class Uuid(msb: Long, lsb: Long) {
    @Deprecated("use uuid4 instead", ReplaceWith("uuid4()"))
    constructor() : this(genUuid())

    /** The most significant 64 bits of this UUID's 128 bit value. */
    val mostSignificantBits: Long = msb

    /** The least significant 64 bits of this UUID's 128 bit value. */
    val leastSignificantBits: Long = lsb

    init {
        this.freeze()
    }

    /**
     * The variant of the [Uuid], determines the interpretation of the bits.
     *
     * - **`0`** – special case for the Nil UUID as well as reserved for NCS
     * - **`2`** – Leach-Salz, as defined in [RFC 4122](https://tools.ietf.org/html/rfc4122) and used by this class
     * - **`6`** – reserved for Microsoft (GUID) backwards compatibility
     * - **`7`** – reserved for future extension
     *
     * @return The variant number of this [Uuid].
     * @sample com.benasher44.uuid.UuidTest.variants
     * @see <a href="https://tools.ietf.org/html/rfc4122#section-4.1.1">RFC 4122: Section 4.1.1</a>
     */
    public val variant: Int
        get() = (leastSignificantBits.ushr((64 - (leastSignificantBits ushr 62)).toInt()) and (leastSignificantBits shr 63)).toInt()

    /**
     * The version of the [Uuid], denoting the generating algorithm.
     *
     * - **`0`** – special case for the Nil UUID
     * - **`1`** – time-based
     * - **`2`** – DCE security
     * - **`3`** – name-based using MD5 hashing
     * - **`4`** – random or pseudo-random
     * - **`5`** – name-based using SHA-1 hashing
     * - **`6`–`15`** – reserved for future extension
     *
     * Note that the version returned by this function is only meaningful if the [Uuid.variant] is
     * [RFC 4122](https://tools.ietf.org/html/rfc4122).
     *
     * @return The version number of this [Uuid].
     * @sample com.benasher44.uuid.UuidTest.versions
     * @see <a href="https://tools.ietf.org/html/rfc4122#section-4.1.3">RFC 4122: Section 4.1.3</a>
     */
    public val version: Int
        get() = ((mostSignificantBits shr 12) and 0x0f).toInt()

    companion object {

        /** Generates a random UUID */
        private fun genUuid(): ByteArray {
            val bytes = getRandomUuidBytes()
            // Set the version bit
            bytes[6] = ((bytes[6].toInt() and 0x0F) or 0x40).toByte()

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
        @Deprecated(
            message = "Use Uuid.fromString() instead.",
            replaceWith = ReplaceWith("Uuid.fromString(from)"),
            level = WARNING
        )
        fun parse(from: String): Uuid? {
            return try {
                fromString(from)
            } catch (_: Throwable) {
                null
            }
        }

        /**
         * Parses a UUID from a String
         *
         * @param from The String, from which to deserialize the UUID
         * @return a UUID, if the string is a valid UUID string or throws an [IllegalArgumentException]
         */
        fun fromString(from: String): Uuid {
            require(from.length == UUID_STRING_LENGTH) {
                "Uuid string has invalid length: $from"
            }
            require(hyphenIndices.all { from[it] == '-' }) {
                "Uuid string has invalid format: $from"
            }

            val bytes = ByteArray(UUID_BYTES)
            var byte = 0
            for (range in uuidCharRanges) {
                var i = range.first
                while (i < range.last) {
                    // Collect each pair of UUID chars and their int representations
                    val left = halfByteFromChar(from[i++])
                    val right = halfByteFromChar(from[i++])
                    require(left != null && right != null) {
                        "Uuid string has invalid characters: $from"
                    }

                    // smash them together into a single byte
                    bytes[byte++] = (left.shl(4) or right).toByte()
                }
            }
            return Uuid(bytes)
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

    /**
     * Converts the UUID to a UUID string, per RFC4122
     */
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

    /**
     * @return true if other is a UUID and its uuid bytes are equal to this one
     */
    override fun equals(other: Any?): Boolean {
        if (other !is Uuid) return false
        return other.uuid.contentEquals(uuid)
    }

    /**
     * @return The hashCode of the uuid bytes
     */
    override fun hashCode(): Int = uuid.contentHashCode()
}

/**
 * Set the [Uuid.version] on this big-endian [ByteArray]. The [Uuid.variant] is
 * always set to the RFC 4122 one since this is the only variant supported by
 * the [Uuid] implementation.
 *
 * @return Itself after setting the [Uuid.variant] and [Uuid.version].
 */
@Suppress("NOTHING_TO_INLINE")
// @kotlin.internal.InlineOnly
internal inline fun ByteArray.setVersion(version: Int) = apply {
    this[6] = ((this[6].toInt() and 0x0F) or (version shl 4)).toByte()
    this[8] = ((this[8].toInt() and 0x3F) or 0x80).toByte()
}

/**
 * Construct new version 4 [UUID][Uuid].
 *
 * Version 4 UUIDs are randomly generated from the best available random source.
 * The selection of that source is depends on the platform. Some systems may be
 * bad at generating sufficient entropy, e.g. virtual machines, and this might
 * lead to collisions faster than desired. Version 5 may be used if this is the
 * case and no other measures are possible to increase the entropy of the random
 * source of the platform.
 *
 * @return New version 4 [UUID][Uuid] of random data.
 * @sample com.benasher44.uuid.UuidTest.uuid4_generation
 * @see <a href="https://tools.ietf.org/html/rfc4122#section-4.4">RFC 4122: Section 4.4</a>
 */
// @SinceKotlin("1.x")
public fun uuid4(): Uuid =
    Uuid(getRandomUuidBytes().setVersion(4))
