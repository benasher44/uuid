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

    @Test fun variants() {
        assertEquals(0, UUID.parse("00000000-0000-0000-0000-000000000000")!!.variant, "Nil or NCS")
        assertEquals(2, UUID.parse("00000000-0000-0000-8000-000000000000")!!.variant, "RFC 4122")
        assertEquals(6, UUID.parse("00000000-0000-0000-c000-000000000000")!!.variant, "Microsoft")
        assertEquals(7, UUID.parse("00000000-0000-0000-e000-000000000000")!!.variant, "Future")
    }

    @Test fun versions() {
        assertEquals(0, UUID.parse("00000000-0000-0000-0000-000000000000")!!.version, "Nil")
        assertEquals(1, UUID.parse("00000000-0000-1000-0000-000000000000")!!.version, "time-based")
        assertEquals(2, UUID.parse("00000000-0000-2000-0000-000000000000")!!.version, "DCE security")
        assertEquals(3, UUID.parse("00000000-0000-3000-0000-000000000000")!!.version, "name-based using MD5 hashing")
        assertEquals(4, UUID.parse("00000000-0000-4000-0000-000000000000")!!.version, "random or pseudo-random")
        assertEquals(5, UUID.parse("00000000-0000-5000-0000-000000000000")!!.version, "name-based using SHA-1 hashing")

        assertEquals(6, UUID.parse("00000000-0000-6000-0000-000000000000")!!.version, "future #1")
        assertEquals(7, UUID.parse("00000000-0000-7000-0000-000000000000")!!.version, "future #2")
        assertEquals(8, UUID.parse("00000000-0000-8000-0000-000000000000")!!.version, "future #3")
        assertEquals(9, UUID.parse("00000000-0000-9000-0000-000000000000")!!.version, "future #4")
        assertEquals(10, UUID.parse("00000000-0000-a000-0000-000000000000")!!.version, "future #5")
        assertEquals(11, UUID.parse("00000000-0000-b000-0000-000000000000")!!.version, "future #6")
        assertEquals(12, UUID.parse("00000000-0000-c000-0000-000000000000")!!.version, "future #7")
        assertEquals(13, UUID.parse("00000000-0000-d000-0000-000000000000")!!.version, "future #8")
        assertEquals(14, UUID.parse("00000000-0000-e000-0000-000000000000")!!.version, "future #9")
        assertEquals(15, UUID.parse("00000000-0000-f000-0000-000000000000")!!.version, "future #10")
    }
}
