@file:Suppress("MemberVisibilityCanBePrivate")

package com.benasher44.uuid

/**
 * A RFC4122 UUID
 *
 * @property uuidBytes The underlying UUID bytes
 * @constructor Constructs a new UUID from the given ByteArray
 * @throws IllegalArgumentException, if uuid.count() is not 16
 */
public actual class Uuid
@Deprecated("Use `uuidOf` instead.", ReplaceWith("uuidOf(uuid)"))
constructor(internal val uuidBytes: ByteArray) : Comparable<Uuid> {

    @Suppress("DEPRECATION")
    public actual constructor(msb: Long, lsb: Long) : this(fromBits(msb, lsb))

    public actual val mostSignificantBits: Long
        get() = uuidBytes.bits(0, 8)

    public actual val leastSignificantBits: Long
        get() = uuidBytes.bits(8, 16)

    init {
        require(uuidBytes.count() == UUID_BYTES) {
            "Invalid UUID bytes. Expected $UUID_BYTES bytes; found ${uuidBytes.count()}"
        }
        this.freeze()
    }

    private companion object {
        private fun ByteArray.bits(start: Int, end: Int): Long {
            var b = 0L
            var i = start
            do {
                b = (b shl 8) or (get(i).toLong() and 0xff)
            } while (++i < end)
            return b
        }

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

        /** @returns the Int representation of a given UUID character */
        private fun halfByteFromChar(char: Char) = when (char) {
            in '0'..'9' -> char.code - 48
            in 'a'..'f' -> char.code - 87
            in 'A'..'F' -> char.code - 55
            else -> null
        }

        /** The ranges of sections of UUID bytes, to be separated by hyphens */
        private val uuidByteRanges: List<IntRange> = listOf(
            0 until 4,
            4 until 6,
            6 until 8,
            8 until 10,
            10 until 16,
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
        return characters.concatToString()
    }

    /**
     * @return true if other is a UUID and its uuid bytes are equal to this one
     */
    override fun equals(other: Any?): Boolean =
        other is Uuid && uuidBytes.contentEquals(other.uuidBytes)

    /**
     * @return The hashCode of the uuid bytes
     */
    override fun hashCode(): Int =
        uuidBytes.contentHashCode()

    /**
     * @return The result of comparing [uuidBytes] between this and [other]
     */
    override fun compareTo(other: Uuid): Int {
        for (i in (0 until UUID_BYTES)) {
            val compareResult = uuidBytes[i].compareTo(other.uuidBytes[i])
            if (compareResult != 0) return compareResult
        }
        return 0
    }
}

public actual val Uuid.bytes: ByteArray
    get() = uuidBytes

public actual val Uuid.variant: Int
    get() = (leastSignificantBits.ushr((64 - (leastSignificantBits ushr 62)).toInt()) and (leastSignificantBits shr 63)).toInt()

public actual val Uuid.version: Int
    get() = ((mostSignificantBits shr 12) and 0x0f).toInt()

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

@Suppress("DEPRECATION")
public actual fun uuidOf(bytes: ByteArray): Uuid = Uuid(bytes)

/** Returns the Int representation of a given UUID character */
private fun halfByteFromChar(char: Char) = when (char) {
    in '0'..'9' -> char.code - 48
    in 'a'..'f' -> char.code - 87
    in 'A'..'F' -> char.code - 55
    else -> null
}

public actual fun uuidFrom(string: String): Uuid {
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

@Suppress("DEPRECATION")
public actual fun uuid4(): Uuid =
    Uuid(getRandomUuidBytes().setVersion(4))
