package au.lovecraft.currency

import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CurrencyAmountSerializationTest {

  @Test
  fun `encodes to JSON string without currency symbol`() {
    val amount = CurrencyAmount.fromString("12.34")!!
    val json = Json.encodeToString(amount)
    assertEquals("\"12.34\"", json)
  }

  @Test
  fun `roundtrips via JSON`() {
    val original = CurrencyAmount.fromString("9876543.21")!!
    val json = Json.encodeToString(original)
    val decoded = Json.decodeFromString<CurrencyAmount>(json)
    assertEquals(original, decoded)
  }

  @Test
  fun `zero encodes and decodes`() {
    val json = Json.encodeToString(CurrencyAmount.Zero)
    assertEquals("\"0\"", json)
    val decoded = Json.decodeFromString<CurrencyAmount>(json)
    assertEquals(CurrencyAmount.Zero, decoded)
  }

  @Test
  fun `decoding with currency symbol fails`() {
    assertFailsWith<SerializationException> {
      Json.decodeFromString<CurrencyAmount>("\"$12.34\"")
    }
  }
}
