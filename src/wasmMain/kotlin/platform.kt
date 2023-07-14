package com.benasher44.uuid

import kotlin.random.Random

internal actual fun getRandomUuidBytes(): ByteArray = Random.Default.nextBytes(UUID_BYTES)

internal actual fun <T> T.freeze(): T = this
