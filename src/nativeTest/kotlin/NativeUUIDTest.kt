import com.benasher44.uuid.UUID
import com.benasher44.uuid.UUID_BYTES
import kotlinx.cinterop.*
import platform.Foundation.NSUUID
import platform.posix.memcmp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NativeUUIDTest {
    @Test
    fun `UUID.toString() matches macOS`() {
        val uuidL = UUID()
        val nativeUuidString = uuidL.uuid.usePinned {
            NSUUID(it.addressOf(0).reinterpret()).UUIDString
        }.toLowerCase()
        assertEquals(uuidL.toString(), nativeUuidString)
    }

    @Test
    fun `UUID bytes match macOS`() {
        val uuidL = UUID()
        val nativeUuid = NSUUID(uuidL.toString())

        assertTrue {
            memScoped {
                val bytes = allocArray<UByteVar>(UUID_BYTES)
                nativeUuid.getUUIDBytes(bytes)
                memcmp(uuidL.uuid.toCValues(), bytes.reinterpret<ByteVar>(), UUID_BYTES.toULong()) == 0
            }
        }
    }
}