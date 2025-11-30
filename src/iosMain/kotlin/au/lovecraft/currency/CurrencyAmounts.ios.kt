package au.lovecraft.currency

import au.lovecraft.math.decimal.Decimal
import au.lovecraft.math.decimal.Rounding
import platform.Foundation.NSDecimalNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterCurrencyStyle

actual fun CurrencyAmount.formatted(
  withSymbol: Boolean,
  roundedToDollars: Boolean
): String {
  val roundedNumber: Decimal =
    decimal.rounded(if(roundedToDollars) 0 else 2, Rounding.HalfEven)
  val formatter = NSNumberFormatter().apply {
    numberStyle = NSNumberFormatterCurrencyStyle
    currencyCode = "AU"
    if (!withSymbol) currencySymbol = ""
  }
  return requireNotNull(formatter.stringFromNumber(roundedNumber.value))
}
