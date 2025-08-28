package com.nmichail.groovy_kmp.presentation.navigation

import HomeScreen
import com.nmichail.groovy_kmp.feature.auth.login.LoginViewModel
import com.nmichail.groovy_kmp.presentation.navigation.Screen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.nmichail.groovy_kmp.domain.models.PlayerState
import com.nmichail.groovy_kmp.presentation.screen.favourite.FavouriteScreen
import com.nmichail.groovy_kmp.presentation.screen.favourite.MyLikesScreen
import com.nmichail.groovy_kmp.presentation.screen.favourite.MyLikedAlbumsScreen
import com.nmichail.groovy_kmp.presentation.screen.home.components.Albums.album.AlbumScreen
import com.nmichail.groovy_kmp.presentation.screen.home.components.Albums.album.AlbumViewModel
import com.nmichail.groovy_kmp.feature.auth.login.LoginScreen
import com.nmichail.groovy_kmp.presentation.screen.player.FullPlayerScreen
import com.nmichail.groovy_kmp.presentation.screen.player.PlayerBar
import com.nmichail.groovy_kmp.presentation.screen.profile.ProfileScreen
import com.nmichail.groovy_kmp.feature.auth.register.RegisterScreen
import com.nmichail.groovy_kmp.feature.auth.register.RegisterViewModel
import com.nmichail.groovy_kmp.presentation.screen.search.SearchScreen
import com.nmichail.groovy_kmp.presentation.screen.artist.ArtistScreen
import com.nmichail.groovy_kmp.presentation.screen.artist.AllTracksScreen
import com.nmichail.groovy_kmp.presentation.screen.artists.AllArtistsScreen
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
    val playerViewModel = remember { koin.get<com.nmichail.groovy_kmp.presentation.screen.player.PlayerViewModel>() }
    val albumViewModel = remember { koin.get<AlbumViewModel>() }
    var selectedTab by remember { mutableStateOf<Screen.MainSection>(Screen.MainSection.Home) }
    var initialRoute by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        println("🔐 Navigation: Checking for saved session...")
        loginViewModel.checkSavedSession { hasSession ->
            if (hasSession) {
                println("🔐 Navigation: Found saved session, navigating to main")
                initialRoute = "main"
            } else {
                println("🔐 Navigation: No saved session found, navigating to login")
                initialRoute = Screen.Login.route
            }
        }
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
                                println("🔐 Navigation: Login successful, navigating to main")
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
            println("🔐 Navigation: Entering main scene")
            val user = loginViewModel.getUser()
            println("🔐 Navigation: User data: $user")
            MainSection(
                selectedTab = selectedTab,
                onTabSelected = { tab -> selectedTab = tab },
                userSession = user, // Pass the user from LoginViewModel
                userEmail = user?.email,
                userUsername = user?.username,
                playerViewModel = playerViewModel,
                albumViewModel = albumViewModel,
                onLogout = {
                    println("🔐 Navigation: Logout initiated")
                    CoroutineScope(Dispatchers.Main).launch {
                        try {
                            loginViewModel.clearSession()
                            println("🔐 Navigation: Session cleared, navigating to login screen")
                            navigator.navigate(Screen.Login.route)
                            println("🔐 Navigation: Successfully navigated to login")
                        } catch (e: Exception) {
                            println("❌ Navigation: Error during logout: ${e.message}")
                            e.printStackTrace()
                        }
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
    userSession: Any?,
    onLogout: () -> Unit,
    userEmail: String? = null,
    userUsername: String? = null,
    playerViewModel: com.nmichail.groovy_kmp.presentation.screen.player.PlayerViewModel,
    albumViewModel: AlbumViewModel
) {
    // PlayerViewModel is now passed as parameter
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
    var previousScreen by rememberSaveable { mutableStateOf<String?>(null) }
    val backgroundColor = albumViewModel.getBackgroundColor()
    var albumIdFromLikes by rememberSaveable { mutableStateOf<String?>(null) }
    var showArtistScreen by rememberSaveable { mutableStateOf<String?>(null) }
    var albumIdFromArtist by rememberSaveable { mutableStateOf<String?>(null) }
    var showAllTracksScreen by rememberSaveable { mutableStateOf<String?>(null) }
    var showAllArtistsScreen by rememberSaveable { mutableStateOf(false) }

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
            onBackClick = { 
                showFullPlayer = false
                previousScreen?.let { screen ->
                    when (screen) {
                        "home" -> {
                            showFavouriteFromHome = false
                            onTabSelected(Screen.MainSection.Home)
                        }
                        "favourite" -> {
                            onTabSelected(Screen.MainSection.Favourite)
                        }
                        "search" -> {
                            onTabSelected(Screen.MainSection.Search)
                        }
                        "profile" -> {
                            onTabSelected(Screen.MainSection.Profile)
                        }
                        "album" -> {
                        }
                    }
                }
                previousScreen = null
            },
            onBackToAlbumClick = {
                albumIdForReturn = currentTrack.albumId
                showFullPlayer = false
                previousScreen = "album"
            },
            onArtistClick = { artistName ->
                showArtistScreen = artistName
                showFullPlayer = false
                previousScreen = "artist"
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
    } else if (showAllArtistsScreen) {
        Scaffold(
            bottomBar = {
                if (currentTrack != null) {
                    PlayerBar(
                        currentTrack = currentTrack,
                        playerState = playerState,
                        progress = progress,
                        onPlayerBarClick = {
                            showFullPlayer = true
                            previousScreen = "all_artists"
                        },
                        onPlayPauseClick = {
                            if (playerState is PlayerState.Playing) playerViewModel.pause(playerInfo.playlist, currentTrack) else playerViewModel.resume(playerInfo.playlist, currentTrack)
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
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                AllArtistsScreen(
                    onArtistClick = { artistName ->
                        showArtistScreen = artistName
                        showAllArtistsScreen = false
                    },
                    onBackClick = {
                        showAllArtistsScreen = false
                    }
                )
            }
        }
    } else if (albumIdForReturn != null) {
        // Use the passed albumViewModel parameter
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
                                previousScreen = "album"
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
                        onArtistClick = { artistName ->
                            showArtistScreen = artistName
                            albumIdForReturn = null
                            previousScreen = "album"
                        },
                        onPlayClick = {},
                        onPauseClick = {},
                        onTrackClick = {}
                    )
                }
            }
        }
    } else if (showArtistScreen != null) {
        Scaffold(
            bottomBar = {
                if (currentTrack != null) {
                    PlayerBar(
                        currentTrack = currentTrack,
                        playerState = playerState,
                        progress = progress,
                        onPlayerBarClick = {
                            previousScreen = "artist"
                            showFullPlayer = true
                            showArtistScreen = null
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
                ArtistScreen(
                    artistName = showArtistScreen!!,
                    onBackClick = { 
                        showArtistScreen = null
                        // Возвращаемся на предыдущий экран
                        previousScreen?.let { screen ->
                            when (screen) {
                                "home" -> {
                                    showFavouriteFromHome = false
                                    onTabSelected(Screen.MainSection.Home)
                                }
                                "favourite" -> {
                                    onTabSelected(Screen.MainSection.Favourite)
                                }
                                "search" -> {
                                    onTabSelected(Screen.MainSection.Search)
                                }
                                "profile" -> {
                                    onTabSelected(Screen.MainSection.Profile)
                                }
                                "album" -> {
                                }
                                "artist" -> {
                                    showFullPlayer = true
                                }
                            }
                        }
                        previousScreen = null
                    },
                    onTrackClick = { track ->
                        playerViewModel.play(listOf(track), track)
                    },
                    onAlbumClick = { album ->
                        album.id?.let { albumId ->
                            albumIdFromArtist = albumId
                            showArtistScreen = null
                            previousScreen = "artist"
                        }
                    },
                    onPlayClick = {
                        // TODO: Play all tracks by artist
                    },
                    onPauseClick = {
                        if (currentTrack != null) {
                            playerViewModel.pause(playerInfo.playlist, currentTrack)
                        }
                    },
                    onShowAllTracksClick = {
                        showAllTracksScreen = showArtistScreen
                        showArtistScreen = null
                        previousScreen = "artist"
                    }
                )
            }
        }
    } else if (showAllTracksScreen != null) {
        Scaffold(
            bottomBar = {
                if (currentTrack != null) {
                    PlayerBar(
                        currentTrack = currentTrack,
                        playerState = playerState,
                        progress = progress,
                        onPlayerBarClick = {
                            previousScreen = "all_tracks"
                            showFullPlayer = true
                            showAllTracksScreen = null
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
                AllTracksScreen(
                    artistName = showAllTracksScreen!!,
                    onBackClick = { 
                        showAllTracksScreen = null
                        showArtistScreen = showAllTracksScreen
                        previousScreen = "artist"
                    },
                    onTrackClick = { track ->
                        playerViewModel.play(listOf(track), track)
                    },
                    currentTrack = currentTrack,
                    isPlaying = playerState is PlayerState.Playing
                )
            }
        }
    } else if (albumIdFromArtist != null) {
        // Use the passed albumViewModel parameter
        val albumState by albumViewModel.state.collectAsState()
        LaunchedEffect(albumIdFromArtist) {
            albumIdFromArtist?.let { albumViewModel.load(it) }
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
                                previousScreen = "artist"
                                showFullPlayer = true
                                albumIdFromArtist = null
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
                        onBack = { albumIdFromArtist = null },
                        onArtistClick = { artistName ->
                            showArtistScreen = artistName
                            albumIdFromArtist = null
                            previousScreen = "album"
                        },
                        onPlayClick = {},
                        onPauseClick = {},
                        onTrackClick = {}
                    )
                }
            }
        }
    } else if (albumIdFromLikes != null) {
        // Use the passed albumViewModel parameter
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
                                previousScreen = "album"
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
                        onArtistClick = { artistName ->
                            showArtistScreen = artistName
                            albumIdFromLikes = null
                            previousScreen = "album"
                        },
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
                            onPlayerBarClick = { 
                                previousScreen = when (selectedTab) {
                                    Screen.MainSection.Home -> "home"
                                    Screen.MainSection.Search -> "search"
                                    Screen.MainSection.Favourite -> "favourite"
                                    Screen.MainSection.Profile -> "profile"
                                }
                                showFullPlayer = true 
                            },
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
                                onAlbumsClick = { showMyLikedAlbums = true },
                                onArtistClick = { artistName ->
                                    showArtistScreen = artistName
                                    previousScreen = "favourite"
                                }
                            )
                        } else {
                            HomeScreen(
                                onMyLikesClick = { 
                                    showFavouriteFromHome = true
                                    onTabSelected(Screen.MainSection.Favourite)
                                },
                                onArtistClick = { artistName ->
                                    showArtistScreen = artistName
                                    previousScreen = "home"
                                },
                                onViewAllArtistsClick = {
                                    showAllArtistsScreen = true
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
                                onAlbumsClick = { showMyLikedAlbums = true },
                                onArtistClick = { artistName ->
                                    showArtistScreen = artistName
                                    previousScreen = "favourite"
                                }
                            )
                        }
                    }
                    Screen.MainSection.Profile -> ProfileScreen(
                        email = userEmail,
                        username = userUsername,
                        onLogout = onLogout
                    )
                }
            }
        }
    }
}