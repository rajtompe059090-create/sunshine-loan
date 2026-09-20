package com.sunshineloan.app.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.sunshineloan.app.model.ApplyLoanUiState
import com.sunshineloan.app.model.LoanApplication
import com.sunshineloan.app.model.LoanStatus
import com.sunshineloan.app.ui.MainViewModel
import com.sunshineloan.app.ui.theme.SunshineBackground
import com.sunshineloan.app.ui.theme.SunshineBorder
import com.sunshineloan.app.ui.theme.SunshineBorderLight
import com.sunshineloan.app.ui.theme.SunshineError
import com.sunshineloan.app.ui.theme.SunshineOrangeContainer
import com.sunshineloan.app.ui.theme.SunshineOrangeDark
import com.sunshineloan.app.ui.theme.SunshineOrangePrimary
import com.sunshineloan.app.ui.theme.SunshineSuccess
import com.sunshineloan.app.ui.theme.SunshineTextHint
import com.sunshineloan.app.ui.theme.SunshineTextPrimary
import com.sunshineloan.app.ui.theme.SunshineTextSecondary
import com.sunshineloan.app.ui.theme.SunshineWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplyLoanScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToStatus: () -> Unit
) {
    val state by viewModel.applyLoanState.collectAsState()
    val activeApp by viewModel.activeApplication.collectAsState()

    val stepTitles = listOf(
        "1. Personal Details",
        "2. Employment & Income",
        "3. KYC Verification",
        "4. Loan Requirement",
        "5. Loan Offer",
        "6. Review & Consent",
        "7. Submission",
        "8. Verification Fee & QR",
        "9. Application Status"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Apply for Loan",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = SunshineTextPrimary
                        )
                        Text(
                            text = "Step ${state.currentStep} of 9: ${stepTitles.getOrElse(state.currentStep - 1) { "" }}",
                            fontSize = 11.sp,
                            color = SunshineTextSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (state.currentStep > 1 && state.currentStep != 7 && state.currentStep != 9) {
                                viewModel.setApplyLoanStep(state.currentStep - 1)
                            } else {
                                onNavigateBack()
                            }
                        },
                        modifier = Modifier.testTag("apply_loan_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = SunshineTextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SunshineWhite)
            )
        },
        containerColor = SunshineBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Step Progress Bar
            LinearProgressIndicator(
                progress = { state.currentStep / 9f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = SunshineOrangePrimary,
                trackColor = SunshineOrangeContainer
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (state.currentStep) {
                    1 -> item { Step1PersonalDetails(viewModel = viewModel, state = state) }
                    2 -> item { Step2EmploymentIncome(viewModel = viewModel, state = state) }
                    3 -> item { Step3KycVerification(viewModel = viewModel, state = state) }
                    4 -> item { Step4LoanRequirement(viewModel = viewModel, state = state) }
                    5 -> item { Step5LoanOffer(viewModel = viewModel, state = state) }
                    6 -> item { Step6ReviewConsent(viewModel = viewModel, state = state) }
                    7 -> item { Step7Submission(viewModel = viewModel, state = state, onProceedToPayment = { viewModel.setApplyLoanStep(8) }, onViewStatus = onNavigateToStatus) }
                    8 -> item { Step8PaymentQr(viewModel = viewModel, state = state, onDone = { viewModel.setApplyLoanStep(9) }) }
                    9 -> item { Step9StatusTracker(viewModel = viewModel, app = activeApp ?: state.submittedApplication, onGoHome = onNavigateToHome) }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// STEP 1: PERSONAL DETAILS
// -------------------------------------------------------------
@Composable
fun Step1PersonalDetails(viewModel: MainViewModel, state: ApplyLoanUiState) {
    var fullName by remember { mutableStateOf(state.fullName) }
    var dob by remember { mutableStateOf(state.dob) }
    var gender by remember { mutableStateOf(state.gender) }
    var email by remember { mutableStateOf(state.email) }
    var address by remember { mutableStateOf(state.address) }
    var city by remember { mutableStateOf(state.city) }
    var stateName by remember { mutableStateOf(state.state) }
    var pincode by remember { mutableStateOf(state.pincode) }
    var localError by remember { mutableStateOf<String?>(null) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SunshineWhite),
        border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "Personal Information",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = SunshineTextPrimary
            )
            Text(
                text = "Enter your official personal details matching your government ID.",
                fontSize = 12.sp,
                color = SunshineTextSecondary
            )

            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it; localError = null },
                label = { Text("Full Name (as per PAN/Aadhaar)*") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("apply_fullname_input"),
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = dob,
                    onValueChange = { dob = it; localError = null },
                    label = { Text("DOB (DD/MM/YYYY)*") },
                    placeholder = { Text("15/08/1990") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("apply_dob_input"),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Gender*", fontSize = 12.sp, color = SunshineTextSecondary)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Male", "Female").forEach { g ->
                            FilterChip(
                                selected = gender == g,
                                onClick = { gender = g },
                                label = { Text(g, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SunshineOrangeContainer,
                                    selectedLabelColor = SunshineOrangeDark
                                )
                            )
                        }
                    }
                }
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it; localError = null },
                label = { Text("Email Address*") },
                placeholder = { Text("name@example.com") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("apply_email_input"),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            OutlinedTextField(
                value = address,
                onValueChange = { address = it; localError = null },
                label = { Text("Residential Address*") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("apply_address_input"),
                maxLines = 2
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it; localError = null },
                    label = { Text("City*") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = stateName,
                    onValueChange = { stateName = it; localError = null },
                    label = { Text("State*") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            OutlinedTextField(
                value = pincode,
                onValueChange = { if (it.length <= 6 && it.all { c -> c.isDigit() }) pincode = it; localError = null },
                label = { Text("6-Digit PIN Code*") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("apply_pincode_input"),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            if (localError != null) {
                Text(text = localError!!, color = SunshineError, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Button(
                onClick = {
                    if (fullName.isBlank()) {
                        localError = "Please enter your full name."
                    } else if (dob.isBlank()) {
                        localError = "Please enter your Date of Birth."
                    } else if (email.isBlank() || !email.contains("@")) {
                        localError = "Please enter a valid email address."
                    } else if (address.isBlank()) {
                        localError = "Please enter your residential address."
                    } else if (pincode.length != 6) {
                        localError = "Please enter a valid 6-digit PIN code."
                    } else {
                        viewModel.updatePersonalDetails(
                            fullName = fullName,
                            dob = dob,
                            gender = gender,
                            email = email,
                            address = address,
                            city = city.ifBlank { "Metro" },
                            state = stateName.ifBlank { "India" },
                            pincode = pincode
                        )
                        viewModel.setApplyLoanStep(2)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("step1_continue_btn"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SunshineOrangePrimary)
            ) {
                Text("Continue to Employment", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(6.dp))
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
            }
        }
    }
}

// -------------------------------------------------------------
// STEP 2: EMPLOYMENT & INCOME
// -------------------------------------------------------------
@Composable
fun Step2EmploymentIncome(viewModel: MainViewModel, state: ApplyLoanUiState) {
    var employmentType by remember { mutableStateOf(state.employmentType) }
    var companyName by remember { mutableStateOf(state.companyName) }
    var monthlyIncome by remember { mutableStateOf(state.monthlyIncome) }
    var salaryMode by remember { mutableStateOf(state.salaryMode) }
    var existingEmi by remember { mutableStateOf(state.existingEmi) }
    var localError by remember { mutableStateOf<String?>(null) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SunshineWhite),
        border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                text = "Employment & Income",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = SunshineTextPrimary
            )
            Text(
                text = "We assess your repayment capacity to offer competitive interest rates.",
                fontSize = 12.sp,
                color = SunshineTextSecondary
            )

            Text(text = "Employment Type*", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = SunshineTextPrimary)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Salaried", "Self-Employed", "Business").forEach { type ->
                    FilterChip(
                        selected = employmentType == type,
                        onClick = { employmentType = type },
                        label = { Text(type, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SunshineOrangeContainer,
                            selectedLabelColor = SunshineOrangeDark
                        )
                    )
                }
            }

            OutlinedTextField(
                value = companyName,
                onValueChange = { companyName = it; localError = null },
                label = { Text("Company / Business Name*") },
                placeholder = { Text("e.g. Infosys, TCS, Sunshine Enterprises") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("apply_company_input"),
                singleLine = true
            )

            OutlinedTextField(
                value = monthlyIncome,
                onValueChange = { monthlyIncome = it.filter { c -> c.isDigit() }; localError = null },
                label = { Text("Monthly In-Hand Income (₹)*") },
                leadingIcon = { Text("₹", fontWeight = FontWeight.Bold, color = SunshineOrangeDark) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("apply_income_input"),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Text(text = "Salary Credit Mode*", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = SunshineTextPrimary)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Bank Transfer", "Cheque", "Cash").forEach { mode ->
                    FilterChip(
                        selected = salaryMode == mode,
                        onClick = { salaryMode = mode },
                        label = { Text(mode, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SunshineOrangeContainer,
                            selectedLabelColor = SunshineOrangeDark
                        )
                    )
                }
            }

            OutlinedTextField(
                value = existingEmi,
                onValueChange = { existingEmi = it.filter { c -> c.isDigit() }; localError = null },
                label = { Text("Existing Monthly EMI Obligations (₹)") },
                placeholder = { Text("0") },
                leadingIcon = { Text("₹", fontWeight = FontWeight.Bold, color = SunshineTextSecondary) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            if (localError != null) {
                Text(text = localError!!, color = SunshineError, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Button(
                onClick = {
                    if (companyName.isBlank()) {
                        localError = "Please enter your company or business name."
                    } else if (monthlyIncome.isBlank() || (monthlyIncome.toDoubleOrNull() ?: 0.0) < 10000) {
                        localError = "Minimum monthly income of ₹10,000 required for loan eligibility."
                    } else {
                        viewModel.updateEmploymentDetails(
                            employmentType = employmentType,
                            companyName = companyName,
                            monthlyIncome = monthlyIncome,
                            salaryMode = salaryMode,
                            existingEmi = existingEmi.ifBlank { "0" }
                        )
                        viewModel.setApplyLoanStep(3)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("step2_continue_btn"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SunshineOrangePrimary)
            ) {
                Text("Continue to KYC", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(6.dp))
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
            }
        }
    }
}

// -------------------------------------------------------------
// STEP 3: KYC VERIFICATION
// -------------------------------------------------------------
@Composable
fun Step3KycVerification(viewModel: MainViewModel, state: ApplyLoanUiState) {
    var aadhaarNumber by remember { mutableStateOf(state.aadhaarNumber) }
    var panNumber by remember { mutableStateOf(state.panNumber) }
    var aadhaarFrontUri by remember { mutableStateOf(state.aadhaarFrontUri) }
    var aadhaarBackUri by remember { mutableStateOf(state.aadhaarBackUri) }
    var panUri by remember { mutableStateOf(state.panUri) }
    var selfieUri by remember { mutableStateOf(state.selfieUri) }
    var localError by remember { mutableStateOf<String?>(null) }

    // Pickers using Android standard zero-permission Photo Picker
    val aadhaarFrontLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? -> uri?.let { aadhaarFrontUri = it.toString() } }

    val aadhaarBackLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? -> uri?.let { aadhaarBackUri = it.toString() } }

    val panLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? -> uri?.let { panUri = it.toString() } }

    val selfieLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? -> uri?.let { selfieUri = it.toString() } }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SunshineWhite),
        border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                text = "KYC Document Verification",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = SunshineTextPrimary
            )
            Text(
                text = "Government regulations require 100% verified KYC for digital loan processing.",
                fontSize = 12.sp,
                color = SunshineTextSecondary
            )

            // Aadhaar Number
            OutlinedTextField(
                value = aadhaarNumber,
                onValueChange = {
                    if (it.length <= 12 && it.all { c -> c.isDigit() }) {
                        aadhaarNumber = it
                        localError = null
                    }
                },
                label = { Text("12-Digit Aadhaar Number*") },
                placeholder = { Text("1234 5678 9012") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("apply_aadhaar_input"),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // Aadhaar Uploads
            Text(text = "Aadhaar Card Photos*", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = SunshineTextPrimary)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                KycUploadCard(
                    modifier = Modifier.weight(1f),
                    title = "Front Side",
                    imageUri = aadhaarFrontUri,
                    onClick = { aadhaarFrontLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
                )
                KycUploadCard(
                    modifier = Modifier.weight(1f),
                    title = "Back Side",
                    imageUri = aadhaarBackUri,
                    onClick = { aadhaarBackLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
                )
            }

            // PAN Number
            OutlinedTextField(
                value = panNumber,
                onValueChange = {
                    if (it.length <= 10) {
                        panNumber = it.uppercase()
                        localError = null
                    }
                },
                label = { Text("10-Character PAN Number*") },
                placeholder = { Text("ABCDE1234F") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("apply_pan_input"),
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters)
            )

            // PAN & Selfie
            Text(text = "PAN Card & Selfie Photo*", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = SunshineTextPrimary)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                KycUploadCard(
                    modifier = Modifier.weight(1f),
                    title = "PAN Card Photo",
                    imageUri = panUri,
                    onClick = { panLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
                )
                KycUploadCard(
                    modifier = Modifier.weight(1f),
                    title = "Selfie Photo",
                    imageUri = selfieUri,
                    onClick = { selfieLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
                )
            }

            if (localError != null) {
                Text(text = localError!!, color = SunshineError, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Button(
                onClick = {
                    if (aadhaarNumber.length != 12) {
                        localError = "Please enter a valid 12-digit Aadhaar number."
                    } else if (panNumber.length != 10) {
                        localError = "Please enter a valid 10-character PAN number."
                    } else {
                        viewModel.updateKycDetails(
                            aadhaarNumber = aadhaarNumber,
                            aadhaarFrontUri = aadhaarFrontUri,
                            aadhaarBackUri = aadhaarBackUri,
                            panNumber = panNumber,
                            panUri = panUri,
                            selfieUri = selfieUri
                        )
                        viewModel.setApplyLoanStep(4)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("step3_continue_btn"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SunshineOrangePrimary)
            ) {
                Text("Continue to Loan Requirement", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(6.dp))
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
            }
        }
    }
}

@Composable
fun KycUploadCard(
    modifier: Modifier = Modifier,
    title: String,
    imageUri: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(100.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SunshineBackground),
        border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (imageUri.isNotBlank()) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Surface(
                    shape = RoundedCornerShape(topStart = 8.dp),
                    color = SunshineSuccess,
                    modifier = Modifier.align(Alignment.BottomEnd)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = SunshineWhite, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("Uploaded", color = SunshineWhite, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.AddAPhoto, contentDescription = null, tint = SunshineOrangeDark, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(title, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = SunshineTextPrimary)
                    Text("Tap to upload", fontSize = 9.sp, color = SunshineTextSecondary)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// STEP 4: LOAN REQUIREMENT
// -------------------------------------------------------------
@Composable
fun Step4LoanRequirement(viewModel: MainViewModel, state: ApplyLoanUiState) {
    var amount by remember { mutableStateOf(state.requestedAmount) }
    var purpose by remember { mutableStateOf(state.loanPurpose) }
    var tenure by remember { mutableIntStateOf(state.tenureMonths) }

    val purposes = listOf(
        "Personal & Family Needs",
        "Medical Emergency",
        "Home Renovation",
        "Education / Skill Upgrade",
        "Business Expansion",
        "Debt Consolidation"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SunshineWhite),
        border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                text = "Loan Requirement",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = SunshineTextPrimary
            )
            Text(
                text = "Select your required loan amount and preferred repayment tenure.",
                fontSize = 12.sp,
                color = SunshineTextSecondary
            )

            // Amount Slider
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SunshineOrangeContainer.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Requested Loan Amount", fontSize = 12.sp, color = SunshineTextSecondary)
                    Text(
                        text = "₹${viewModel.calculator.formatCurrency(amount).replace("₹", "").replace("-", "")}",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = SunshineOrangeDark
                    )
                    Slider(
                        value = amount.toFloat(),
                        onValueChange = { amount = (Math.round(it / 5000) * 5000).toDouble() },
                        valueRange = 20000f..500000f,
                        steps = 95,
                        colors = SliderDefaults.colors(
                            thumbColor = SunshineOrangeDark,
                            activeTrackColor = SunshineOrangePrimary
                        ),
                        modifier = Modifier.testTag("apply_amount_slider")
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Min: ₹20,000", fontSize = 11.sp, color = SunshineTextSecondary)
                        Text("Max: ₹5,00,000", fontSize = 11.sp, color = SunshineTextSecondary)
                    }
                }
            }

            // Purpose Selection
            Text(text = "Loan Purpose*", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = SunshineTextPrimary)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                purposes.chunked(2).forEach { rowList ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        rowList.forEach { p ->
                            FilterChip(
                                modifier = Modifier.weight(1f),
                                selected = purpose == p,
                                onClick = { purpose = p },
                                label = { Text(p, fontSize = 11.sp, maxLines = 1) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SunshineOrangeContainer,
                                    selectedLabelColor = SunshineOrangeDark
                                )
                            )
                        }
                    }
                }
            }

            // Tenure Selection
            Text(text = "Repayment Tenure (Months)*", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = SunshineTextPrimary)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(6, 12, 18, 24, 36).forEach { t ->
                    FilterChip(
                        modifier = Modifier.weight(1f),
                        selected = tenure == t,
                        onClick = { tenure = t },
                        label = { Text("${t}M", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SunshineOrangeContainer,
                            selectedLabelColor = SunshineOrangeDark
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Button(
                onClick = {
                    viewModel.updateLoanRequirement(amount, purpose, tenure)
                    viewModel.setApplyLoanStep(5)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("step4_continue_btn"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SunshineOrangePrimary)
            ) {
                Text("Generate Personalized Offer", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(6.dp))
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
            }
        }
    }
}

// -------------------------------------------------------------
// STEP 5: LOAN OFFER
// -------------------------------------------------------------
@Composable
fun Step5LoanOffer(viewModel: MainViewModel, state: ApplyLoanUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SunshineWhite),
        border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Your Sanction Offer",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = SunshineTextPrimary
                )
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFE8F5E9)
                ) {
                    Text(
                        text = "PRE-APPROVED",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SunshineSuccess,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            // Highlight Monthly EMI
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SunshineOrangeContainer)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "ESTIMATED MONTHLY EMI", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = SunshineOrangeDark)
                    Text(
                        text = "₹${viewModel.calculator.formatCurrency(state.monthlyEmi).replace("₹", "").replace("-", "")}",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = SunshineOrangeDark
                    )
                    Text(
                        text = "for ${state.tenureMonths} months @ ${state.interestRatePerAnnum}% p.a.",
                        fontSize = 12.sp,
                        color = SunshineTextSecondary
                    )
                }
            }

            // Key Fact Statement (KFS) Breakdown as per RBI digital lending norms
            Text(
                text = "Key Fact Statement (RBI Compliant)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = SunshineTextPrimary
            )

            OfferRow("Sanctioned Loan Amount", "₹${viewModel.calculator.formatCurrency(state.requestedAmount).replace("₹", "")}")
            OfferRow("Annual Interest Rate (Fixed)", "${state.interestRatePerAnnum}% p.a.")
            OfferRow("Repayment Tenure", "${state.tenureMonths} Months (${state.tenureMonths} EMIs)")
            OfferRow("Total Interest Payable", "₹${viewModel.calculator.formatCurrency(state.totalInterest).replace("₹", "")}")
            OfferRow("Total Repayment Amount", "₹${viewModel.calculator.formatCurrency(state.totalRepayment).replace("₹", "")}")
            HorizontalDivider(color = SunshineBorderLight)
            OfferRow("Documentation & Processing Fee (1.5% + GST)", "₹${viewModel.calculator.formatCurrency(state.processingFee).replace("₹", "")}")
            OfferRow("Net Disbursal in Bank Account", "₹${viewModel.calculator.formatCurrency(state.netDisbursalAmount).replace("₹", "")}", isHighlight = true)

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = { viewModel.setApplyLoanStep(6) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("step5_continue_btn"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SunshineOrangePrimary)
            ) {
                Text("Accept Offer & Review", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(6.dp))
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
            }
        }
    }
}

@Composable
fun OfferRow(label: String, value: String, isHighlight: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = if (isHighlight) 14.sp else 13.sp,
            fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Normal,
            color = if (isHighlight) SunshineTextPrimary else SunshineTextSecondary
        )
        Text(
            text = value,
            fontSize = if (isHighlight) 15.sp else 13.sp,
            fontWeight = FontWeight.Bold,
            color = if (isHighlight) SunshineOrangeDark else SunshineTextPrimary
        )
    }
}

// -------------------------------------------------------------
// STEP 6: REVIEW & CONSENT
// -------------------------------------------------------------
@Composable
fun Step6ReviewConsent(viewModel: MainViewModel, state: ApplyLoanUiState) {
    var consent1 by remember { mutableStateOf(state.termsAccepted) }
    var consent2 by remember { mutableStateOf(state.creditBureauConsent) }
    var consent3 by remember { mutableStateOf(true) }
    var localError by remember { mutableStateOf<String?>(null) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SunshineWhite),
        border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                text = "Review & Final Consent",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = SunshineTextPrimary
            )
            Text(
                text = "Please verify your application details before final submission.",
                fontSize = 12.sp,
                color = SunshineTextSecondary
            )

            // Section Summaries
            ReviewSectionCard(
                title = "Applicant Details",
                onEdit = { viewModel.setApplyLoanStep(1) },
                items = listOf(
                    "Name" to state.fullName,
                    "DOB & Gender" to "${state.dob} (${state.gender})",
                    "Email" to state.email,
                    "Address" to "${state.city}, ${state.state} - ${state.pincode}"
                )
            )

            ReviewSectionCard(
                title = "Employment & Income",
                onEdit = { viewModel.setApplyLoanStep(2) },
                items = listOf(
                    "Employment" to state.employmentType,
                    "Employer" to state.companyName,
                    "Monthly Income" to "₹${state.monthlyIncome}",
                    "Credit Mode" to state.salaryMode
                )
            )

            ReviewSectionCard(
                title = "KYC & Verification",
                onEdit = { viewModel.setApplyLoanStep(3) },
                items = listOf(
                    "Aadhaar Number" to "XXXX-XXXX-${state.aadhaarNumber.takeLast(4)}",
                    "PAN Card" to state.panNumber
                )
            )

            ReviewSectionCard(
                title = "Loan Terms & Disbursal",
                onEdit = { viewModel.setApplyLoanStep(4) },
                items = listOf(
                    "Loan Amount" to "₹${viewModel.calculator.formatCurrency(state.requestedAmount).replace("₹", "")}",
                    "Monthly EMI" to "₹${viewModel.calculator.formatCurrency(state.monthlyEmi).replace("₹", "")}",
                    "Tenure" to "${state.tenureMonths} Months",
                    "Net Disbursal" to "₹${viewModel.calculator.formatCurrency(state.netDisbursalAmount).replace("₹", "")}"
                )
            )

            // Mandatory Statutory Consents
            Text(
                text = "Declarations & Consents",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = SunshineTextPrimary
            )

            Row(verticalAlignment = Alignment.Top) {
                Checkbox(
                    checked = consent1,
                    onCheckedChange = { consent1 = it; localError = null },
                    colors = CheckboxDefaults.colors(checkedColor = SunshineOrangePrimary)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "I hereby declare that all information and documents submitted are authentic, accurate, and complete.",
                    fontSize = 12.sp,
                    color = SunshineTextSecondary
                )
            }

            Row(verticalAlignment = Alignment.Top) {
                Checkbox(
                    checked = consent2,
                    onCheckedChange = { consent2 = it; localError = null },
                    colors = CheckboxDefaults.colors(checkedColor = SunshineOrangePrimary)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "I authorize Sunshine Loan and its lending partners to pull my credit history from CIBIL/Experian as per RBI guidelines.",
                    fontSize = 12.sp,
                    color = SunshineTextSecondary
                )
            }

            Row(verticalAlignment = Alignment.Top) {
                Checkbox(
                    checked = consent3,
                    onCheckedChange = { consent3 = it; localError = null },
                    colors = CheckboxDefaults.colors(checkedColor = SunshineOrangePrimary)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "I understand that nominal document verification fee (if required) does NOT guarantee loan sanction. Approval is strictly subject to underwriting.",
                    fontSize = 12.sp,
                    color = SunshineTextSecondary
                )
            }

            if (localError != null) {
                Text(text = localError!!, color = SunshineError, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = {
                    if (!consent1 || !consent2 || !consent3) {
                        localError = "Please accept all statutory declarations and consents to proceed."
                    } else {
                        viewModel.submitLoanApplication { submittedApp ->
                            // Submitted successfully
                        }
                    }
                },
                enabled = !state.isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("step6_submit_btn"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SunshineOrangePrimary)
            ) {
                if (state.isSubmitting) {
                    CircularProgressIndicator(color = SunshineWhite, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Submitting to Firebase...")
                } else {
                    Text("Submit Loan Application", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ReviewSectionCard(title: String, onEdit: () -> Unit, items: List<Pair<String, String>>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = SunshineBackground),
        border = CardDefaults.outlinedCardBorder().copy(width = 0.5.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SunshineTextPrimary)
                Text(
                    text = "Edit",
                    fontSize = 12.sp,
                    color = SunshineOrangeDark,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable(onClick = onEdit)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            items.forEach { (k, v) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = k, fontSize = 11.sp, color = SunshineTextSecondary)
                    Text(text = v, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = SunshineTextPrimary)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// STEP 7: APPLICATION SUBMISSION
// -------------------------------------------------------------
@Composable
fun Step7Submission(
    viewModel: MainViewModel,
    state: ApplyLoanUiState,
    onProceedToPayment: () -> Unit,
    onViewStatus: () -> Unit
) {
    val app = state.submittedApplication

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SunshineWhite),
        border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8F5E9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Success",
                    tint = SunshineSuccess,
                    modifier = Modifier.size(40.dp)
                )
            }

            Text(
                text = "Application Submitted!",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = SunshineTextPrimary
            )

            Text(
                text = "Your Sunshine Loan application has been successfully logged with our underwriting team.",
                fontSize = 13.sp,
                color = SunshineTextSecondary,
                textAlign = TextAlign.Center
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SunshineOrangeContainer)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "APPLICATION REFERENCE ID", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SunshineOrangeDark)
                    Text(
                        text = app?.applicationId ?: "SL-2026-PENDING",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = SunshineTextPrimary
                    )
                    Text(text = "Please note this ID for all inquiries", fontSize = 11.sp, color = SunshineTextSecondary)
                }
            }

            Button(
                onClick = onProceedToPayment,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("step7_proceed_payment_btn"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SunshineOrangePrimary)
            ) {
                Text("Verification Notice & Payment (Step 8)", fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = onViewStatus,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("step7_view_status_btn"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Track Application Status")
            }
        }
    }
}

// -------------------------------------------------------------
// STEP 8: PAYMENT NOTICE & QR
// -------------------------------------------------------------
@Composable
fun Step8PaymentQr(viewModel: MainViewModel, state: ApplyLoanUiState, onDone: () -> Unit) {
    var refId by remember { mutableStateOf(state.paymentReferenceId) }
    var isConfirmed by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SunshineWhite),
        border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.QrCode, contentDescription = null, tint = SunshineOrangeDark, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Verification Charge & Admin QR",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = SunshineTextPrimary
                )
            }

            // CRITICAL TRANSPARENCY NOTICE (As mandated: never guarantee loan approval)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                border = CardDefaults.outlinedCardBorder().copy(width = 1.dp, brush = Brush.linearGradient(listOf(Color(0xFFFFB300), Color(0xFFFFE082))))
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
                    Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = Color(0xFFF57F17), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "MANDATORY STATUTORY NOTICE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE65100)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Payment of any administrative or document verification fee does NOT guarantee loan approval or sanction. Final sanction is subject to underwriting, document verification, and credit evaluation. Beware of fraudulent promises.",
                            fontSize = 11.sp,
                            color = SunshineTextPrimary,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            // Admin QR Code Box
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SunshineBackground),
                border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Official Sunshine Loan Verification UPI QR", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SunshineTextPrimary)
                    Spacer(modifier = Modifier.height(10.dp))

                    // QR Display Box
                    Box(
                        modifier = Modifier
                            .size(170.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SunshineWhite)
                            .border(1.dp, SunshineBorder, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(imageVector = Icons.Default.QrCode, contentDescription = "QR Code", tint = SunshineTextPrimary, modifier = Modifier.size(100.dp))
                            Text("UPI ID: sunshine@icici", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SunshineOrangeDark)
                            Text("Scan via PhonePe, GPay, Paytm", fontSize = 9.sp, color = SunshineTextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Document Verification Fee: ₹${viewModel.calculator.formatCurrency(state.processingFee).replace("₹", "")}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = SunshineOrangeDark
                    )
                }
            }

            OutlinedTextField(
                value = refId,
                onValueChange = { refId = it },
                label = { Text("UPI UTR / Reference ID (12 digits)") },
                placeholder = { Text("e.g. 423456789012") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("apply_payment_ref_input"),
                singleLine = true
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = {
                        if (refId.isNotBlank()) {
                            viewModel.recordPaymentReference(refId)
                        }
                        onDone()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("step8_submit_ref_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SunshineOrangePrimary)
                ) {
                    Text("Confirm & Continue", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onDone,
                    modifier = Modifier.height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Skip for Now")
                }
            }
        }
    }
}

