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
    fun generates_a_UUID() {
        val uuid = UUID()
        val uuidString = uuid.toString()
        assertEquals(uuidString.length, UUID_STRING_LENGTH)
        assertNull(UUID.hyphenIndices.find { uuidString[it] != '-' })
        for (range in UUID.uuidCharRanges) {
            assertNull(range.find { !isValidUUIDChar(uuidString[it]) })
        }
    }

    @Test
    fun parses_a_UUID_from_a_string() {
        val uuid = UUID()
        val uuidFromStr = UUID.parse(uuid.toString())!!
        assertEquals(uuid, uuidFromStr)
        // double check hashcode equality, while we're here
        assertEquals(uuid.hashCode(), uuidFromStr.hashCode())
    }

    @Test
    fun throws_when_passed_invalid_number_of_bytes() {
        assertFails { UUID(ByteArray(17)) }
        assertFails { UUID(ByteArray(15)) }
    }

    @Test
    fun provides_higher_and_lower_bits() {
        val uuid = UUID.parse("c480d6ab-cb0c-427b-a9a6-19c5f8a146bd")!!
        assertEquals(-6222257497095190851, uuid.leastSignificantBits)
        assertEquals(-4287190811922382213, uuid.mostSignificantBits)
    }

    @Test
    fun generates_a_UUID_with_correct_version_and_variant_bits() {
        val uuid = UUID()

        assertEquals(4, uuid.version)
        assertEquals(2, uuid.variant)
    }
}
