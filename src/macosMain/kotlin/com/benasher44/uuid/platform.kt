package com.benasher44.uuid

import kotlinx.cinterop.UByteVar
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import platform.Foundation.NSUUID
import platform.posix.O_RDONLY
import platform.posix.close
import platform.posix.open
import platform.posix.read

internal actual fun getRandomUUIDBytes(): ByteArray {
    val bytes = ByteArray(UUID_BYTES)
    val fd = open("/dev/urandom", O_RDONLY)
    bytes.usePinned {
        read(fd, it.addressOf(0), UUID_BYTES.toULong())
    }
    close(fd)
    return bytes
}

actual fun UUID.toNativeString(): String {
    uuid.usePinned {
        return NSUUID(it.addressOf(0).reinterpret<UByteVar>()).description!!
    }
}