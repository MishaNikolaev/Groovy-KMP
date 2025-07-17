package com.nmichail.groovy_kmp.presentation.screen.player

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nmichail.groovy_kmp.domain.models.*
import com.nmichail.groovy_kmp.presentation.screen.home.components.Albums.PlatformImage
import com.nmichail.groovy_kmp.presentation.AlbumFontFamily
import kotlinx.coroutines.delay
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import com.nmichail.groovy_kmp.presentation.screen.home.components.Albums.album.generateAlbumColor

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FullPlayerScreen(
    currentTrack: Track?,
    playerState: PlayerState,
    progress: Float,
    onBackClick: () -> Unit,
    onBackToAlbumClick: (() -> Unit)? = null,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onSeek: (Float) -> Unit,
    onShuffleClick: () -> Unit,
    onRepeatClick: () -> Unit,
    isShuffleEnabled: Boolean = false,
    repeatMode: RepeatMode = RepeatMode.None,
    currentPosition: Long = 0L,
    duration: Long = 0L,
    backgroundColor: Color = Color.White
) {
    if (currentTrack == null) return

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = androidx.compose.animation.core.tween(100)
    )

    var isDragging by remember { mutableStateOf(false) }
    var dragProgress by remember { mutableStateOf(progress) }
    var lastSeekProgress by remember { mutableStateOf<Float?>(null) }

    LaunchedEffect(progress) {
        if (lastSeekProgress != null && kotlin.math.abs(progress - lastSeekProgress!!) < 0.01f) {
            isDragging = false
            lastSeekProgress = null
        }
        if (!isDragging) {
            dragProgress = progress
        }
    }

    val albumViewModel = remember { org.koin.mp.KoinPlatform.getKoin().get<com.nmichail.groovy_kmp.presentation.screen.home.components.Albums.album.AlbumViewModel>() }
    val albumColor = remember(currentTrack?.coverUrl) {
        generateAlbumColor(currentTrack?.coverUrl)
    }
    val albumState by albumViewModel.state.collectAsState()
    val currentAlbum = if (albumState?.album?.id == currentTrack.albumId) albumState?.album else null
    val artistPhotoUrl = currentAlbum?.artistPhotoUrl

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(albumColor)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onBackToAlbumClick?.invoke() ?: onBackClick() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Text(
                text = "Now Playing",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            )
            IconButton(onClick = { /* TODO: More options */ }) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "More",
                    tint = Color.White
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .size(220.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Color.LightGray)
        ) {
            PlatformImage(
                url = currentTrack.coverUrl,
                contentDescription = currentTrack.title,
                modifier = Modifier.fillMaxSize(),
                onColorExtracted = { color ->
                    currentTrack.albumId?.let {
                        println("[FullPlayerScreen] setAlbumColor for albumId=$it color=$color")
                        albumViewModel.setAlbumColor(it, color)
                    }
                }
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = currentTrack.title ?: "",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                color = Color.White,
                fontFamily = AlbumFontFamily
            ),
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.basicMarquee()
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 48.dp)
                .clickable(enabled = !currentTrack.artist.isNullOrBlank()) { /* TODO: обработка клика по артисту */ },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
            ) {
                PlatformImage(
                    url = artistPhotoUrl,
                    contentDescription = currentTrack.artist,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = currentTrack.artist ?: "",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 16.sp,
                    fontFamily = AlbumFontFamily
                ),
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.basicMarquee()
            )
        }
        Spacer(modifier = Modifier.height(22.dp))
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Slider(
                value = if (isDragging) dragProgress else progress,
                onValueChange = { newProgress ->
                    isDragging = true
                    dragProgress = newProgress
                },
                onValueChangeFinished = {
                    lastSeekProgress = dragProgress
                    onSeek(dragProgress)
                    // isDragging сбросится в LaunchedEffect, когда progress обновится
                },
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color.White,
                    inactiveTrackColor = Color(0x33FFFFFF)
                )
            )
            val displayedPosition = if (isDragging) {
                (dragProgress * duration).toLong()
            } else {
                currentPosition
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatTime(displayedPosition),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White
                    )
                )
                Text(
                    text = formatTime(duration),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        // Первый ряд: Mute, Previous, Play, Next, Like
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* TODO: Mute */ }, modifier = Modifier.size(48.dp)) {
                Icon(
                    imageVector = Icons.Filled.VolumeOff,
                    contentDescription = "Mute",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            IconButton(onClick = onPreviousClick, modifier = Modifier.size(56.dp)) {
                Icon(
                    imageVector = Icons.Filled.SkipPrevious,
                    contentDescription = "Previous",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
            IconButton(
                onClick = onPlayPauseClick,
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        color = Color(0xFFE94057),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = if (playerState is PlayerState.Playing) {
                        Icons.Filled.Pause
                    } else {
                        Icons.Filled.PlayArrow
                    },
                    contentDescription = if (playerState is PlayerState.Playing) "Pause" else "Play",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
            IconButton(onClick = onNextClick, modifier = Modifier.size(56.dp)) {
                Icon(
                    imageVector = Icons.Filled.SkipNext,
                    contentDescription = "Next",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
            IconButton(onClick = { /* TODO: Like */ }, modifier = Modifier.size(48.dp)) {
                Icon(
                    imageVector = Icons.Filled.FavoriteBorder,
                    contentDescription = "Like",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        // Второй ряд: Repeat, Playlist, Lyrics, Таймер, Shuffle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onRepeatClick, modifier = Modifier.size(40.dp)) {
                Icon(
                    imageVector = when (repeatMode) {
                        RepeatMode.None -> Icons.Filled.Repeat
                        RepeatMode.One -> Icons.Filled.RepeatOne
                        RepeatMode.All -> Icons.Filled.Repeat
                    },
                    contentDescription = "Repeat",
                    tint = if (repeatMode != RepeatMode.None) Color(0xFFE94057) else Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            IconButton(onClick = { /* TODO: Playlist */ }, modifier = Modifier.size(40.dp)) {
                Icon(
                    imageVector = Icons.Filled.PlaylistAdd,
                    contentDescription = "Add to Playlist",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            IconButton(onClick = { /* TODO: Lyrics */ }, modifier = Modifier.size(40.dp)) {
                Icon(
                    imageVector = Icons.Filled.LibraryMusic,
                    contentDescription = "Lyrics",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            IconButton(onClick = { /* TODO: Timer */ }, modifier = Modifier.size(40.dp)) {
                Icon(
                    imageVector = Icons.Filled.Timer,
                    contentDescription = "Timer",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            IconButton(onClick = onShuffleClick, modifier = Modifier.size(40.dp)) {
                Icon(
                    imageVector = Icons.Filled.Shuffle,
                    contentDescription = "Shuffle",
                    tint = if (isShuffleEnabled) Color(0xFFE94057) else Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

private fun formatTime(timeMs: Long): String {
    if (timeMs <= 0) return "0:00"
    
    val totalSeconds = timeMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%d:%02d", minutes, seconds)
} 