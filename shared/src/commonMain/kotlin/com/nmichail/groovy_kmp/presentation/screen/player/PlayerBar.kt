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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nmichail.groovy_kmp.domain.models.PlayerState
import com.nmichail.groovy_kmp.domain.models.Track
import com.nmichail.groovy_kmp.presentation.screen.home.components.Albums.PlatformImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform.getKoin
import com.nmichail.groovy_kmp.presentation.screen.home.components.Albums.album.AlbumViewModel
import com.nmichail.groovy_kmp.presentation.AlbumFontFamily
import com.nmichail.groovy_kmp.presentation.screen.home.components.Albums.album.generateAlbumColor

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlayerBar(
    currentTrack: Track?,
    playerState: PlayerState,
    progress: Float,
    onPlayerBarClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onTrackProgressChanged: (Float) -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White
) {
    if (currentTrack == null) return

    val albumViewModel = remember { getKoin().get<AlbumViewModel>() }
    val albumColor = remember(currentTrack?.coverUrl) {
        generateAlbumColor(currentTrack?.coverUrl)
    }

    var isDragging by remember { mutableStateOf(false) }
    var dragProgress by remember { mutableStateOf(progress) }

    LaunchedEffect(progress) {
        if (!isDragging) {
            dragProgress = progress
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable { onPlayerBarClick() },
        shape = RoundedCornerShape(0.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(albumColor)
        ) {
            Slider(
                value = if (isDragging) dragProgress else progress,
                onValueChange = { newProgress ->
                    isDragging = true
                    dragProgress = newProgress
                },
                onValueChangeFinished = {
                    isDragging = false
                    onTrackProgressChanged(dragProgress)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color.White,
                    inactiveTrackColor = Color(0x33FFFFFF)
                )
            )
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.1f))
                ) {
                    PlatformImage(
                        url = currentTrack.coverUrl,
                        contentDescription = currentTrack.title,
                        modifier = Modifier.fillMaxSize(),
                        onColorExtracted = { color ->
                            currentTrack.albumId?.let {
                                println("[PlayerBar] setAlbumColor for albumId=$it color=$color")
                                albumViewModel.setAlbumColor(it, color)
                            }
                        }
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = currentTrack.title ?: "",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = Color.White,
                            fontFamily = AlbumFontFamily
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.basicMarquee()
                    )
                    Text(
                        text = currentTrack.artist ?: "",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp,
                            fontFamily = AlbumFontFamily
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.basicMarquee()
                    )
                }
                    IconButton(
                        onClick = onPreviousClick,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.SkipPrevious,
                            contentDescription = "Previous",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    IconButton(
                        onClick = onPlayPauseClick,
                    modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = if (playerState is PlayerState.Playing) {
                                Icons.Filled.Pause
                            } else {
                                Icons.Filled.PlayArrow
                            },
                            contentDescription = if (playerState is PlayerState.Playing) "Pause" else "Play",
                        tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    IconButton(
                        onClick = onNextClick,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.SkipNext,
                            contentDescription = "Next",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                }
            }
        }
    }
}

@Composable
fun DraggablePlayerBar(
    currentTrack: Track?,
    playerState: PlayerState,
    progress: Float,
    onPlayerBarClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onTrackProgressChanged: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var isDragging by remember { mutableStateOf(false) }
    var dragProgress by remember { mutableStateOf(progress) }
    
    LaunchedEffect(progress) {
        if (!isDragging) {
            dragProgress = progress
        }
    }
    
    PlayerBar(
        currentTrack = currentTrack,
        playerState = playerState,
        progress = if (isDragging) dragProgress else progress,
        onPlayerBarClick = onPlayerBarClick,
        onPlayPauseClick = onPlayPauseClick,
        onNextClick = onNextClick,
        onPreviousClick = onPreviousClick,
        onTrackProgressChanged = onTrackProgressChanged,
        modifier = modifier
    )
} 