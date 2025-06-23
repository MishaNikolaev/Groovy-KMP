package com.nmichail.groovy_kmp.presentation.navigation

import LoginViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.nmichail.groovy_kmp.presentation.screen.login.LoginScreen
import com.nmichail.groovy_kmp.presentation.screen.register.RegisterScreen
import com.nmichail.groovy_kmp.presentation.screen.home.HomeScreen
import com.nmichail.groovy_kmp.presentation.screen.search.SearchScreen
import com.nmichail.groovy_kmp.presentation.screen.favourite.FavouriteScreen
import com.nmichail.groovy_kmp.presentation.screen.profile.ProfileScreen
import com.nmichail.groovy_kmp.presentation.screen.register.RegisterViewModel
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.path
import moe.tlaster.precompose.navigation.rememberNavigator
import org.koin.mp.KoinPlatform.getKoin

@Composable
fun Navigation() {
    val navigator = rememberNavigator()
    val koin = getKoin()
    val loginViewModel = remember { koin.get<LoginViewModel>() }
    val registerViewModel = remember { koin.get<RegisterViewModel>() }
    var selectedTab by remember { mutableStateOf<Screen.MainSection>(Screen.MainSection.Home) }

    NavHost(
        navigator = navigator,
        initialRoute = Screen.Login.route
    ) {
        scene(route = Screen.Login.route) {
            LoginScreen(
                onSignIn = { email, password ->
                    loginViewModel.login(email, password) { isSuccess ->
                        if (isSuccess) {
                            navigator.navigate("main")
                            selectedTab = Screen.MainSection.Home
                        }
                    }
                },
                onCreateAccount = { navigator.navigate(Screen.Register.route) },
                isLoading = loginViewModel.isLoading,
                errorMessage = loginViewModel.errorMessage
            )
        }

        scene(route = Screen.Register.route) {
            RegisterScreen(
                onRegister = { email, password, username ->
                    registerViewModel.register(email, password, username) { isSuccess ->
                        if (isSuccess) {
                            navigator.navigate(Screen.Login.route)
                        }
                    }
                },
                onLogin = { navigator.navigate(Screen.Login.route) },
                isLoading = registerViewModel.isLoading,
                errorMessage = registerViewModel.errorMessage
            )
        }

        scene(route = "main") {
            MainSection(selectedTab) { tab ->
                selectedTab = tab
            }
        }
    }
}

@Composable
private fun MainSection(
    selectedTab: Screen.MainSection,
    onTabSelected: (Screen.MainSection) -> Unit
) {
    Scaffold(
        bottomBar = {
            BottomBar(
                currentRoute = selectedTab.route,
                onNavigate = onTabSelected
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedTab) {
                Screen.MainSection.Home -> HomeScreen()
                Screen.MainSection.Search -> SearchScreen()
                Screen.MainSection.Favourite -> FavouriteScreen()
                Screen.MainSection.Profile -> ProfileScreen()
            }
        }
    }
} 