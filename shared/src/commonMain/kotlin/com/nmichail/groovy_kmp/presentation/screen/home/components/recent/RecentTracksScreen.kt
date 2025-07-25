package com.nmichail.groovy_kmp.presentation.screen.home.components.recent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nmichail.groovy_kmp.presentation.AlbumFontFamily
import com.nmichail.groovy_kmp.presentation.screen.home.components.Albums.PlatformImage
import com.nmichail.groovy_kmp.presentation.screen.player.PlayerViewModel
import com.nmichail.groovy_kmp.domain.models.PlayerState
import com.nmichail.groovy_kmp.presentation.screen.home.components.Albums.album.TrackRow
import org.koin.mp.KoinPlatform.getKoin
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.foundation.clickable

@Composable
fun RecentTracksScreen(onBack: () -> Unit) {
    val viewModel = remember { getKoin().get<RecentTracksViewModel>() }
    val tracks by viewModel.tracks.collectAsState()
    val lastTrack = tracks.firstOrNull()
    val coverColor = lastTrack?.coverColor ?: 0xFFB0AFAF
    val coverUrl = lastTrack?.coverUrl

    LaunchedEffect(Unit) { viewModel.load() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "History",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = AlbumFontFamily
                )
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        val playerViewModel = remember { getKoin().get<PlayerViewModel>() }
        val playerInfo by playerViewModel.playerInfo.collectAsState()
        val isPlaying = playerInfo.state is PlayerState.Playing
        val currentTrack = playerInfo.track
        val coroutineScope = rememberCoroutineScope()
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 40.dp)
        ) {
            itemsIndexed(tracks) { index, track ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            coroutineScope.launch {
                                playerViewModel.setPlaylist(tracks, "Recent Tracks")
                                playerViewModel.play(tracks, track)
                            }
                        }
                        .padding(vertical = 8.dp, horizontal = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF2F2F2)),
                        contentAlignment = Alignment.Center

                    ) {
                        PlatformImage(
                            url = track.coverUrl,
                            contentDescription = track.title,
                            modifier = Modifier.fillMaxSize(),
                            onColorExtracted = null
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = track.title ?: "",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 18.sp
                            ),
                            maxLines = 1
                        )
                        Text(
                            text = track.artist ?: "",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.Gray,
                                fontSize = 14.sp
                            ),
                            maxLines = 1
                        )
                    }
                    if (isPlaying && currentTrack?.id == track.id) {
                        Spacer(modifier = Modifier.width(8.dp))
                        // Можно добавить AnimatedPlayingIndicator или иконку play
                    }
                }
            }
        }
    }
} 