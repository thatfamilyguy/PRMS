package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.data.model.User
import com.example.data.model.UserRole
import com.example.ui.admin.AdminDashboardScreen
import com.example.ui.admin.AdminDashboardViewModel
import com.example.ui.auth.AuthViewModel
import com.example.ui.auth.LoginScreen
import com.example.ui.auth.RoleSelectionScreen
import com.example.ui.manager.ManagerDashboardScreen
import com.example.ui.manager.ManagerDashboardViewModel
import com.example.ui.nurse.NurseDashboardScreen
import com.example.ui.nurse.NurseDashboardViewModel
import com.example.ui.navigation.Screen
import com.example.ui.theme.PRMSMedicalTheme

class MainActivity : FragmentActivity() {

    private val authViewModel: AuthViewModel by viewModels()
    private val adminViewModel: AdminDashboardViewModel by viewModels()
    private val managerViewModel: ManagerDashboardViewModel by viewModels()
    private val nurseViewModel: NurseDashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        // High-Speed Splash Screen (API 26 Compatible via androidx.core:core-splashscreen)
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PRMSMedicalTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF0F172A)
                ) {
                    val navController = rememberNavController()
                    var loggedInUser by remember { mutableStateOf<User?>(null) }
                    var selectedRole by remember { mutableStateOf<UserRole?>(null) }

                    NavHost(
                        navController = navController,
                        startDestination = Screen.RoleSelection.route
                    ) {
                        // Role Selection Screen Route (Appears after splash screen)
                        composable(Screen.RoleSelection.route) {
                            RoleSelectionScreen(
                                onRoleSelectedForLogin = { role ->
                                    selectedRole = role
                                    authViewModel.resetState()
                                    navController.navigate(Screen.Login.route)
                                },
                                onInstantDemoLogin = { role ->
                                    selectedRole = role
                                    authViewModel.loginWithPreset(role)
                                    navController.navigate(Screen.Login.route)
                                }
                            )
                        }

                        // Login Screen Route
                        composable(Screen.Login.route) {
                            LoginScreen(
                                authViewModel = authViewModel,
                                selectedRole = selectedRole,
                                onBackToRoleSelection = {
                                    navController.navigate(Screen.RoleSelection.route) {
                                        popUpTo(Screen.RoleSelection.route) { inclusive = true }
                                    }
                                },
                                onLoginSuccess = { user ->
                                    loggedInUser = user
                                    when (user.role) {
                                        UserRole.ADMIN -> navController.navigate(Screen.AdminDashboard.route) {
                                            popUpTo(Screen.RoleSelection.route) { inclusive = true }
                                        }
                                        UserRole.MANAGER -> navController.navigate(Screen.ManagerDashboard.route) {
                                            popUpTo(Screen.RoleSelection.route) { inclusive = true }
                                        }
                                        UserRole.NURSE -> navController.navigate(Screen.NurseDashboard.route) {
                                            popUpTo(Screen.RoleSelection.route) { inclusive = true }
                                        }
                                    }
                                }
                            )
                        }

                        // Admin Dashboard Route
                        composable(Screen.AdminDashboard.route) {
                            val user = loggedInUser ?: return@composable
                            AdminDashboardScreen(
                                currentUser = user,
                                viewModel = adminViewModel,
                                onLogout = {
                                    loggedInUser = null
                                    selectedRole = null
                                    authViewModel.resetState()
                                    navController.navigate(Screen.RoleSelection.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }

                        // Manager Dashboard Route
                        composable(Screen.ManagerDashboard.route) {
                            val user = loggedInUser ?: return@composable
                            ManagerDashboardScreen(
                                currentUser = user,
                                viewModel = managerViewModel,
                                onLogout = {
                                    loggedInUser = null
                                    selectedRole = null
                                    authViewModel.resetState()
                                    navController.navigate(Screen.RoleSelection.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }

                        // Nurse View Route
                        composable(Screen.NurseDashboard.route) {
                            val user = loggedInUser ?: return@composable
                            NurseDashboardScreen(
                                currentUser = user,
                                viewModel = nurseViewModel,
                                onLogout = {
                                    loggedInUser = null
                                    selectedRole = null
                                    authViewModel.resetState()
                                    navController.navigate(Screen.RoleSelection.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
