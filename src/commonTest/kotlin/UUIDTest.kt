import com.benasher44.uuid.UUID
import com.benasher44.uuid.UUID_STRING_LENGTH
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNull

class UUIDTest {

    private val uuidChars = HashSet(UUID.uuidChars)

    private fun isValidUUIDChar(char: Char) = uuidChars.contains(char)

    @Test
    fun `generates a UUID`() {
        val uuid = UUID()
        val uuidString = uuid.toString()
        assertEquals(uuidString.length, UUID_STRING_LENGTH)
        assertNull(UUID.hyphenIndices.find { uuidString[it] != '-' })
        for (range in UUID.uuidCharRanges) {
            assertNull(range.find { !isValidUUIDChar(uuidString[it]) })
        }
    }

    @Test
    fun `parses a UUID from a string`() {
        val uuid = UUID()
        val uuidFromStr = UUID.parse(uuid.toString())!!
        assertEquals(uuid, uuidFromStr)
        // double check hashcode equality, while we're here
        assertEquals(uuid.hashCode(), uuidFromStr.hashCode())
    }

    @Test
    fun `throws when passed invalid number of bytes`() {
        assertFails { UUID(ByteArray(17)) }
        assertFails { UUID(ByteArray(15)) }
    }

    @Test
    fun `generates a UUID with correct version bit`() {
        val uuid = UUID()

        assertEquals(uuid.version(), 4)
    }
}
