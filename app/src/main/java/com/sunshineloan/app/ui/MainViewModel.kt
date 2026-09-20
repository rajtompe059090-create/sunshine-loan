package com.sunshineloan.app.ui

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.PhoneAuthProvider
import com.sunshineloan.app.calculator.LoanCalculator
import com.sunshineloan.app.data.UserPreferences
import com.sunshineloan.app.firebase.ApplicationRepository
import com.sunshineloan.app.firebase.AuthRepository
import com.sunshineloan.app.firebase.AuthState
import com.sunshineloan.app.firebase.LoanRepository
import com.sunshineloan.app.model.ApplyLoanUiState
import com.sunshineloan.app.model.LoanApplication
import com.sunshineloan.app.model.LoanCalculationResult
import com.sunshineloan.app.model.LoanRecord
import com.sunshineloan.app.model.LoanStatus
import com.sunshineloan.app.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed class SaveState {
    object Idle : SaveState()
    object Saving : SaveState()
    data class Success(val message: String) : SaveState()
    data class Error(val error: String) : SaveState()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val calculator = LoanCalculator()
    val preferences = UserPreferences.getInstance(application)
    val authRepo = AuthRepository.getInstance(application)
    val loanRepo = LoanRepository.getInstance(application)
    val appRepo = ApplicationRepository.getInstance(application)

    // Auth & Firebase State
    val authState: StateFlow<AuthState> = authRepo.authState
    val isFirebaseConfigured: Boolean
        get() = authRepo.isFirebaseConfigured()

    // User Profile & Applications
    private val _userProfile = MutableStateFlow(
        UserProfile(
            fullName = preferences.cachedUserName.value,
            email = preferences.cachedEmail.value
        )
    )
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _userApplications = MutableStateFlow<List<LoanApplication>>(emptyList())
    val userApplications: StateFlow<List<LoanApplication>> = _userApplications.asStateFlow()

    private val _activeApplication = MutableStateFlow<LoanApplication?>(null)
    val activeApplication: StateFlow<LoanApplication?> = _activeApplication.asStateFlow()

    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin.asStateFlow()

    // 9-Step Apply Loan Flow State
    private val _applyLoanState = MutableStateFlow(ApplyLoanUiState.computeOffer(100000.0, 12, 11.5))
    val applyLoanState: StateFlow<ApplyLoanUiState> = _applyLoanState.asStateFlow()

    // Inputs
    val loanAmountInput = MutableStateFlow("120000")
    val interestRateInput = MutableStateFlow("4.5")
    val loanTermYearsInput = MutableStateFlow("20")

    // Calculation result derived from inputs and user preferences
    val calculationResult: StateFlow<LoanCalculationResult> = combine(
        loanAmountInput,
        interestRateInput,
        loanTermYearsInput,
        preferences.fundingFeeRate,
        preferences.guarantyRate
    ) { amountStr, rateStr, termStr, fundingFeeRate, guarantyRate ->
        val principal = amountStr.toDoubleOrNull() ?: 0.0
        val rate = rateStr.toDoubleOrNull() ?: 0.0
        val term = termStr.toIntOrNull() ?: 0
        calculator.calculate(
            principal = principal,
            annualInterestRate = rate,
            loanTermYears = term,
            fundingFeeRate = fundingFeeRate,
            guarantyRate = guarantyRate
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        calculator.calculate(120000.0, 4.5, 20)
    )

    // Save status
    private val _saveState = MutableStateFlow<SaveState>(SaveState.Idle)
    val saveState: StateFlow<SaveState> = _saveState.asStateFlow()

    // Historical Records
    private val _historyRecords = MutableStateFlow<List<LoanRecord>>(emptyList())
    val historyRecords: StateFlow<List<LoanRecord>> = _historyRecords.asStateFlow()

    private val _isLoadingHistory = MutableStateFlow(false)
    val isLoadingHistory: StateFlow<Boolean> = _isLoadingHistory.asStateFlow()

    init {
        loadHistory()
        loadUserApplications()
        loadUserProfile()
        checkAdminStatus()
    }

    fun onLoanAmountChanged(value: String) {
        loanAmountInput.value = value.filter { it.isDigit() || it == '.' }
    }

    fun onInterestRateChanged(value: String) {
        interestRateInput.value = value.filter { it.isDigit() || it == '.' }
    }

    fun onLoanTermChanged(value: String) {
        loanTermYearsInput.value = value.filter { it.isDigit() }
    }

    fun setPredefinedTerm(years: Int) {
        loanTermYearsInput.value = years.toString()
    }

    fun sendOtp(phoneNumber: String, activity: Activity, resendToken: PhoneAuthProvider.ForceResendingToken? = null) {
        authRepo.sendOtp(phoneNumber, activity, resendToken)
    }

    fun verifyOtp(verificationId: String, code: String) {
        viewModelScope.launch {
            authRepo.verifyOtp(verificationId, code)
        }
    }

    fun signOut() {
        authRepo.signOut()
    }

    fun resetAuthState() {
        authRepo.resetState()
    }

    fun resetSaveState() {
        _saveState.value = SaveState.Idle
    }

    fun saveCurrentCalculation() {
        val current = calculationResult.value
        if (current.loanAmount <= 0.0) {
            _saveState.value = SaveState.Error("Please enter a valid loan amount before saving.")
            return
        }

        viewModelScope.launch {
            _saveState.value = SaveState.Saving
            val userId = authRepo.getUserId()
            val dateFormat = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault())
            val formattedDate = dateFormat.format(Date())

            val record = LoanRecord(
                userId = userId,
                loanAmount = current.loanAmount,
                interestRate = current.annualInterestRate,
                loanTermYears = current.loanTermYears,
                fundingFeeAmount = current.fundingFeeAmount,
                maxGuarantyAmount = current.maxGuarantyAmount,
                netMonthlyPayment = current.netMonthlyPayment,
                netLoanCost = current.netLoanCost,
                timestamp = System.currentTimeMillis(),
                formattedDate = formattedDate,
                currencyCode = preferences.currencyCode.value
            )

            val result = loanRepo.saveLoanRecord(userId, record)
            if (result.isSuccess) {
                _saveState.value = SaveState.Success("VA Loan calculation saved successfully!")
                loadHistory()
            } else {
                _saveState.value = SaveState.Error("Failed to save calculation. Please retry.")
            }
        }
    }

    fun loadCalculationIntoForm(record: LoanRecord) {
        loanAmountInput.value = if (record.loanAmount % 1.0 == 0.0) record.loanAmount.toInt().toString() else record.loanAmount.toString()
        interestRateInput.value = record.interestRate.toString()
        loanTermYearsInput.value = record.loanTermYears.toString()
    }

    fun loadHistory() {
        viewModelScope.launch {
            _isLoadingHistory.value = true
            val userId = authRepo.getUserId()
            loanRepo.getLoanRecordsFlow(userId).collect { records ->
                _historyRecords.value = records
                _isLoadingHistory.value = false
            }
        }
    }

    fun deleteRecord(recordId: String) {
        viewModelScope.launch {
            val userId = authRepo.getUserId()
            loanRepo.deleteLoanRecord(userId, recordId)
            loadHistory()
        }
    }

    // ==========================================
    // SUNSHINE LOAN APPLICATION & PROFILE FLOW
    // ==========================================

    fun loadUserApplications() {
        viewModelScope.launch {
            val userId = authRepo.getUserId()
            appRepo.getUserApplicationsFlow(userId).collect { apps ->
                _userApplications.value = apps
                if (_activeApplication.value == null && apps.isNotEmpty()) {
                    _activeApplication.value = apps.first()
                }
            }
        }
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            val userId = authRepo.getUserId()
            val phone = authRepo.getCurrentUser()?.phoneNumber.orEmpty()
            appRepo.getUserProfileFlow(userId, phone).collect { profile ->
                val updatedProfile = if (profile.fullName.isBlank() && preferences.cachedUserName.value != "Valued Customer") {
                    profile.copy(fullName = preferences.cachedUserName.value)
                } else profile
                _userProfile.value = updatedProfile
            }
        }
    }

    fun checkAdminStatus() {
        viewModelScope.launch {
            val userId = authRepo.getUserId()
            val admin = appRepo.verifyAdminStatus(userId)
            _isAdmin.value = admin
        }
    }

    fun selectActiveApplication(app: LoanApplication) {
        _activeApplication.value = app
        preferences.setActiveApplicationId(app.applicationId)
    }

    fun startNewApplication() {
        val cachedName = userProfile.value.fullName.ifBlank { preferences.cachedUserName.value }
        val cachedEmail = userProfile.value.email.ifBlank { preferences.cachedEmail.value }
        val currentOffer = ApplyLoanUiState.computeOffer(100000.0, 12, 11.5)
        _applyLoanState.value = currentOffer.copy(
            currentStep = 1,
            fullName = if (cachedName != "Valued Customer") cachedName else "",
            email = cachedEmail,
            dob = userProfile.value.dob,
            gender = userProfile.value.gender,
            address = userProfile.value.address,
            city = userProfile.value.city,
            state = userProfile.value.state,
            pincode = userProfile.value.pincode,
            employmentType = userProfile.value.employmentType,
            monthlyIncome = if (userProfile.value.monthlyIncome > 0) userProfile.value.monthlyIncome.toInt().toString() else "35000",
            errorMessage = null,
            submittedApplication = null
        )
    }

    fun setApplyLoanStep(step: Int) {
        _applyLoanState.value = _applyLoanState.value.copy(
            currentStep = step.coerceIn(1, 9),
            errorMessage = null
        )
    }

    fun updatePersonalDetails(
        fullName: String,
        dob: String,
        gender: String,
        email: String,
        address: String,
        city: String,
        state: String,
        pincode: String
    ) {
        preferences.setUserName(fullName)
        preferences.setUserEmail(email)
        _applyLoanState.value = _applyLoanState.value.copy(
            fullName = fullName,
            dob = dob,
            gender = gender,
            email = email,
            address = address,
            city = city,
            state = state,
            pincode = pincode,
            errorMessage = null
        )
    }

    fun updateEmploymentDetails(
        employmentType: String,
        companyName: String,
        monthlyIncome: String,
        salaryMode: String,
        existingEmi: String
    ) {
        _applyLoanState.value = _applyLoanState.value.copy(
            employmentType = employmentType,
            companyName = companyName,
            monthlyIncome = monthlyIncome.filter { it.isDigit() },
            salaryMode = salaryMode,
            existingEmi = existingEmi.filter { it.isDigit() },
            errorMessage = null
        )
    }

    fun updateKycDetails(
        aadhaarNumber: String,
        aadhaarFrontUri: String,
        aadhaarBackUri: String,
        panNumber: String,
        panUri: String,
        selfieUri: String
    ) {
        _applyLoanState.value = _applyLoanState.value.copy(
            aadhaarNumber = aadhaarNumber.filter { it.isDigit() }.take(12),
            aadhaarFrontUri = aadhaarFrontUri,
            aadhaarBackUri = aadhaarBackUri,
            panNumber = panNumber.uppercase().take(10),
            panUri = panUri,
            selfieUri = selfieUri,
            errorMessage = null
        )
    }

    fun updateLoanRequirement(amount: Double, purpose: String, tenureMonths: Int) {
        val offer = ApplyLoanUiState.computeOffer(amount, tenureMonths, 11.5)
        _applyLoanState.value = _applyLoanState.value.copy(
            requestedAmount = offer.requestedAmount,
            loanPurpose = purpose,
            tenureMonths = offer.tenureMonths,
            interestRatePerAnnum = offer.interestRatePerAnnum,
            monthlyEmi = offer.monthlyEmi,
            totalInterest = offer.totalInterest,
            totalRepayment = offer.totalRepayment,
            processingFee = offer.processingFee,
            netDisbursalAmount = offer.netDisbursalAmount,
            errorMessage = null
        )
    }

    fun submitLoanApplication(onSuccess: (LoanApplication) -> Unit) {
        val state = _applyLoanState.value
        val userId = authRepo.getUserId()
        val phone = authRepo.getCurrentUser()?.phoneNumber ?: userProfile.value.phone

        viewModelScope.launch {
            _applyLoanState.value = _applyLoanState.value.copy(isSubmitting = true, errorMessage = null)

            val appId = LoanApplication.generateApplicationId()
            val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
            val formattedDate = dateFormat.format(Date())

            val incomeDouble = state.monthlyIncome.toDoubleOrNull() ?: 0.0
            val emiDouble = state.existingEmi.toDoubleOrNull() ?: 0.0

            val application = LoanApplication(
                applicationId = appId,
                userId = userId,
                userPhone = phone,
                status = LoanStatus.SUBMITTED,
                statusMessage = "Application submitted successfully and queued for review.",
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
                formattedDate = formattedDate,

                fullName = state.fullName.trim(),
                dob = state.dob.trim(),
                gender = state.gender,
                email = state.email.trim(),
                address = state.address.trim(),
                city = state.city.trim(),
                state = state.state.trim(),
                pincode = state.pincode.trim(),

                employmentType = state.employmentType,
                companyName = state.companyName.trim(),
                monthlyIncome = incomeDouble,
                salaryMode = state.salaryMode,
                existingEmi = emiDouble,

                aadhaarNumber = state.aadhaarNumber,
                aadhaarFrontUri = state.aadhaarFrontUri,
                aadhaarBackUri = state.aadhaarBackUri,
                panNumber = state.panNumber,
                panUri = state.panUri,
                selfieUri = state.selfieUri,

                loanAmount = state.requestedAmount,
                loanPurpose = state.loanPurpose,
                tenureMonths = state.tenureMonths,

                interestRatePerAnnum = state.interestRatePerAnnum,
                monthlyEmi = state.monthlyEmi,
                totalInterest = state.totalInterest,
                totalRepayment = state.totalRepayment,
                processingFee = state.processingFee,
                netDisbursalAmount = state.netDisbursalAmount,

                termsAccepted = state.termsAccepted,
                creditBureauConsent = state.creditBureauConsent,

                paymentRequired = false, // Transparent notice
                paymentAmount = state.processingFee
            )

            val saveResult = appRepo.submitApplication(application)
            if (saveResult.isSuccess) {
                val savedApp = saveResult.getOrNull() ?: application
                _activeApplication.value = savedApp
                preferences.setActiveApplicationId(savedApp.applicationId)
                _applyLoanState.value = _applyLoanState.value.copy(
                    isSubmitting = false,
                    submittedApplication = savedApp,
                    currentStep = 7 // Move to Step 7 (Submission confirmation)
                )
                loadUserApplications()
                onSuccess(savedApp)
            } else {
                _applyLoanState.value = _applyLoanState.value.copy(
                    isSubmitting = false,
                    errorMessage = "Failed to submit application. Please retry."
                )
            }
        }
    }

    fun recordPaymentReference(refId: String) {
        val trimmed = refId.trim()
        val current = _applyLoanState.value
        _applyLoanState.value = current.copy(
            paymentReferenceId = trimmed,
            paymentDone = trimmed.isNotBlank()
        )
        val active = _activeApplication.value
        if (active != null && trimmed.isNotBlank()) {
            val updated = active.copy(
                isPaymentCompleted = true,
                paymentReferenceId = trimmed,
                statusMessage = "Verification fee recorded. Under credit review."
            )
            _activeApplication.value = updated
            viewModelScope.launch {
                appRepo.submitApplication(updated)
            }
        }
    }

    fun saveUserProfile(profile: UserProfile, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            val userId = authRepo.getUserId()
            val phone = authRepo.getCurrentUser()?.phoneNumber ?: profile.phone
            val toSave = profile.copy(userId = userId, phone = phone)
            preferences.setUserName(toSave.fullName)
            preferences.setUserEmail(toSave.email)
            _userProfile.value = toSave
            appRepo.saveUserProfile(toSave)
            onComplete()
        }
    }

    fun updateApplicationStatusByAdmin(
        applicationId: String,
        userId: String,
        newStatus: LoanStatus,
        remarks: String
    ) {
        viewModelScope.launch {
            appRepo.updateApplicationStatusByAdmin(applicationId, userId, newStatus, remarks)
            loadUserApplications()
        }
    }
}

