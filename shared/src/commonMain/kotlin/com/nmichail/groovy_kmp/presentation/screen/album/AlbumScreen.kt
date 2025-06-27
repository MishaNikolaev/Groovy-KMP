package com.nmichail.groovy_kmp.presentation.screen.album

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nmichail.groovy_kmp.domain.models.AlbumWithTracks
import com.nmichail.groovy_kmp.presentation.screen.player.PlayerViewModel
import org.koin.mp.KoinPlatform.getKoin
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.remember

@Composable
fun AlbumScreen(
    albumId: String,
    onTrackClick: (trackId: String) -> Unit,
    onBack: () -> Unit
) {
    val viewModel = remember { getKoin().get<AlbumViewModel>() }
    val playerViewModel = getKoin().get<PlayerViewModel>()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(viewModel, albumId) {
        viewModel.load(albumId)
    }

    state?.let { albumWithTracks ->
        Column(modifier = Modifier.fillMaxSize()) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
            }
            AlbumContent(albumWithTracks) { trackId ->
                val track = albumWithTracks.tracks.find { it.id == trackId }
                if (track != null) playerViewModel.play(track)
                onTrackClick(trackId)
            }
        }
    } ?: Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Загрузка...", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun AlbumContent(
    albumWithTracks: AlbumWithTracks,
    onTrackClick: (trackId: String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = albumWithTracks.album.title ?: "",
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = albumWithTracks.album.artist ?: "",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(albumWithTracks.tracks) { track ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { track.id?.let { onTrackClick(it) } },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(track.title, modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(track.artist, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
} 