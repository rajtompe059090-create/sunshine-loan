package com.sunshineloan.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sunshineloan.app.ui.MainViewModel
import com.sunshineloan.app.ui.theme.SunshineBackground
import com.sunshineloan.app.ui.theme.SunshineBorder
import com.sunshineloan.app.ui.theme.SunshineError
import com.sunshineloan.app.ui.theme.SunshineOrangeContainer
import com.sunshineloan.app.ui.theme.SunshineOrangeDark
import com.sunshineloan.app.ui.theme.SunshineOrangePrimary
import com.sunshineloan.app.ui.theme.SunshineTextHint
import com.sunshineloan.app.ui.theme.SunshineTextPrimary
import com.sunshineloan.app.ui.theme.SunshineTextSecondary
import com.sunshineloan.app.ui.theme.SunshineWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onLogoutConfirmed: () -> Unit
) {
    val currencyCode by viewModel.preferences.currencyCode.collectAsState()
    val fundingFeeRate by viewModel.preferences.fundingFeeRate.collectAsState()
    val guarantyRate by viewModel.preferences.guarantyRate.collectAsState()

    var showLogoutDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }

    var fundingFeeInput by remember(fundingFeeRate) { mutableStateOf(fundingFeeRate.toString()) }
    var guarantyInput by remember(guarantyRate) { mutableStateOf(guarantyRate.toString()) }

    // Logout Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Log Out?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to end your current authenticated session?") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        viewModel.signOut()
                        onLogoutConfirmed()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SunshineError)
                ) {
                    Text("Log Out", color = SunshineWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = SunshineTextSecondary)
                }
            }
        )
    }

    // Privacy Policy Dialog
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text("Privacy Policy", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(
                        text = "Sunshine Loan Privacy Disclosure\n\n" +
                                "1. Information We Collect:\n" +
                                "We collect your phone number strictly for authentication via Firebase Authentication. Loan calculations you choose to save are linked exclusively to your user identifier and stored in Firestore.\n\n" +
                                "2. Security & Confidentiality:\n" +
                                "We do not sell, rent, or distribute personal financial inquiries to third-party lenders or advertising networks. Security rules restrict access to your saved calculations solely to your authenticated account.\n\n" +
                                "3. Financial Estimates:\n" +
                                "Calculations processed within this application are strictly mathematical simulations based on user inputs and configurable assumptions.",
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = SunshineTextSecondary
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showPrivacyDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = SunshineOrangePrimary)
                ) {
                    Text("Close", color = SunshineWhite)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = SunshineWhite
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = SunshineWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SunshineOrangePrimary
                )
            )
        },
        containerColor = SunshineBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Section 1: Currency Standard
            SectionHeader(title = "App Currency", icon = Icons.Default.AttachMoney)

            Card(
                colors = CardDefaults.cardColors(containerColor = SunshineWhite),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = true,
                            onClick = null,
                            colors = RadioButtonDefaults.colors(selectedColor = SunshineOrangePrimary)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("INR (₹)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("Indian Rupee — Standard Application Currency", fontSize = 12.sp, color = SunshineTextSecondary)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section 2: VA Loan Assumption Defaults
            SectionHeader(title = "VA Calculation Assumptions", icon = Icons.Default.Tune)

            Card(
                colors = CardDefaults.cardColors(containerColor = SunshineWhite),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Customize the baseline assumption rates used in loan calculation estimates:",
                        fontSize = 12.sp,
                        color = SunshineTextSecondary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Funding Fee %
                    Text(
                        text = "Assumed Funding Fee Rate (%)",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = SunshineTextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = fundingFeeInput,
                        onValueChange = { input ->
                            fundingFeeInput = input
                            input.toDoubleOrNull()?.let { viewModel.preferences.setFundingFeeRate(it) }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        suffix = { Text("%", fontWeight = FontWeight.Bold, color = SunshineOrangeDark) },
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SunshineOrangePrimary,
                            unfocusedBorderColor = SunshineBorder,
                            focusedContainerColor = SunshineWhite,
                            unfocusedContainerColor = SunshineWhite
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Maximum Guaranty %
                    Text(
                        text = "Assumed Maximum Guaranty Rate (%)",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = SunshineTextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = guarantyInput,
                        onValueChange = { input ->
                            guarantyInput = input
                            input.toDoubleOrNull()?.let { viewModel.preferences.setGuarantyRate(it) }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        suffix = { Text("%", fontWeight = FontWeight.Bold, color = SunshineOrangeDark) },
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SunshineOrangePrimary,
                            unfocusedBorderColor = SunshineBorder,
                            focusedContainerColor = SunshineWhite,
                            unfocusedContainerColor = SunshineWhite
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedButton(
                        onClick = {
                            viewModel.preferences.resetToDefaults()
                            fundingFeeInput = "2.15"
                            guarantyInput = "25.0"
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SunshineOrangeDark),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Reset to VA Defaults (2.15% & 25.0%)", fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section 3: Information & Account
            SectionHeader(title = "App Information", icon = Icons.Default.Info)

            Card(
                colors = CardDefaults.cardColors(containerColor = SunshineWhite),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    // About Us row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToAbout() }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = SunshineOrangeDark, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("About Us", fontWeight = FontWeight.Medium, fontSize = 15.sp)
                        }
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = SunshineTextHint, modifier = Modifier.size(14.dp))
                    }

                    HorizontalDivider(color = SunshineBorder, thickness = 0.5.dp)

                    // Privacy Policy row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showPrivacyDialog = true }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Policy, contentDescription = null, tint = SunshineOrangeDark, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Privacy Policy", fontWeight = FontWeight.Medium, fontSize = 15.sp)
                        }
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = SunshineTextHint, modifier = Modifier.size(14.dp))
                    }

                    HorizontalDivider(color = SunshineBorder, thickness = 0.5.dp)

                    // Version Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("App Version", fontWeight = FontWeight.Medium, fontSize = 15.sp)
                        Text("1.0.0 (Build Debug)", fontSize = 13.sp, color = SunshineTextSecondary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Logout Button
            Button(
                onClick = { showLogoutDialog = true },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFEBEE),
                    contentColor = SunshineError
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("logout_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null,
                    tint = SunshineError,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Logout",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = SunshineError
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SectionHeader(title: String, icon: ImageVector) {
    Row(
        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = SunshineOrangeDark,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = SunshineTextSecondary
        )
    }
}
