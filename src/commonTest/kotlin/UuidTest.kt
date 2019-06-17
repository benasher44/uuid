import com.benasher44.uuid.Uuid
import com.benasher44.uuid.UUID_STRING_LENGTH
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNull

class UuidTest {

    private val uuidChars = HashSet(Uuid.uuidChars)

    private fun isValidUuidChar(char: Char) = uuidChars.contains(char)

    @Test
    fun generates_a_UUID() {
        val uuid = Uuid()
        val uuidString = uuid.toString()
        assertEquals(uuidString.length, UUID_STRING_LENGTH)
        assertNull(Uuid.hyphenIndices.find { uuidString[it] != '-' })
        for (range in Uuid.uuidCharRanges) {
            assertNull(range.find { !isValidUuidChar(uuidString[it]) })
        }
    }

    @Test
    fun parses_a_UUID_from_a_string() {
        val uuid = Uuid()
        val uuidFromStr = Uuid.parse(uuid.toString())!!
        assertEquals(uuid, uuidFromStr)
        // double check hashcode equality, while we're here
        assertEquals(uuid.hashCode(), uuidFromStr.hashCode())
    }

    @Test
    fun throws_when_passed_invalid_number_of_bytes() {
        assertFails { Uuid(ByteArray(17)) }
        assertFails { Uuid(ByteArray(15)) }
    }

    @Test
    fun provides_higher_and_lower_bits() {
        val uuid = Uuid.parse("c480d6ab-cb0c-427b-a9a6-19c5f8a146bd")!!
        assertEquals(-6222257497095190851, uuid.leastSignificantBits)
        assertEquals(-4287190811922382213, uuid.mostSignificantBits)
    }

    @Test
    fun generates_a_UUID_with_correct_version_and_variant_bits() {
        val uuid = Uuid()

        assertEquals(4, uuid.version)
        assertEquals(2, uuid.variant)
    }
}
