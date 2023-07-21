
package com.benasher44.uuid

import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.convert
import platform.posix.read

@OptIn(UnsafeNumber::class)
internal actual fun getRandomUuidBytes(): ByteArray {
    return bytesWithURandomFd { fd, bytePtr ->
        read(fd, bytePtr, UUID_BYTES.convert())
    }
}
