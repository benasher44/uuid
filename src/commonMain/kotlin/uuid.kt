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
 */
// @SinceKotlin("1.x")
public expect class Uuid : Comparable<Uuid> {

    /**
     * Construct new [Uuid] instance using the given data.
     *
     * @param msb The 64 most significant bits of the [Uuid].
     * @param lsb The 64 least significant bits of the [Uuid].
     */
    // @SinceKotlin("1.x")
    public constructor(msb: Long, lsb: Long)
}

/** Gets the raw UUID bytes */
// @SinceKotlin("1.x")
public expect val Uuid.bytes: ByteArray

/** The most significant 64 bits of this UUID's 128 bit value. */
// @SinceKotlin("1.x")
public expect val Uuid.mostSignificantBits: Long

/** The least significant 64 bits of this UUID's 128 bit value. */
// @SinceKotlin("1.x")
public expect val Uuid.leastSignificantBits: Long

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
// @SinceKotlin("1.x")
public expect val Uuid.variant: Int

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
// @SinceKotlin("1.x")
public expect val Uuid.version: Int

/**
 * Construct new [Uuid] instance from a [String]
 *
 * @param from The [String] representation of the UUID
 */
// @SinceKotlin("1.x")
public expect fun uuidFrom(string: String): Uuid

/**
 * Constructs a new [Uuid] from the given [bytes]
 * @throws IllegalArgumentException, if bytes.count() is not 16
 */
// @SinceKotlin("1.x")
public expect fun uuidOf(bytes: ByteArray): Uuid

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
public expect fun uuid4(): Uuid
