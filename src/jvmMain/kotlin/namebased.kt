package com.benasher44.uuid

import java.security.MessageDigest

private class JvmHasher(
    algorithmName: String,
    override val version: Int
) : UuidHasher {
    private val digest = MessageDigest.getInstance(algorithmName)

    override fun update(input: ByteArray) {
        digest.update(input)
    }

    override fun digest(): ByteArray {
        return digest.digest()
    }
}

@ExperimentalStdlibApi
public fun uuid5Of(namespace: Uuid, name: String): Uuid =
    nameBasedUuidOf(namespace, name, JvmHasher("SHA-1", 5))

@ExperimentalStdlibApi
public fun uuid3Of(namespace: Uuid, name: String): Uuid =
    nameBasedUuidOf(namespace, name, JvmHasher("MD5", 3))
