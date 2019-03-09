import com.benasher44.uuid.UUID
import com.benasher44.uuid.UUID_STRING_LENGTH
import kotlin.test.*

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
}
