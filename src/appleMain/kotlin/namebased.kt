
package com.benasher44.uuid

import kotlinx.cinterop.addressOf
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import platform.CoreCrypto.CC_MD5
import platform.CoreCrypto.CC_MD5_DIGEST_LENGTH
import platform.CoreCrypto.CC_SHA1
import platform.CoreCrypto.CC_SHA1_DIGEST_LENGTH

/**
 * Constructs a "Name-Based" version 3 [UUID][Uuid].
 *
 * Version 3 UUIDs are created by combining a name and
 * a namespace using the MD5 hash function.
 *
 * @param namespace for the "Name-Based" UUID
 * @param name withing the namespace for the "Name-Based" UUID
 * @return New version 3 [UUID][Uuid].
 * @see <a href="https://tools.ietf.org/html/rfc4122#section-4.3">RFC 4122: Section 4.3</a>
 */
public fun uuid3Of(namespace: Uuid, name: String): Uuid =
    nameBasedUuidOf(namespace, name, AppleHasher(AppleHasher.Companion::md5Digest, 3))

/**
 * Constructs a "Name-Based" version 5 [UUID][Uuid].
 *
 * Version 5 UUIDs are created by combining a name and
 * a namespace using the SHA-1 hash function.
 *
 * @param namespace for the "Name-Based" UUID
 * @param name withing the namespace for the "Name-Based" UUID
 * @return New version 5 [UUID][Uuid].
 * @see <a href="https://tools.ietf.org/html/rfc4122#section-4.3">RFC 4122: Section 4.3</a>
 */
public fun uuid5Of(namespace: Uuid, name: String): Uuid =
    nameBasedUuidOf(namespace, name, AppleHasher(AppleHasher.Companion::sha1Digest, 5))

private class AppleHasher(
    private val digestFunc: (ByteArray) -> ByteArray,
    override val version: Int,
) : UuidHasher {
    private var data = ByteArray(0)

    override fun update(input: ByteArray) {
        val prevLength = data.size
        data = data.copyOf(data.size + input.size)
        input.copyInto(data, prevLength)
    }

    override fun digest(): ByteArray {
        return digestFunc(data)
    }

    companion object {
        fun sha1Digest(data: ByteArray): ByteArray {
            return ByteArray(CC_SHA1_DIGEST_LENGTH).also { bytes ->
                bytes.usePinned { digestPin ->
                    data.usePinned { dataPin ->
                        CC_SHA1(dataPin.addressOf(0), data.size.toUInt(), digestPin.addressOf(0).reinterpret())
                    }
                }
            }
        }

        fun md5Digest(data: ByteArray): ByteArray {
            return ByteArray(CC_MD5_DIGEST_LENGTH).also { bytes ->
                bytes.usePinned { digestPin ->
                    data.usePinned { dataPin ->
                        CC_MD5(dataPin.addressOf(0), data.size.toUInt(), digestPin.addressOf(0).reinterpret())
                    }
                }
            }
        }
    }
}
