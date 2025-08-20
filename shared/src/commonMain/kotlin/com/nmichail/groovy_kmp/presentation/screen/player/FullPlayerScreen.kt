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
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.RepeatMode as AnimationRepeatMode
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.ui.draw.alpha
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nmichail.groovy_kmp.presentation.screen.home.components.Albums.album.AlbumViewModel
import com.nmichail.groovy_kmp.presentation.screen.player.VideoPlayer
import org.koin.mp.KoinPlatform.getKoin
import kotlin.time.TimeSource
import kotlin.time.Duration.Companion.milliseconds
import com.nmichail.groovy_kmp.domain.repository.TrackRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FullPlayerScreen(
    currentTrack: Track?,
    playerState: PlayerState,
    progress: Float,
    onBackClick: () -> Unit,
    onBackToAlbumClick: (() -> Unit)? = null,
    onArtistClick: ((String) -> Unit)? = null,
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
    val trackRepository = remember { getKoin().get<TrackRepository>() }
    val coroutineScope = rememberCoroutineScope()
    
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
    val albumState by albumViewModel.state.collectAsState()
    
    // Get the current album from state if it matches the track's album
    val currentAlbum = if (albumState?.album?.id == currentTrack?.albumId) albumState?.album else null
    
    var albumColor by remember(currentTrack?.coverColor, currentTrack?.albumId, currentAlbum?.coverColor) {
        val initialColor = currentTrack?.coverColor?.let { Color(it) } 
            ?: currentAlbum?.coverColor?.let { Color(it) }
            ?: albumViewModel.getAlbumCoverColor(currentTrack?.albumId) 
            ?: Color(0xFFAAA287)
        println("[FullPlayerScreen] Initial albumColor: $initialColor for track: ${currentTrack?.title}, albumId: ${currentTrack?.albumId}")
        mutableStateOf(initialColor)
    }
    
    // Update color when it changes in AlbumViewModel
    LaunchedEffect(currentTrack?.albumId) {
        currentTrack?.albumId?.let { albumId ->
            val storedColor = albumViewModel.getAlbumCoverColor(albumId)
            if (storedColor != Color(0xFFAAA287)) {
                albumColor = storedColor
                println("[FullPlayerScreen] Updated albumColor from stored: $storedColor for album $albumId")
            }
        }
    }
    
    // Update color when track changes
    LaunchedEffect(currentTrack?.id) {
        currentTrack?.let { track ->
            val newColor = track.coverColor?.let { Color(it) } 
                ?: currentAlbum?.coverColor?.let { Color(it) }
                ?: albumViewModel.getAlbumCoverColor(track.albumId) 
                ?: Color(0xFFAAA287)
            albumColor = newColor
            println("[FullPlayerScreen] Updated albumColor for new track: $newColor for track: ${track.title}")
        }
    }
    val artistPhotoUrl = currentAlbum?.artistPhotoUrl

    key(currentTrack?.id) {
        // Always extract color from cover image
        PlatformImage(
            url = currentTrack?.coverUrl,
            contentDescription = null,
            modifier = Modifier.size(1.dp).alpha(0f),
            onColorExtracted = { color ->
                currentTrack?.albumId?.let {
                    albumViewModel.setAlbumColor(it, color)
                    albumColor = color
                    println("[FullPlayerScreen] Color extracted from invisible cover: $color for album $it")
                }
            }
        )
    }





    val scrollState = rememberScrollState()
    var isLiked by remember { mutableStateOf(false) }
    
    val density = LocalDensity.current
    val topPadding = with(density) { 8.dp.toPx() }
    val adaptiveTopPadding = with(density) { (topPadding + 8).toDp() }

    LaunchedEffect(currentTrack.id) {
        try {
            isLiked = trackRepository.isTrackLiked(currentTrack.id ?: "")
            println("[FullPlayerScreen] Track ${currentTrack.title} liked status: $isLiked")
        } catch (e: Exception) {
            println("[FullPlayerScreen] Error checking liked status: ${e.message}")
            isLiked = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (showVideo && currentTrack.videoUrl != null) {
            VideoPlayer(
                uri = currentTrack.videoUrl!!,
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
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(adaptiveTopPadding))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    resetUserInteraction()
                    onBackClick()
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
            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .size(270.dp)
                    .clip(RoundedCornerShape(18.dp))
            ) {
                if (showLyrics && currentTrack.lyrics != null) {
                    val lyrics = currentTrack.lyrics!!
                    val lyricsText = lyrics.lines.joinToString("\n") { it.text }
                    val infiniteTransition = rememberInfiniteTransition()
                    val animatedAlpha by infiniteTransition.animateFloat(
                        initialValue = 0.4f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(700, easing = LinearEasing),
                            repeatMode = AnimationRepeatMode.Reverse
                        )
                    )
                    
                    // Simple lyrics display since we don't have structured lyrics with timeMs
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = lyricsText.ifEmpty { "No lyrics available" },
                            fontFamily = AlbumFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(16.dp)
                                .alpha(animatedAlpha)
                        )
                    }
                } else if (!showVideo) {
                    PlatformImage(
                        url = currentTrack.coverUrl,
                        contentDescription = currentTrack.title,
                        modifier = Modifier.fillMaxSize(),
                        onColorExtracted = { color ->
                            currentTrack.albumId?.let {
                                albumViewModel.setAlbumColor(it, color)
                                albumColor = color
                                println("[FullPlayerScreen] Color extracted from visible cover: $color for album $it")
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
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
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp)
                    .clickable(enabled = !currentTrack.artist.isNullOrBlank()) { 
                        resetUserInteraction()
                        currentTrack.artist?.let { artist ->
                            onArtistClick?.invoke(artist)
                        }
                    },
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
            Spacer(modifier = Modifier.height(24.dp))
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
            Spacer(modifier = Modifier.height(20.dp))
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
            Spacer(modifier = Modifier.height(16.dp))
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
                            RepeatMode.None -> Icons.Filled.Repeat
                            RepeatMode.One -> Icons.Filled.RepeatOne
                            RepeatMode.All -> Icons.Filled.Repeat
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
                            if (!isLiked) {
                                trackRepository.likeTrack(currentTrack.id ?: "")
                                isLiked = true
                            } else {
                                trackRepository.unlikeTrack(currentTrack.id ?: "")
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