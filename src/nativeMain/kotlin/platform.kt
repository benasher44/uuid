package com.benasher44.uuid

import kotlin.native.concurrent.freeze
import kotlin.native.FreezingIsDeprecated

@OptIn(FreezingIsDeprecated::class).
internal actual fun <T> T.freeze() = this.freeze()
