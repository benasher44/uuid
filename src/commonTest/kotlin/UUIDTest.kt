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
    fun `generates_a_UUID`() {
        val uuid = UUID()
        val uuidString = uuid.toString()
        assertEquals(uuidString.length, UUID_STRING_LENGTH)
        assertNull(UUID.hyphenIndices.find { uuidString[it] != '-' })
        for (range in UUID.uuidCharRanges) {
            assertNull(range.find { !isValidUUIDChar(uuidString[it]) })
        }
    }

    @Test
    fun `parses_a_UUID_from_a_string`() {
        val uuid = UUID()
        val uuidFromStr = UUID.parse(uuid.toString())!!
        assertEquals(uuid, uuidFromStr)
        // double check hashcode equality, while we're here
        assertEquals(uuid.hashCode(), uuidFromStr.hashCode())
    }

    @Test
    fun `throws_when_passed_invalid_number_of_bytes`() {
        assertFails { UUID(ByteArray(17)) }
        assertFails { UUID(ByteArray(15)) }
    }
}
