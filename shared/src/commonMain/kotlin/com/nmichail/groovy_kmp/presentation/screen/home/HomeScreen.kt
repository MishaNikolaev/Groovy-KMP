import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import groovy_kmp.shared.generated.resources.Res
import groovy_kmp.shared.generated.resources.queen_example
import groovy_kmp.shared.generated.resources.wham_example
import org.jetbrains.compose.resources.painterResource
import androidx.compose.foundation.rememberScrollState
import com.nmichail.groovy_kmp.presentation.screen.home.components.Albums.AlbumUi
import com.nmichail.groovy_kmp.presentation.screen.home.components.Albums.AlbumsSection
import com.nmichail.groovy_kmp.presentation.screen.home.components.Artists.ArtistsSection
import androidx.compose.foundation.verticalScroll
import com.nmichail.groovy_kmp.presentation.screen.home.components.Playlists.PlaylistUi
import com.nmichail.groovy_kmp.presentation.screen.home.components.Playlists.PlaylistsSection
import groovy_kmp.shared.generated.resources.playlist_example
import com.nmichail.groovy_kmp.presentation.screen.home.components.Playlists_Liked_Heard.PlaylistsLikedHeard

import androidx.compose.runtime.LaunchedEffect
import com.nmichail.groovy_kmp.presentation.screen.home.components.neuromusic.GenresCarousel
import groovy_kmp.shared.generated.resources.logo_groovy_vou
import com.nmichail.groovy_kmp.presentation.screen.home.HomeViewModel
import org.koin.mp.KoinPlatform.getKoin
import androidx.compose.runtime.*
import com.nmichail.groovy_kmp.presentation.screen.home.components.Albums.album.AlbumScreen

@Composable
fun HomeScreen() {
    val viewModel = remember { getKoin().get<HomeViewModel>() }
    val albums by viewModel.albums.collectAsState()
    var selectedAlbumId by remember { mutableStateOf<String?>(null) }

    if (selectedAlbumId == null) {
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
                            fontSize = 24.sp
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

            PlaylistsLikedHeard()
            Spacer(modifier = Modifier.height(14.dp))
            AlbumsSection(
                albums = albums.map { AlbumUi(it.title ?: "", it.artist ?: "", it.coverUrl, it.id) },
                onAlbumClick = { albumUi ->
                    println("Clicked album: title=${albumUi.title}, id=${albumUi.id}")
                    albumUi.id?.let { selectedAlbumId = it }
                },
                onViewAllClick = { /* TODO */ }
            )
            Spacer(modifier = Modifier.height(24.dp))
            ArtistsSection(
                title = "Top artists",
                artists = listOf(
                    Pair("Queen", Res.drawable.queen_example),
                    Pair("Wham", Res.drawable.wham_example),
                    Pair("Queen", Res.drawable.queen_example)
                ),
                onArtistClick = { /* TODO: handle click */ },
                onViewAllClick = { /* TODO */ }
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
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
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
    } else {
        val albumViewModel = remember { getKoin().get<com.nmichail.groovy_kmp.presentation.screen.home.components.Albums.album.AlbumViewModel>() }
        val albumWithTracks by albumViewModel.state.collectAsState()

        LaunchedEffect(selectedAlbumId) {
            selectedAlbumId?.let { albumViewModel.load(it) }
        }

        albumWithTracks?.let { album ->
            AlbumScreen(
                albumWithTracks = album,
                onBack = { selectedAlbumId = null },
                onArtistClick = { artistName -> /* TODO: обработка клика по артисту */ },
                onLikeClick = { /* TODO: обработка лайка */ },
                onPlayClick = { /* TODO: обработка play */ },
                onPauseClick = { /* TODO: обработка паузы */ },
                onTrackClick = { trackId -> /* TODO: обработка клика по треку */ }
            )
        } ?: Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Загрузка...", style = MaterialTheme.typography.bodyLarge)
        }
    }
}
