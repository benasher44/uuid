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

}