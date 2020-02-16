package com.benasher44.uuid

/**
 * Typesafe enumeration for [Endianness](https://en.wikipedia.org/wiki/Endianness).
 */
// @ExperimentalStdlibApi
// @SinceKotlin("1.x")
public expect enum class ByteOrder {
    /**
     * Denotes big-endian byte order, where the bytes of a multibyte value are
     * ordered from most to least significant.
     */
    BIG_ENDIAN,

    /**
     * Denotes little-endian byte order, where the bytes of a multibyte value
     * are ordered from least to most significant.
     */
    LITTLE_ENDIAN;

    /**
     * Get the native byte order of the underlying platform.
     */
    public fun native(): ByteOrder
}
