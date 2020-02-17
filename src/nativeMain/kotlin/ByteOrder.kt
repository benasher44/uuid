package com.benasher44.uuid

import kotlin.native.concurrent.SharedImmutable
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.IntVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.value

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
