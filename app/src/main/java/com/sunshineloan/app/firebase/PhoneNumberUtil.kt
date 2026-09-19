package com.sunshineloan.app.firebase

/**
 * Utility for formatting and validating international phone numbers (E.164 standard),
 * with special handling for India (+91) 10-digit mobile numbering conventions.
 */
object PhoneNumberUtil {

    data class FormatResult(
        val formattedNumber: String?,
        val errorMessage: String?
    )

    /**
     * Formats raw phone input into an E.164 compliant string (e.g. +919876543210).
     *
     * @param countryCode e.g. "+91"
     * @param rawInput e.g. "9876543210", "09876543210", "98765 43210"
     */
    fun format(countryCode: String, rawInput: String): FormatResult {
        val trimmed = rawInput.trim().replace(" ", "").replace("-", "").replace("(", "").replace(")", "")
        if (trimmed.isEmpty()) {
            return FormatResult(null, "Please enter your mobile number.")
        }

        val cleanCountryCode = if (countryCode.startsWith("+")) countryCode else "+$countryCode"
        val codeDigits = cleanCountryCode.removePrefix("+")

        // If user explicitly typed a leading '+' in the input field
        if (trimmed.startsWith("+")) {
            val digits = trimmed.removePrefix("+").filter { it.isDigit() }
            if (digits.length < 7) {
                return FormatResult(null, "Phone number is too short.")
            }
            if (trimmed.startsWith("+91") && digits.length != 12) {
                return FormatResult(
                    null,
                    "Indian mobile numbers must have exactly 10 digits after +91 (got ${digits.length - 2})."
                )
            }
            return FormatResult("+$digits", null)
        }

        var digits = trimmed.filter { it.isDigit() }
        if (digits.isEmpty()) {
            return FormatResult(null, "Please enter numbers only.")
        }

        if (cleanCountryCode == "+91") {
            // Strip leading 91 if the user entered 12 digits (e.g. 919876543210)
            if (digits.length == 12 && digits.startsWith("91")) {
                digits = digits.substring(2)
            }
            // Strip leading 0 if the user entered 11 digits (e.g. 09876543210)
            if (digits.length == 11 && digits.startsWith("0")) {
                digits = digits.substring(1)
            }
            // Strip any multiple leading zeroes
            while (digits.startsWith("0") && digits.length > 10) {
                digits = digits.substring(1)
            }

            if (digits.length != 10) {
                return FormatResult(
                    null,
                    "Please enter a valid 10-digit Indian mobile number (currently ${digits.length} digits)."
                )
            }

            // Standard Indian mobile numbering starts with 6, 7, 8, or 9
            val firstDigit = digits[0]
            if (firstDigit !in '6'..'9') {
                return FormatResult(
                    null,
                    "Indian mobile numbers typically start with 6, 7, 8, or 9."
                )
            }

            return FormatResult("+91$digits", null)
        } else {
            // General country code handling
            if (digits.startsWith(codeDigits) && digits.length > codeDigits.length + 5) {
                digits = digits.substring(codeDigits.length)
            }
            while (digits.startsWith("0") && digits.length > 8) {
                digits = digits.substring(1)
            }
            if (digits.length < 6) {
                return FormatResult(null, "Phone number is too short.")
            }
            return FormatResult("$cleanCountryCode$digits", null)
        }
    }
}
