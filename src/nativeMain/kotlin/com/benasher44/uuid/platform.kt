package com.benasher44.uuid

import kotlin.native.concurrent.freeze

internal actual fun <T> T.freeze() = this.freeze()
