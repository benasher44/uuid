import com.benasher44.uuid.UUID
import com.benasher44.uuid.UUID_BYTES
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import platform.Foundation.NSUUID
import kotlin.native.concurrent.isFrozen
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
        val nativeBytes = ByteArray(UUID_BYTES)
        nativeBytes.usePinned {
            nativeUuid.getUUIDBytes(it.addressOf(0).reinterpret())
        }
        assertTrue(uuidL.uuid.contentEquals(nativeBytes))
    }

    @Test
    fun `UUID is frozen after initialization`() {
        assertTrue(UUID().isFrozen)
    }
}