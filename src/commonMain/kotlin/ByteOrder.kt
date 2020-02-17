package com.benasher44.uuid

import com.benasher44.uuid.ByteOrder.BIG_ENDIAN
import com.benasher44.uuid.ByteOrder.LITTLE_ENDIAN

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

    public companion object {
        /**
         * Get the native byte order of the underlying platform.
         */
        public val native: ByteOrder
    }
}

/**
 * Whether the native byte order of this system is little endian, or not.
 *
 * @see ByteOrder.native
 * @see ByteOrder.LITTLE_ENDIAN
 */
public inline val isLittleEndianSystem: Boolean
    get() = ByteOrder.native == LITTLE_ENDIAN

/**
 * Whether the native byte order of this system is big endian, or not.
 *
 * @see ByteOrder.native
 * @see ByteOrder.BIG_ENDIAN
 */
public inline val isBigEndianSystem: Boolean
    get() = ByteOrder.native == BIG_ENDIAN

// TODO the following functions should be directly defined on the corresponding
//   types and not as extension function, however, this is only possible once we
//   port this library to the stdlib.

// region Short ----------------------------------------------------------------

/**
 * Reverse the byte order of this value.
 *
 * @see ByteOrder
 * @see isBigEndianSystem
 * @see isLittleEndianSystem
 */
// @ExperimentalStdlibApi
// @SinceKotlin("1.x")
public fun Short.swapBytes(): Short =
    (((toInt() and 0xff00) shr 8) or (toInt() shl 8)).toShort()

/**
 * Reverse the byte order of all values in this array.
 *
 * @see ByteOrder
 * @see isBigEndianSystem
 * @see isLittleEndianSystem
 */
// @ExperimentalStdlibApi
// @SinceKotlin("1.x")
public fun ShortArray.swapBytes() {
    for (i in indices) this[i] = this[i].swapBytes()
}

/**
 * Create a new array where the byte order of all values is reversed.
 *
 * @see ByteOrder
 * @see isBigEndianSystem
 * @see isLittleEndianSystem
 */
// @ExperimentalStdlibApi
// @SinceKotlin("1.x")
public fun ShortArray.swappedBytes(): ShortArray =
    ShortArray(size) { this[it].swapBytes() }

// endregion Short -------------------------------------------------------------
// region Int ------------------------------------------------------------------

/**
 * Reverse the byte order of this value.
 *
 * @see ByteOrder
 * @see isBigEndianSystem
 * @see isLittleEndianSystem
 */
// @ExperimentalStdlibApi
// @SinceKotlin("1.x")
public fun Int.swapBytes(): Int =
    (this ushr 24) or (this shr 8 and 0xff00) or (this shl 8 and 0xff0000) or (this shl 24)

/**
 * Reverse the byte order of all values in this array.
 *
 * @see ByteOrder
 * @see isBigEndianSystem
 * @see isLittleEndianSystem
 */
// @ExperimentalStdlibApi
// @SinceKotlin("1.x")
public fun IntArray.swapBytes() {
    for (i in indices) this[i] = this[i].swapBytes()
}

/**
 * Create a new array where the byte order of all values is reversed.
 *
 * @see ByteOrder
 * @see isBigEndianSystem
 * @see isLittleEndianSystem
 */
// @ExperimentalStdlibApi
// @SinceKotlin("1.x")
public fun IntArray.swappedBytes(): IntArray =
    IntArray(size) { this[it].swapBytes() }

// endregion Int ---------------------------------------------------------------
// region Long -----------------------------------------------------------------

/**
 * Reverse the byte order of this value.
 *
 * @see ByteOrder
 * @see isBigEndianSystem
 * @see isLittleEndianSystem
 */
// @ExperimentalStdlibApi
// @SinceKotlin("1.x")
public fun Long.swapBytes(): Long =
    (((this and 0x00ff00ff00ff00ff) shl 8) or ((this ushr 8) and 0x00ff00ff00ff00ff)).let { n ->
        ((n shl 48) or ((n and 0xffff0000) shl 16)) or ((n ushr 16) and 0xffff0000) or (n ushr 48)
    }

/**
 * Reverse the byte order of all values in this array.
 *
 * @see ByteOrder
 * @see isBigEndianSystem
 * @see isLittleEndianSystem
 */
// @ExperimentalStdlibApi
// @SinceKotlin("1.x")
public fun LongArray.swapBytes() {
    for (i in indices) this[i] = this[i].swapBytes()
}

/**
 * Create a new array where the byte order of all values is reversed.
 *
 * @see ByteOrder
 * @see isBigEndianSystem
 * @see isLittleEndianSystem
 */
// @ExperimentalStdlibApi
// @SinceKotlin("1.x")
public fun LongArray.swappedBytes(): LongArray =
    LongArray(size) { this[it].swapBytes() }

// endregion Long --------------------------------------------------------------
// region Float ----------------------------------------------------------------

/**
 * Reverse the byte order of this value.
 *
 * @see ByteOrder
 * @see isBigEndianSystem
 * @see isLittleEndianSystem
 */
// @ExperimentalStdlibApi
// @SinceKotlin("1.x")
public fun Float.swapBytes(): Float =
    Float.fromBits(toRawBits().swapBytes())

/**
 * Reverse the byte order of all values in this array.
 *
 * @see ByteOrder
 * @see isBigEndianSystem
 * @see isLittleEndianSystem
 */
