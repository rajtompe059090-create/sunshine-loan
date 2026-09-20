package com.sunshineloan.app.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Standard Application Statuses for Sunshine Loan
 */
enum class LoanStatus(val label: String, val description: String) {
    SUBMITTED("Submitted", "Your application has been received and queued for review."),
    UNDER_REVIEW("Under Review", "Our credit underwriting team is verifying your documents and credit profile."),
    DOCUMENTS_REQUIRED("Documents Required", "Additional clarification or documents are required to proceed."),
    APPROVED("Approved", "Congratulations! Your loan has been approved. Awaiting disbursal."),
    REJECTED("Rejected", "Your application could not be approved based on current credit criteria."),
    DISBURSED("Disbursed", "Loan amount has been disbursed to your verified bank account.");

    companion object {
        fun fromString(statusStr: String?): LoanStatus {
            return entries.firstOrNull { it.name.equals(statusStr, ignoreCase = true) || it.label.equals(statusStr, ignoreCase = true) }
                ?: SUBMITTED
        }
    }
}

/**
 * Complete Loan Application Model encompassing all 9 steps of the user journey.
 */
data class LoanApplication(
    val applicationId: String = "",
    val userId: String = "",
    val userPhone: String = "",
    val status: LoanStatus = LoanStatus.SUBMITTED,
    val statusMessage: String = "Application submitted successfully.",
    val adminRemarks: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val formattedDate: String = "",

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
    val monthlyIncome: Double = 0.0,
    val salaryMode: String = "Bank Transfer", // Bank Transfer, Cheque, Cash
    val existingEmi: Double = 0.0,

    // STEP 3: KYC Verification
    val aadhaarNumber: String = "",
    val aadhaarFrontUri: String = "",
    val aadhaarBackUri: String = "",
    val panNumber: String = "",
    val panUri: String = "",
    val selfieUri: String = "",

    // STEP 4: Loan Requirement
    val loanAmount: Double = 50000.0,
    val loanPurpose: String = "Personal Use", // Personal Use, Medical Emergency, Home Renovation, Education, Business
    val tenureMonths: Int = 12,

    // STEP 5: Loan Offer
    val interestRatePerAnnum: Double = 11.5,
    val monthlyEmi: Double = 0.0,
    val totalInterest: Double = 0.0,
    val totalRepayment: Double = 0.0,
    val processingFee: Double = 0.0, // Clearly disclosed nominal documentation charge
    val netDisbursalAmount: Double = 0.0,

    // STEP 6: Review & Consent
    val termsAccepted: Boolean = true,
    val creditBureauConsent: Boolean = true,

    // STEP 8: Payment / Processing QR
    val paymentRequired: Boolean = false,
    val paymentAmount: Double = 0.0,
    val isPaymentCompleted: Boolean = false,
    val paymentReferenceId: String = "",
    val paymentQrUrl: String = "", // Admin provided QR
    val paymentNotice: String = "NOTICE: Paying any administrative or document verification fee does NOT guarantee loan approval. Final sanction is strictly subject to underwriting and verification."
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "applicationId" to applicationId,
            "userId" to userId,
            "userPhone" to userPhone,
            "status" to status.name,
            "statusMessage" to statusMessage,
            "adminRemarks" to adminRemarks,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt,
            "formattedDate" to formattedDate,

            // Step 1
            "fullName" to fullName,
            "dob" to dob,
            "gender" to gender,
            "email" to email,
            "address" to address,
            "city" to city,
            "state" to state,
            "pincode" to pincode,

            // Step 2
            "employmentType" to employmentType,
            "companyName" to companyName,
            "monthlyIncome" to monthlyIncome,
            "salaryMode" to salaryMode,
            "existingEmi" to existingEmi,

            // Step 3
            "aadhaarNumber" to aadhaarNumber,
            "aadhaarFrontUri" to aadhaarFrontUri,
            "aadhaarBackUri" to aadhaarBackUri,
            "panNumber" to panNumber,
            "panUri" to panUri,
            "selfieUri" to selfieUri,

            // Step 4
            "loanAmount" to loanAmount,
            "loanPurpose" to loanPurpose,
            "tenureMonths" to tenureMonths,

            // Step 5
            "interestRatePerAnnum" to interestRatePerAnnum,
            "monthlyEmi" to monthlyEmi,
            "totalInterest" to totalInterest,
            "totalRepayment" to totalRepayment,
            "processingFee" to processingFee,
            "netDisbursalAmount" to netDisbursalAmount,

            // Step 6
            "termsAccepted" to termsAccepted,
            "creditBureauConsent" to creditBureauConsent,

            // Step 8
            "paymentRequired" to paymentRequired,
            "paymentAmount" to paymentAmount,
            "isPaymentCompleted" to isPaymentCompleted,
            "paymentReferenceId" to paymentReferenceId,
            "paymentQrUrl" to paymentQrUrl,
            "paymentNotice" to paymentNotice
        )
    }

    companion object {
        fun generateApplicationId(): String {
            val dateStr = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
            val randomSuffix = UUID.randomUUID().toString().replace("-", "").take(4).uppercase()
            return "SL-$dateStr-$randomSuffix"
        }

        fun fromMap(id: String, map: Map<String, Any?>): LoanApplication {
            val statusString = (map["status"] as? String).orEmpty()
            return LoanApplication(
                applicationId = (map["applicationId"] as? String) ?: id,
                userId = (map["userId"] as? String).orEmpty(),
                userPhone = (map["userPhone"] as? String).orEmpty(),
                status = LoanStatus.fromString(statusString),
                statusMessage = (map["statusMessage"] as? String) ?: "Application submitted.",
                adminRemarks = (map["adminRemarks"] as? String).orEmpty(),
                createdAt = (map["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
                updatedAt = (map["updatedAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
                formattedDate = (map["formattedDate"] as? String).orEmpty(),

                fullName = (map["fullName"] as? String).orEmpty(),
                dob = (map["dob"] as? String).orEmpty(),
                gender = (map["gender"] as? String) ?: "Male",
                email = (map["email"] as? String).orEmpty(),
                address = (map["address"] as? String).orEmpty(),
                city = (map["city"] as? String).orEmpty(),
                state = (map["state"] as? String).orEmpty(),
                pincode = (map["pincode"] as? String).orEmpty(),

                employmentType = (map["employmentType"] as? String) ?: "Salaried",
                companyName = (map["companyName"] as? String).orEmpty(),
                monthlyIncome = (map["monthlyIncome"] as? Number)?.toDouble() ?: 0.0,
                salaryMode = (map["salaryMode"] as? String) ?: "Bank Transfer",
                existingEmi = (map["existingEmi"] as? Number)?.toDouble() ?: 0.0,

                aadhaarNumber = (map["aadhaarNumber"] as? String).orEmpty(),
                aadhaarFrontUri = (map["aadhaarFrontUri"] as? String).orEmpty(),
                aadhaarBackUri = (map["aadhaarBackUri"] as? String).orEmpty(),
                panNumber = (map["panNumber"] as? String).orEmpty(),
                panUri = (map["panUri"] as? String).orEmpty(),
                selfieUri = (map["selfieUri"] as? String).orEmpty(),

                loanAmount = (map["loanAmount"] as? Number)?.toDouble() ?: 50000.0,
                loanPurpose = (map["loanPurpose"] as? String) ?: "Personal Use",
                tenureMonths = (map["tenureMonths"] as? Number)?.toInt() ?: 12,

                interestRatePerAnnum = (map["interestRatePerAnnum"] as? Number)?.toDouble() ?: 11.5,
                monthlyEmi = (map["monthlyEmi"] as? Number)?.toDouble() ?: 0.0,
                totalInterest = (map["totalInterest"] as? Number)?.toDouble() ?: 0.0,
                totalRepayment = (map["totalRepayment"] as? Number)?.toDouble() ?: 0.0,
                processingFee = (map["processingFee"] as? Number)?.toDouble() ?: 0.0,
                netDisbursalAmount = (map["netDisbursalAmount"] as? Number)?.toDouble() ?: 0.0,

                termsAccepted = (map["termsAccepted"] as? Boolean) ?: true,
                creditBureauConsent = (map["creditBureauConsent"] as? Boolean) ?: true,

                paymentRequired = (map["paymentRequired"] as? Boolean) ?: false,
                paymentAmount = (map["paymentAmount"] as? Number)?.toDouble() ?: 0.0,
                isPaymentCompleted = (map["isPaymentCompleted"] as? Boolean) ?: false,
                paymentReferenceId = (map["paymentReferenceId"] as? String).orEmpty(),
                paymentQrUrl = (map["paymentQrUrl"] as? String).orEmpty(),
                paymentNotice = (map["paymentNotice"] as? String)
                    ?: "NOTICE: Paying any administrative or document verification fee does NOT guarantee loan approval. Final sanction is strictly subject to underwriting and verification."
            )
        }
    }
}

/**
 * User Profile Model
 */
data class UserProfile(
    val userId: String = "",
    val phone: String = "",
    val fullName: String = "",
    val email: String = "",
    val dob: String = "",
    val gender: String = "Male",
    val address: String = "",
    val city: String = "",
    val state: String = "",
    val pincode: String = "",
    val employmentType: String = "Salaried",
    val monthlyIncome: Double = 0.0,
    val isAdmin: Boolean = false // Only server / Firestore /admins collection can set this
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "userId" to userId,
            "phone" to phone,
            "fullName" to fullName,
            "email" to email,
            "dob" to dob,
            "gender" to gender,
            "address" to address,
            "city" to city,
            "state" to state,
            "pincode" to pincode,
            "employmentType" to employmentType,
            "monthlyIncome" to monthlyIncome
            // Note: isAdmin is deliberately excluded from client updates for security
        )
    }

    companion object {
        fun fromMap(userId: String, map: Map<String, Any?>, isAdminRole: Boolean = false): UserProfile {
            return UserProfile(
                userId = userId,
                phone = (map["phone"] as? String).orEmpty(),
                fullName = (map["fullName"] as? String).orEmpty(),
                email = (map["email"] as? String).orEmpty(),
                dob = (map["dob"] as? String).orEmpty(),
                gender = (map["gender"] as? String) ?: "Male",
                address = (map["address"] as? String).orEmpty(),
                city = (map["city"] as? String).orEmpty(),
                state = (map["state"] as? String).orEmpty(),
                pincode = (map["pincode"] as? String).orEmpty(),
                employmentType = (map["employmentType"] as? String) ?: "Salaried",
                monthlyIncome = (map["monthlyIncome"] as? Number)?.toDouble() ?: 0.0,
                isAdmin = isAdminRole
            )
        }
    }
}
