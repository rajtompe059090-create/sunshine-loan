package com.sunshineloan.app.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sunshineloan.app.model.LoanRecord
import com.sunshineloan.app.ui.MainViewModel
import com.sunshineloan.app.ui.theme.SunshineBackground
import com.sunshineloan.app.ui.theme.SunshineBorder
import com.sunshineloan.app.ui.theme.SunshineError
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
fun HistoryScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit,
    onSelectRecordToLoad: () -> Unit
) {
    val records by viewModel.historyRecords.collectAsState()
    val isLoading by viewModel.isLoadingHistory.collectAsState()
    val currencyCode by viewModel.preferences.currencyCode.collectAsState()

    var recordToDelete by remember { mutableStateOf<LoanRecord?>(null) }

    // Delete Confirmation Dialog
    if (recordToDelete != null) {
        AlertDialog(
            onDismissRequest = { recordToDelete = null },
            title = { Text("Delete Loan Record?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to remove this calculation record? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        recordToDelete?.id?.let { viewModel.deleteRecord(it) }
                        recordToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SunshineError)
                ) {
                    Text("Delete", color = SunshineWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { recordToDelete = null }) {
                    Text("Cancel", color = SunshineTextSecondary)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Historical Records",
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
                actions = {
                    IconButton(
                        onClick = { viewModel.loadHistory() },
                        modifier = Modifier.testTag("refresh_history_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                isLoading && records.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = SunshineOrangePrimary)
                    }
                }
                records.isEmpty() -> {
                    // Empty State
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .clip(CircleShape)
                                .background(SunshineOrangeContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                tint = SunshineOrangeDark,
                                modifier = Modifier.size(44.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = "No Saved Calculations Yet",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = SunshineTextPrimary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Calculate a loan in the VA Loan Calculator and tap 'SAVE CALCULATION' to track it here.",
                            fontSize = 14.sp,
                            color = SunshineTextSecondary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = onNavigateBack,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SunshineOrangePrimary)
                        ) {
                            Icon(imageVector = Icons.Default.Calculate, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Go to Calculator", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(records, key = { it.id }) { record ->
                            HistoryRecordCard(
                                record = record,
                                currencyCode = currencyCode,
                                onOpen = {
                                    viewModel.loadCalculationIntoForm(record)
                                    onSelectRecordToLoad()
                                },
                                onDelete = { recordToDelete = record },
                                formatCurrency = { amount ->
                                    viewModel.calculator.formatCurrency(amount, record.currencyCode.ifBlank { currencyCode })
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryRecordCard(
    record: LoanRecord,
    currencyCode: String,
    onOpen: () -> Unit,
    onDelete: () -> Unit,
    formatCurrency: (Double) -> String
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SunshineWhite),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("history_record_${record.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Loan Amount & Term Badges + Delete Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = formatCurrency(record.loanAmount),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = SunshineOrangeDark
                    )
                    Text(
                        text = record.formattedDate.ifBlank { "Saved calculation" },
                        fontSize = 11.sp,
                        color = SunshineTextHint
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = SunshineOrangeContainer
                    ) {
                        Text(
                            text = "${record.loanTermYears} Yrs @ ${record.interestRate}%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SunshineOrangeDark,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete record",
                            tint = SunshineError,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            HorizontalDivider(
                color = SunshineBorder,
                thickness = 0.5.dp,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            // Key Metrics 2-column layout
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Net Monthly Payment",
                        fontSize = 12.sp,
                        color = SunshineTextSecondary
                    )
                    Text(
                        text = formatCurrency(record.netMonthlyPayment),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = SunshineTextPrimary
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Net Loan Cost",
                        fontSize = 12.sp,
                        color = SunshineTextSecondary
                    )
                    Text(
                        text = formatCurrency(record.netLoanCost),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SunshineTextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Funding Fee",
                        fontSize = 12.sp,
                        color = SunshineTextSecondary
                    )
                    Text(
                        text = formatCurrency(record.fundingFeeAmount),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = SunshineTextPrimary
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Max Guaranty",
                        fontSize = 12.sp,
                        color = SunshineTextSecondary
                    )
                    Text(
                        text = formatCurrency(record.maxGuarantyAmount),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = SunshineTextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action: Load into Calculator
            OutlinedButton(
                onClick = onOpen,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = SunshineOrangeDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Calculate,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Load into Calculator",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
