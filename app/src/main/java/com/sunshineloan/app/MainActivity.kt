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
import com.sunshineloan.app.ui.screens.HistoryScreen
import com.sunshineloan.app.ui.screens.LoanCalculatorScreen
import com.sunshineloan.app.ui.screens.LoginScreen
import com.sunshineloan.app.ui.screens.SettingsScreen
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
    const val CALCULATOR = "calculator"
    const val HISTORY = "history"
    const val SETTINGS = "settings"
    const val ABOUT = "about"
}

@Composable
fun SunshineAppNavHost(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val isInitiallyLoggedIn = viewModel.authRepo.isUserLoggedIn()
    val startDestination = if (isInitiallyLoggedIn) Routes.CALCULATOR else Routes.LOGIN

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {
                    navController.navigate(Routes.CALCULATOR) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.CALCULATOR) {
            LoanCalculatorScreen(
                viewModel = viewModel,
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
