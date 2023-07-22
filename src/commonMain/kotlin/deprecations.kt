package com.benasher44.uuid

@Deprecated("Use `Uuid` instead.", ReplaceWith("Uuid"))
public typealias UUID = Uuid

@Deprecated(
    message = "Use uuidFrom() instead. This will be removed in the next release.",
    replaceWith = ReplaceWith("Uuid.bytes"),
)
public val Uuid.uuid: ByteArray
    get() = bytes
