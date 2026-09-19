package com.sunshineloan.app.calculator

import com.sunshineloan.app.model.LoanCalculationResult
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.pow
import kotlin.math.round

/**
 * Robust Loan Calculation Engine implementing standard amortizing mortgage formulas
 * and configurable VA loan assumptions.
 */
class LoanCalculator {

    companion object {
        const val DEFAULT_LOAN_AMOUNT = 120000.0
        const val DEFAULT_INTEREST_RATE = 4.5
        const val DEFAULT_LOAN_TERM_YEARS = 20

        // Configurable VA loan assumptions
        // Standard VA funding fee for first-time use with zero down payment is typically 2.15%
        const val DEFAULT_FUNDING_FEE_RATE = 2.15
        // VA typically guarantees up to 25% of the loan amount
        const val DEFAULT_GUARANTY_RATE = 25.0
    }

    /**
     * Calculates monthly payment and loan cost metrics using the standard amortization formula.
     *
     * @param principal Principal loan amount (P)
     * @param annualInterestRate Annual interest rate in percent (e.g. 4.5)
     * @param loanTermYears Loan term in years (e.g. 20)
     * @param fundingFeeRate Configurable VA funding fee percentage (default 2.15%)
     * @param guarantyRate Configurable VA maximum guaranty percentage (default 25.0%)
     */
    fun calculate(
        principal: Double,
        annualInterestRate: Double,
        loanTermYears: Int,
        fundingFeeRate: Double = DEFAULT_FUNDING_FEE_RATE,
        guarantyRate: Double = DEFAULT_GUARANTY_RATE
    ): LoanCalculationResult {
        // Sanitize and validate inputs
        val safePrincipal = if (principal.isFinite() && principal > 0) principal else 0.0
        val safeInterestRate = if (annualInterestRate.isFinite() && annualInterestRate >= 0) annualInterestRate else 0.0
        val safeTermYears = if (loanTermYears > 0) loanTermYears else 0
        val safeFundingRate = if (fundingFeeRate.isFinite() && fundingFeeRate >= 0) fundingFeeRate else 0.0
        val safeGuarantyRate = if (guarantyRate.isFinite() && guarantyRate >= 0) guarantyRate else 0.0

        val totalPaymentsCount = safeTermYears * 12

        val monthlyPayment: Double
        if (totalPaymentsCount <= 0 || safePrincipal <= 0.0) {
            monthlyPayment = 0.0
        } else if (safeInterestRate <= 0.0) {
            // 0% interest rate
            monthlyPayment = safePrincipal / totalPaymentsCount
        } else {
            // Positive interest rate amortization formula:
            // r = Monthly interest rate = Annual interest rate / 12 / 100
            val r = safeInterestRate / 12.0 / 100.0
            val n = totalPaymentsCount.toDouble()
            val compound = (1.0 + r).pow(n)

            monthlyPayment = if (compound - 1.0 != 0.0) {
                safePrincipal * (r * compound) / (compound - 1.0)
            } else {
                safePrincipal / n
            }
        }

        val totalPayments = if (totalPaymentsCount > 0) monthlyPayment * totalPaymentsCount else 0.0
        val netLoanCost = if (totalPayments > safePrincipal) totalPayments - safePrincipal else 0.0

        val fundingFeeAmount = safePrincipal * (safeFundingRate / 100.0)
        val maxGuarantyAmount = safePrincipal * (safeGuarantyRate / 100.0)

        return LoanCalculationResult(
            loanAmount = roundTo2Decimals(safePrincipal),
            annualInterestRate = safeInterestRate,
            loanTermYears = safeTermYears,
            fundingFeeRate = safeFundingRate,
            guarantyRate = safeGuarantyRate,
            fundingFeeAmount = roundTo2Decimals(fundingFeeAmount),
            maxGuarantyAmount = roundTo2Decimals(maxGuarantyAmount),
            netMonthlyPayment = roundTo2Decimals(monthlyPayment),
            netLoanCost = roundTo2Decimals(netLoanCost),
            totalPayments = roundTo2Decimals(totalPayments)
        )
    }

    /**
     * Formats currency as Indian Rupees (₹ - INR) using standard Indian number formatting (e.g. ₹1,00,000.00).
     */
    fun formatCurrency(amount: Double, currencyCode: String = "INR"): String {
        return try {
            val isNegative = amount < 0
            val absAmount = kotlin.math.abs(amount)
            val intPart = absAmount.toLong()
            val fracPart = kotlin.math.round((absAmount - intPart) * 100).toLong()
            val fracString = String.format(Locale.ROOT, "%02d", fracPart.coerceIn(0, 99))

            val intStr = intPart.toString()
            val formattedInt = if (intStr.length <= 3) {
                intStr
            } else {
                val lastThree = intStr.substring(intStr.length - 3)
                val rest = intStr.substring(0, intStr.length - 3)
                val parts = mutableListOf<String>()
                var idx = rest.length
                while (idx > 0) {
                    val start = (idx - 2).coerceAtLeast(0)
                    parts.add(0, rest.substring(start, idx))
                    idx = start
                }
                parts.joinToString(",") + "," + lastThree
            }
            val prefix = if (isNegative) "-₹" else "₹"
            "$prefix$formattedInt.$fracString"
        } catch (_: Exception) {
            "₹%.2f".format(amount)
        }
    }

    private fun roundTo2Decimals(value: Double): Double {
        return round(value * 100.0) / 100.0
    }
}
