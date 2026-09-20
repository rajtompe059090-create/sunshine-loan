package com.sunshineloan.app.model

import kotlin.math.pow
import kotlin.math.round

/**
 * Encapsulates the UI state of the 9-Step Apply Loan Flow.
 */
data class ApplyLoanUiState(
    val currentStep: Int = 1,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val submittedApplication: LoanApplication? = null,

    // STEP 1: Personal Details
    val fullName: String = "",
    val dob: String = "",
    val gender: String = "Male",
    val email: String = "",
    val address: String = "",
    val city: String = "",
    val state: String = "",
    val pincode: String = "",

    // STEP 2: Employment & Income
    val employmentType: String = "Salaried", // Salaried, Self-Employed, Business, Professional
    val companyName: String = "",
    val monthlyIncome: String = "35000",
    val salaryMode: String = "Bank Transfer", // Bank Transfer, Cheque, Cash
    val existingEmi: String = "0",

    // STEP 3: KYC Verification
    val aadhaarNumber: String = "",
    val aadhaarFrontUri: String = "",
    val aadhaarBackUri: String = "",
    val panNumber: String = "",
    val panUri: String = "",
    val selfieUri: String = "",

    // STEP 4: Loan Requirement
    val requestedAmount: Double = 100000.0,
    val loanPurpose: String = "Personal & Family Needs",
    val tenureMonths: Int = 12,

    // STEP 5: Configured Loan Offer
    val interestRatePerAnnum: Double = 11.5, // 11.5% p.a.
    val monthlyEmi: Double = 0.0,
    val totalInterest: Double = 0.0,
    val totalRepayment: Double = 0.0,
    val processingFee: Double = 0.0, // 1.5% + 18% GST
    val netDisbursalAmount: Double = 0.0,

    // STEP 6: Review & Consent
    val termsAccepted: Boolean = true,
    val creditBureauConsent: Boolean = true,

    // STEP 8: Payment / QR
    val paymentReferenceId: String = "",
    val paymentDone: Boolean = false,
    val adminPaymentQr: String = "" // Optional admin QR code
) {
    companion object {
        fun computeOffer(amount: Double, tenureMonths: Int, annualRate: Double = 11.5): ApplyLoanUiState {
            val safeAmount = if (amount > 0) amount else 50000.0
            val safeMonths = if (tenureMonths > 0) tenureMonths else 12
            val r = annualRate / 12.0 / 100.0
            val n = safeMonths.toDouble()
            val compound = (1.0 + r).pow(n)

            val emi = if (compound - 1.0 != 0.0) {
                safeAmount * (r * compound) / (compound - 1.0)
            } else {
                safeAmount / n
            }

            val totalRepayment = emi * safeMonths
            val totalInterest = totalRepayment - safeAmount
            val rawFee = safeAmount * 0.015 // 1.5%
            val feeWithGst = rawFee * 1.18 // + 18% GST
            val netDisbursal = safeAmount - feeWithGst

            return ApplyLoanUiState(
                requestedAmount = safeAmount,
                tenureMonths = safeMonths,
                interestRatePerAnnum = annualRate,
                monthlyEmi = round(emi * 100.0) / 100.0,
                totalInterest = round(totalInterest * 100.0) / 100.0,
                totalRepayment = round(totalRepayment * 100.0) / 100.0,
                processingFee = round(feeWithGst * 100.0) / 100.0,
                netDisbursalAmount = round(netDisbursal * 100.0) / 100.0
            )
        }
    }
}
