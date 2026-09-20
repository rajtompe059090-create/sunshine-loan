package com.sunshineloan.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sunshineloan.app.ui.MainViewModel
import com.sunshineloan.app.ui.screens.AboutScreen
import com.sunshineloan.app.ui.screens.ApplyLoanScreen
import com.sunshineloan.app.ui.screens.HistoryScreen
import com.sunshineloan.app.ui.screens.HomeScreen
import com.sunshineloan.app.ui.screens.LoanCalculatorScreen
import com.sunshineloan.app.ui.screens.LoanStatusScreen
import com.sunshineloan.app.ui.screens.LoginScreen
import com.sunshineloan.app.ui.screens.MyApplicationsScreen
import com.sunshineloan.app.ui.screens.ProfileScreen
import com.sunshineloan.app.ui.screens.SettingsScreen
import com.sunshineloan.app.ui.screens.SupportScreen
import com.sunshineloan.app.ui.theme.SunshineBackground
import com.sunshineloan.app.ui.theme.SunshineLoanTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SunshineLoanTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = SunshineBackground
                ) {
                    SunshineAppNavHost(viewModel = viewModel)
                }
            }
        }
    }
}

object Routes {
    const val LOGIN = "login"
    const val HOME = "home"
    const val APPLY_LOAN = "apply_loan"
    const val CALCULATOR = "calculator"
    const val MY_APPLICATIONS = "my_applications"
    const val LOAN_STATUS = "loan_status"
    const val PROFILE = "profile"
    const val SUPPORT = "support"
    const val HISTORY = "history"
    const val SETTINGS = "settings"
    const val ABOUT = "about"
}

@Composable
fun SunshineAppNavHost(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val isInitiallyLoggedIn = viewModel.authRepo.isUserLoggedIn()
    val startDestination = if (isInitiallyLoggedIn) Routes.HOME else Routes.LOGIN

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToApplyLoan = {
                    navController.navigate(Routes.APPLY_LOAN)
                },
                onNavigateToCalculator = {
                    navController.navigate(Routes.CALCULATOR)
                },
                onNavigateToMyApplications = {
                    navController.navigate(Routes.MY_APPLICATIONS)
                },
                onNavigateToLoanStatus = {
                    navController.navigate(Routes.LOAN_STATUS)
                },
                onNavigateToProfile = {
                    navController.navigate(Routes.PROFILE)
                },
                onNavigateToSupport = {
                    navController.navigate(Routes.SUPPORT)
                },
                onNavigateToSettings = {
                    navController.navigate(Routes.SETTINGS)
                }
            )
        }

        composable(Routes.APPLY_LOAN) {
            ApplyLoanScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = false }
                    }
                },
                onNavigateToStatus = {
                    navController.navigate(Routes.LOAN_STATUS)
                }
            )
        }

        composable(Routes.MY_APPLICATIONS) {
            MyApplicationsScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToApplyLoan = {
                    navController.navigate(Routes.APPLY_LOAN)
                },
                onSelectApplication = { _ ->
                    navController.navigate(Routes.LOAN_STATUS)
                }
            )
        }

        composable(Routes.LOAN_STATUS) {
            LoanStatusScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToApplyLoan = {
                    navController.navigate(Routes.APPLY_LOAN)
                }
            )
        }

        composable(Routes.PROFILE) {
            ProfileScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.SUPPORT) {
            SupportScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.CALCULATOR) {
            LoanCalculatorScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToHistory = {
                    viewModel.loadHistory()
                    navController.navigate(Routes.HISTORY)
                },
                onNavigateToSettings = {
                    navController.navigate(Routes.SETTINGS)
                }
            )
        }

        composable(Routes.HISTORY) {
            HistoryScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSelectRecordToLoad = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToAbout = {
                    navController.navigate(Routes.ABOUT)
                },
                onLogoutConfirmed = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.ABOUT) {
            AboutScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
