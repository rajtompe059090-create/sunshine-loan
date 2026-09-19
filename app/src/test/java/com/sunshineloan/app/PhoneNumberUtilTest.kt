package com.sunshineloan.app

import com.sunshineloan.app.firebase.PhoneNumberUtil
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class PhoneNumberUtilTest {

    @Test
    fun testIndianStandardNumber() {
        val res = PhoneNumberUtil.format("+91", "9876543210")
        assertEquals("+919876543210", res.formattedNumber)
        assertNull(res.errorMessage)
    }

    @Test
    fun testIndianNumberWithLeadingZero() {
        val res = PhoneNumberUtil.format("+91", "09876543210")
        assertEquals("+919876543210", res.formattedNumber)
        assertNull(res.errorMessage)
    }

    @Test
    fun testIndianNumberWithLeading91() {
        val res = PhoneNumberUtil.format("+91", "919876543210")
        assertEquals("+919876543210", res.formattedNumber)
        assertNull(res.errorMessage)
    }

    @Test
    fun testIndianNumberWithPlus91Typed() {
        val res = PhoneNumberUtil.format("+91", "+919876543210")
        assertEquals("+919876543210", res.formattedNumber)
        assertNull(res.errorMessage)
    }

    @Test
    fun testIndianNumberWithSpacesAndDashes() {
        val res = PhoneNumberUtil.format("+91", "98765 43210")
        assertEquals("+919876543210", res.formattedNumber)
        assertNull(res.errorMessage)

        val res2 = PhoneNumberUtil.format("+91", "9876-543-210")
        assertEquals("+919876543210", res2.formattedNumber)
        assertNull(res2.errorMessage)
    }

    @Test
    fun testIndianNumberTooShort() {
        val res = PhoneNumberUtil.format("+91", "98765")
        assertNull(res.formattedNumber)
        assertNotNull(res.errorMessage)
    }

    @Test
    fun testIndianNumberInvalidStartingDigit() {
        val res = PhoneNumberUtil.format("+91", "1234567890")
        assertNull(res.formattedNumber)
        assertNotNull(res.errorMessage)
    }
}
