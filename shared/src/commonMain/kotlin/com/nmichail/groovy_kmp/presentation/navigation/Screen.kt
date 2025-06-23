package com.nmichail.groovy_kmp.presentation.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Register : Screen("register")
    
    sealed class MainSection(route: String) : Screen(route) {
        data object Home : MainSection("home")
        data object Search : MainSection("search")
        data object Favourite : MainSection("favourite")
        data object Profile : MainSection("profile")
    }
} 