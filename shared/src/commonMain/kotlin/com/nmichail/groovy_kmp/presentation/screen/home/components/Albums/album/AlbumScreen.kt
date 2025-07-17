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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nmichail.groovy_kmp.domain.models.AlbumWithTracks
import com.nmichail.groovy_kmp.presentation.screen.home.components.Albums.PlatformImage
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.collectAsState
import com.nmichail.groovy_kmp.domain.models.PlayerState
import com.nmichail.groovy_kmp.presentation.screen.player.PlayerViewModel
import org.koin.mp.KoinPlatform.getKoin
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.scale
import com.nmichail.groovy_kmp.presentation.screen.home.components.Albums.AnimatedPlayingIndicator
import androidx.compose.runtime.LaunchedEffect
import com.nmichail.groovy_kmp.presentation.screen.home.components.Albums.album.AlbumViewModel
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.nmichail.groovy_kmp.presentation.AlbumFontFamily
import com.nmichail.groovy_kmp.presentation.screen.home.HomeViewModel
import com.nmichail.groovy_kmp.presentation.screen.home.components.Albums.AlbumUi
import com.nmichail.groovy_kmp.presentation.screen.home.components.Albums.AlbumCard
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.alpha
import com.nmichail.groovy_kmp.domain.models.Track
import kotlinx.coroutines.launch
import androidx.compose.ui.text.style.TextAlign

// Вынести генерацию albumColor в функцию
fun generateAlbumColor(url: String?): Color {
    val hash = (url ?: "").hashCode()
    val hue = (hash % 360).toFloat()
    val saturation = when {
        hash % 3 == 0 -> 0.4f
        hash % 3 == 1 -> 0.25f
        else -> 0.15f
    }
    val lightness = when {
        hash % 4 == 0 -> 0.35f
        hash % 4 == 1 -> 0.45f
        hash % 4 == 2 -> 0.55f
        else -> 0.4f
    }
    val c = (1 - kotlin.math.abs(2 * lightness - 1)) * saturation
    val x = c * (1 - kotlin.math.abs((hue / 60) % 2 - 1))
    val m = lightness - c / 2
    val (r, g, b) = when {
        hue < 60 -> Triple(c, x, 0f)
        hue < 120 -> Triple(x, c, 0f)
        hue < 180 -> Triple(0f, c, x)
        hue < 240 -> Triple(0f, x, c)
        hue < 300 -> Triple(x, 0f, c)
        else -> Triple(c, 0f, x)
    }
    return Color(
        red = (r + m).coerceIn(0f, 1f),
        green = (g + m).coerceIn(0f, 1f),
        blue = (b + m).coerceIn(0f, 1f),
        alpha = 0.7f
    )
}


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
    val playerViewModel = remember { getKoin().get<PlayerViewModel>() }
    val playerInfo by playerViewModel.playerInfo.collectAsState()
    val isPlaying = playerInfo.state is PlayerState.Playing
    val currentTrack = playerInfo.track
    
    val albumViewModel = remember { getKoin().get<AlbumViewModel>() }
    val backgroundColor = albumViewModel.getBackgroundColor()
    val coroutineScope = rememberCoroutineScope()

    val albumColor = remember(albumWithTracks.album.coverUrl) {
        generateAlbumColor(albumWithTracks.album.coverUrl)
    }

    androidx.compose.runtime.key(albumWithTracks.album.id) {
        if (albumColor == Color(0xFFAAA287)) {
            PlatformImage(
                url = albumWithTracks.album.coverUrl,
                contentDescription = null,
                modifier = Modifier.size(1.dp).alpha(0f),
                onColorExtracted = { color ->
                    albumWithTracks.album.id?.let {
                        albumViewModel.setAlbumColor(it, color)
                    }
                }
            )
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(450.dp)
                    .background(albumColor)
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
                        PlatformImage(
                            url = albumWithTracks.album.coverUrl,
                            contentDescription = albumWithTracks.album.title,
                            modifier = Modifier.fillMaxSize(),
                            onColorExtracted = { color ->
                                albumWithTracks.album.id?.let {
                                    println("[AlbumScreen] setAlbumColor for albumId=$it color=$color")
                                    albumViewModel.setAlbumColor(it, color)
                                }
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = albumWithTracks.album.title ?: "",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontSize = 22.sp, // чуть больше
                            fontWeight = FontWeight.Bold,
                            fontFamily = AlbumFontFamily,
                            color = Color.White
                        ),
                        maxLines = 2
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 48.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        val author = albumWithTracks.album.artist ?: ""
                        val year = albumWithTracks.album.createdAt ?: ""
                        val authorYear = if (author.isNotBlank() && year.isNotBlank()) "$author · $year" else author + year
                        Row(
                            modifier = Modifier.clickable(enabled = author.isNotBlank()) { albumWithTracks.album.artist?.let { onArtistClick(it) } },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                            ) {
                                PlatformImage(
                                    url = albumWithTracks.album.artistPhotoUrl,
                                    contentDescription = albumWithTracks.album.artist,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = authorYear,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 14.sp,
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontFamily = AlbumFontFamily
                                )
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
                                coroutineScope.launch {
                                    val firstTrack = albumWithTracks.tracks.firstOrNull()
                                    if (currentTrack?.id != firstTrack?.id) {
                                        playerViewModel.setPlaylist(albumWithTracks.tracks, albumWithTracks.album.title ?: "Unknown Album")
                                        firstTrack?.let { playerViewModel.play(albumWithTracks.tracks, it) }
                                    } else {
                                        if (isPlaying) {
                                            playerViewModel.pause(albumWithTracks.tracks, currentTrack ?: return@launch)
                                        } else {
                                            playerViewModel.resume(albumWithTracks.tracks, currentTrack ?: return@launch)
                                        }
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
        }

        item {
            Spacer(modifier = Modifier.height(14.dp))
        }

        itemsIndexed(albumWithTracks.tracks) { index, track ->
            TrackRow(
                track = track,
                isPlaying = isPlaying && currentTrack?.id == track.id,
                onClick = {
                    coroutineScope.launch {
                        playerViewModel.setPlaylist(albumWithTracks.tracks, albumWithTracks.album.title ?: "Unknown Album")
                        playerViewModel.play(albumWithTracks.tracks, track)
                    }
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
                    }
    }
}

@Composable
fun TrackRow(track: com.nmichail.groovy_kmp.domain.models.Track, isPlaying: Boolean, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 24.dp)
    ) {
        Box(Modifier.width(32.dp), contentAlignment = Alignment.Center) {
            if (isPlaying) {
                AnimatedPlayingIndicator()
            }
        }
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = track.title ?: "",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    fontFamily = AlbumFontFamily
                )
            )
            Text(
                text = track.artist ?: "",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Gray,
                    fontSize = 13.sp,
                    fontFamily = AlbumFontFamily
                )
            )
        }
    }
}