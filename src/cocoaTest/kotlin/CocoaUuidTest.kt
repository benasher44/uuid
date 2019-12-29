package com.benasher44.uuid

import kotlin.native.concurrent.isFrozen
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import platform.Foundation.NSUUID

class CocoaUuidTest {
    @Test
    fun `UUID.toString() matches NSUUID`() {
        val uuidL = uuid4()
        val nativeUuidString = uuidL.bytes.usePinned {
            NSUUID(it.addressOf(0).reinterpret()).UUIDString
        }.toLowerCase()
        assertEquals(uuidL.toString(), nativeUuidString)
    }

    @Test
    fun `UUID bytes match NSUUID`() {
        val uuidL = uuid4()
        val nativeUuid = NSUUID(uuidL.toString())
        val nativeBytes = ByteArray(UUID_BYTES)
        nativeBytes.usePinned {
            nativeUuid.getUUIDBytes(it.addressOf(0).reinterpret())
        }
        assertTrue(uuidL.bytes.contentEquals(nativeBytes))
    }

    @Test
    fun `UUID is frozen after initialization`() {
        assertTrue(uuid4().isFrozen)
    }
}
