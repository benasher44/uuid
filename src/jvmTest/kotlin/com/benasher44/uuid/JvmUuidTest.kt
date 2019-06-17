package com.benasher44.uuid

import org.junit.Test
import kotlin.test.assertEquals

class JvmUuidTest {

    @Test
    fun `should set correct version and variant bits`() {
        val uuidL = Uuid()
        val platformUuid = java.util.UUID(uuidL.msb, uuidL.lsb)

        assertEquals(4, platformUuid.version())
        assertEquals(2, platformUuid.variant())
    }

    @Test
    fun `should match platform UUID string`() {
        val uuidL = Uuid()
        val platformUuid = java.util.UUID(uuidL.msb, uuidL.lsb)

        assertEquals(platformUuid.toString(), uuidL.toString())
    }

    @Test
    fun `should match platform UUID bytes`() {
        val uuidL = Uuid()
        val platformUuid = java.util.UUID.fromString(uuidL.toString())

        assertEquals(platformUuid.mostSignificantBits, uuidL.msb)
        assertEquals(platformUuid.leastSignificantBits, uuidL.lsb)
    }
}