// -------------------------------------------------------------
// STEP 9: APPLICATION STATUS TRACKER
// -------------------------------------------------------------
@Composable
fun Step9StatusTracker(
    viewModel: MainViewModel,
    app: LoanApplication?,
    onGoHome: () -> Unit
) {
    val currentApp = app ?: LoanApplication(
        applicationId = "SL-PENDING",
        status = LoanStatus.UNDER_REVIEW
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SunshineWhite),
        border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Application Status Tracker",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SunshineTextPrimary
                    )
                    Text(
                        text = "ID: ${currentApp.applicationId}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SunshineOrangeDark
                    )
                }
                StatusBadge(status = currentApp.status)
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SunshineOrangeContainer.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = currentApp.status.label,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = SunshineOrangeDark
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = currentApp.statusMessage.ifBlank { currentApp.status.description },
                        fontSize = 12.sp,
                        color = SunshineTextPrimary
                    )
                }
            }

            // Timeline Steps
            Text(text = "Application Lifecycle", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SunshineTextPrimary)

            val timeline = listOf(
                Triple("Application Submitted", "Your digital application was received", currentApp.createdAt > 0),
                Triple("Document Verification", "Verifying Aadhaar, PAN and KYC files", currentApp.status != LoanStatus.SUBMITTED),
                Triple("Credit Underwriting", "Assessing eligibility and repayment terms", currentApp.status == LoanStatus.UNDER_REVIEW || currentApp.status == LoanStatus.APPROVED || currentApp.status == LoanStatus.DISBURSED),
                Triple("Sanction & Approval", "Final credit approval & agreement sanction", currentApp.status == LoanStatus.APPROVED || currentApp.status == LoanStatus.DISBURSED),
                Triple("Bank Disbursal", "Direct credit to your verified bank account", currentApp.status == LoanStatus.DISBURSED)
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                timeline.forEachIndexed { idx, (stage, desc, isDone) ->
                    Row(verticalAlignment = Alignment.Top) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(if (isDone) SunshineSuccess else SunshineBorder),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isDone) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = SunshineWhite, modifier = Modifier.size(14.dp))
                            } else {
                                Text("${idx + 1}", fontSize = 11.sp, color = SunshineTextSecondary)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = stage,
                                fontSize = 13.sp,
                                fontWeight = if (isDone) FontWeight.Bold else FontWeight.Medium,
                                color = if (isDone) SunshineTextPrimary else SunshineTextSecondary
                            )
                            Text(text = desc, fontSize = 11.sp, color = SunshineTextHint)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Button(
                onClick = onGoHome,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("status_return_home_btn"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SunshineOrangePrimary)
            ) {
                Text("Return to Home Dashboard", fontWeight = FontWeight.Bold)
            }
        }
    }
}
