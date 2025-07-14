package com.nmichail.groovy_kmp.presentation.navigation

import HomeScreen
import LoginViewModel
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.nmichail.groovy_kmp.presentation.screen.login.LoginScreen
import com.nmichail.groovy_kmp.presentation.screen.register.RegisterScreen
import com.nmichail.groovy_kmp.presentation.screen.search.SearchScreen
import com.nmichail.groovy_kmp.presentation.screen.favourite.FavouriteScreen
import com.nmichail.groovy_kmp.presentation.screen.profile.ProfileScreen
import com.nmichail.groovy_kmp.presentation.screen.register.RegisterViewModel
import com.nmichail.groovy_kmp.data.manager.SessionManager
import com.nmichail.groovy_kmp.data.local.model.UserSession
import com.nmichail.groovy_kmp.domain.models.PlayerState
import com.nmichail.groovy_kmp.presentation.screen.player.PlayerBar
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.rememberNavigator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform.getKoin

@Composable
fun Navigation() {
    val navigator = rememberNavigator()
    val koin = getKoin()
    val loginViewModel = remember { koin.get<LoginViewModel>() }
    val registerViewModel = remember { koin.get<RegisterViewModel>() }
    val sessionManager = remember { koin.get<SessionManager>() }
    var selectedTab by remember { mutableStateOf<Screen.MainSection>(Screen.MainSection.Home) }
    var userSession by remember { mutableStateOf<UserSession?>(null) }
    var initialRoute by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        userSession = sessionManager.getSession()
        initialRoute = if (userSession != null) "main" else Screen.Login.route
    }
    if (initialRoute == null) return

    NavHost(
        navigator = navigator,
        initialRoute = initialRoute!!
    ) {
        scene(route = Screen.Login.route) {
            LoginScreen(
                onSignIn = { email, password ->
                    loginViewModel.login(email, password) { isSuccess ->
                        if (isSuccess) {
                            CoroutineScope(Dispatchers.Main).launch {
                                val user = loginViewModel.getUser()
                                val token = loginViewModel.getToken()
                                if (user != null && token != null) {
                                    sessionManager.saveSession(UserSession(user.email, user.username, token))
                                    userSession = UserSession(user.email, user.username, token)
                                }
                                navigator.navigate("main")
                                selectedTab = Screen.MainSection.Home
                            }
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
                onLogin = { navigator.navigate(Screen.Login.route) }
            )
        }

        scene(route = "main") {
            MainSection(
                selectedTab = selectedTab,
                onTabSelected = { tab -> selectedTab = tab },
                userSession = userSession,
                onLogout = {
                    CoroutineScope(Dispatchers.Main).launch {
                        sessionManager.clearSession()
                        userSession = null
                        navigator.navigate(Screen.Login.route)
                    }
                }
            )
        }
    }
}

@Composable
private fun MainSection(
    selectedTab: Screen.MainSection,
    onTabSelected: (Screen.MainSection) -> Unit,
    userSession: UserSession?,
    onLogout: () -> Unit
) {
    val playerViewModel = remember { getKoin().get<com.nmichail.groovy_kmp.presentation.screen.player.PlayerViewModel>() }
    val playerInfo by playerViewModel.playerInfo.collectAsState()
    val currentTrack = playerInfo.track
    val playerState = playerInfo.state
    val progress = if (playerInfo.progress.totalDuration > 0) {
        playerInfo.progress.currentPosition.toFloat() / playerInfo.progress.totalDuration
    } else 0f

    Scaffold(
        bottomBar = {
            Column {
                if (currentTrack != null) {
                    PlayerBar(
                        currentTrack = currentTrack,
                        playerState = playerState,
                        progress = progress,
                        onPlayerBarClick = { /* TODO: открыть полный плеер */ },
                        onPlayPauseClick = {
                            if (playerState is PlayerState.Playing) playerViewModel.pause(playerInfo.playlist, currentTrack!!) else playerViewModel.resume(playerInfo.playlist, currentTrack!!)
                        },
                        onNextClick = { playerViewModel.skipToNext(playerInfo.playlist, currentTrack!!) },
                        onPreviousClick = { playerViewModel.skipToPrevious(playerInfo.playlist, currentTrack!!) },
                        onTrackProgressChanged = { newProgress ->
                            val duration = playerInfo.progress.totalDuration
                            if (duration > 0) {
                                playerViewModel.onTrackProgressChanged(newProgress)
                            }
                        }
                    )
                }
                BottomBar(
                    currentRoute = selectedTab.route,
                    onNavigate = onTabSelected
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedTab) {
                Screen.MainSection.Home -> HomeScreen()
                Screen.MainSection.Search -> SearchScreen()
                Screen.MainSection.Favourite -> FavouriteScreen()
                Screen.MainSection.Profile -> ProfileScreen(
                    email = userSession?.email,
                    username = userSession?.username,
                    onLogout = onLogout
                )
            }
        }
    }
}