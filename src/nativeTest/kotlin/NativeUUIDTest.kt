import com.benasher44.uuid.UUID
import com.benasher44.uuid.UUID_BYTES
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.UByteVar
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.toCValues
import kotlinx.cinterop.usePinned
import platform.Foundation.NSUUID
import platform.posix.memcmp
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

        assertTrue {
            memScoped {
                val bytes = allocArray<UByteVar>(UUID_BYTES)
                nativeUuid.getUUIDBytes(bytes)
                memcmp(uuidL.uuid.toCValues(), bytes.reinterpret<ByteVar>(), UUID_BYTES.toULong()) == 0
            }
        }
    }

    @Test
    fun `UUIS is frozen after initialization`() {
        assertTrue(UUID().isFrozen)
    }
}