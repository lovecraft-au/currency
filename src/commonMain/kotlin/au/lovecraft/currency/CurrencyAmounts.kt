package au.lovecraft.currency

import au.lovecraft.math.decimal.Decimal

expect fun CurrencyAmount.formatted(
    withSymbol: Boolean = true,
    roundedToDollars: Boolean = false
): String

fun CurrencyAmount.formattedOrFree(
    withSymbol: Boolean = true,
    free: String = "free"
): String = if (this == CurrencyAmount.Zero) free else formatted(withSymbol)

val String.dollars: CurrencyAmount inline get() = CurrencyAmount.fromString(this)
    ?: error(
        "Invalid dollar amount '$this'"
    )

val Int.dollars: CurrencyAmount inline get() = CurrencyAmount.fromDecimal(Decimal.from(this))