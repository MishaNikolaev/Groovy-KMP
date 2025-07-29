package com.nmichail.groovy_kmp.presentation.navigation

import HomeScreen
import LoginViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.nmichail.groovy_kmp.data.local.model.UserSession
import com.nmichail.groovy_kmp.data.manager.SessionManager
import com.nmichail.groovy_kmp.domain.models.PlayerState
import com.nmichail.groovy_kmp.presentation.screen.favourite.FavouriteScreen
import com.nmichail.groovy_kmp.presentation.screen.favourite.MyLikesScreen
import com.nmichail.groovy_kmp.presentation.screen.favourite.MyLikedAlbumsScreen
import com.nmichail.groovy_kmp.presentation.screen.home.components.Albums.album.AlbumScreen
import com.nmichail.groovy_kmp.presentation.screen.home.components.Albums.album.AlbumViewModel
import com.nmichail.groovy_kmp.presentation.screen.login.LoginScreen
import com.nmichail.groovy_kmp.presentation.screen.player.FullPlayerScreen
import com.nmichail.groovy_kmp.presentation.screen.player.PlayerBar
import com.nmichail.groovy_kmp.presentation.screen.profile.ProfileScreen
import com.nmichail.groovy_kmp.presentation.screen.register.RegisterScreen
import com.nmichail.groovy_kmp.presentation.screen.register.RegisterViewModel
import com.nmichail.groovy_kmp.presentation.screen.search.SearchScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.rememberNavigator
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
    var showFullPlayer by rememberSaveable { mutableStateOf(false) }
    var albumIdForReturn by rememberSaveable { mutableStateOf<String?>(null) }
    var showMyLikes by rememberSaveable { mutableStateOf(false) }
    var showMyLikedAlbums by rememberSaveable { mutableStateOf(false) }
    var showFavouriteFromHome by rememberSaveable { mutableStateOf(false) }
    val albumViewModel = remember { getKoin().get<AlbumViewModel>() }
    val backgroundColor = albumViewModel.getBackgroundColor()
    var albumIdFromLikes by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(currentTrack?.albumId) {
        currentTrack?.albumId?.let { albumId ->
            albumViewModel.load(albumId)
        }
    }

    if (showFullPlayer == true && currentTrack != null) {
        FullPlayerScreen(
            currentTrack = currentTrack,
            playerState = playerState,
            progress = progress,
            onBackClick = { showFullPlayer = false },
            onBackToAlbumClick = {
                albumIdForReturn = currentTrack.albumId
                showFullPlayer = false
            },
            onPlayPauseClick = {
                if (playerState is PlayerState.Playing) playerViewModel.pause(playerInfo.playlist, currentTrack) else playerViewModel.resume(playerInfo.playlist, currentTrack)
            },
            onNextClick = {
                val index = playerInfo.playlist.indexOfFirst { it.id == currentTrack.id }
                val nextIndex = if (index == -1) 0 else (index + 1) % playerInfo.playlist.size
                val nextTrack = playerInfo.playlist.getOrNull(nextIndex)
                if (nextTrack != null) playerViewModel.play(playerInfo.playlist, nextTrack)
            },
            onPreviousClick = {
                val index = playerInfo.playlist.indexOfFirst { it.id == currentTrack.id }
                val prevIndex = if (index == -1) 0 else (index - 1 + playerInfo.playlist.size) % playerInfo.playlist.size
                val prevTrack = playerInfo.playlist.getOrNull(prevIndex)
                if (prevTrack != null) playerViewModel.play(playerInfo.playlist, prevTrack)
            },
            onSeek = { newProgress ->
                val duration = playerInfo.progress.totalDuration
                if (duration > 0) {
                    playerViewModel.onTrackProgressChanged(newProgress)
                }
            },
            onShuffleClick = { playerViewModel.toggleShuffle() },
            onRepeatClick = { playerViewModel.toggleRepeatMode() },
            isShuffleEnabled = playerInfo.isShuffleEnabled,
            repeatMode = playerInfo.repeatMode,
            currentPosition = playerInfo.progress.currentPosition,
            duration = playerInfo.progress.totalDuration,
            backgroundColor = backgroundColor
        )
    } else if (albumIdForReturn != null) {
        val albumViewModel = remember { getKoin().get<AlbumViewModel>() }
        val albumState by albumViewModel.state.collectAsState()
        LaunchedEffect(albumIdForReturn) {
            albumIdForReturn?.let { albumViewModel.load(it) }
        }
        albumState?.let { albumWithTracks ->
            Scaffold(
                bottomBar = {
                    if (currentTrack != null) {
                        PlayerBar(
                            currentTrack = currentTrack,
                            playerState = playerState,
                            progress = progress,
                            onPlayerBarClick = {
                                showFullPlayer = true
                                albumIdForReturn = null
                            },
                            onPlayPauseClick = {
                                if (playerState is PlayerState.Playing) playerViewModel.pause(playerInfo.playlist, currentTrack) else playerViewModel.resume(playerInfo.playlist, currentTrack)
                            },
                            onNextClick = {
                                val index = playerInfo.playlist.indexOfFirst { it.id == currentTrack.id }
                                val nextIndex = if (index == -1) 0 else (index + 1) % playerInfo.playlist.size
                                val nextTrack = playerInfo.playlist.getOrNull(nextIndex)
                                if (nextTrack != null) playerViewModel.play(playerInfo.playlist, nextTrack)
                            },
                            onPreviousClick = {
                                val index = playerInfo.playlist.indexOfFirst { it.id == currentTrack.id }
                                val prevIndex = if (index == -1) 0 else (index - 1 + playerInfo.playlist.size) % playerInfo.playlist.size
                                val prevTrack = playerInfo.playlist.getOrNull(prevIndex)
                                if (prevTrack != null) playerViewModel.play(playerInfo.playlist, prevTrack)
                            },
                            onTrackProgressChanged = { newProgress ->
                                val duration = playerInfo.progress.totalDuration
                                if (duration > 0) {
                                    val newPosition = (duration * newProgress).toLong()
                                    playerViewModel.onTrackProgressChanged(newProgress)
                                }
                            }
                        )
                    }
                }
            ) { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    AlbumScreen(
                        albumWithTracks = albumWithTracks,
                        onBack = { albumIdForReturn = null },
                        onArtistClick = {},
                        onPlayClick = {},
                        onPauseClick = {},
                        onTrackClick = {}
                    )
                }
            }
        }
    } else if (albumIdFromLikes != null) {
        val albumViewModel = remember { getKoin().get<AlbumViewModel>() }
        val albumState by albumViewModel.state.collectAsState()
        LaunchedEffect(albumIdFromLikes) {
            albumIdFromLikes?.let { albumViewModel.load(it) }
        }
        albumState?.let { albumWithTracks ->
            Scaffold(
                bottomBar = {
                    if (currentTrack != null) {
                        PlayerBar(
                            currentTrack = currentTrack,
                            playerState = playerState,
                            progress = progress,
                            onPlayerBarClick = {
                                showFullPlayer = true
                                albumIdFromLikes = null
                            },
                            onPlayPauseClick = {
                                if (playerState is PlayerState.Playing) playerViewModel.pause(playerInfo.playlist, currentTrack) else playerViewModel.resume(playerInfo.playlist, currentTrack)
                            },
                            onNextClick = {
                                val index = playerInfo.playlist.indexOfFirst { it.id == currentTrack.id }
                                val nextIndex = if (index == -1) 0 else (index + 1) % playerInfo.playlist.size
                                val nextTrack = playerInfo.playlist.getOrNull(nextIndex)
                                if (nextTrack != null) playerViewModel.play(playerInfo.playlist, nextTrack)
                            },
                            onPreviousClick = {
                                val index = playerInfo.playlist.indexOfFirst { it.id == currentTrack.id }
                                val prevIndex = if (index == -1) 0 else (index - 1 + playerInfo.playlist.size) % playerInfo.playlist.size
                                val prevTrack = playerInfo.playlist.getOrNull(prevIndex)
                                if (prevTrack != null) playerViewModel.play(playerInfo.playlist, prevTrack)
                            },
                            onTrackProgressChanged = { newProgress ->
                                val duration = playerInfo.progress.totalDuration
                                if (duration > 0) {
                                    val newPosition = (duration * newProgress).toLong()
                                    playerViewModel.onTrackProgressChanged(newProgress)
                                }
                            }
                        )
                    }
                }
            ) { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    AlbumScreen(
                        albumWithTracks = albumWithTracks,
                        onBack = { albumIdFromLikes = null },
                        onArtistClick = {},
                        onPlayClick = {},
                        onPauseClick = {},
                        onTrackClick = {}
                    )
                }
            }
        }
    } else {
        Scaffold(
            bottomBar = {
                Column {
                    if (currentTrack != null) {
                        PlayerBar(
                            currentTrack = currentTrack,
                            playerState = playerState,
                            progress = progress,
                            onPlayerBarClick = { showFullPlayer = true },
                            onPlayPauseClick = {
                                if (playerState is PlayerState.Playing) playerViewModel.pause(playerInfo.playlist, currentTrack!!) else playerViewModel.resume(playerInfo.playlist, currentTrack!!)
                            },
                            onNextClick = {
                                val index = playerInfo.playlist.indexOfFirst { it.id == currentTrack?.id }
                                val nextIndex = if (index == -1) 0 else (index + 1) % playerInfo.playlist.size
                                val nextTrack = playerInfo.playlist.getOrNull(nextIndex)
                                if (nextTrack != null) playerViewModel.play(playerInfo.playlist, nextTrack)
                            },
                            onPreviousClick = {
                                val index = playerInfo.playlist.indexOfFirst { it.id == currentTrack?.id }
                                val prevIndex = if (index == -1) 0 else (index - 1 + playerInfo.playlist.size) % playerInfo.playlist.size
                                val prevTrack = playerInfo.playlist.getOrNull(prevIndex)
                                if (prevTrack != null) playerViewModel.play(playerInfo.playlist, prevTrack)
                            },
                            onTrackProgressChanged = { newProgress ->
                                val duration = playerInfo.progress.totalDuration
                                if (duration > 0) {
                                    playerViewModel.onTrackProgressChanged(newProgress)
                                }
                            },
                            backgroundColor = backgroundColor
                        )
                    }
                    BottomBar(
                        currentRoute = selectedTab.route,
                        onNavigate = { tab ->
                            if (showFavouriteFromHome && tab == Screen.MainSection.Home) {
                                showFavouriteFromHome = false
                                onTabSelected(Screen.MainSection.Home)
                            } else {
                                onTabSelected(tab)
                            }
                        }
                    )
                }
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                when (selectedTab) {
                    Screen.MainSection.Home -> {
                        if (showFavouriteFromHome) {
                            FavouriteScreen(
                                onMyLikesClick = { showMyLikes = true },
                                onAlbumsClick = { showMyLikedAlbums = true }
                            )
                        } else {
                            HomeScreen(
                                onMyLikesClick = { 
                                    showFavouriteFromHome = true
                                    onTabSelected(Screen.MainSection.Favourite)
                                }
                            )
                        }
                    }
                    Screen.MainSection.Search -> SearchScreen()
                    Screen.MainSection.Favourite -> {
                        if (showMyLikes) {
                            MyLikesScreen(
                                onBackClick = { showMyLikes = false },
                                onAlbumClick = { albumId ->
                                    albumIdFromLikes = albumId
                                    showMyLikes = false
                                }
                            )
                        } else if (showMyLikedAlbums) {
                            MyLikedAlbumsScreen(
                                onBackClick = { showMyLikedAlbums = false },
                                onAlbumClick = { albumId ->
                                    albumIdFromLikes = albumId
                                    showMyLikedAlbums = false
                                }
                            )
                        } else {
                            FavouriteScreen(
                                onMyLikesClick = { showMyLikes = true },
                                onAlbumsClick = { showMyLikedAlbums = true }
                            )
                        }
                    }
                    Screen.MainSection.Profile -> ProfileScreen(
                        email = userSession?.email,
                        username = userSession?.username,
                        onLogout = onLogout
                    )
                }
            }
        }
    }
}