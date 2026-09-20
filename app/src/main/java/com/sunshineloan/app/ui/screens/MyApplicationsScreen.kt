package com.sunshineloan.app.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sunshineloan.app.model.LoanApplication
import com.sunshineloan.app.model.LoanStatus
import com.sunshineloan.app.ui.MainViewModel
import com.sunshineloan.app.ui.theme.SunshineBackground
import com.sunshineloan.app.ui.theme.SunshineOrangeContainer
import com.sunshineloan.app.ui.theme.SunshineOrangeDark
import com.sunshineloan.app.ui.theme.SunshineOrangePrimary
import com.sunshineloan.app.ui.theme.SunshineTextHint
import com.sunshineloan.app.ui.theme.SunshineTextPrimary
import com.sunshineloan.app.ui.theme.SunshineTextSecondary
import com.sunshineloan.app.ui.theme.SunshineWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApplicationsScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToApplyLoan: () -> Unit,
    onSelectApplication: (LoanApplication) -> Unit
) {
    val applications by viewModel.userApplications.collectAsState()
    var selectedFilter by remember { mutableStateOf<LoanStatus?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val filtered = applications.filter { app ->
        (selectedFilter == null || app.status == selectedFilter) &&
                (searchQuery.isBlank() ||
                        app.applicationId.contains(searchQuery, ignoreCase = true) ||
                        app.loanPurpose.contains(searchQuery, ignoreCase = true))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Applications",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SunshineTextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("my_apps_back_btn")) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = SunshineTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SunshineWhite)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.startNewApplication()
                    onNavigateToApplyLoan()
                },
                containerColor = SunshineOrangePrimary,
                contentColor = SunshineWhite,
                modifier = Modifier.testTag("my_apps_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Apply for New Loan")
            }
        },
        containerColor = SunshineBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by Application ID or Purpose") },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = SunshineTextHint) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("my_apps_search_input"),
                singleLine = true
            )

            // Status Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == null,
                    onClick = { selectedFilter = null },
                    label = { Text("All (${applications.size})") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SunshineOrangeContainer,
                        selectedLabelColor = SunshineOrangeDark
                    )
                )
                FilterChip(
                    selected = selectedFilter == LoanStatus.UNDER_REVIEW,
                    onClick = { selectedFilter = if (selectedFilter == LoanStatus.UNDER_REVIEW) null else LoanStatus.UNDER_REVIEW },
                    label = { Text("Under Review") }
                )
                FilterChip(
                    selected = selectedFilter == LoanStatus.APPROVED,
                    onClick = { selectedFilter = if (selectedFilter == LoanStatus.APPROVED) null else LoanStatus.APPROVED },
                    label = { Text("Approved") }
                )
            }

            if (filtered.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Assignment,
                            contentDescription = null,
                            tint = SunshineTextHint,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (searchQuery.isNotBlank()) "No applications found matching search." else "No loan applications yet",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = SunshineTextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Apply now to get quick personal or business loans.",
                            fontSize = 13.sp,
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
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filtered, key = { it.applicationId }) { app ->
                        ApplicationListItemCard(
                            app = app,
                            viewModel = viewModel,
                            onClick = {
                                viewModel.selectActiveApplication(app)
                                onSelectApplication(app)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ApplicationListItemCard(
    app: LoanApplication,
    viewModel: MainViewModel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("app_card_${app.applicationId}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SunshineWhite),
        border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = app.applicationId,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = SunshineTextPrimary
                )
                StatusBadge(status = app.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Amount: ₹${viewModel.calculator.formatCurrency(app.loanAmount).replace("₹", "")}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = SunshineOrangeDark
                    )
                    Text(
                        text = "${app.loanPurpose} • ${app.tenureMonths} Months",
                        fontSize = 12.sp,
                        color = SunshineTextSecondary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Track",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = SunshineOrangeDark
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null,
                        tint = SunshineOrangeDark,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            if (app.formattedDate.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Applied on: ${app.formattedDate}",
                    fontSize = 11.sp,
                    color = SunshineTextHint
                )
            }
        }
    }
}
