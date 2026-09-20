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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sunshineloan.app.model.LoanApplication
import com.sunshineloan.app.model.LoanStatus
import com.sunshineloan.app.ui.MainViewModel
import com.sunshineloan.app.ui.theme.SunshineBackground
import com.sunshineloan.app.ui.theme.SunshineBorder
import com.sunshineloan.app.ui.theme.SunshineBorderLight
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
fun LoanStatusScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToApplyLoan: () -> Unit
) {
    val activeApp by viewModel.activeApplication.collectAsState()
    val allApplications by viewModel.userApplications.collectAsState()
    val isAdmin by viewModel.isAdmin.collectAsState()

    var searchId by remember { mutableStateOf("") }
    var displayedApp by remember(activeApp, allApplications) {
        mutableStateOf(activeApp ?: allApplications.firstOrNull())
    }

    var showAdminDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Loan Status",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SunshineTextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("loan_status_back_btn")) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = SunshineTextPrimary)
                    }
                },
                actions = {
                    if (isAdmin) {
                        IconButton(onClick = { showAdminDialog = true }) {
                            Icon(imageVector = Icons.Default.AdminPanelSettings, contentDescription = "Admin Update Status", tint = SunshineOrangeDark)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SunshineWhite)
            )
        },
        containerColor = SunshineBackground
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Search or select application
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchId,
                        onValueChange = { searchId = it },
                        placeholder = { Text("Search by Application ID") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("loan_status_search_input"),
                        singleLine = true
                    )
                    Button(
                        onClick = {
                            val found = allApplications.firstOrNull { it.applicationId.contains(searchId.trim(), ignoreCase = true) }
                            if (found != null) {
                                displayedApp = found
                                viewModel.selectActiveApplication(found)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SunshineOrangePrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("loan_status_search_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                    }
                }
            }

            if (displayedApp == null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SunshineWhite)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No Active Application Found",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = SunshineTextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Start a 9-step application or enter an existing Application ID above.",
                                fontSize = 12.sp,
                                color = SunshineTextSecondary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    viewModel.startNewApplication()
                                    onNavigateToApplyLoan()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SunshineOrangePrimary),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Apply for Loan", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                val app = displayedApp!!

                // Status Overview Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SunshineWhite),
                        border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Application #${app.applicationId}",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SunshineTextPrimary
                                    )
                                    Text(
                                        text = "Applied for ${app.loanPurpose}",
                                        fontSize = 12.sp,
                                        color = SunshineTextSecondary
                                    )
                                }
                                StatusBadge(status = app.status)
                            }

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = SunshineOrangeContainer.copy(alpha = 0.5f))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = app.status.label,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SunshineOrangeDark
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = app.statusMessage.ifBlank { app.status.description },
                                        fontSize = 12.sp,
                                        color = SunshineTextPrimary
                                    )
                                    if (app.adminRemarks.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Note: ${app.adminRemarks}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = SunshineOrangeDark
                                        )
                                    }
                                }
                            }

                            HorizontalDivider(color = SunshineBorderLight)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(text = "Loan Amount", fontSize = 11.sp, color = SunshineTextSecondary)
                                    Text(
                                        text = "₹${viewModel.calculator.formatCurrency(app.loanAmount).replace("₹", "")}",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SunshineTextPrimary
                                    )
                                }
                                Column {
                                    Text(text = "Monthly EMI", fontSize = 11.sp, color = SunshineTextSecondary)
                                    Text(
                                        text = "₹${viewModel.calculator.formatCurrency(app.monthlyEmi).replace("₹", "")}",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SunshineOrangeDark
                                    )
                                }
                                Column {
                                    Text(text = "Tenure", fontSize = 11.sp, color = SunshineTextSecondary)
                                    Text(
                                        text = "${app.tenureMonths} Months",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SunshineTextPrimary
                                    )
                                }
                            }
                        }
                    }
                }

                // Application Timeline
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SunshineWhite),
                        border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Text(
                                text = "Verification & Approval Timeline",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = SunshineTextPrimary
                            )

                            val steps = listOf(
                                Triple("Application Submitted", "Logged digitally with underwriting", app.createdAt > 0),
                                Triple("KYC & Document Verification", "Verifying Aadhaar & PAN details", app.status != LoanStatus.SUBMITTED),
                                Triple("Credit Assessment", "Evaluating credit bureau score & obligations", app.status == LoanStatus.UNDER_REVIEW || app.status == LoanStatus.APPROVED || app.status == LoanStatus.DISBURSED),
                                Triple("Sanction Approval", "Loan sanctioned and agreement ready", app.status == LoanStatus.APPROVED || app.status == LoanStatus.DISBURSED),
                                Triple("Bank Disbursal", "Transferred via NEFT/IMPS to verified bank", app.status == LoanStatus.DISBURSED)
                            )

                            steps.forEachIndexed { idx, (title, desc, isDone) ->
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
                                            text = title,
                                            fontSize = 13.sp,
                                            fontWeight = if (isDone) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isDone) SunshineTextPrimary else SunshineTextSecondary
                                        )
                                        Text(text = desc, fontSize = 11.sp, color = SunshineTextHint)
                                    }
                                }
                            }
                        }
                    }
                }

                // Important Advisory Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = SunshineOrangeContainer.copy(alpha = 0.4f)),
                        border = CardDefaults.outlinedCardBorder().copy(width = 0.5.dp)
                    ) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
                            Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = SunshineOrangeDark, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Transparent Lending Advisory",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SunshineOrangeDark
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Sunshine Loan processes applications purely on credit merit and verified documentation. We never demand cash bribes or guarantee approvals without credit check.",
                                    fontSize = 11.sp,
                                    color = SunshineTextSecondary,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Admin Status Update Dialog
    if (showAdminDialog && displayedApp != null) {
        AdminStatusUpdateDialog(
            app = displayedApp!!,
            onDismiss = { showAdminDialog = false },
            onUpdate = { newStatus, remarks ->
                viewModel.updateApplicationStatusByAdmin(
                    applicationId = displayedApp!!.applicationId,
                    userId = displayedApp!!.userId,
                    newStatus = newStatus,
                    remarks = remarks
                )
                showAdminDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminStatusUpdateDialog(
    app: LoanApplication,
    onDismiss: () -> Unit,
    onUpdate: (LoanStatus, String) -> Unit
) {
    var selectedStatus by remember { mutableStateOf(app.status) }
    var remarks by remember { mutableStateOf(app.adminRemarks) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Admin: Update Application Status", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "App ID: ${app.applicationId}",
                    fontSize = 12.sp,
                    color = SunshineTextSecondary
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedStatus.label,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select Status") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        LoanStatus.entries.forEach { status ->
                            DropdownMenuItem(
                                text = { Text(status.label) },
                                onClick = {
                                    selectedStatus = status
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = remarks,
                    onValueChange = { remarks = it },
                    label = { Text("Admin Remarks / Reason") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onUpdate(selectedStatus, remarks) },
                colors = ButtonDefaults.buttonColors(containerColor = SunshineOrangePrimary)
            ) {
                Text("Save Status")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
