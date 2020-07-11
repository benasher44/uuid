package com.benasher44.uuid

import kotlin.experimental.and
import kotlin.experimental.or

internal fun ByteArray.swapIntByteOrderAt(index: Int) {
    var int = 0
    for (i in 0 until 3) {
        int = int.or(this[index + i].toInt().shl(i))
    }
    val newInt = htonl(int)
    for (i in 0 until 3) {
        this[index + i] = newInt.shr(i).and(0xF).toByte()
    }
}

internal fun ByteArray.swapShortByteOrderAt(index: Int) {
    var short = this[index].toShort()
    short = short.or(this[index + 1].toInt().shl(1).toShort())

    val newShort = htons(short)
    this[index] = newShort.and(0xF.toShort()).toByte()
    this[index + 1] = newShort
        .toInt().shr(1).toShort()
        .and(0xF.toShort())
        .toByte()
}
