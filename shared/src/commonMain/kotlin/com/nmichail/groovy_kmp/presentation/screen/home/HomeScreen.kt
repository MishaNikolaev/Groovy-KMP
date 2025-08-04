import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nmichail.groovy_kmp.presentation.AlbumFontFamily
import com.nmichail.groovy_kmp.presentation.screen.home.HomeViewModel
import com.nmichail.groovy_kmp.presentation.screen.home.components.recent.RecentTracksScreen
import com.nmichail.groovy_kmp.presentation.screen.home.components.Albums.AlbumUi
import com.nmichail.groovy_kmp.presentation.screen.home.components.Albums.AlbumsSection
import com.nmichail.groovy_kmp.presentation.screen.home.components.Albums.album.AlbumScreen
import com.nmichail.groovy_kmp.presentation.screen.home.components.Albums.album.AllAlbumsScreen
import com.nmichail.groovy_kmp.presentation.screen.home.components.Artists.ArtistsSectionWithPhotos
import com.nmichail.groovy_kmp.presentation.screen.home.components.Playlists.PlaylistUi
import com.nmichail.groovy_kmp.presentation.screen.home.components.Playlists.PlaylistsSection
import com.nmichail.groovy_kmp.presentation.screen.home.components.Playlists_Liked_Heard.PlaylistsLikedHeard
import com.nmichail.groovy_kmp.presentation.screen.home.components.neuromusic.GenresCarousel
import groovy_kmp.shared.generated.resources.*
import moe.tlaster.precompose.navigation.BackHandler
import org.jetbrains.compose.resources.painterResource
import org.koin.mp.KoinPlatform.getKoin
import com.nmichail.groovy_kmp.presentation.screen.home.components.recent.RecentTracksViewModel

@Composable
fun HomeScreen(
    onMyLikesClick: () -> Unit = {},
    onArtistClick: (String) -> Unit = {},
    onViewAllArtistsClick: () -> Unit = {}
) {
    val viewModel = remember { getKoin().get<HomeViewModel>() }
    val albums by viewModel.albums.collectAsState()
    val artists by viewModel.artists.collectAsState()
    var selectedAlbumId by rememberSaveable { mutableStateOf<String?>(null) }
    var showAllAlbums by rememberSaveable { mutableStateOf(false) }
    var showHistoryScreen by rememberSaveable { mutableStateOf(false) }
    var showAllArtists by rememberSaveable { mutableStateOf(false) }

    val recentTracksViewModel = remember { getKoin().get<RecentTracksViewModel>() }
    val recentTracks by recentTracksViewModel.tracks.collectAsState()
    val lastPlayedTrack = recentTracks.firstOrNull()
    LaunchedEffect(Unit) { recentTracksViewModel.load() }

    BackHandler(enabled = selectedAlbumId != null || showAllAlbums || showAllArtists) {
        if (selectedAlbumId != null) {
            selectedAlbumId = null
        } else if (showAllAlbums) {
            showAllAlbums = false
        } else if (showAllArtists) {
            showAllArtists = false
        }
    }

    if (selectedAlbumId == null && !showAllAlbums && !showHistoryScreen && !showAllArtists) {
        LaunchedEffect(viewModel) {
            viewModel.load()
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F8FC))
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(34.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Image(
                        painter = painterResource(Res.drawable.logo_groovy_vou),
                        contentDescription = "Heart",
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Groovy",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            fontFamily = AlbumFontFamily
                        )
                    )
                }
                IconButton(onClick = { /* TODO */ }) {
                    Icon(
                        imageVector = Icons.Filled.Notifications,
                        contentDescription = "Notifications",
                        tint = Color.Black,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            PlaylistsLikedHeard(
                onHistoryClick = { showHistoryScreen = true },
                onMyLikesClick = onMyLikesClick,
                lastPlayedTrack = lastPlayedTrack
            )
            Spacer(modifier = Modifier.height(14.dp))
            AlbumsSection(
                albums = albums.map { AlbumUi(it.title ?: "", it.artist ?: "", it.coverUrl, it.id) },
                onAlbumClick = { albumUi ->
                    albumUi.id?.let { selectedAlbumId = it }
                },
                onViewAllClick = { showAllAlbums = true }
            )
            Spacer(modifier = Modifier.height(24.dp))
            ArtistsSectionWithPhotos(
                title = "Top artists",
                artists = artists.take(3),
                onArtistClick = onArtistClick,
                onViewAllClick = onViewAllArtistsClick
            )

            Spacer(modifier = Modifier.height(24.dp))

            PlaylistsSection(
                playlists = listOf(
                    PlaylistUi("Summer vibes", Res.drawable.playlist_example),
                    PlaylistUi("Doomer music", Res.drawable.playlist_example),
                    PlaylistUi("Pop Hits", Res.drawable.playlist_example)
                ),
                onPlaylistClick = { /* TODO */ },
                onViewAllClick = { /* TODO */ }
            )

            Spacer(modifier = Modifier.height(36.dp))
            Text(
                text = "This is — Neuromusic",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = AlbumFontFamily
                ),
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Distracts from noise and helps you focus on what matters",
                style = MaterialTheme.typography.titleMedium.copy(color = Color.Gray),
                modifier = Modifier.padding(bottom = 24.dp)
            )
            GenresCarousel()
            Spacer(modifier = Modifier.height(6.dp))
        }
    } else if (showHistoryScreen) {
        RecentTracksScreen(onBack = { showHistoryScreen = false })
    } else if (showAllAlbums) {
            AllAlbumsScreen(
            onBack = { showAllAlbums = false },
            onAlbumClick = { albumUi ->
                albumUi.id?.let { selectedAlbumId = it }
                showAllAlbums = false
            }
        )
    } else {
        val albumViewModel = remember { getKoin().get<com.nmichail.groovy_kmp.presentation.screen.home.components.Albums.album.AlbumViewModel>() }
        val albumWithTracks by albumViewModel.state.collectAsState()

        LaunchedEffect(selectedAlbumId) {
            selectedAlbumId?.let { albumId ->
                try {
                    println("[HomeScreen] Loading album: $albumId")
                    albumViewModel.load(albumId)
                } catch (e: Exception) {
                    println("[HomeScreen] Error loading album $albumId: ${e.message}")
                    e.printStackTrace()
                }
            }
        }

        if (albumWithTracks != null) {
            AlbumScreen(
                albumWithTracks = albumWithTracks!!,
                onBack = { selectedAlbumId = null },
                onArtistClick = onArtistClick,
                onPlayClick = { /* TODO: обработка play */ },
                onPauseClick = { /* TODO: обработка паузы */ },
                onTrackClick = { trackId -> /* TODO: обработка клика по треку */ }
            )
        } else {
            Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(color = Color.Black)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Loading album...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}
