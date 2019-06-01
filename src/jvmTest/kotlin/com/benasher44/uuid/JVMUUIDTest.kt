package com.benasher44.uuid

import org.junit.Test
import kotlin.test.assertEquals

class JVMUUIDTest {

    @Test
    fun `sets correct version bit`() {
        val uuidL = UUID()
        val platformUuid = java.util.UUID(uuidL.mostSignificantBits, uuidL.leastSignificantBits)

        assertEquals(4, platformUuid.version())
    }

    @Test
    fun `matches platform UUID string`() {
        val uuidL = UUID()
        val platformUuid = java.util.UUID(uuidL.mostSignificantBits, uuidL.leastSignificantBits)

        assertEquals(platformUuid.toString(), uuidL.toString())
    }

    @Test
    fun `matches platform UUID bytes`() {
        val uuidL = UUID()
        val platformUuid = java.util.UUID.fromString(uuidL.toString())

        assertEquals(platformUuid.mostSignificantBits, uuidL.mostSignificantBits)
        assertEquals(platformUuid.leastSignificantBits, uuidL.leastSignificantBits)
    }
}