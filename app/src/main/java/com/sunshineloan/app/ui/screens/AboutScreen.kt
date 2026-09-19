package com.sunshineloan.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sunshineloan.app.R
import com.sunshineloan.app.ui.theme.SunshineBackground
import com.sunshineloan.app.ui.theme.SunshineBorder
import com.sunshineloan.app.ui.theme.SunshineOrangeContainer
import com.sunshineloan.app.ui.theme.SunshineOrangeDark
import com.sunshineloan.app.ui.theme.SunshineOrangePrimary
import com.sunshineloan.app.ui.theme.SunshineOrangeSurface
import com.sunshineloan.app.ui.theme.SunshineTextPrimary
import com.sunshineloan.app.ui.theme.SunshineTextSecondary
import com.sunshineloan.app.ui.theme.SunshineWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "About Us",
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App Branding Header
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(SunshineOrangeContainer)
                    .border(2.dp, SunshineOrangePrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_sunshine_logo),
                    contentDescription = "Sunshine Loan Logo",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Sunshine Loan: VA Loan Calc",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = SunshineTextPrimary
            )

            Text(
                text = "Version 1.0.0",
                fontSize = 13.sp,
                color = SunshineTextSecondary,
                modifier = Modifier.padding(top = 2.dp, bottom = 20.dp)
            )

            // About App Card
            InfoSectionCard(
                title = "About Sunshine Loan",
                icon = Icons.Default.Info
            ) {
                Text(
                    text = "Sunshine Loan is a specialized mortgage computation tool engineered to assist veterans, active servicemembers, and eligible military families in evaluating VA home loan possibilities. It offers real-time monthly payment amortization calculations, configurable VA funding fee assessments, and guaranty eligibility projections.",
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = SunshineTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Explanation of VA Loan Calculations
            InfoSectionCard(
                title = "How VA Loan Calculations Work",
                icon = Icons.Default.AssignmentTurnedIn
            ) {
                Text(
                    text = "• Amortization Formula:\nCalculates equal monthly principal and interest payments using standard compounding mathematics based on annual interest rate, loan term, and principal balance.\n\n• VA Funding Fee:\nA one-time statutory fee charged by the Department of Veterans Affairs to help sustain the loan program for future generations. For first-time purchase loans with 0% down, the standard fee is 2.15% (subject to military branch, down payment, and prior use).\n\n• Maximum Guaranty:\nThe VA guarantees a portion of the loan (typically up to 25% for standard conforming loan amounts), protecting the private lender in case of default and allowing veterans to obtain favorable terms without private mortgage insurance (PMI).",
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    color = SunshineTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // VA Loan Programs Overview
            InfoSectionCard(
                title = "VA Loan Programs Overview",
                icon = Icons.Default.Home
            ) {
                ProgramItem(
                    title = "1. VA Purchase Loan",
                    description = "Enables eligible borrowers to purchase a primary single-family home, condo, or new build with competitive interest rates and typically no down payment requirement."
                )

                HorizontalDivider(color = SunshineBorder, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 10.dp))

                ProgramItem(
                    title = "2. Interest Rate Reduction Refinance Loan (IRRRL)",
                    description = "Also known as a VA Streamline Refinance, this program simplifies refinancing an existing VA loan to lower interest rates and reduce monthly payments with minimal underwriting and no appraisal."
                )

                HorizontalDivider(color = SunshineBorder, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 10.dp))

                ProgramItem(
                    title = "3. VA Cash-Out Refinance",
                    description = "Allows homeowners to refinance a non-VA or VA loan into a new VA loan while taking cash out from accumulated home equity to pay off debt, fund education, or make home improvements."
                )

                HorizontalDivider(color = SunshineBorder, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 10.dp))

                ProgramItem(
                    title = "4. Native American Direct Loan (NADL)",
                    description = "Direct financing provided by the Department of Veterans Affairs to eligible Native American veterans to finance, build, or renovate homes situated on Federal Trust land."
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mandatory Financial Disclaimer
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFE082)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Financial Disclaimer",
                            tint = SunshineOrangeDark,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Important Financial Disclaimer",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = SunshineOrangeDark
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "This application provides estimates only. It is not a lender and does not guarantee loan approval, rates, eligibility, funding fees, or loan terms. Actual eligibility, payment schedules, and closing requirements must be verified with an approved VA mortgage lender or the Department of Veterans Affairs.",
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = SunshineTextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun InfoSectionCard(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SunshineWhite),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = SunshineOrangePrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = SunshineTextPrimary
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun ProgramItem(
    title: String,
    description: String
) {
    Column {
        Text(
            text = title,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = SunshineTextPrimary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = description,
            fontSize = 12.sp,
            lineHeight = 17.sp,
            color = SunshineTextSecondary
        )
    }
}
