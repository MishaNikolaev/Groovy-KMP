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
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
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
    val albumColor = remember(currentTrack?.coverColor, currentTrack?.albumId) {
        currentTrack?.coverColor?.let { Color(it) }
            ?: albumViewModel.getAlbumCoverColor(currentTrack?.albumId)
    }
    val albumState by albumViewModel.state.collectAsState()
    val currentAlbum = if (albumState?.album?.id == currentTrack.albumId) albumState?.album else null
    val artistPhotoUrl = currentAlbum?.artistPhotoUrl

    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(albumColor)
            .padding(16.dp)
            .verticalScroll(scrollState),
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
                .size(270.dp)
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
                color = Color.White.copy(alpha = 0.85f),
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
            Spacer(modifier = Modifier.height(12.dp))
            Slider(
                value = if (isDragging) dragProgress else progress,
                onValueChange = { newProgress ->
                    isDragging = true
                    dragProgress = newProgress
                },
                onValueChangeFinished = {
                    lastSeekProgress = dragProgress
                    onSeek(dragProgress)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color.White,
                    inactiveTrackColor = Color.White.copy(alpha = 0.25f)
                ),
                thumb = {
                    Box(
                        Modifier
                            .size(8.dp)
                            .offset(y = 6.dp, x = 4.dp)
                            .background(Color.White, shape = CircleShape)
                    )
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPreviousClick, modifier = Modifier.size(64.dp)) {
                Icon(
                    imageVector = Icons.Filled.SkipPrevious,
                    contentDescription = "Previous",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
            IconButton(
                onClick = onPlayPauseClick,
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        color = Color.White,
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
                    tint = Color.Black,
                    modifier = Modifier.size(48.dp)
                )
            }
            IconButton(onClick = onNextClick, modifier = Modifier.size(64.dp)) {
                Icon(
                    imageVector = Icons.Filled.SkipNext,
                    contentDescription = "Next",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
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
                    tint = Color.LightGray,
                    modifier = Modifier.size(22.dp)
                )
            }
            IconButton(onClick = { /* TODO: Playlist */ }, modifier = Modifier.size(40.dp)) {
                Icon(
                    imageVector = Icons.Filled.PlaylistAdd,
                    contentDescription = "Add to Playlist",
                    tint = Color.LightGray,
                    modifier = Modifier.size(22.dp)
                )
            }
            IconButton(onClick = { /* TODO: Lyrics */ }, modifier = Modifier.size(40.dp)) {
                Icon(
                    imageVector = Icons.Filled.Lyrics,
                    contentDescription = "Lyrics",
                    tint = Color.LightGray,
                    modifier = Modifier.size(22.dp)
                )
            }
            IconButton(onClick = { /* TODO: Timer */ }, modifier = Modifier.size(40.dp)) {
                Icon(
                    imageVector = Icons.Filled.Shuffle,
                    contentDescription = "Shuffle",
                    tint = Color.LightGray,
                    modifier = Modifier.size(22.dp)
                )
            }
            IconButton(onClick = onShuffleClick, modifier = Modifier.size(40.dp)) {
                Icon(
                    imageVector = Icons.Filled.FavoriteBorder,
                    contentDescription = "FavoriteBorder",
                    tint = Color.LightGray,
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
    return minutes.toString() + ":" + seconds.toString().padStart(2, '0')
} 