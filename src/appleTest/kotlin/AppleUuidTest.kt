package com.benasher44.uuid

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.readBytes
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSUUID
import platform.Foundation.dataWithContentsOfFile
import kotlin.native.concurrent.isFrozen
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CocoaUuidTest {
    @Test
    fun `UUID toString matches NSUUID`() {
        val uuidL = uuid4()
        val nativeUuidString = uuidL.bytes.usePinned {
            NSUUID(it.addressOf(0).reinterpret()).UUIDString
        }.lowercase()
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

    @Test
    fun `test uuid5`() {
        enumerateUuid5Data { namespace, name, result ->
            assertEquals(result, uuid5Of(namespace, name))
        }
    }

    @Test
    fun `test uuid3`() {
        enumerateUuid3Data { namespace, name, result ->
            assertEquals(result, uuid3Of(namespace, name))
        }
    }
}

private fun enumerateUuid3Data(enumerationLambda: (namespace: Uuid, name: String, result: Uuid) -> Unit) {
    enumerateData("src/commonTest/data/uuid3.txt", enumerationLambda)
}

private fun enumerateUuid5Data(enumerationLambda: (namespace: Uuid, name: String, result: Uuid) -> Unit) {
    enumerateData("src/commonTest/data/uuid5.txt", enumerationLambda)
}

private fun enumerateData(path: String, enumerationLambda: (namespace: Uuid, name: String, result: Uuid) -> Unit) {
    val data = NSData.dataWithContentsOfFile("$PROJECT_DIR_ROOT/$path")!!
    val str = memScoped {
        data.bytes!!.getPointer(this)
            .reinterpret<ByteVar>()
            .readBytes(data.length.toInt())
            .decodeToString()
    }
    for (row in str.split("\n")) {
        if (row.isEmpty()) continue
        val (namespaceStr, name, resultStr) = row.split(",")
        enumerationLambda(uuidFrom(namespaceStr), name, uuidFrom(resultStr))
    }
}
