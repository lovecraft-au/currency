package au.lovecraft.currency

import au.lovecraft.math.decimal.Decimal
import au.lovecraft.math.decimal.Rounding

actual fun CurrencyAmount.formatted(
    withSymbol: Boolean,
    roundedToDollars: Boolean
): String {
    val scale: Int = if (roundedToDollars) 0 else 2
    val roundedNumber = decimal.rounded(scale.toShort(), Rounding.HalfEven)

    // Format with thousands separators
    val numStr = roundedNumber.value.toFixed(scale)
    val parts = numStr.split(".")
    val integerPart = parts[0]
    val decimalPart = if (parts.size > 1) "." + parts[1] else ""

    // Add thousands separators
    val formattedInteger = integerPart.reversed().chunked(3).joinToString(",").reversed()
    val formattedValue = formattedInteger + decimalPart

    return if (withSymbol) {
        "$$formattedValue"
    } else {
        formattedValue
    }
}
