import com.benasher44.uuid.UUID
import com.benasher44.uuid.toNativeString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class UUIDTest {
    @Test
    fun `generates a UUID`() {
        val uuid = UUID()
        assertEquals(uuid.toString().length, 36)
        assertEquals(uuid.toString(), uuid.toNativeString().toLowerCase())
    }

    @Test
    fun `parses a UUID from a string`() {
        val uuid = UUID()
        assertEquals(uuid, UUID.parse(uuid.toString())!!)
    }
}
