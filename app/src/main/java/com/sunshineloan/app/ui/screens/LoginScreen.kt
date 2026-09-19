package com.sunshineloan.app.ui.screens

import android.app.Activity
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sunshineloan.app.R
import com.sunshineloan.app.firebase.AuthState
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

@Composable
fun LoginScreen(
    viewModel: MainViewModel,
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val authState by viewModel.authState.collectAsState()

    var mobileNumber by remember { mutableStateOf("") }
    var selectedCountryCode by remember { mutableStateOf("+91") }
    var countryMenuExpanded by remember { mutableStateOf(false) }
    var otpCode by remember { mutableStateOf("") }
    var cooldownSeconds by remember { mutableIntStateOf(0) }
    var localError by remember { mutableStateOf<String?>(null) }

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

    // Auto navigate on authentication
    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) {
            onLoginSuccess()
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
        if (!viewModel.isFirebaseConfigured) {
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
                        if (mobileNumber.length < 6) {
                            localError = "Please enter a valid phone number"
                            return@Button
                        }
                        localError = null
                        val fullPhone = "$selectedCountryCode$mobileNumber"
                        if (activity != null) {
                            cooldownSeconds = 60
                            viewModel.sendOtp(fullPhone, activity, resendToken)
                        }
                    },
                    enabled = mobileNumber.length >= 6 && cooldownSeconds == 0 && !isLoading,
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
                            localError = "Please enter the 6-digit OTP"
                            return@Button
                        }
                        localError = null
                        if (verificationId.isNotBlank()) {
                            viewModel.verifyOtp(verificationId, otpCode)
                        } else {
                            // If running in development without Firebase config
                            onLoginSuccess()
                        }
                    },
                    enabled = otpCode.length == 6 && !isLoading,
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
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Error",
                        tint = SunshineError,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = errorToShow,
                        color = SunshineError,
                        fontSize = 13.sp
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
