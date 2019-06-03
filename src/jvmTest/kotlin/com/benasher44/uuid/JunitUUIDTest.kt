package com.benasher44.uuid

import org.junit.Test
import kotlin.test.assertEquals

class JunitUUIDTest {

    @Test
    fun shouldSetCorrectVersionBit() {
        val uuidL = UUID()
        val platformUuid = java.util.UUID(uuidL.mostSignificantBits, uuidL.leastSignificantBits)

        assertEquals(platformUuid.version(), 4)
    }

    @Test
    fun shouldSetCorrectVariantBit() {
        val uuidL = UUID()
        val platformUuid = java.util.UUID(uuidL.mostSignificantBits, uuidL.leastSignificantBits)

        assertEquals(platformUuid.variant(), 2)
    }

    @Test
    fun shouldMatchPlatformUUIDString() {
        val uuidL = UUID()
        val platformUuid = java.util.UUID(uuidL.mostSignificantBits, uuidL.leastSignificantBits)

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