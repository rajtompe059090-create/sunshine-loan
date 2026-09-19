package com.sunshineloan.app.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Persists user preferences locally (currency, VA calculation assumption defaults).
 */
class UserPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _currencyCode = MutableStateFlow(
        prefs.getString(KEY_CURRENCY, "INR") ?: "INR"
    )
    val currencyCode: StateFlow<String> = _currencyCode.asStateFlow()

    private val _fundingFeeRate = MutableStateFlow(
        prefs.getFloat(KEY_FUNDING_FEE_RATE, 2.15f).toDouble()
    )
    val fundingFeeRate: StateFlow<Double> = _fundingFeeRate.asStateFlow()

    private val _guarantyRate = MutableStateFlow(
        prefs.getFloat(KEY_GUARANTY_RATE, 25.0f).toDouble()
    )
    val guarantyRate: StateFlow<Double> = _guarantyRate.asStateFlow()

    fun setCurrency(currency: String) {
        val valid = "INR"
        prefs.edit().putString(KEY_CURRENCY, valid).apply()
        _currencyCode.value = valid
    }

    fun setFundingFeeRate(rate: Double) {
        val safe = if (rate.isFinite() && rate >= 0.0) rate else 2.15
        prefs.edit().putFloat(KEY_FUNDING_FEE_RATE, safe.toFloat()).apply()
        _fundingFeeRate.value = safe
    }

    fun setGuarantyRate(rate: Double) {
        val safe = if (rate.isFinite() && rate >= 0.0) rate else 25.0
        prefs.edit().putFloat(KEY_GUARANTY_RATE, safe.toFloat()).apply()
        _guarantyRate.value = safe
    }

    fun resetToDefaults() {
        setFundingFeeRate(2.15)
        setGuarantyRate(25.0)
        setCurrency("INR")
    }

    companion object {
        private const val PREFS_NAME = "sunshine_loan_prefs"
        private const val KEY_CURRENCY = "currency_code"
        private const val KEY_FUNDING_FEE_RATE = "funding_fee_rate"
        private const val KEY_GUARANTY_RATE = "guaranty_rate"

        @Volatile
        private var INSTANCE: UserPreferences? = null

        fun getInstance(context: Context): UserPreferences {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserPreferences(context).also { INSTANCE = it }
            }
        }
    }
}
