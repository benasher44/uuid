import com.benasher44.uuid.UUID
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
            NSUUID(it.addressOf(0).reinterpret()).description!!
        }.toLowerCase()
        assertEquals(uuidL.toString(), nativeUuidString)
    }

    @Test
    fun `UUID bytes match macOS`() {
        val uuidL = UUID()
        val nativeUuid = NSUUID(uuidL.toString())

        assertTrue {
            memScoped {
                val bytes = allocArray<UByteVar>(16)
                nativeUuid.getUUIDBytes(bytes)
                memcmp(uuidL.uuid.toCValues(), bytes.reinterpret<ByteVar>(), 16) == 0
            }
        }

    }
}