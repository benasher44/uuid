package com.benasher44.uuid

import org.junit.Test
import kotlin.test.assertEquals

class JunitUUIDTest {

    @Test
    fun shouldMatchPlatformUUIDString() {
        val uuidL = UUID()
        val platformUuid = java.util.UUID(uuidL.mostSignificantBits,uuidL.leastSignificantBits)

        assertEquals(uuidL.toString(), platformUuid.toString())
    }

    @Test
    fun shouldMatchPlatformUUIDbytes() {
        val uuidL = UUID()
        val platformUuid = java.util.UUID.fromString(uuidL.toString())

        assertEquals(uuidL.mostSignificantBits, platformUuid.mostSignificantBits)
        assertEquals(uuidL.leastSignificantBits, platformUuid.leastSignificantBits)
    }
}