// @ExperimentalStdlibApi
// @SinceKotlin("1.x")
public fun FloatArray.swapBytes() {
    for (i in indices) this[i] = this[i].swapBytes()
}

/**
 * Create a new array where the byte order of all values is reversed.
 *
 * @see ByteOrder
 * @see isBigEndianSystem
 * @see isLittleEndianSystem
 */
// @ExperimentalStdlibApi
// @SinceKotlin("1.x")
public fun FloatArray.swappedBytes(): FloatArray =
    FloatArray(size) { this[it].swapBytes() }

// endregion Float -------------------------------------------------------------
// region Double ---------------------------------------------------------------

/**
 * Reverse the byte order of this value.
 *
 * @see ByteOrder
 * @see isBigEndianSystem
 * @see isLittleEndianSystem
 */
// @ExperimentalStdlibApi
// @SinceKotlin("1.x")
public fun Double.swapBytes(): Double =
    Double.fromBits(toRawBits().swapBytes())

/**
 * Reverse the byte order of all values in this array.
 *
 * @see ByteOrder
 * @see isBigEndianSystem
 * @see isLittleEndianSystem
 */
// @ExperimentalStdlibApi
// @SinceKotlin("1.x")
public fun DoubleArray.swapBytes() {
    for (i in indices) this[i] = this[i].swapBytes()
}

/**
 * Create a new array where the byte order of all values is reversed.
 *
 * @see ByteOrder
 * @see isBigEndianSystem
 * @see isLittleEndianSystem
 */
// @ExperimentalStdlibApi
// @SinceKotlin("1.x")
public fun DoubleArray.swappedBytes(): DoubleArray =
    DoubleArray(size) { this[it].swapBytes() }

// endregion Double ------------------------------------------------------------
// region UShort ---------------------------------------------------------------

/**
 * Reverse the byte order of this value.
 *
 * @see ByteOrder
 * @see isBigEndianSystem
 * @see isLittleEndianSystem
 */
// @ExperimentalStdlibApi
// @SinceKotlin("1.x")
@ExperimentalUnsignedTypes
public fun UShort.swapBytes(): UShort =
    toShort().swapBytes().toUShort()

/**
 * Reverse the byte order of all values in this array.
 *
 * @see ByteOrder
 * @see isBigEndianSystem
 * @see isLittleEndianSystem
 */
// @ExperimentalStdlibApi
// @SinceKotlin("1.x")
@ExperimentalUnsignedTypes
public fun UShortArray.swapBytes() {
    for (i in indices) this[i] = this[i].swapBytes()
}

/**
 * Create a new array where the byte order of all values is reversed.
 *
 * @see ByteOrder
 * @see isBigEndianSystem
 * @see isLittleEndianSystem
 */
// @ExperimentalStdlibApi
// @SinceKotlin("1.x")
@ExperimentalUnsignedTypes
public fun UShortArray.swappedBytes(): UShortArray =
    UShortArray(size) { this[it].swapBytes() }

// endregion UShort ------------------------------------------------------------
// region UInt -----------------------------------------------------------------

/**
 * Reverse the byte order of this value.
 *
 * @see ByteOrder
 * @see isBigEndianSystem
 * @see isLittleEndianSystem
 */
// @ExperimentalStdlibApi
// @SinceKotlin("1.x")
@ExperimentalUnsignedTypes
public fun UInt.swapBytes(): UInt =
    toInt().swapBytes().toUInt()

/**
 * Reverse the byte order of all values in this array.
 *
 * @see ByteOrder
 * @see isBigEndianSystem
 * @see isLittleEndianSystem
 */
// @ExperimentalStdlibApi
// @SinceKotlin("1.x")
@ExperimentalUnsignedTypes
public fun UIntArray.swapBytes() {
    for (i in indices) this[i] = this[i].swapBytes()
}

/**
 * Create a new array where the byte order of all values is reversed.
 *
 * @see ByteOrder
 * @see isBigEndianSystem
 * @see isLittleEndianSystem
 */
// @ExperimentalStdlibApi
// @SinceKotlin("1.x")
@ExperimentalUnsignedTypes
public fun UIntArray.swappedBytes(): UIntArray =
    UIntArray(size) { this[it].swapBytes() }

// endregion UInt --------------------------------------------------------------
// region ULong ----------------------------------------------------------------

/**
 * Reverse the byte order of this value.
 *
 * @see ByteOrder
 * @see isBigEndianSystem
 * @see isLittleEndianSystem
 */
// @ExperimentalStdlibApi
// @SinceKotlin("1.x")
@ExperimentalUnsignedTypes
public fun ULong.swapBytes(): ULong =
    toLong().swapBytes().toULong()

/**
 * Reverse the byte order of all values in this array.
 *
 * @see ByteOrder
 * @see isBigEndianSystem
 * @see isLittleEndianSystem
 */
// @ExperimentalStdlibApi
// @SinceKotlin("1.x")
@ExperimentalUnsignedTypes
public fun ULongArray.swapBytes() {
    for (i in indices) this[i] = this[i].swapBytes()
}

/**
 * Create a new array where the byte order of all values is reversed.
 *
 * @see ByteOrder
 * @see isBigEndianSystem
 * @see isLittleEndianSystem
 */
// @ExperimentalStdlibApi
// @SinceKotlin("1.x")
@ExperimentalUnsignedTypes
public fun ULongArray.swappedBytes(): ULongArray =
    ULongArray(size) { this[it].swapBytes() }

// endregion ULong -------------------------------------------------------------
