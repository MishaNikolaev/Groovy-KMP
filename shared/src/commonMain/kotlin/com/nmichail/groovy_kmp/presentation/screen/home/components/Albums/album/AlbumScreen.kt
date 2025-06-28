package com.nmichail.groovy_kmp.presentation.screen.home.components.Albums.album

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nmichail.groovy_kmp.domain.models.AlbumWithTracks
import com.nmichail.groovy_kmp.presentation.screen.home.components.Albums.PlatformImage
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.collectAsState
import com.nmichail.groovy_kmp.domain.models.PlayerState
import com.nmichail.groovy_kmp.presentation.screen.player.PlayerViewModel
import org.koin.mp.KoinPlatform.getKoin
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.scale
import com.nmichail.groovy_kmp.presentation.screen.home.components.Albums.AnimatedPlayingIndicator

@Composable
fun AlbumScreen(
    albumWithTracks: AlbumWithTracks,
    onBack: () -> Unit,
    onArtistClick: (String) -> Unit,
    onLikeClick: () -> Unit,
    onPlayClick: () -> Unit,
    onPauseClick: () -> Unit,
    onTrackClick: (trackId: String) -> Unit
) {
    val scrollState = rememberScrollState()
    val playerViewModel = getKoin().get<PlayerViewModel>()
    val playerState by playerViewModel.state.collectAsState()
    val currentTrack by playerViewModel.currentTrack.collectAsState()
    val isPlaying = playerState is PlayerState.Playing
    val hasTrack = currentTrack != null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(440.dp)
                .background(
                    Color(0xFFAAA287)
                )
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
            ) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.LightGray)
                ) {
                    PlatformImage(albumWithTracks.album.coverUrl, albumWithTracks.album.title)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = albumWithTracks.album.title ?: "",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = albumWithTracks.album.artist ?: "",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Normal,
                            fontSize = 18.sp,
                            color = Color.White
                        ),
                        modifier = Modifier.clickable { albumWithTracks.album.artist?.let { onArtistClick(it) } }
                    )
                    if (!albumWithTracks.album.createdAt.isNullOrBlank()) {
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = albumWithTracks.album.createdAt ?: "",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Normal,
                                color = Color.White,
                                fontSize = 18.sp
                            ),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = onLikeClick,
                        modifier = Modifier
                            .size(64.dp)
                            .background(Color(0x33AAAAAA), shape = CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.FavoriteBorder,
                            contentDescription = "Like",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(48.dp))
                    IconButton(
                        onClick = {
                            if (!hasTrack && albumWithTracks.tracks.isNotEmpty()) {
                                playerViewModel.play(albumWithTracks.tracks.first())
                            } else {
                                if (isPlaying) {
                                    playerViewModel.pause()
                                } else {
                                    playerViewModel.resume()
                                }
                            }
                        },
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color(0xFFFFD600), shape = CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.Black,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        albumWithTracks.tracks.forEachIndexed { index, track ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        track.id?.let {
                            val found = albumWithTracks.tracks.find { t -> t.id == it }
                            if (found != null) playerViewModel.play(found)
                            onTrackClick(it)
                        }
                    }
                    .background(Color.White)
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.width(24.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (currentTrack?.id == track.id && isPlaying) {
                        AnimatedPlayingIndicator()
                    } else {
                        Text(
                            text = "${index + 1}",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = Color.Gray,
                                fontWeight = FontWeight.Normal
                            )
                        )
                    }
                }
                Text(
                    text = track.title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.Black,
                        fontWeight = FontWeight.Normal
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }
        Spacer(modifier = Modifier.height(40.dp))
    }
}