package com.benasher44.uuid

import kotlin.random.Random

internal actual fun getRandomUuidBytes() = Random.Default.nextBytes(UUID_BYTES)

internal actual fun <T> T.freeze() = this

internal actual fun htonl(value: Int): Int {
    TODO("Unsupported for JS. PRs welcome!")
}

internal actual fun htons(value: Short): Short {
    TODO("Unsupported for JS. PRs welcome!")
}
