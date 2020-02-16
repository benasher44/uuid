package com.benasher44.uuid

import com.benasher44.uuid.ByteOrder.BIG_ENDIAN
import com.benasher44.uuid.ByteOrder.LITTLE_ENDIAN
import java.nio.ByteBuffer
import java.nio.ByteOrder.nativeOrder
import java.nio.ByteOrder as PlatformByteOrder
import java.nio.ByteOrder.BIG_ENDIAN as NIO_BIG_ENDIAN
import java.nio.ByteOrder.LITTLE_ENDIAN as NIO_LITTLE_ENDIAN

actual enum class ByteOrder {
    BIG_ENDIAN, LITTLE_ENDIAN;

    actual fun native() = nativeOrder().into()
}

/**
 * Converts the Kotlin [ByteOrder] to its Java [ByteOrder][PlatformByteOrder]
 * equivalent.
 */
// @ExperimentalStdlibApi
// @SinceKotlin("1.x")
internal fun ByteOrder.into(): PlatformByteOrder =
    when (this) {
        BIG_ENDIAN -> NIO_BIG_ENDIAN
        LITTLE_ENDIAN -> NIO_LITTLE_ENDIAN
    }

/**
 * Converts the Java [ByteOrder][PlatformByteOrder] to its Kotlin [ByteOrder]
 * equivalent.
 */
// @ExperimentalStdlibApi
// @SinceKotlin("1.x")
internal fun PlatformByteOrder.into(): ByteOrder =
    when (this) {
        NIO_BIG_ENDIAN -> BIG_ENDIAN
        NIO_LITTLE_ENDIAN -> LITTLE_ENDIAN
        else -> error("internal error: entered unreachable code")
    }

/**
 * Convenience extension for [ByteBuffer] to accept Kotlin‘s [ByteOrder].
 */
// @ExperimentalStdlibApi
// @SinceKotlin("1.x")
@Suppress("NOTHING_TO_INLINE") // @kotlin.internal.InlineOnly
internal inline fun ByteBuffer.order(order: ByteOrder): ByteBuffer =
    order(order.into())
