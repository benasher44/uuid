package com.benasher44.uuid

expect internal fun getRandomUUIDBytes(): ByteArray

expect fun UUID.toNativeString(): String