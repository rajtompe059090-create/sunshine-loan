package com.sunshineloan.app

import com.sunshineloan.app.calculator.LoanCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LoanCalculatorTest {

    private lateinit var calculator: LoanCalculator

    @Before
    fun setUp() {
        calculator = LoanCalculator()
    }

    @Test
    fun testStandardDefaultCalculation() {
        // Sample default values: Loan: 120,000, Rate: 4.5%, Term: 20 years
        val result = calculator.calculate(
            principal = 120000.0,
            annualInterestRate = 4.5,
            loanTermYears = 20,
            fundingFeeRate = 2.15,
            guarantyRate = 25.0
        )

        assertEquals(120000.0, result.loanAmount, 0.01)
        assertEquals(4.5, result.annualInterestRate, 0.01)
        assertEquals(20, result.loanTermYears)

        // Expected monthly payment for $120,000 at 4.5% over 20 years (240 payments):
        // r = 0.045 / 12 = 0.00375
        // (1 + r)^240 = 2.45499...
        // Monthly payment = 120000 * (0.00375 * 2.45499) / (2.45499 - 1) ≈ $759.08
        assertEquals(759.08, result.netMonthlyPayment, 0.10)

        // Total payments = 759.08 * 240 ≈ 182,179.20
        // Net loan cost (interest) = 182,179.20 - 120,000 ≈ 62,179.20
        assertTrue("Net loan cost should be positive", result.netLoanCost > 0)
        assertEquals(62179.20, result.netLoanCost, 25.0)

        // Funding fee: 120,000 * 2.15% = 2,580.00
        assertEquals(2580.0, result.fundingFeeAmount, 0.01)

        // Maximum guaranty: 120,000 * 25.0% = 30,000.00
        assertEquals(30000.0, result.maxGuarantyAmount, 0.01)
    }

    @Test
    fun testZeroInterestCalculation() {
        val result = calculator.calculate(
            principal = 120000.0,
            annualInterestRate = 0.0,
            loanTermYears = 20
        )

        // 120000 / (20 * 12) = 120000 / 240 = 500.00
        assertEquals(500.00, result.netMonthlyPayment, 0.01)
        assertEquals(0.00, result.netLoanCost, 0.01)
        assertEquals(120000.00, result.totalPayments, 0.01)
    }

    @Test
    fun testZeroOrNegativeInputs() {
        val result = calculator.calculate(
            principal = -5000.0,
            annualInterestRate = 4.5,
            loanTermYears = 0
        )

        assertEquals(0.0, result.netMonthlyPayment, 0.001)
        assertEquals(0.0, result.netLoanCost, 0.001)
        assertEquals(0.0, result.loanAmount, 0.001)
    }

    @Test
    fun testCurrencyFormatting() {
        val usdFormatted = calculator.formatCurrency(120000.0, "USD")
        assertTrue(usdFormatted.contains("$"))
        assertTrue(usdFormatted.contains("120,000"))

        val inrFormatted = calculator.formatCurrency(120000.0, "INR")
        assertTrue(inrFormatted.contains("₹"))
    }
}
