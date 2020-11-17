package com.benasher44.uuid

import platform.posix.read

internal actual fun getRandomUuidBytes(): ByteArray {
    return bytesWithURandomFd { fd, bytePtr ->
        read(fd, bytePtr, UUID_BYTES.toUInt())
    }
}
