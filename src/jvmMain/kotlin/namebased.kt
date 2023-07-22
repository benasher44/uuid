package com.benasher44.uuid

import java.security.MessageDigest

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
    nameBasedUuidOf(namespace, name, JvmHasher("MD5", 3))

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
    nameBasedUuidOf(namespace, name, JvmHasher("SHA-1", 5))

private class JvmHasher(
    algorithmName: String,
    override val version: Int,
) : UuidHasher {
    private val digest = MessageDigest.getInstance(algorithmName)

    override fun update(input: ByteArray) {
        digest.update(input)
    }

    override fun digest(): ByteArray {
        return digest.digest()
    }
}
