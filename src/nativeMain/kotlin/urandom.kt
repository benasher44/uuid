package com.benasher44.uuid

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.posix.EACCES
import platform.posix.EBADF
import platform.posix.EBUSY
import platform.posix.EDQUOT
import platform.posix.EEXIST
import platform.posix.EFAULT
import platform.posix.EFBIG
import platform.posix.EINTR
import platform.posix.EINVAL
import platform.posix.EISDIR
import platform.posix.ELOOP
import platform.posix.EMFILE
import platform.posix.ENAMETOOLONG
import platform.posix.ENFILE
import platform.posix.ENODEV
import platform.posix.ENOENT
import platform.posix.ENOMEM
import platform.posix.ENOSPC
import platform.posix.ENOTDIR
import platform.posix.ENXIO
import platform.posix.EOPNOTSUPP
import platform.posix.EOVERFLOW
import platform.posix.EPERM
import platform.posix.EROFS
import platform.posix.ETXTBSY
import platform.posix.EWOULDBLOCK
import platform.posix.O_RDONLY
import platform.posix.close
import platform.posix.open

// from https://man7.org/linux/man-pages/man2/open.2.html#ERRORS
private val errorCodes = setOf(
    EACCES,
    EBADF,
    EBUSY,
    EDQUOT,
    EEXIST,
    EFAULT,
    EFBIG,
    EINTR,
    EINVAL,
    EISDIR,
    ELOOP,
    EMFILE,
    ENAMETOOLONG,
    ENFILE,
    ENODEV,
    ENOENT,
    ENOMEM,
    ENOSPC,
    ENOTDIR,
    ENXIO,
    EOPNOTSUPP,
    EOVERFLOW,
    EPERM,
    EROFS,
    ETXTBSY,
    EWOULDBLOCK,
)

internal fun bytesWithURandomFd(fdLambda: (Int, CPointer<ByteVar>) -> Unit): ByteArray {
    return ByteArray(UUID_BYTES).also { bytes ->
        val fd = open("/dev/urandom", O_RDONLY)
        check(!errorCodes.contains(fd)) { "Failed to access /dev/urandom: $fd" }
        try {
            bytes.usePinned {
                fdLambda(fd, it.addressOf(0))
            }
        } finally {
            close(fd)
        }
    }
}
