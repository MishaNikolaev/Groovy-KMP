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
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.RepeatMode as AnimationRepeatMode
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.ui.draw.alpha
import com.nmichail.groovy_kmp.presentation.screen.home.components.Albums.album.AlbumViewModel
import com.nmichail.groovy_kmp.domain.models.RepeatMode as PlayerRepeatMode
import com.nmichail.groovy_kmp.presentation.screen.player.VideoPlayer
import org.koin.mp.KoinPlatform.getKoin
import kotlin.time.TimeSource
import kotlin.time.Duration.Companion.milliseconds
import com.nmichail.groovy_kmp.data.local.TrackCache
import kotlinx.coroutines.launch

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
    repeatMode: PlayerRepeatMode = PlayerRepeatMode.None,
    currentPosition: Long = 0L,
    duration: Long = 0L,
    backgroundColor: Color = Color.White
) {
    if (currentTrack == null) return

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec =  tween(100)
    )

    var isDragging by remember { mutableStateOf(false) }
    var dragProgress by remember { mutableStateOf(progress) }
    var lastSeekProgress by remember { mutableStateOf<Float?>(null) }
    var showLyrics by remember { mutableStateOf(false) }
    var showVideo by remember { mutableStateOf(false) }
    var lastUserInteraction by remember { mutableStateOf(TimeSource.Monotonic.markNow()) }

    LaunchedEffect(progress) {
        if (lastSeekProgress != null && kotlin.math.abs(progress - lastSeekProgress!!) < 0.01f) {
            isDragging = false
            lastSeekProgress = null
        }
        if (!isDragging) {
            dragProgress = progress
        }
    }

    fun resetUserInteraction() { lastUserInteraction = TimeSource.Monotonic.markNow() }

    LaunchedEffect(lastUserInteraction, currentTrack?.id, playerState) {
        showVideo = false
        if (currentTrack?.videoUrl != null && playerState is PlayerState.Playing) {
            delay(5000)
            if (lastUserInteraction.elapsedNow().inWholeMilliseconds >= 5000 && playerState is PlayerState.Playing) {
                showVideo = true
            }
        }
    }

    val albumViewModel = remember { getKoin().get<AlbumViewModel>() }
    val albumColor = remember(currentTrack?.coverColor, currentTrack?.albumId) {
        currentTrack?.coverColor?.let { Color(it) }
            ?: albumViewModel.getAlbumCoverColor(currentTrack?.albumId)
    }
    val albumState by albumViewModel.state.collectAsState()
    val currentAlbum = if (albumState?.album?.id == currentTrack.albumId) albumState?.album else null
    val artistPhotoUrl = currentAlbum?.artistPhotoUrl

    val scrollState = rememberScrollState()
    var isLiked by remember(currentTrack.id) { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(currentTrack.id) {
        val cached = TrackCache.loadTracks() ?: emptyList()
        isLiked = cached.any { it.id == currentTrack.id }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (showVideo && currentTrack.videoUrl != null) {
            VideoPlayer(
                uri = currentTrack.videoUrl,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(albumColor)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    resetUserInteraction()
                    onBackToAlbumClick?.invoke() ?: onBackClick()
                }) {
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
                IconButton(onClick = { resetUserInteraction() /* TODO: More options */ }) {
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
            ) {
                if (showLyrics && currentTrack.lyrics != null) {
                    val lyrics = currentTrack.lyrics.lines
                    val infiniteTransition = rememberInfiniteTransition()
                    val animatedAlpha by infiniteTransition.animateFloat(
                        initialValue = 0.4f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(700, easing = LinearEasing),
                            repeatMode = AnimationRepeatMode.Reverse
                        )
                    )
                    val maxTime = lyrics.filter { it.timeMs <= currentPosition }.maxOfOrNull { it.timeMs } ?: 0L
                    val activeIndices = lyrics.withIndex().filter { it.value.timeMs == maxTime }.map { it.index }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val centerIndex = activeIndices.firstOrNull() ?: 0
                        val visibleLines = (-1..1).map { offset -> centerIndex + offset }
                        visibleLines.forEach { idx ->
                            val line = lyrics.getOrNull(idx)
                            if (line != null) {
                                val isActive = idx in activeIndices
                                val isDots = line.text.trim().replace(" ", "").replace("…", ".").all { it == '.' }
                                println("[LYRICS] line='${line.text}' isActive=$isActive isDots=$isDots")
                                Text(
                                    text = line.text,
                                    fontFamily = AlbumFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = when {
                                        isActive && isDots -> 80.sp
                                        isActive -> 32.sp
                                        else -> 22.sp
                                    },
                                    color = if (isActive) Color.White else Color.White.copy(alpha = 0.4f),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .padding(vertical = 2.dp)
                                        .alpha(if (isActive && isDots) animatedAlpha else 1f)
                                )
                            }
                        }
                    }
                } else if (!showVideo) {
                    PlatformImage(
                        url = currentTrack.coverUrl,
                        contentDescription = currentTrack.title,
                        modifier = Modifier.fillMaxSize(),
                        onColorExtracted = { color ->
                            currentTrack.albumId?.let {
                                albumViewModel.setAlbumColor(it, color)
                            }
                        }
                    )
                }
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
                    .clickable(enabled = !currentTrack.artist.isNullOrBlank()) { resetUserInteraction() /* TODO: обработка клика по артисту */ },
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
                        resetUserInteraction()
                        isDragging = true
                        dragProgress = newProgress
                    },
                    onValueChangeFinished = {
                        resetUserInteraction()
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
                IconButton(onClick = {
                    resetUserInteraction()
                    onPreviousClick()
                }, modifier = Modifier.size(64.dp)) {
                    Icon(
                        imageVector = Icons.Filled.SkipPrevious,
                        contentDescription = "Previous",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
                IconButton(
                    onClick = {
                        resetUserInteraction()
                        onPlayPauseClick()
                    },
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
                IconButton(onClick = {
                    resetUserInteraction()
                    onNextClick()
                }, modifier = Modifier.size(64.dp)) {
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
                IconButton(onClick = { onRepeatClick() }, modifier = Modifier.size(40.dp)) {
                    Icon(
                        imageVector = when (repeatMode) {
                            PlayerRepeatMode.None -> Icons.Filled.Repeat
                            PlayerRepeatMode.One -> Icons.Filled.RepeatOne
                            PlayerRepeatMode.All -> Icons.Filled.Repeat
                            else -> Icons.Filled.Repeat
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
                IconButton(
                    onClick = {
                        if (currentTrack.lyrics != null) {
                            showLyrics = !showLyrics
                        }
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Lyrics,
                        contentDescription = "Lyrics",
                        tint = if (showLyrics && currentTrack.lyrics != null) Color.White else if (currentTrack.lyrics != null) Color.LightGray else Color.Gray,
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
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            val cached = TrackCache.loadTracks()?.toMutableList() ?: mutableListOf()
                            if (!isLiked) {
                                // Лайк: добавить трек в кэш если его нет
                                if (currentTrack.id != null && cached.none { it.id == currentTrack.id }) {
                                    cached.add(currentTrack)
                                    TrackCache.saveTracks(cached)
                                }
                                isLiked = true
                            } else {
                                // Дизлайк: удалить трек из кэша
                                val updated = cached.filter { it.id != currentTrack.id }
                                TrackCache.saveTracks(updated)
                                isLiked = false
                            }
                        }
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = if (isLiked) "Unlike" else "Like",
                        tint = if (isLiked) Color.Red else Color.LightGray,
                        modifier = Modifier.size(22.dp)
                    )
                }
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