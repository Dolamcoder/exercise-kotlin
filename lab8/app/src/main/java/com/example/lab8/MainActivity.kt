package com.example.lab8

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lab8.auth.AuthViewModel
import com.example.lab8.ui.screen.HomeScreen
import com.example.lab8.ui.screen.SignInScreen
import com.example.lab8.ui.screen.SignUpScreen
import com.example.lab8.ui.theme.Lab8Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab8Theme {
                val authViewModel: AuthViewModel = viewModel()
                AppNavigation(authViewModel = authViewModel)
            }
        }
    }
}

private object AppRoute {
    const val SignIn = "sign_in"
    const val SignUp = "sign_up"
    const val Home = "home"
}

@Composable
fun AppNavigation(authViewModel: AuthViewModel) {
    val navController = rememberNavController()
    val uiState = authViewModel.uiState

    NavHost(
        navController = navController,
        startDestination = if (uiState.isAuthenticated) AppRoute.Home else AppRoute.SignIn
    ) {
        composable(AppRoute.SignIn) {
            LaunchedEffect(uiState.isAuthenticated) {
                if (uiState.isAuthenticated) {
                    navController.navigateSingleTopTo(AppRoute.Home)
                }
            }

            SignInScreen(
                uiState = uiState,
                onEmailChanged = authViewModel::onEmailChange,
                onPasswordChanged = authViewModel::onPasswordChange,
                onSignInClick = authViewModel::signIn,
                onNavigateToSignUp = { navController.navigateSingleTopTo(AppRoute.SignUp) }
            )
        }

        composable(AppRoute.SignUp) {
            LaunchedEffect(uiState.isAuthenticated) {
                if (uiState.isAuthenticated) {
                    navController.navigateSingleTopTo(AppRoute.Home)
                }
            }

            SignUpScreen(
                uiState = uiState,
                onEmailChanged = authViewModel::onEmailChange,
                onPasswordChanged = authViewModel::onPasswordChange,
                onConfirmPasswordChanged = authViewModel::onConfirmPasswordChange,
                onSignUpClick = authViewModel::signUp,
                onNavigateToSignIn = { navController.navigateSingleTopTo(AppRoute.SignIn) }
            )
        }

        composable(AppRoute.Home) {
            LaunchedEffect(uiState.isAuthenticated) {
                if (!uiState.isAuthenticated) {
                    navController.navigateSingleTopTo(AppRoute.SignIn)
                }
            }

            HomeScreen(onSignOutClick = authViewModel::signOut)
        }
    }
}

private fun NavHostController.navigateSingleTopTo(route: String) {
    navigate(route) {
        popUpTo(graph.startDestinationId) { inclusive = true }
        launchSingleTop = true
    }
}