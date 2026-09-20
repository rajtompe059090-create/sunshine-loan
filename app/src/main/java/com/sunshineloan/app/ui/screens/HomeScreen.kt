package com.sunshineloan.app.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sunshineloan.app.R
import com.sunshineloan.app.model.LoanStatus
import com.sunshineloan.app.ui.MainViewModel
import com.sunshineloan.app.ui.theme.SunshineBackground
import com.sunshineloan.app.ui.theme.SunshineBorder
import com.sunshineloan.app.ui.theme.SunshineBorderLight
import com.sunshineloan.app.ui.theme.SunshineOrangeContainer
import com.sunshineloan.app.ui.theme.SunshineOrangeDark
import com.sunshineloan.app.ui.theme.SunshineOrangeLight
import com.sunshineloan.app.ui.theme.SunshineOrangePrimary
import com.sunshineloan.app.ui.theme.SunshineSuccess
import com.sunshineloan.app.ui.theme.SunshineTextHint
import com.sunshineloan.app.ui.theme.SunshineTextPrimary
import com.sunshineloan.app.ui.theme.SunshineTextSecondary
import com.sunshineloan.app.ui.theme.SunshineWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToApplyLoan: () -> Unit,
    onNavigateToCalculator: () -> Unit,
    onNavigateToMyApplications: () -> Unit,
    onNavigateToLoanStatus: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToSupport: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val applications by viewModel.userApplications.collectAsState()
    val activeApp by viewModel.activeApplication.collectAsState()
    val currentUser = viewModel.authRepo.getCurrentUser()

    val displayName = userProfile.fullName.ifBlank { "Valued Customer" }
    val displayPhone = currentUser?.phoneNumber ?: userProfile.phone.ifBlank { "Verified User" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_sunshine_logo),
                            contentDescription = "Sunshine Loan Logo",
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Sunshine Loan",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = SunshineTextPrimary
                            )
                            Text(
                                text = "VA & Indian Personal Loans",
                                fontSize = 11.sp,
                                color = SunshineTextSecondary
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = onNavigateToProfile,
                        modifier = Modifier.testTag("home_profile_icon_btn")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(SunshineOrangeContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile",
                                tint = SunshineOrangeDark,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    IconButton(
                        onClick = onNavigateToSettings,
                        modifier = Modifier.testTag("home_settings_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = SunshineTextSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SunshineWhite
                )
            )
        },
        containerColor = SunshineBackground
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. WELCOME GREETING CARD
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SunshineWhite),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(SunshineBorder, SunshineBorderLight)))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Namaste,",
                                fontSize = 14.sp,
                                color = SunshineTextSecondary
                            )
                            Text(
                                text = displayName,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = SunshineTextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = null,
                                    tint = SunshineOrangeDark,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = displayPhone,
                                    fontSize = 12.sp,
                                    color = SunshineTextSecondary
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(SunshineOrangeContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = displayName.firstOrNull()?.uppercase() ?: "S",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = SunshineOrangeDark
                            )
                        }
                    }
                }
            }

            // 2. PROMINENT APPLY LOAN HERO CARD
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_apply_loan_hero_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        SunshineOrangePrimary,
                                        SunshineOrangeDark
                                    )
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = SunshineWhite.copy(alpha = 0.25f)
                                ) {
                                    Text(
                                        text = "INSTANT PRE-APPROVAL",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SunshineWhite,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                                Text(
                                    text = "Up to ₹10,00,000",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = SunshineWhite
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "Get Instant Funds for Your Dreams",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = SunshineWhite,
                                lineHeight = 28.sp
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Simple 9-step digital application • Lowest interest starting at 10.5% p.a. • Zero paperwork",
                                fontSize = 13.sp,
                                color = SunshineWhite.copy(alpha = 0.92f)
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            Button(
                                onClick = {
                                    viewModel.startNewApplication()
                                    onNavigateToApplyLoan()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("home_apply_loan_btn"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SunshineWhite,
                                    contentColor = SunshineOrangeDark
                                )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "Apply Loan Now",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. ACTIVE APPLICATION TRACKER (if user has active application)
            if (activeApp != null) {
                val app = activeApp!!
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToLoanStatus() }
                            .testTag("home_active_application_card"),
                        shape = RoundedCornerShape(16.dp),
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
                                    text = "ACTIVE APPLICATION",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SunshineTextSecondary
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
                                        text = "ID: ${app.applicationId}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = SunshineTextPrimary
                                    )
                                    Text(
                                        text = "Amount: ₹${viewModel.calculator.formatCurrency(app.loanAmount).replace("₹", "").replace("-", "")}",
                                        fontSize = 13.sp,
                                        color = SunshineOrangeDark,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Track Status",
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
                        }
                    }
                }
            }

            // 4. MAIN HUB DASHBOARD OPTIONS (Grid 2x3)
            item {
                Text(
                    text = "Quick Services",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = SunshineTextPrimary,
                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Row 1: Apply Loan & Loan Calculator
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DashboardActionCard(
                            modifier = Modifier.weight(1f),
                            title = "Apply Loan",
                            subtitle = "9-step quick process",
                            icon = Icons.Default.AttachMoney,
                            iconTint = SunshineOrangeDark,
                            iconBg = SunshineOrangeContainer,
                            onClick = {
                                viewModel.startNewApplication()
                                onNavigateToApplyLoan()
                            },
                            testTag = "home_opt_apply_loan"
                        )

                        DashboardActionCard(
                            modifier = Modifier.weight(1f),
                            title = "Loan Calculator",
                            subtitle = "Calculate EMI & costs",
                            icon = Icons.Default.Calculate,
                            iconTint = Color(0xFF1976D2),
                            iconBg = Color(0xFFE3F2FD),
                            onClick = onNavigateToCalculator,
                            testTag = "home_opt_calculator"
                        )
                    }

                    // Row 2: My Applications & Loan Status
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DashboardActionCard(
                            modifier = Modifier.weight(1f),
                            title = "My Applications",
                            subtitle = "${applications.size} recorded",
                            icon = Icons.Default.AssignmentTurnedIn,
                            iconTint = Color(0xFF388E3C),
                            iconBg = Color(0xFFE8F5E9),
                            onClick = onNavigateToMyApplications,
                            testTag = "home_opt_my_applications"
                        )

                        DashboardActionCard(
                            modifier = Modifier.weight(1f),
                            title = "Loan Status",
                            subtitle = "Track live progress",
                            icon = Icons.Default.CheckCircle,
                            iconTint = Color(0xFF7B1FA2),
                            iconBg = Color(0xFFF3E5F5),
                            onClick = onNavigateToLoanStatus,
                            testTag = "home_opt_loan_status"
                        )
                    }

                    // Row 3: My Profile & Help and Support
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DashboardActionCard(
                            modifier = Modifier.weight(1f),
                            title = "My Profile",
                            subtitle = "KYC & personal info",
                            icon = Icons.Default.Person,
                            iconTint = Color(0xFFE64A19),
                            iconBg = Color(0xFFFBE9E7),
                            onClick = onNavigateToProfile,
                            testTag = "home_opt_profile"
                        )

                        DashboardActionCard(
                            modifier = Modifier.weight(1f),
                            title = "Help & Support",
                            subtitle = "Helpline & Grievance",
                            icon = Icons.Default.Info,
                            iconTint = Color(0xFF00796B),
                            iconBg = Color(0xFFE0F2F1),
                            onClick = onNavigateToSupport,
                            testTag = "home_opt_support"
                        )
                    }
                }
            }

            // 5. SECURITY & REGULATORY TRANSPARENCY NOTICE
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SunshineOrangeContainer.copy(alpha = 0.5f)),
                    border = CardDefaults.outlinedCardBorder().copy(width = 0.5.dp, brush = Brush.linearGradient(listOf(SunshineOrangeLight, SunshineOrangeContainer)))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = SunshineOrangeDark,
                            modifier = Modifier
                                .size(24.dp)
                                .padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "100% Safe & RBI Regulated",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = SunshineTextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Sunshine Loan adheres strictly to Indian Digital Lending guidelines. Never share OTPs. Nominal verification charges never guarantee loan approval.",
                                fontSize = 11.sp,
                                color = SunshineTextSecondary,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun DashboardActionCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        modifier = modifier
            .height(115.dp)
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SunshineWhite),
        border = CardDefaults.outlinedCardBorder().copy(width = 1.dp, brush = Brush.linearGradient(listOf(SunshineBorder, SunshineBorderLight)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = SunshineTextPrimary,
                    maxLines = 1
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = SunshineTextSecondary,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun StatusBadge(status: LoanStatus) {
    val (bgColor, textColor) = when (status) {
        LoanStatus.SUBMITTED -> Color(0xFFE3F2FD) to Color(0xFF1976D2)
        LoanStatus.UNDER_REVIEW -> Color(0xFFFFF3E0) to Color(0xFFE65100)
        LoanStatus.DOCUMENTS_REQUIRED -> Color(0xFFFFEBEE) to Color(0xFFC62828)
        LoanStatus.APPROVED -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
        LoanStatus.REJECTED -> Color(0xFFFFEBEE) to Color(0xFFD32F2F)
        LoanStatus.DISBURSED -> Color(0xFFEDE7F6) to Color(0xFF512DA8)
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = bgColor
    ) {
        Text(
            text = status.label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}
