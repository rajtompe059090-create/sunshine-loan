package com.sunshineloan.app

import com.sunshineloan.app.model.ApplyLoanUiState
import com.sunshineloan.app.model.LoanApplication
import com.sunshineloan.app.model.LoanStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LoanApplicationModelTest {

    @Test
    fun testLoanApplicationCreationAndStatus() {
        val app = LoanApplication(
            applicationId = "SL-2026-987654",
            userId = "user_abc_123",
            userPhone = "+919876543210",
            loanAmount = 150000.0,
            tenureMonths = 24,
            monthlyEmi = 7200.0,
            loanPurpose = "Business Expansion",
            status = LoanStatus.UNDER_REVIEW
        )

        assertEquals("SL-2026-987654", app.applicationId)
        assertEquals(LoanStatus.UNDER_REVIEW, app.status)
        assertEquals("Under Review", app.status.label)
        assertEquals(150000.0, app.loanAmount, 0.01)
    }

    @Test
    fun testApplyLoanUiStateProgression() {
        var state = ApplyLoanUiState(currentStep = 1)
        assertEquals(1, state.currentStep)
        assertTrue(state.submittedApplication == null)

        state = state.copy(currentStep = 9)
        assertEquals(9, state.currentStep)

        val app = LoanApplication(applicationId = "SL-TEST-001")
        state = state.copy(submittedApplication = app)
        assertEquals("SL-TEST-001", state.submittedApplication?.applicationId)
    }

    @Test
    fun testAadhaarAndPanFormatValidation() {
        // Aadhaar: 12 numeric digits
        val validAadhaar = "123456789012"
        val invalidAadhaarShort = "12345"
        val invalidAadhaarAlpha = "12345678901A"

        assertTrue(validAadhaar.length == 12 && validAadhaar.all { it.isDigit() })
        assertFalse(invalidAadhaarShort.length == 12 && invalidAadhaarShort.all { it.isDigit() })
        assertFalse(invalidAadhaarAlpha.length == 12 && invalidAadhaarAlpha.all { it.isDigit() })

        // PAN: 5 uppercase letters, 4 digits, 1 uppercase letter
        val validPan = "ABCDE1234F"
        val panRegex = Regex("^[A-Z]{5}[0-9]{4}[A-Z]$")

        assertTrue(panRegex.matches(validPan))
        assertFalse(panRegex.matches("abcde1234f"))
        assertFalse(panRegex.matches("12345ABCDE"))
    }
}
