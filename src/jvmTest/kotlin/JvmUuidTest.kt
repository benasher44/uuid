package com.benasher44.uuid

import java.io.File
import kotlin.test.assertEquals
import org.junit.Test

@ExperimentalStdlibApi
class JvmUuidTest {

    @Test
    fun `should set correct version and variant bits`() {
        val uuidL = uuid4()
        val platformUuid = java.util.UUID(uuidL.mostSignificantBits, uuidL.leastSignificantBits)

        assertEquals(4, platformUuid.version())
        assertEquals(2, platformUuid.variant())
    }

    @Test
    fun `should match platform UUID string`() {
        val uuidL = uuid4()
        val platformUuid = java.util.UUID(uuidL.mostSignificantBits, uuidL.leastSignificantBits)

        assertEquals(platformUuid.toString(), uuidL.toString())
    }

    @Test
    fun `should match platform UUID bytes`() {
        val uuidL = uuid4()
        val platformUuid = java.util.UUID.fromString(uuidL.toString())

        assertEquals(platformUuid.mostSignificantBits, uuidL.mostSignificantBits)
        assertEquals(platformUuid.leastSignificantBits, uuidL.leastSignificantBits)
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
    for (row in File("$PROJECT_DIR_ROOT/$path").readLines()) {
        if (row.isEmpty()) continue
        val (namespaceStr, name, resultStr) = row.split(",")
        enumerationLambda(uuidFrom(namespaceStr), name, uuidFrom(resultStr))
    }
}
