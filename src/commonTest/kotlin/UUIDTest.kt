import com.benasher44.uuid.UUID
import kotlin.test.*

class UUIDTest {
    // Ranges of non-hyphen characters
    private val uuidCharRanges: List<IntRange> = listOf(
        0 until 8,
        9 until 13,
        14 until 18,
        19 until 23,
        24 until 36
    )

    // Indices of the hyphen characters in a UUID string
    private val hyphenIndices = listOf(8, 13, 18, 23)

    private fun isValidUUIDChar(char: Char): Boolean {
        return (char in CharRange('0', '9') || char in CharRange('a', 'f'))
    }

    @Test
    fun `generates_a_UUID`() {
        val uuid = UUID()
        val uuidString = uuid.toString()
        assertEquals(uuidString.length, 36)
        assertNull(hyphenIndices.find { uuidString[it] != '-' })
        for (range in uuidCharRanges) {
            assertNull(range.find { !isValidUUIDChar(uuidString[it]) })
        }
    }

    @Test
    fun `parses_a_UUID_from_a_string`() {
        val uuid = UUID()
        assertEquals(uuid, UUID.parse(uuid.toString())!!)
    }
}
