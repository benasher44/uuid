package com.benasher44.uuid

import kotlinx.cinterop.addressOf
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import platform.windows.BCRYPT_USE_SYSTEM_PREFERRED_RNG
import platform.windows.BCryptGenRandom

internal actual fun getRandomUUIDBytes(): ByteArray {
    val bytes = ByteArray(UUID_BYTES)
    bytes.usePinned {
        BCryptGenRandom(null, it.addressOf(0).reinterpret(), UUID_BYTES.toUInt(), BCRYPT_USE_SYSTEM_PREFERRED_RNG)
    }
    return bytes
}