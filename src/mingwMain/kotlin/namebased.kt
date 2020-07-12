package com.benasher44.uuid

import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.sizeOf
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.value
import platform.windows.BCRYPT_ALG_HANDLEVar
import platform.windows.BCRYPT_HASH_HANDLEVar
import platform.windows.BCryptCloseAlgorithmProvider
import platform.windows.BCryptCreateHash
import platform.windows.BCryptDestroyHash
import platform.windows.BCryptFinishHash
import platform.windows.BCryptGetProperty
import platform.windows.BCryptHashData
import platform.windows.BCryptOpenAlgorithmProvider
import platform.windows.DWORDVar
import platform.windows.GetProcessHeap
import platform.windows.HeapAlloc
import platform.windows.HeapFree
import platform.windows.PBYTEVar
import platform.windows.UCHARVar

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
@ExperimentalStdlibApi
public fun uuid3Of(namespace: Uuid, name: String): Uuid =
    nameBasedUuidOf(namespace, name, MingwHasher("MD5", 3))

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
@ExperimentalStdlibApi
public fun uuid5Of(namespace: Uuid, name: String): Uuid =
    nameBasedUuidOf(namespace, name, MingwHasher("SHA1", 5))

private class MingwHasher(
    private val algorithmName: String,
    override val version: Int
) : UuidHasher {
    private var data = ByteArray(0)

    override fun update(input: ByteArray) {
        val prevLength = data.size
        data = data.copyOf(data.size + input.size)
        input.copyInto(data, prevLength)
    }

    override fun digest(): ByteArray {
        return memScoped {
            val alg = alloc<BCRYPT_ALG_HANDLEVar>()
            val hash = alloc<BCRYPT_HASH_HANDLEVar>()
            val pbHashObj = alloc<PBYTEVar>()

            try {

                var status: Int

                status = BCryptOpenAlgorithmProvider(alg.ptr, algorithmName, null, 0)
                check(status >= 0) { "BCryptOpenAlgorithmProvider failed with code $status" }

                val cbHashObj = alloc<DWORDVar>()
                val cbData = alloc<DWORDVar>()
                status = BCryptGetProperty(
                    alg.ptr,
                    "KeyObjectLength",
                    cbHashObj.reinterpret<UCHARVar>().ptr,
                    sizeOf<DWORDVar>().toUInt(),
                    cbData.ptr,
                    0
                )
                check(status >= 0) { "BCryptGetProperty for KeyObjectLength failed with code $status" }

                pbHashObj.value =
                    HeapAlloc(GetProcessHeap()?.reinterpret(), 0, cbHashObj.value.toULong())?.reinterpret()
                check(pbHashObj.value != null) { "HeapAlloc (1) failed" }

                val cbHash = alloc<DWORDVar>()
                status = BCryptGetProperty(
                    alg.ptr,
                    "HashDigestLength",
                    cbHash.reinterpret<UCHARVar>().ptr,
                    sizeOf<DWORDVar>().toUInt(),
                    cbData.ptr,
                    0
                )
                check(status >= 0) { "BCryptGetProperty for HashDigestLength failed with code $status" }

                status = BCryptCreateHash(alg.ptr, hash.ptr, pbHashObj.value, cbHashObj.value, null, 0, 0)
                check(status >= 0) { "BCryptCreateHash failed with code $status" }

                data.usePinned {
                    status = BCryptHashData(hash.ptr, it.addressOf(0).reinterpret(), data.size.toUInt(), 0)
                }
                check(status >= 0) { "BCryptHashData failed with code $status" }

                ByteArray(cbHash.value.toInt()).also { bytes ->
                    bytes.usePinned {
                        status = BCryptFinishHash(hash.ptr, it.addressOf(0).reinterpret(), cbHash.value, 0)
                    }
                    check(status >= 0) { "BCryptFinishHash failed with code $status" }
                }
            } finally {
                if (alg.value != null) {
                    BCryptCloseAlgorithmProvider(alg.ptr, 0)
                }
                if (hash.value != null) {
                    BCryptDestroyHash(hash.ptr)
                }
                if (pbHashObj.value != null) {
                    HeapFree(GetProcessHeap(), 0, pbHashObj.ptr)
                }
            }
        }
    }
}
