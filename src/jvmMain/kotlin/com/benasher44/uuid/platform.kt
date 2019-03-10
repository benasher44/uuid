package com.benasher44.uuid

import kotlin.random.Random

internal actual fun getRandomUUIDBytes(): ByteArray = Random.nextBytes(UUID_BYTES)

internal actual fun <T> T.freeze() = this