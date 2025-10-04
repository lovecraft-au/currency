package au.lovecraft.currency

import au.lovecraft.math.decimal.Decimal
import au.lovecraft.math.decimal.Rounding
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

actual fun CurrencyAmount.formatted(
  withSymbol: Boolean,
  roundedToDollars: Boolean
): String {
  val scale: Short = if (roundedToDollars) 0 else 2
  val roundedNumber = decimal.rounded(scale, Rounding.HalfEven) // aka "Banker's rounding"
  val currencyFormat = (NumberFormat.getCurrencyInstance(Locale("en", "AU")) as DecimalFormat).apply {
    // decimalFormatSymbols returns a copy, it must be set again
    decimalFormatSymbols = decimalFormatSymbols.apply {
      if (!withSymbol) currencySymbol = ""
    }
    if (roundedToDollars) {
      maximumFractionDigits = 0
      minimumFractionDigits = 0
    }
  }
  return currencyFormat.format(roundedNumber.value)
}

actual fun Decimal.isValidAsCurrencyAmount() = isValidAsCurrencyAmountCommon()
