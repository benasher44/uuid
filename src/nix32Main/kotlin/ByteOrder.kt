package com.benasher44.uuid

// @ExperimentalStdlibApi
// @SinceKotlin("1.x")
public actual enum class ByteOrder {
    BIG_ENDIAN, LITTLE_ENDIAN;

    actual fun native(): ByteOrder {
        error("Not yet implemented")
    }
}
