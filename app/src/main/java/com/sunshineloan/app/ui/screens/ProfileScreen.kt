package com.sunshineloan.app.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sunshineloan.app.model.UserProfile
import com.sunshineloan.app.ui.MainViewModel
import com.sunshineloan.app.ui.theme.SunshineBackground
import com.sunshineloan.app.ui.theme.SunshineError
import com.sunshineloan.app.ui.theme.SunshineOrangeContainer
import com.sunshineloan.app.ui.theme.SunshineOrangeDark
import com.sunshineloan.app.ui.theme.SunshineOrangePrimary
import com.sunshineloan.app.ui.theme.SunshineTextHint
import com.sunshineloan.app.ui.theme.SunshineTextPrimary
import com.sunshineloan.app.ui.theme.SunshineTextSecondary
import com.sunshineloan.app.ui.theme.SunshineWhite
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit
) {
    val profile by viewModel.userProfile.collectAsState()
    val currentUser = viewModel.authRepo.getCurrentUser()
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var fullName by remember(profile) { mutableStateOf(profile.fullName) }
    var email by remember(profile) { mutableStateOf(profile.email) }
    var dob by remember(profile) { mutableStateOf(profile.dob) }
    var gender by remember(profile) { mutableStateOf(profile.gender) }
    var address by remember(profile) { mutableStateOf(profile.address) }
    var city by remember(profile) { mutableStateOf(profile.city) }
    var stateName by remember(profile) { mutableStateOf(profile.state) }
    var pincode by remember(profile) { mutableStateOf(profile.pincode) }
    var employmentType by remember(profile) { mutableStateOf(profile.employmentType) }
    var monthlyIncome by remember(profile) { mutableStateOf(if (profile.monthlyIncome > 0) profile.monthlyIncome.toInt().toString() else "") }

    val verifiedPhone = currentUser?.phoneNumber ?: profile.phone.ifBlank { "Unregistered" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Profile",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SunshineTextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("profile_back_btn")) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = SunshineTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SunshineWhite)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = SunshineBackground
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Avatar & Phone Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SunshineWhite),
                    border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(SunshineOrangeContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = fullName.firstOrNull()?.uppercase() ?: "S",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = SunshineOrangeDark
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = fullName.ifBlank { "Valued Customer" },
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = SunshineTextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = null,
                                    tint = SunshineOrangeDark,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = verifiedPhone,
                                    fontSize = 13.sp,
                                    color = SunshineTextSecondary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "• Verified via OTP",
                                    fontSize = 11.sp,
                                    color = SunshineOrangeDark,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            // Editable Personal Details
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SunshineWhite),
                    border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "Personal Details",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = SunshineTextPrimary
                        )

                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = { Text("Full Name") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("profile_fullname_input"),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
                        )

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email Address") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("profile_email_input"),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = dob,
                                onValueChange = { dob = it },
                                label = { Text("DOB (DD/MM/YYYY)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )

                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "Gender", fontSize = 11.sp, color = SunshineTextSecondary)
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    listOf("Male", "Female").forEach { g ->
                                        FilterChip(
                                            selected = gender == g,
                                            onClick = { gender = g },
                                            label = { Text(g, fontSize = 11.sp) },
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
                            value = address,
                            onValueChange = { address = it },
                            label = { Text("Current Residential Address") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 2
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = city,
                                onValueChange = { city = it },
                                label = { Text("City") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = stateName,
                                onValueChange = { stateName = it },
                                label = { Text("State") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }

                        OutlinedTextField(
                            value = pincode,
                            onValueChange = { if (it.length <= 6 && it.all { c -> c.isDigit() }) pincode = it },
                            label = { Text("PIN Code (6 digits)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        Button(
                            onClick = {
                                val updated = profile.copy(
                                    fullName = fullName.trim(),
                                    email = email.trim(),
                                    dob = dob.trim(),
                                    gender = gender,
                                    address = address.trim(),
                                    city = city.trim(),
                                    state = stateName.trim(),
                                    pincode = pincode.trim(),
                                    employmentType = employmentType,
                                    monthlyIncome = monthlyIncome.toDoubleOrNull() ?: profile.monthlyIncome
                                )
                                viewModel.saveUserProfile(updated) {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Profile details saved successfully!")
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("profile_save_btn"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SunshineOrangePrimary)
                        ) {
                            Icon(imageVector = Icons.Default.Save, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Save Profile Changes", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Account & Security Options
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SunshineWhite),
                    border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Account Security",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = SunshineTextPrimary
                        )

                        Text(
                            text = "Logged in securely via Firebase OTP verification. Your phone number is your Sunshine Loan Customer ID.",
                            fontSize = 12.sp,
                            color = SunshineTextSecondary
                        )

                        OutlinedButton(
                            onClick = {
                                viewModel.authRepo.signOut()
                                onLogout()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("profile_logout_btn"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = SunshineError)
                        ) {
                            Icon(imageVector = Icons.Default.ExitToApp, contentDescription = null, tint = SunshineError)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Sign Out of Sunshine Loan", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}
