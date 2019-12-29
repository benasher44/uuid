@file:Suppress("RedundantVisibilityModifier")

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuidFrom

@Deprecated("Use `Uuid` instead.", ReplaceWith("Uuid"))
public typealias UUID = Uuid

/**
 * Parses a UUID from a String
 *
 * @param from The String, from which to deserialize the UUID
 * @return a UUID, if the string is a valid UUID string
 */
@Deprecated(
    message = "Use uuidFrom() instead. This will be removed in the next release",
    replaceWith = ReplaceWith("uuidFrom(from)"),
    level = DeprecationLevel.ERROR
)
public fun Uuid.Companion.parse(from: String): Uuid? {
    return try {
        uuidFrom(from)
    } catch (_: Throwable) {
        null
    }
}

@Deprecated(
    message = "Use uuidFrom() instead. This will be removed in the next release.",
    replaceWith = ReplaceWith("Uuid.bytes")
)
public val Uuid.uuid: ByteArray
    get() = uuidBytes
