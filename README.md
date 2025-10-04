# Currency (Kotlin Multiplatform)

A tiny Kotlin Multiplatform library for working with money amounts in a predictable, base‑10 way.

Currency provides a small, opinionated value type — CurrencyAmount — built on top of the Decimal library (au.lovecraft:decimal). It focuses on common needs for prices and totals: non‑negative amounts, cents precision (2 fractional digits), explicit rounding, arithmetic helpers, and simple platform‑aware formatting.

Note: This is an early, functionally incomplete draft. The API may change.


## Why another currency lib?

- Built on true decimal arithmetic: uses Decimal under the hood to avoid floating‑point surprises across platforms.
- Opinionated domain rules: amounts are always non‑negative; coercion to two decimal places on construction (Banker’s rounding) to reduce drift.
- Small surface area: a single value class CurrencyAmount with a handful of helpers for arithmetic, rounding, conversion to cents, and formatting.


## Supported targets

Configured in this module’s Gradle build:

- Android (KMP Android target)
- JVM
- iOS (arm64 device and simulator)
- Wasm JS (browser)

Kotlin version: 2.2.20


## Installation

This library is not yet published to a public repository. Recommended ways to consume it today:

- Include as a Git submodule and reference the project directly, or
- Use a Gradle included build for local development.

Example using an included build:

settings.gradle.kts in your app project:

```
includeBuild("../currency") // path where this repo lives
```

Then in your module’s build.gradle.kts:

```
dependencies {
    implementation(project(":currency"))
}
```

Notes:
- Currency depends on au.lovecraft:decimal. This is brought in transitively.
- Wasm JS target pulls npm package decimal.js (10.6.0) automatically.


## Quick start

```kotlin
// Construct
val a = CurrencyAmount.fromString("12.345")!!   // -> 12.35 (HalfEven to 2 dp)
val b = CurrencyAmount.fromCents(1235u)          // -> 12.35
val c = 99.dollars                               // from Int extension
val d = "1_234.00".replace("_", "").dollars   // from String extension

// Arithmetic (operators return CurrencyAmount)
val sum = a + c
val diff = c - b
val fee = CurrencyAmount.fromDecimal(Decimal(1))
val total = sum + fee

// Multiply and divide
val tenPercent = Percent.Ten
val discounted = total * tenPercent              // percentage of amount
val tripled = total * 3                          // Int multiplier
val split = total.divideAndRound(3)              // HalfEven to 2 dp

// Multi‑step arithmetic with full precision via Decimal and single coercion at the end
val result = total.mapDecimal { d ->
    ((d * Decimal(3)) + Decimal(1)) / Decimal(2)
}

// Conversions
val cents: ULong = result.toCents()              // 1235uL for 12.35

// Formatting
val s1 = result.formatted(withSymbol = true)     // e.g. "$12.35" (platform‑specific)
val s2 = result.formatted(withSymbol = false)    // e.g. "12.35"
val s3 = result.formatted(roundedToDollars = true) // e.g. "$12"
```

Additional helpers:

- "123.45".dollars and 123.dollars
- formattedOrFree(withSymbol: Boolean = true, free: String = "free")


## Rounding and validity

- Coercion: CurrencyAmount.fromDecimal() and fromString() coerce to two fractional digits using HalfEven (aka “Banker’s rounding”).
- divideAndRound(): divides and rounds to two fractional digits using HalfEven.
- Valid values: CurrencyAmount is always non‑negative. Constructing with a negative Decimal will error.
- toString(): mirrors the underlying Decimal’s plain representation.


## Formatting

Common API:

```
fun CurrencyAmount.formatted(
    withSymbol: Boolean = true,
    roundedToDollars: Boolean = false
): String
```

- formattedOrFree(withSymbol: Boolean = true, free: String = "free"): returns free when the amount is zero.

Platform notes:

- JVM/Android: uses java.text.NumberFormat with Locale("en", "AU"). Symbol can be hidden.
- iOS: uses NSNumberFormatter with currencyCode = "AU". Symbol can be hidden.
- Wasm JS: includes a simple formatter that adds thousands separators and “$”; adapt as needed.


## Platform backends

- Depends on au.lovecraft:decimal for cross‑platform decimal math.
- Wasm JS additionally uses npm package decimal.js for decimal math via the Decimal library.


## Building

- Requires JDK 21 for JVM/Android (configured via jvmToolchain(21)).
- Wasm JS target pulls decimal.js automatically.

Commands:

```
./gradlew build
```


## Roadmap

- Finish rounding/formatting surface, and multi‑currency support (configurable locale and currency code)
- Publish artifacts to a public repository
- Expand platform coverage/tests and add sample app(s)


## License

This software is released under the LGPL License.
See [LICENSE.md](LICENSE.md) for details.
