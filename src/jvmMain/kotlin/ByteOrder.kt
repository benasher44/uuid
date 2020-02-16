package com.benasher44.uuid

import com.benasher44.uuid.ByteOrder.BIG_ENDIAN
import com.benasher44.uuid.ByteOrder.LITTLE_ENDIAN
import java.nio.ByteBuffer

actual enum class ByteOrder {
    BIG_ENDIAN, LITTLE_ENDIAN;

    actual companion object {
        actual inline val native: ByteOrder
            get() = java.nio.ByteOrder.nativeOrder().toKotlinByteOrder()
    }
}

/**
 * Converts [ByteOrder] to its [java.nio.ByteOrder] equivalent.
 */
// @ExperimentalStdlibApi
// @SinceKotlin("1.x")
public fun ByteOrder.toJavaByteOrder(): java.nio.ByteOrder =
    when (this) {
        BIG_ENDIAN -> java.nio.ByteOrder.BIG_ENDIAN
        LITTLE_ENDIAN -> java.nio.ByteOrder.LITTLE_ENDIAN
    }

/**
 * Converts [java.nio.ByteOrder] to its [ByteOrder] equivalent.
 */
// @ExperimentalStdlibApi
// @SinceKotlin("1.x")
public fun java.nio.ByteOrder.toKotlinByteOrder(): ByteOrder =
    when (this) {
        java.nio.ByteOrder.BIG_ENDIAN -> BIG_ENDIAN
        java.nio.ByteOrder.LITTLE_ENDIAN -> LITTLE_ENDIAN
        else -> error("internal error: entered unreachable code")
    }

/**
 * Convenience extension for [ByteBuffer] to accept Kotlin‘s [ByteOrder].
 */
// @ExperimentalStdlibApi
// @SinceKotlin("1.x")
@Suppress("NOTHING_TO_INLINE") // @kotlin.internal.InlineOnly
public inline fun ByteBuffer.order(order: ByteOrder): ByteBuffer =
    order(order.toJavaByteOrder())
