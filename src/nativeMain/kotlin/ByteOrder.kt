package com.benasher44.uuid

import kotlinx.cinterop.*
import kotlin.native.concurrent.SharedImmutable

// @ExperimentalStdlibApi
// @SinceKotlin("1.x")
public actual enum class ByteOrder {
    BIG_ENDIAN, LITTLE_ENDIAN;

    public actual companion object {
        @SharedImmutable
        public actual val native: ByteOrder = memScoped {
            val i = alloc<IntVar>()
            i.value = 1
            val b = i.reinterpret<ByteVar>()
            if (b.value == 0.toByte()) BIG_ENDIAN else LITTLE_ENDIAN
        }
    }
}
