@file:kotlin.jvm.JvmName("UuidUtil")
@file:Suppress("RedundantVisibilityModifier", "MemberVisibilityCanBePrivate")

package com.benasher44.uuid

import kotlin.native.concurrent.SharedImmutable

// Number of bytes in a UUID
internal const val UUID_BYTES = 16

// Number of characters in a UUID string
internal const val UUID_STRING_LENGTH = 36

// Ranges of non-hyphen characters in a UUID string
@SharedImmutable
internal val UUID_CHAR_RANGES: List<IntRange> = listOf(
    0 until 8,
    9 until 13,
    14 until 18,
    19 until 23,
    24 until 36
)

// Indices of the hyphen characters in a UUID string
@SharedImmutable
internal val UUID_HYPHEN_INDICES = listOf(8, 13, 18, 23)

// UUID chars arranged from smallest to largest, so they can be indexed by their byte representations
@SharedImmutable
internal val UUID_CHARS = ('0'..'9') + ('a'..'f')

/**
 * A RFC4122 UUID
 *
 * @property uuid The underlying UUID bytes
 * @constructor Constructs a new UUID from the given ByteArray
 * @throws IllegalArgumentException, if uuid.count() is not 16
 */
public class Uuid @Deprecated("Use `uuidOf` instead.", ReplaceWith("uuidOf(uuid)")) constructor(internal val uuidBytes: ByteArray) {

    /**
     * Construct new [Uuid] instance using the given data.
     *
     * @param msb The 64 most significant bits of the [Uuid].
     * @param lsb The 64 least significant bits of the [Uuid].
     */
    @Suppress("DEPRECATION")
    public constructor(msb: Long, lsb: Long) : this(fromBits(msb, lsb))

    /**
     * The UUID's raw bytes
     */
    public val bytes: ByteArray
        get() = uuidBytes

    /** The most significant 64 bits of this UUID's 128 bit value. */
    public val mostSignificantBits: Long by lazy {
        (0..7).fold(0L) { bits, i ->
            bits shl 8 or (uuidBytes[i].toLong() and 0xff)
        }
    }

    /** The least significant 64 bits of this UUID's 128 bit value. */
    public val leastSignificantBits: Long by lazy {
        (8..15).fold(0L) { bits, i ->
            bits shl 8 or (uuidBytes[i].toLong() and 0xff)
        }
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

    init {
        require(uuidBytes.count() == UUID_BYTES) {
            "Invalid UUID bytes. Expected $UUID_BYTES bytes; found ${uuidBytes.count()}"
        }
        this.freeze()
    }

    companion object {

        /** Creates the [ByteArray] from most and least significant bits */
        private fun fromBits(msb: Long, lsb: Long) = ByteArray(UUID_BYTES).also { bytes ->
            (7 downTo 0).fold(msb) { x, i ->
                bytes[i] = (x and 0xff).toByte()
                x shr 8
            }
            (15 downTo 8).fold(lsb) { x, i ->
                bytes[i] = (x and 0xff).toByte()
                x shr 8
            }
        }

        /** The ranges of sections of UUID bytes, to be separated by hyphens */
        private val uuidByteRanges: List<IntRange> = listOf(
            0 until 4,
            4 until 6,
            6 until 8,
            8 until 10,
            10 until 16
        )
    }

    /**
     * Converts the UUID to a UUID string, per RFC4122
     */
    override fun toString(): String {
        val characters = CharArray(UUID_STRING_LENGTH)
        var charIndex = 0
        for (range in uuidByteRanges) {
            for (i in range) {
                val octetPair = uuidBytes[i]
                // convert the octet pair in this byte into 2 characters
                val left = octetPair.toInt().shr(4) and 0b00001111
                val right = octetPair.toInt() and 0b00001111
                characters[charIndex++] = UUID_CHARS[left]
                characters[charIndex++] = UUID_CHARS[right]
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
        return other.uuidBytes.contentEquals(uuidBytes)
    }

    /**
     * @return The hashCode of the uuid bytes
     */
    override fun hashCode(): Int = uuidBytes.contentHashCode()
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
@Suppress("DEPRECATION")
public fun uuid4(): Uuid =
    Uuid(getRandomUuidBytes().setVersion(4))

/**
 * Constructs a new [Uuid] from the given [bytes]
 * @throws IllegalArgumentException, if bytes.count() is not 16
 */
@Suppress("DEPRECATION")
public fun uuidOf(bytes: ByteArray): Uuid = Uuid(bytes)

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
 * @param string The String, from which to deserialize the UUID
 * @return a UUID, if the string is a valid UUID
 * @throws [IllegalArgumentException], if [string] is invalid
 */
public fun uuidFrom(string: String): Uuid {
    require(string.length == UUID_STRING_LENGTH) {
        "Uuid string has invalid length: $string"
    }
    require(UUID_HYPHEN_INDICES.all { string[it] == '-' }) {
        "Uuid string has invalid format: $string"
    }

    val bytes = ByteArray(UUID_BYTES)
    var byte = 0
    for (range in UUID_CHAR_RANGES) {
        var i = range.first
        while (i < range.last) {
            // Collect each pair of UUID chars and their int representations
            val left = halfByteFromChar(string[i++])
            val right = halfByteFromChar(string[i++])
            require(left != null && right != null) {
                "Uuid string has invalid characters: $string"
            }

            // smash them together into a single byte
            bytes[byte++] = (left.shl(4) or right).toByte()
        }
    }
    @Suppress("DEPRECATION")
    return Uuid(bytes)
}
