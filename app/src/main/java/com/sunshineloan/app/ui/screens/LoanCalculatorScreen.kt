package com.sunshineloan.app.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sunshineloan.app.ui.MainViewModel
import com.sunshineloan.app.ui.SaveState
import com.sunshineloan.app.ui.theme.SunshineBackground
import com.sunshineloan.app.ui.theme.SunshineBorder
import com.sunshineloan.app.ui.theme.SunshineOrangeContainer
import com.sunshineloan.app.ui.theme.SunshineOrangeDark
import com.sunshineloan.app.ui.theme.SunshineOrangePrimary
import com.sunshineloan.app.ui.theme.SunshineOrangeSurface
import com.sunshineloan.app.ui.theme.SunshineTextHint
import com.sunshineloan.app.ui.theme.SunshineTextPrimary
import com.sunshineloan.app.ui.theme.SunshineTextSecondary
import com.sunshineloan.app.ui.theme.SunshineWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanCalculatorScreen(
    viewModel: MainViewModel,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val loanAmount by viewModel.loanAmountInput.collectAsState()
    val interestRate by viewModel.interestRateInput.collectAsState()
    val loanTermYears by viewModel.loanTermYearsInput.collectAsState()
    val calculation by viewModel.calculationResult.collectAsState()
    val currencyCode by viewModel.preferences.currencyCode.collectAsState()
    val saveState by viewModel.saveState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(saveState) {
        when (saveState) {
            is SaveState.Success -> {
                snackbarHostState.showSnackbar((saveState as SaveState.Success).message)
                viewModel.resetSaveState()
            }
            is SaveState.Error -> {
                snackbarHostState.showSnackbar((saveState as SaveState.Error).error)
                viewModel.resetSaveState()
            }
            else -> {}
        }
    }

    val currencyPrefix = "₹"

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Sunshine Loan: VA Loan Calc",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = SunshineWhite
                    )
                },
                actions = {
                    IconButton(
                        onClick = onNavigateToHistory,
                        modifier = Modifier.testTag("nav_history_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Historical Records",
                            tint = SunshineWhite
                        )
                    }
                    IconButton(
                        onClick = onNavigateToSettings,
                        modifier = Modifier.testTag("nav_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Specification Card
            Card(
                colors = CardDefaults.cardColors(containerColor = SunshineWhite),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("specification_card")
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp, 22.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(SunshineOrangePrimary)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "VA Loan Specification",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = SunshineTextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Field 1: Loan Amount
                    Text(
                        text = "Loan Amount ($currencyPrefix)",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = SunshineTextSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = loanAmount,
                        onValueChange = { viewModel.onLoanAmountChanged(it) },
                        placeholder = { Text("e.g. 120000", color = SunshineTextHint) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        prefix = {
                            Text(
                                text = "$currencyPrefix ",
                                fontWeight = FontWeight.Bold,
                                color = SunshineOrangeDark
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
                            .testTag("loan_amount_input")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Field 2: Interest Rate
                    Text(
                        text = "Interest Rate (% per year)",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = SunshineTextSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = interestRate,
                        onValueChange = { viewModel.onInterestRateChanged(it) },
                        placeholder = { Text("e.g. 4.5", color = SunshineTextHint) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        suffix = {
                            Text(
                                text = "%",
                                fontWeight = FontWeight.Bold,
                                color = SunshineOrangeDark
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
                            .testTag("interest_rate_input")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Field 3: Loan Term
                    Text(
                        text = "Loan Term (years)",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = SunshineTextSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = loanTermYears,
                        onValueChange = { viewModel.onLoanTermChanged(it) },
                        placeholder = { Text("e.g. 20", color = SunshineTextHint) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        suffix = {
                            Text(
                                text = "years",
                                fontWeight = FontWeight.SemiBold,
                                color = SunshineTextSecondary
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
                            .testTag("loan_term_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quick Chips for Common Terms
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(15, 20, 25, 30).forEach { years ->
                            val isSelected = loanTermYears == years.toString()
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) SunshineOrangePrimary else SunshineBackground,
                                border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, SunshineBorder),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.setPredefinedTerm(years) }
                            ) {
                                Text(
                                    text = "$years Y",
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) SunshineWhite else SunshineTextSecondary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Results Section Card
            Card(
                colors = CardDefaults.cardColors(containerColor = SunshineWhite),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("results_section_card")
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Calculation Results",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = SunshineTextPrimary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Net Monthly Payment Hero Highlight
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SunshineOrangeContainer),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Net Monthly Payment",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = SunshineOrangeDark
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = viewModel.calculator.formatCurrency(
                                    calculation.netMonthlyPayment,
                                    currencyCode
                                ),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = SunshineOrangeDark
                            )
                            Text(
                                text = "Principal + Interest based on ${calculation.loanTermYears} years at ${calculation.annualInterestRate}%",
                                fontSize = 11.sp,
                                color = SunshineTextSecondary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Breakdown List
                    ResultRowItem(
                        label = "Loan Amount",
                        value = viewModel.calculator.formatCurrency(calculation.loanAmount, currencyCode)
                    )

                    HorizontalDivider(color = SunshineBorder, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 10.dp))

                    ResultRowItem(
                        label = "Funding Fee (${calculation.fundingFeeRate}% est.)",
                        value = viewModel.calculator.formatCurrency(calculation.fundingFeeAmount, currencyCode),
                        subtitle = "Assumed standard first-use VA rate (configurable in Settings)"
                    )

                    HorizontalDivider(color = SunshineBorder, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 10.dp))

                    ResultRowItem(
                        label = "Maximum Guaranty (${calculation.guarantyRate}% est.)",
                        value = viewModel.calculator.formatCurrency(calculation.maxGuarantyAmount, currencyCode),
                        subtitle = "VA program guaranty assumption (configurable in Settings)"
                    )

                    HorizontalDivider(color = SunshineBorder, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 10.dp))

                    ResultRowItem(
                        label = "Net Monthly Payment",
                        value = viewModel.calculator.formatCurrency(calculation.netMonthlyPayment, currencyCode),
                        highlight = true
                    )

                    HorizontalDivider(color = SunshineBorder, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 10.dp))

                    ResultRowItem(
                        label = "Net Loan Cost (Total Interest)",
                        value = viewModel.calculator.formatCurrency(calculation.netLoanCost, currencyCode)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Large Orange SAVE Button
            Button(
                onClick = { viewModel.saveCurrentCalculation() },
                enabled = calculation.loanAmount > 0.0 && saveState !is SaveState.Saving,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SunshineOrangePrimary,
                    disabledContainerColor = SunshineOrangeContainer
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("save_calculation_button")
            ) {
                if (saveState is SaveState.Saving) {
                    CircularProgressIndicator(
                        color = SunshineWhite,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.BookmarkBorder,
                        contentDescription = null,
                        tint = SunshineWhite
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SAVE CALCULATION",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = SunshineWhite
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Informational Disclaimer note
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = SunshineTextHint,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Estimates only. Actual VA rates & fees vary based on entitlement and lender terms.",
                    fontSize = 11.sp,
                    color = SunshineTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ResultRowItem(
    label: String,
    value: String,
    subtitle: String? = null,
    highlight: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 14.sp,
                color = if (highlight) SunshineOrangeDark else SunshineTextPrimary,
                fontWeight = if (highlight) FontWeight.Bold else FontWeight.Medium
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = SunshineTextHint,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = if (highlight) SunshineOrangeDark else SunshineTextPrimary
        )
    }
}
