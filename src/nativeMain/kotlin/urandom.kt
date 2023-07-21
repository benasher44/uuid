
package com.benasher44.uuid

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.posix.O_RDONLY
import platform.posix.close
import platform.posix.errno
import platform.posix.open

internal fun bytesWithURandomFd(fdLambda: (Int, CPointer<ByteVar>) -> Unit): ByteArray {
    return ByteArray(UUID_BYTES).also { bytes ->
        val fd = open("/dev/urandom", O_RDONLY)
        check(fd != -1) { "Failed to access /dev/urandom: $errno" }
        try {
            bytes.usePinned {
                fdLambda(fd, it.addressOf(0))
            }
        } finally {
            close(fd)
        }
    }
}
