package com.benasher44.uuid

internal expect fun getRandomUUIDBytes(): ByteArray

internal expect fun <T> T.freeze(): T