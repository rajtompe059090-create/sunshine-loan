package com.sunshineloan.app.ui.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.PhoneAuthProvider
import com.sunshineloan.app.R
import com.sunshineloan.app.firebase.AuthState
import com.sunshineloan.app.firebase.PhoneNumberUtil
import com.sunshineloan.app.ui.MainViewModel
import com.sunshineloan.app.ui.theme.SunshineBorder
import com.sunshineloan.app.ui.theme.SunshineError
import com.sunshineloan.app.ui.theme.SunshineOrangeContainer
import com.sunshineloan.app.ui.theme.SunshineOrangeDark
import com.sunshineloan.app.ui.theme.SunshineOrangePrimary
import com.sunshineloan.app.ui.theme.SunshineTextHint
import com.sunshineloan.app.ui.theme.SunshineTextPrimary
import com.sunshineloan.app.ui.theme.SunshineTextSecondary
import com.sunshineloan.app.ui.theme.SunshineWhite
import kotlinx.coroutines.delay

private fun Context.findActivity(): Activity? {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}

private const val DEBUG_SHA1_FINGERPRINT = "49:15:46:5B:C2:86:21:F7:0D:7B:98:80:64:18:EE:D4:1E:98:F3:4E"
private const val DEBUG_SHA256_FINGERPRINT = "DF:54:F6:05:57:3E:8C:0B:29:D5:B9:06:7C:AC:0C:A4:AD:CF:A7:67:B7:64:3F:1F:6A:93:3F:F3:55:3B:B2:F1"

