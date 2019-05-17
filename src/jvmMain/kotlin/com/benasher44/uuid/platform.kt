package com.benasher44.uuid

import java.security.SecureRandom
import kotlin.random.asKotlinRandom

internal actual fun getRandomUUIDBytes(): ByteArray = SecureRandom().asKotlinRandom().nextBytes(UUID_BYTES)

internal actual fun <T> T.freeze() = this