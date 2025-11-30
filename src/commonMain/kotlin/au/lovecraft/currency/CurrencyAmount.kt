package au.lovecraft.currency

import au.lovecraft.math.decimal.Decimal
import au.lovecraft.math.decimal.Percent
import au.lovecraft.math.decimal.Rounding
import kotlin.jvm.JvmInline
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@JvmInline
@Serializable(with = CurrencyAmountAsStringSerializer::class)
value class CurrencyAmount private constructor(val decimal: Decimal): Comparable<CurrencyAmount> {

  fun toCents(): ULong = decimal.movePointRight(2).truncate().toULong()

  infix operator fun plus(other: CurrencyAmount) = fromDecimal(decimal + other.decimal)

  infix operator fun minus(other: CurrencyAmount) = fromDecimal(decimal - other.decimal)

  infix operator fun times(other: Decimal) = fromDecimal(decimal * other)

  infix operator fun times(other: Int) = fromDecimal(decimal * Decimal(other))

  infix operator fun times(other: Percent) = fromDecimal(decimal * other.decimal)

  infix operator fun div(other: Decimal) = fromDecimal(decimal / other)

  override infix operator fun compareTo(other: CurrencyAmount) = decimal.compareTo(other.decimal)

  fun divideAndRound(divisor: Int) = divideAndRound(Decimal(divisor))

  fun divideAndRound(divisor: Decimal) = fromDecimal(decimal.divideRounded(divisor, 2, Rounding.HalfEven))

  override fun toString(): String = decimal.toString()

  companion object {

    /**
     *  Coerces a [Decimal] to a valid [CurrencyAmount].
     *
     * Rounds to 2 decimal places by applying Banker's Rounding.
     */
    fun fromDecimal(decimal: Decimal): CurrencyAmount = CurrencyAmount(decimal.rounded(2, Rounding.HalfEven))

    /**
     * Coerce to CurrencyAmount via `fromDecimal`.
     *
     * Note: The input must NOT include any currency symbol; only a plain numeric value is supported
     * (e.g. "12.34"). Use formatting helpers for symbols when displaying amounts.
     */
    fun fromString(valueString: String): CurrencyAmount? = Decimal.fromString(valueString)
      ?.let(Companion::fromDecimal)

    // No payment amount coercion needed
    fun fromCents(cents: ULong): CurrencyAmount = CurrencyAmount(Decimal(cents).movePointLeft(2))
    fun fromCents(cents: Long): CurrencyAmount = fromCents(cents.toULong())
    fun fromCents(cents: Int): CurrencyAmount = fromCents(cents.toULong())
    val Zero: CurrencyAmount = CurrencyAmount(Decimal.Zero)
  }
}

infix operator fun Decimal.times(other: CurrencyAmount) = other * this
infix operator fun Int.times(other: CurrencyAmount) = other * this

/**
 * [CurrencyAmount] will reduce the precision of the underlying [Decimal] value, so performing multi-stage arithmetic
 * on a [CurrencyAmount] introduces a risk of compounding rounding errors.
 *
 * [mapDecimal] gives a scope and underlying [Decimal] value with which multiple operations can be performed.
 * The returning [Decimal] will be coerced into a valid [CurrencyAmount] when the scope is closed.
 */
inline fun CurrencyAmount.mapDecimal(block: (Decimal) -> Decimal): CurrencyAmount =
  CurrencyAmount.fromDecimal(block(decimal))

/**
 * kotlinx.serialization serializer for CurrencyAmount that encodes/decodes as a JSON string
 * using the CurrencyAmount.toString() representation and CurrencyAmount.fromString().
 */
object CurrencyAmountAsStringSerializer : KSerializer<CurrencyAmount> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("au.lovecraft.currency.CurrencyAmount", PrimitiveKind.STRING)

  override fun serialize(encoder: Encoder, value: CurrencyAmount) {
    encoder.encodeString(value.toString())
  }

  override fun deserialize(decoder: Decoder): CurrencyAmount {
    val s = decoder.decodeString()
    return CurrencyAmount.fromString(s)
      ?: throw SerializationException("Invalid CurrencyAmount string: '$s'")
  }
}