@Composable
fun LoginScreen(
    viewModel: MainViewModel,
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }
    val clipboardManager = LocalClipboardManager.current
    val authState by viewModel.authState.collectAsState()

    var mobileNumber by remember { mutableStateOf("") }
    var selectedCountryCode by remember { mutableStateOf("+91") }
    var countryMenuExpanded by remember { mutableStateOf(false) }

    var otpCode by remember { mutableStateOf("") }
    var storedVerificationId by remember { mutableStateOf<String?>(null) }
    var storedResendToken by remember { mutableStateOf<PhoneAuthProvider.ForceResendingToken?>(null) }
    var verifiedPhoneNumber by remember { mutableStateOf<String?>(null) }

    var cooldownSeconds by remember { mutableIntStateOf(0) }
    var timerTrigger by remember { mutableIntStateOf(0) }

    var localError by remember { mutableStateOf<String?>(null) }
    var showFirebaseDetails by remember { mutableStateOf(false) }
    var shaCopiedNotice by remember { mutableStateOf(false) }

    val countryCodes = remember {
        listOf(
            "+91" to "India (+91)",
            "+1" to "USA / Canada (+1)",
            "+44" to "UK (+44)",
            "+61" to "Australia (+61)",
            "+81" to "Japan (+81)",
            "+65" to "Singapore (+65)",
            "+971" to "UAE (+971)"
        )
    }

    // 60-Second non-blocking countdown loop
    LaunchedEffect(timerTrigger) {
        if (timerTrigger > 0 && cooldownSeconds > 0) {
            while (cooldownSeconds > 0) {
                delay(1000L)
                cooldownSeconds -= 1
            }
        }
    }

    // React to Firebase Auth state transitions
    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthState.Authenticated -> {
                onLoginSuccess()
            }
            is AuthState.CodeSent -> {
                storedVerificationId = state.verificationId
                storedResendToken = state.token
                verifiedPhoneNumber = state.phoneNumber
                cooldownSeconds = 60
                timerTrigger += 1
                localError = null
            }
            is AuthState.Error -> {
                localError = state.message
                // If error happened while sending code (and no code was ever sent), stay on phone card
                if (storedVerificationId == null) {
                    cooldownSeconds = 0
                }
            }
            else -> {}
        }
    }

    val isCodeSent = storedVerificationId != null
    val isLoading = authState is AuthState.Loading
    val isShaError = localError?.contains("SHA-1", ignoreCase = true) == true ||
            (authState as? AuthState.Error)?.message?.contains("SHA-1", ignoreCase = true) == true

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SunshineWhite)
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Sunshine Loan App Logo
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape)
                .background(SunshineOrangeContainer)
                .border(2.dp, SunshineOrangePrimary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_sunshine_logo),
                contentDescription = "Sunshine Loan Logo",
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Sunshine Loan",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = SunshineTextPrimary,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Instant Personal & Business Loans",
            fontSize = 14.sp,
            color = SunshineTextSecondary,
            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
        )

        // CARD 1: Mobile Number Input
        Card(
            colors = CardDefaults.cardColors(containerColor = SunshineWhite),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Mobile Number",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = SunshineTextPrimary
                    )

                    if (isCodeSent) {
                        TextButton(
                            onClick = {
                                storedVerificationId = null
                                storedResendToken = null
                                verifiedPhoneNumber = null
                                otpCode = ""
                                cooldownSeconds = 0
                                localError = null
                                viewModel.resetAuthState()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Change Number",
                                tint = SunshineOrangeDark,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Change",
                                fontSize = 13.sp,
                                color = SunshineOrangeDark,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Country Code Dropdown
                    Box {
                        Row(
                            modifier = Modifier
                                .height(56.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, SunshineBorder, RoundedCornerShape(10.dp))
                                .clickable(enabled = !isCodeSent && !isLoading) {
                                    countryMenuExpanded = true
                                }
                                .padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = selectedCountryCode,
                                fontWeight = FontWeight.Bold,
                                color = SunshineTextPrimary,
                                fontSize = 15.sp
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Select country code",
                                tint = SunshineTextSecondary
                            )
                        }

                        DropdownMenu(
                            expanded = countryMenuExpanded,
                            onDismissRequest = { countryMenuExpanded = false }
                        ) {
                            countryCodes.forEach { (code, label) ->
                                DropdownMenuItem(
                                    text = { Text(label, fontSize = 14.sp) },
                                    onClick = {
                                        selectedCountryCode = code
                                        countryMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Mobile Number Input
                    OutlinedTextField(
                        value = mobileNumber,
                        onValueChange = { input ->
                            if (!isCodeSent) {
                                mobileNumber = input.filter { it.isDigit() }
                                localError = null
                            }
                        },
                        enabled = !isCodeSent && !isLoading,
                        placeholder = { Text("10-digit number", color = SunshineTextHint) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = null,
                                tint = SunshineTextSecondary
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SunshineOrangePrimary,
                            unfocusedBorderColor = SunshineBorder,
                            focusedContainerColor = SunshineWhite,
                            unfocusedContainerColor = SunshineWhite
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("mobile_number_input")
                    )
                }

                // If code not yet sent, show the Get OTP button
                if (!isCodeSent) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val formatResult = PhoneNumberUtil.format(selectedCountryCode, mobileNumber)
                            if (formatResult.formattedNumber == null) {
                                localError = formatResult.errorMessage ?: "Please enter a valid mobile number."
                                return@Button
                            }
                            val targetActivity = activity ?: context.findActivity()
                            if (targetActivity == null) {
                                localError = "Cannot initiate verification: Activity context unavailable."
                                return@Button
                            }
                            localError = null
                            viewModel.sendOtp(formatResult.formattedNumber, targetActivity, null)
                        },
                        enabled = mobileNumber.isNotBlank() && !isLoading,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SunshineOrangePrimary,
                            disabledContainerColor = SunshineOrangeContainer
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("get_otp_button")
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = SunshineWhite,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Sending OTP...",
                                color = SunshineWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        } else {
                            Text(
                                text = "Get OTP",
                                color = SunshineWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }

        // CARD 2: OTP Verification Card (Revealed on Code Sent)
        AnimatedVisibility(
            visible = isCodeSent,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column {
                Spacer(modifier = Modifier.height(18.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = SunshineWhite),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Enter Verification Code",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = SunshineTextPrimary
                        )

                        Text(
                            text = "A 6-digit OTP has been sent via SMS to ${verifiedPhoneNumber ?: (selectedCountryCode + mobileNumber)}",
                            fontSize = 13.sp,
                            color = SunshineTextSecondary,
                            modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                        )

                        OutlinedTextField(
                            value = otpCode,
                            onValueChange = { input ->
                                if (input.length <= 6) {
                                    otpCode = input.filter { it.isDigit() }
                                    localError = null
                                }
                            },
                            placeholder = { Text("Enter 6-digit OTP", color = SunshineTextHint) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = SunshineTextSecondary
                                )
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SunshineOrangePrimary,
                                unfocusedBorderColor = SunshineBorder,
                                focusedContainerColor = SunshineWhite,
                                unfocusedContainerColor = SunshineWhite
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("otp_code_input")
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // 60-Second Resend Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (cooldownSeconds > 0) {
                                Text(
                                    text = "Resend OTP in ${cooldownSeconds}s",
                                    fontSize = 13.sp,
                                    color = SunshineTextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            } else {
                                Text(
                                    text = "Didn't receive OTP?",
                                    fontSize = 13.sp,
                                    color = SunshineTextSecondary
                                )
                            }

                            TextButton(
                                onClick = {
                                    val targetPhone = verifiedPhoneNumber ?: PhoneNumberUtil.format(selectedCountryCode, mobileNumber).formattedNumber
                                    val targetActivity = activity ?: context.findActivity()
                                    if (targetPhone != null && targetActivity != null) {
                                        localError = null
                                        cooldownSeconds = 60
                                        timerTrigger += 1
                                        viewModel.sendOtp(targetPhone, targetActivity, storedResendToken)
                                    }
                                },
                                enabled = cooldownSeconds == 0 && !isLoading,
                                modifier = Modifier.testTag("resend_otp_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Resend OTP",
                                    tint = if (cooldownSeconds == 0 && !isLoading) SunshineOrangePrimary else SunshineTextHint,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Resend OTP",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (cooldownSeconds == 0 && !isLoading) SunshineOrangePrimary else SunshineTextHint
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Login Button
                        Button(
                            onClick = {
                                val currentVerificationId = storedVerificationId
                                if (otpCode.length != 6) {
                                    localError = "Please enter the complete 6-digit OTP code."
                                    return@Button
                                }
                                if (currentVerificationId.isNullOrBlank()) {
                                    localError = "Verification session not found. Please tap 'Resend OTP'."
                                    return@Button
                                }
                                localError = null
                                viewModel.verifyOtp(currentVerificationId, otpCode)
                            },
                            enabled = otpCode.length == 6 && !isLoading && !storedVerificationId.isNullOrBlank(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SunshineOrangePrimary,
                                disabledContainerColor = SunshineOrangeContainer
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("login_button")
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    color = SunshineWhite,
                                    modifier = Modifier.size(22.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Verifying OTP...",
                                    color = SunshineWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            } else {
                                Text(
                                    text = "Login",
                                    color = SunshineWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Error message card
        val errorToShow = localError ?: (authState as? AuthState.Error)?.message
        if (!errorToShow.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Error",
                        tint = SunshineError,
                        modifier = Modifier
                            .size(20.dp)
                            .padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = errorToShow,
                        color = SunshineError,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // SHA-1 & Firebase Setup Guide Card (Always available, auto-expanded if SHA-1 error occurs)
        val shouldExpandSha = showFirebaseDetails || isShaError
        Card(
            colors = CardDefaults.cardColors(containerColor = SunshineOrangeContainer),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showFirebaseDetails = !showFirebaseDetails }
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Firebase Info",
                            tint = SunshineOrangeDark,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Firebase SHA-1 & Project Config",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = SunshineOrangeDark
                        )
                    }
                    Text(
                        text = if (shouldExpandSha) "Hide" else "Show",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SunshineOrangeDark
                    )
                }

                if (shouldExpandSha) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "For Firebase Phone OTP to work, your app's debug SHA-1 must be registered in the Firebase Console:\n" +
                                "1. Open Firebase Console -> Project: sunshine-loan-b8296\n" +
                                "2. Project Settings -> Your Apps -> Android (com.sunshineloan.app)\n" +
                                "3. Click 'Add fingerprint' and paste the SHA-1 below:",
                        fontSize = 12.sp,
                        color = SunshineTextPrimary,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = SunshineWhite),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = DEBUG_SHA1_FINGERPRINT,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = SunshineTextPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(DEBUG_SHA1_FINGERPRINT))
                                    shaCopiedNotice = true
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy SHA-1",
                                    tint = SunshineOrangeDark,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    if (shaCopiedNotice) {
                        Text(
                            text = "Copied SHA-1 to clipboard!",
                            fontSize = 11.sp,
                            color = SunshineOrangeDark,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tip: In Firebase Console -> Authentication -> Sign-in method -> Phone, enable Phone provider. You can also register test phone numbers (e.g. +91 9999999999 with OTP 123456) for instant testing.",
                        fontSize = 11.sp,
                        color = SunshineTextSecondary,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
