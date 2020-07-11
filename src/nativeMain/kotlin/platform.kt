package com.benasher44.uuid

import platform.builtin.builtin_bswap16
import platform.builtin.builtin_bswap32

internal actual fun htonl(value: Int): Int {
    if (!Platform.isLittleEndian) return value
    return builtin_bswap32(value)
}

internal actual fun htons(value: Short): Short {
    if (!Platform.isLittleEndian) return value
    return builtin_bswap16(value)
}
