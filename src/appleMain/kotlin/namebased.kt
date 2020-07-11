package com.benasher44.uuid

import kotlinx.cinterop.addressOf
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import platform.CoreCrypto.CC_MD5
import platform.CoreCrypto.CC_MD5_DIGEST_LENGTH
import platform.CoreCrypto.CC_SHA1
import platform.CoreCrypto.CC_SHA1_DIGEST_LENGTH
import platform.Foundation.NSMutableData
import platform.Foundation.appendBytes

private class AppleHasher(
    private val digestFunc: (NSMutableData) -> ByteArray,
    override val version: Int
) : UuidHasher {
    private val data = NSMutableData()

    override fun update(input: ByteArray) {
        input.usePinned {
            data.appendBytes(
                it.addressOf(0),
                input.size.toUInt()
            )
        }
    }

    override fun digest(): ByteArray {
        return digestFunc(data)
    }

    companion object {
        fun sha1Digest(data: NSMutableData): ByteArray {
            return ByteArray(CC_SHA1_DIGEST_LENGTH).also { bytes ->
                bytes.usePinned {
                    CC_SHA1(data.mutableBytes, data.length, it.addressOf(0).reinterpret())
                }
            }
        }

        fun md5Digest(data: NSMutableData): ByteArray {
            return ByteArray(CC_MD5_DIGEST_LENGTH).also { bytes ->
                bytes.usePinned {
                    CC_MD5(data.mutableBytes, data.length, it.addressOf(0).reinterpret())
                }
            }
        }
    }
}

@ExperimentalStdlibApi
public fun uuid5Of(namespace: Uuid, name: String): Uuid =
    nameBasedUuidOf(namespace, name, AppleHasher(AppleHasher.Companion::sha1Digest, 5))

@ExperimentalStdlibApi
public fun uuid3Of(namespace: Uuid, name: String): Uuid =
    nameBasedUuidOf(namespace, name, AppleHasher(AppleHasher.Companion::md5Digest, 3))
