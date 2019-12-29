package com.benasher44.uuid

internal expect fun getRandomUuidBytes(): ByteArray

internal expect fun <T> T.freeze(): T
