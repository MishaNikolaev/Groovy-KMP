package com.nmichail.groovy_kmp.presentation.screen.player

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
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
import com.nmichail.groovy_kmp.presentation.screen.home.components.Albums.album.AlbumViewModel
import org.koin.mp.KoinPlatform.getKoin

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
    val albumColor = remember(currentTrack?.coverColor, currentTrack?.albumId) {
        currentTrack?.coverColor?.let { Color(it) }
            ?: albumViewModel.getAlbumCoverColor(currentTrack?.albumId)
    }

    var isDragging by remember { mutableStateOf(false) }
    var dragProgress by remember { mutableStateOf(progress) }

    LaunchedEffect(progress) {
        if (!isDragging) {
            dragProgress = progress
        }
    }

    val lightBarColor = androidx.compose.ui.graphics.lerp(albumColor, Color.White, 0.85f)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(5.dp)
            .clickable { onPlayerBarClick() },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(20.dp))
                .background(lightBarColor)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction = progress.coerceIn(0f, 1f))
                    .clip(RoundedCornerShape(20.dp))
                    .background(albumColor.copy(alpha = 0.35f))
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
                        .background(Color.White)
                ) {
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
                Spacer(modifier = Modifier.width(12.dp))
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = currentTrack.title ?: "",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = Color.Black
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.basicMarquee()
                    )
                    Text(
                        text = currentTrack.artist ?: "",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Black.copy(alpha = 0.7f),
                            fontSize = 12.sp
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