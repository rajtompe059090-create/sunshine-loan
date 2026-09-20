package com.sunshineloan.app.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sunshineloan.app.ui.MainViewModel
import com.sunshineloan.app.ui.theme.SunshineBackground
import com.sunshineloan.app.ui.theme.SunshineBorderLight
import com.sunshineloan.app.ui.theme.SunshineOrangeContainer
import com.sunshineloan.app.ui.theme.SunshineOrangeDark
import com.sunshineloan.app.ui.theme.SunshineOrangePrimary
import com.sunshineloan.app.ui.theme.SunshineTextHint
import com.sunshineloan.app.ui.theme.SunshineTextPrimary
import com.sunshineloan.app.ui.theme.SunshineTextSecondary
import com.sunshineloan.app.ui.theme.SunshineWhite

data class FaqItem(val question: String, val answer: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current

    val faqs = listOf(
        FaqItem(
            question = "What are the eligibility criteria for Sunshine Loan?",
            answer = "Any Indian citizen aged between 21 and 58 years with a minimum monthly household or individual income of ₹10,000 and valid Aadhaar & PAN can apply."
        ),
        FaqItem(
            question = "How long does loan verification and disbursal take?",
            answer = "Once your 9-step application and KYC documents are submitted, verification usually completes within 4 to 24 business hours. Disbursal is processed directly to your bank account via IMPS/NEFT."
        ),
        FaqItem(
            question = "Does paying the document verification fee guarantee approval?",
            answer = "No. In strict accordance with RBI Digital Lending guidelines, nominal verification fees cover administrative document verification and never guarantee loan sanction. Approval is determined solely by credit underwriting and verified eligibility."
        ),
        FaqItem(
            question = "Can I prepay or foreclose my loan early?",
            answer = "Yes, you can prepay or foreclose your loan at any point after payment of your first EMI without any hidden penalty charges."
        ),
        FaqItem(
            question = "How is my personal and KYC data protected?",
            answer = "We use 256-bit bank-grade encryption and secure Google Firebase servers. Your data is strictly used for loan assessment and is never sold to unauthorized third parties."
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Help & Support",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SunshineTextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("support_back_btn")) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = SunshineTextPrimary)
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
            // Customer Care Helpline & Email Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SunshineWhite),
                    border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text(
                            text = "Sunshine Loan Customer Care",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = SunshineTextPrimary
                        )
                        Text(
                            text = "Reach out to our customer care team for queries regarding applications, calculations, or disbursals.",
                            fontSize = 12.sp,
                            color = SunshineTextSecondary
                        )

                        ContactRow(
                            icon = Icons.Default.Phone,
                            title = "Toll-Free Helpline",
                            value = "1800 209 8899 / +91 80 4718 2000",
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:18002098899"))
                                context.startActivity(intent)
                            }
                        )

                        HorizontalDivider(color = SunshineBorderLight)

                        ContactRow(
                            icon = Icons.Default.Email,
                            title = "Official Support Email",
                            value = "support@sunshineloan.app",
                            onClick = {
                                val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:support@sunshineloan.app"))
                                context.startActivity(intent)
                            }
                        )

                        HorizontalDivider(color = SunshineBorderLight)

                        ContactRow(
                            icon = Icons.Default.Schedule,
                            title = "Operating Hours",
                            value = "Monday - Saturday, 9:30 AM to 6:30 PM IST"
                        )
                    }
                }
            }

            // Grievance Redressal Officer (Mandatory RBI Compliance)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SunshineWhite),
                    border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = SunshineOrangeDark, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Grievance Redressal Officer",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = SunshineTextPrimary
                            )
                        }

                        Text(
                            text = "As mandated by Reserve Bank of India (RBI) Digital Lending Directives, borrowers may escalate any unresolved disputes to our designated Nodal Grievance Officer:",
                            fontSize = 12.sp,
                            color = SunshineTextSecondary
                        )

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = SunshineOrangeContainer.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(text = "Officer: Rajesh Sharma (Chief Grievance Officer)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SunshineTextPrimary)
                                Text(text = "Email: grievance@sunshineloan.app", fontSize = 12.sp, color = SunshineOrangeDark, fontWeight = FontWeight.SemiBold)
                                Text(text = "Office: Sunshine Financial Services, Level 4, BKC, Bandra East, Mumbai - 400051", fontSize = 11.sp, color = SunshineTextSecondary)
                            }
                        }
                    }
                }
            }

            // FAQs Section
            item {
                Text(
                    text = "Frequently Asked Questions",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = SunshineTextPrimary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            items(faqs) { faq ->
                FaqAccordionCard(faq = faq)
            }

            // Anti-Fraud Advisory
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                    border = CardDefaults.outlinedCardBorder().copy(width = 0.5.dp, brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFEF9A9A)))
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
                        Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = Color(0xFFC62828), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Anti-Fraud Security Advisory",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB71C1C)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Sunshine Loan representatives will NEVER ask for your bank OTP, PIN, password, or direct cash transfers to personal accounts. Stay alert against cyber impostors.",
                                fontSize = 11.sp,
                                color = Color(0xFF37474F),
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ContactRow(
    icon: ImageVector,
    title: String,
    value: String,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = SunshineOrangeDark, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = title, fontSize = 11.sp, color = SunshineTextSecondary)
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (onClick != null) SunshineOrangeDark else SunshineTextPrimary
            )
        }
    }
}

@Composable
fun FaqAccordionCard(faq: FaqItem) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SunshineWhite),
        border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = faq.question,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SunshineTextPrimary,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = SunshineOrangeDark
                )
            }
            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = faq.answer,
                        fontSize = 12.sp,
                        color = SunshineTextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}
