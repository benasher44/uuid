package com.benasher44.uuid

import kotlinx.cinterop.UIntVar
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.usePinned
import platform.windows.CloseHandle
import platform.windows.CreateFileW
import platform.windows.FILE_ATTRIBUTE_NORMAL
import platform.windows.FILE_SHARE_READ
import platform.windows.GENERIC_READ
import platform.windows.GetFileSizeEx
import platform.windows.GetLastError
import platform.windows.INVALID_HANDLE_VALUE
import platform.windows.LARGE_INTEGER
import platform.windows.OPEN_EXISTING
import platform.windows.ReadFile
import kotlin.test.Test
import kotlin.test.assertEquals

class MingwUuidTest {

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
    enumerateData("src\\commonTest\\data\\uuid3.txt", enumerationLambda)
}

private fun enumerateUuid5Data(enumerationLambda: (namespace: Uuid, name: String, result: Uuid) -> Unit) {
    enumerateData("src\\commonTest\\data\\uuid5.txt", enumerationLambda)
}

private fun enumerateData(path: String, enumerationLambda: (namespace: Uuid, name: String, result: Uuid) -> Unit) {
    val str = loadString("$PROJECT_DIR_ROOT\\$path")
    for (row in str.split("\n")) {
        if (row.isEmpty()) continue
        val (namespaceStr, name, resultStr) = row.split(",")
        enumerationLambda(uuidFrom(namespaceStr), name, uuidFrom(resultStr.trimEnd()))
    }
}

private fun loadString(path: String): String {
    val handle = CreateFileW(
        path,
        GENERIC_READ,
        FILE_SHARE_READ.convert(),
        null,
        OPEN_EXISTING.convert(),
        FILE_ATTRIBUTE_NORMAL.convert(),
        null,
    )
    check(handle != INVALID_HANDLE_VALUE) { "Error: ${GetLastError()}" }

    return try {
        memScoped {
            val rawSize = alloc<LARGE_INTEGER>()
            check(GetFileSizeEx(handle, rawSize.ptr) != 0) { "Error: ${GetLastError()}" }

            val size = rawSize.QuadPart.toUInt()

            val buf = ByteArray(size.toInt())
            buf.usePinned { pinned ->
                val bytesRead = alloc<UIntVar>()
                check(ReadFile(handle, pinned.addressOf(0), size, bytesRead.ptr, null) != 0) {
                    "Error: ${GetLastError()}"
                }
            }
            buf.decodeToString()
        }
    } finally {
        CloseHandle(handle)
    }
}
