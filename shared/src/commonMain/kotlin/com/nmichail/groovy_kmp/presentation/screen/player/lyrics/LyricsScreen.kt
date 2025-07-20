package com.nmichail.groovy_kmp.presentation.screen.player.lyrics

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
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
import com.nmichail.groovy_kmp.domain.models.Track
import com.nmichail.groovy_kmp.presentation.AlbumFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LyricsScreen(
    track: Track,
    currentPosition: Long,
    isPlaying: Boolean,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onSeek: (Float) -> Unit,
    onClose: () -> Unit
) {
    val lyrics = track.lyrics?.lines ?: emptyList()
    val activeIndex = remember(currentPosition, lyrics) {
        lyrics.indexOfLast { it.timeMs <= currentPosition }
    }
    val albumColor = remember(track.coverColor) {
        track.coverColor?.let { Color(it) } ?: Color.Black
    }
    val duration = track.duration?.toLong() ?: 0L
    val progress = if (duration > 0) currentPosition.toFloat() / duration else 0f
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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(albumColor)
    ) {
        // Верхняя панель: кнопка назад
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 8.dp, end = 8.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        }
        // Центр: крупный текст лирики
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val visibleLines = (-1..1).map { offset -> activeIndex + offset }
            visibleLines.forEach { idx ->
                val line = lyrics.getOrNull(idx)
                if (line != null) {
                    val isActive = idx == activeIndex
                    Text(
                        text = line.text,
                        fontFamily = AlbumFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = if (isActive) 36.sp else 24.sp,
                        color = if (isActive) Color.White else Color.White.copy(alpha = 0.4f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }
        // Низ: прогресс и кнопки управления
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Прогресс
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
            // Время
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatTime(if (isDragging) (dragProgress * duration).toLong() else currentPosition),
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                )
                Text(
                    text = formatTime(duration),
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Кнопки управления
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
                        imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
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