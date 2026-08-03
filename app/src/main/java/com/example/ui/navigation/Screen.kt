package com.example.ui.navigation

sealed class Screen(val route: String) {
    object RoleSelection : Screen("role_selection")
    object Login : Screen("login")
    object AdminDashboard : Screen("admin_dashboard")
    object ManagerDashboard : Screen("manager_dashboard")
    object NurseDashboard : Screen("nurse_dashboard")
}
