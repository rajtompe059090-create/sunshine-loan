package com.sunshineloan.app.model

/**
 * Result of VA Loan amortization calculation.
 */
data class LoanCalculationResult(
    val loanAmount: Double,
    val annualInterestRate: Double,
    val loanTermYears: Int,
    val fundingFeeRate: Double,
    val guarantyRate: Double,
    val fundingFeeAmount: Double,
    val maxGuarantyAmount: Double,
    val netMonthlyPayment: Double,
    val netLoanCost: Double,
    val totalPayments: Double
)

/**
 * Saved loan record stored in Firestore at users/{uid}/loanRecords/{recordId}
 */
data class LoanRecord(
    val id: String = "",
    val userId: String = "",
    val loanAmount: Double = 0.0,
    val interestRate: Double = 0.0,
    val loanTermYears: Int = 0,
    val fundingFeeAmount: Double = 0.0,
    val maxGuarantyAmount: Double = 0.0,
    val netMonthlyPayment: Double = 0.0,
    val netLoanCost: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis(),
    val formattedDate: String = "",
    val currencyCode: String = "INR"
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "userId" to userId,
            "loanAmount" to loanAmount,
            "interestRate" to interestRate,
            "loanTermYears" to loanTermYears,
            "fundingFeeAmount" to fundingFeeAmount,
            "maxGuarantyAmount" to maxGuarantyAmount,
            "netMonthlyPayment" to netMonthlyPayment,
            "netLoanCost" to netLoanCost,
            "timestamp" to timestamp,
            "formattedDate" to formattedDate,
            "currencyCode" to currencyCode
        )
    }

    companion object {
        fun fromMap(id: String, map: Map<String, Any?>): LoanRecord {
            return LoanRecord(
                id = id,
                userId = (map["userId"] as? String).orEmpty(),
                loanAmount = (map["loanAmount"] as? Number)?.toDouble() ?: 0.0,
                interestRate = (map["interestRate"] as? Number)?.toDouble() ?: 0.0,
                loanTermYears = (map["loanTermYears"] as? Number)?.toInt() ?: 0,
                fundingFeeAmount = (map["fundingFeeAmount"] as? Number)?.toDouble() ?: 0.0,
                maxGuarantyAmount = (map["maxGuarantyAmount"] as? Number)?.toDouble() ?: 0.0,
                netMonthlyPayment = (map["netMonthlyPayment"] as? Number)?.toDouble() ?: 0.0,
                netLoanCost = (map["netLoanCost"] as? Number)?.toDouble() ?: 0.0,
                timestamp = (map["timestamp"] as? Number)?.toLong() ?: System.currentTimeMillis(),
                formattedDate = (map["formattedDate"] as? String).orEmpty(),
                currencyCode = (map["currencyCode"] as? String) ?: "INR"
            )
        }
    }
}
