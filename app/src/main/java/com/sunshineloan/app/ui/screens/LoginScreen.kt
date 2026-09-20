package com.sunshineloan.app.ui.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
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
    var cooldownSeconds by remember { mutableIntStateOf(0) }
    var localError by remember { mutableStateOf<String?>(null) }
    var showFirebaseDetails by remember { mutableStateOf(false) }
    var shaCopiedNotice by remember { mutableStateOf(false) }
    var isConfigured by remember { mutableStateOf(viewModel.isFirebaseConfigured) }
    LaunchedEffect(Unit) {
        isConfigured = viewModel.isFirebaseConfigured
    }

    val countryCodes = listOf(
        "+91" to "India (+91)",
        "+1" to "USA / Canada (+1)",
        "+44" to "UK (+44)",
        "+61" to "Australia (+61)",
        "+81" to "Japan (+81)",
        "+65" to "Singapore (+65)",
        "+971" to "UAE (+971)"
    )

    // Cooldown countdown timer
    LaunchedEffect(cooldownSeconds) {
        if (cooldownSeconds > 0) {
            delay(1000L)
            cooldownSeconds -= 1
        }
    }

    // React to Firebase Auth state transitions
    LaunchedEffect(authState) {
        isConfigured = viewModel.isFirebaseConfigured
        when (authState) {
            is AuthState.Authenticated -> {
                onLoginSuccess()
            }
            is AuthState.CodeSent -> {
                cooldownSeconds = 60
                localError = null
            }
            is AuthState.Error -> {
                cooldownSeconds = 0
            }
            else -> {}
        }
    }

    val isCodeSent = authState is AuthState.CodeSent
    val verificationId = (authState as? AuthState.CodeSent)?.verificationId ?: ""
    val resendToken = (authState as? AuthState.CodeSent)?.token
    val isLoading = authState is AuthState.Loading

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SunshineWhite)
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Sunshine Loan App Logo
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(SunshineOrangeContainer)
                .border(2.dp, SunshineOrangePrimary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_sunshine_logo),
                contentDescription = "Sunshine Loan Logo",
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Sunshine Loan: VA Loan Calc",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = SunshineTextPrimary,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Secure Mobile Verification",
            fontSize = 14.sp,
            color = SunshineTextSecondary,
            modifier = Modifier.padding(top = 4.dp, bottom = 28.dp)
        )

        // Firebase Configuration notice if google-services.json not found
        if (!isConfigured) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Configuration info",
                        tint = SunshineOrangeDark,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Firebase Config Notice",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = SunshineTextPrimary
                        )
                        Text(
                            text = "To enable live SMS OTP verification, add your google-services.json file at 'app/google-services.json'.",
                            fontSize = 12.sp,
                            color = SunshineTextSecondary,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }

        // Phone Input Card
        Card(
            colors = CardDefaults.cardColors(containerColor = SunshineWhite),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Enter Mobile Number",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = SunshineTextPrimary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

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
                                .clickable { countryMenuExpanded = true }
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
                            mobileNumber = input.filter { it.isDigit() }
                            localError = null
                        },
                        placeholder = { Text("Mobile number", color = SunshineTextHint) },
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

                Spacer(modifier = Modifier.height(14.dp))

                // Get OTP Button
                Button(
                    onClick = {
                        val formatResult = PhoneNumberUtil.format(selectedCountryCode, mobileNumber)
                        if (formatResult.formattedNumber == null) {
                            localError = formatResult.errorMessage
                            return@Button
                        }
                        val targetActivity = activity ?: context.findActivity()
                        if (targetActivity == null) {
                            localError = "Cannot initiate verification: Activity context unavailable."
                            return@Button
                        }
                        localError = null
                        viewModel.sendOtp(formatResult.formattedNumber, targetActivity, resendToken)
                    },
                    enabled = mobileNumber.isNotBlank() && cooldownSeconds == 0 && !isLoading,
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
                    if (isLoading && !isCodeSent) {
                        CircularProgressIndicator(
                            color = SunshineWhite,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = if (cooldownSeconds > 0) "Resend OTP in ${cooldownSeconds}s" else if (isCodeSent) "Resend OTP" else "Get OTP",
                            color = if (cooldownSeconds > 0) SunshineTextSecondary else SunshineWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // OTP Input Card (Shown after code is sent or available)
        Card(
            colors = CardDefaults.cardColors(containerColor = SunshineWhite),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Verification Code",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = SunshineTextPrimary,
                    modifier = Modifier.padding(bottom = 8.dp)
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

                Spacer(modifier = Modifier.height(16.dp))

                // Login Button
                Button(
                    onClick = {
                        if (otpCode.length != 6) {
                            localError = "Please enter the complete 6-digit OTP"
                            return@Button
                        }
                        if (verificationId.isBlank()) {
                            localError = "Please tap 'Get OTP' first to send a verification code to your phone."
                            return@Button
                        }
                        localError = null
                        viewModel.verifyOtp(verificationId, otpCode)
                    },
                    enabled = otpCode.length == 6 && !isLoading && verificationId.isNotBlank(),
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
                    if (isLoading && isCodeSent) {
                        CircularProgressIndicator(
                            color = SunshineWhite,
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp
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

        // Error message display
        val errorToShow = localError ?: (authState as? AuthState.Error)?.message
        if (!errorToShow.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Error",
                            tint = SunshineError,
                            modifier = Modifier.size(20.dp).padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = errorToShow,
                            color = SunshineError,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Firebase Setup & SHA-1 Helper
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
                            text = "Firebase SHA-1 & Auth Setup Guide",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = SunshineOrangeDark
                        )
                    }
                    Text(
                        text = if (showFirebaseDetails) "Hide" else "Show",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SunshineOrangeDark
                    )
                }

                if (showFirebaseDetails) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "For OTP delivery to succeed, Firebase requires your app's SHA-1 fingerprint in the Firebase Console:\n" +
                               "1. Go to Firebase Console -> sunshine-loan-b8296\n" +
                               "2. Project Settings -> Your Apps -> Android (com.sunshineloan.app)\n" +
                               "3. Add the following SHA-1 fingerprint:",
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
                                text = "49:15:46:5B:C2:86:21:F7:0D:7B:98:80:64:18:EE:D4:1E:98:F3:4E",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = SunshineTextPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(
                                        AnnotatedString("49:15:46:5B:C2:86:21:F7:0D:7B:98:80:64:18:EE:D4:1E:98:F3:4E")
                                    )
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
                        text = "Tip: In Firebase Console -> Authentication -> Sign-in method -> Phone, add a test phone number (e.g. +91 9999999999 with code 123456) for instant testing.",
                        fontSize = 11.sp,
                        color = SunshineTextSecondary,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // Development quick-access button if evaluating without live SMS credit
        if (!viewModel.isFirebaseConfigured) {
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedButton(
                onClick = { onLoginSuccess() },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = SunshineOrangeDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continue to VA Loan Calculator (Demo Mode)", fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
