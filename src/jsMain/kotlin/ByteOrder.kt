package com.benasher44.uuid

import org.khronos.webgl.Uint32Array
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get
import org.khronos.webgl.set

actual enum class ByteOrder {
    BIG_ENDIAN, LITTLE_ENDIAN;

    actual companion object {
        actual val native by lazy {
            val array = Uint8Array(4)
            val view = Uint32Array(array.buffer)
            view[0] = 1
            if ((view[0] and array[0].toInt()) == 0) LITTLE_ENDIAN else BIG_ENDIAN
        }
    }
}
