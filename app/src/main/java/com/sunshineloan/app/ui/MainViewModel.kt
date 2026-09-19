package com.sunshineloan.app.ui

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.PhoneAuthProvider
import com.sunshineloan.app.calculator.LoanCalculator
import com.sunshineloan.app.data.UserPreferences
import com.sunshineloan.app.firebase.AuthRepository
import com.sunshineloan.app.firebase.AuthState
import com.sunshineloan.app.firebase.LoanRepository
import com.sunshineloan.app.model.LoanCalculationResult
import com.sunshineloan.app.model.LoanRecord
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

    // Auth & Firebase State
    val authState: StateFlow<AuthState> = authRepo.authState
    val isFirebaseConfigured: Boolean = authRepo.isFirebaseConfigured()

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
}
