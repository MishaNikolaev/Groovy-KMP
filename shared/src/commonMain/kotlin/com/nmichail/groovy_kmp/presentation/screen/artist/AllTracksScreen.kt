package com.nmichail.groovy_kmp.presentation.screen.artist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.zIndex
import androidx.compose.ui.layout.ContentScale
import com.nmichail.groovy_kmp.domain.models.Track
import com.nmichail.groovy_kmp.presentation.screen.home.components.Albums.PlatformImage
import com.nmichail.groovy_kmp.presentation.screen.player.PlayerViewModel
import com.nmichail.groovy_kmp.data.local.TrackCache
import com.nmichail.groovy_kmp.presentation.screen.artist.AllTracksViewModel
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform.getKoin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllTracksScreen(
    artistName: String,
    onBackClick: () -> Unit,
    onTrackClick: (Track) -> Unit,
    currentTrack: Track?,
    isPlaying: Boolean
) {
    val koin = getKoin()
    val allTracksViewModel = remember { koin.get<AllTracksViewModel>() }
    val coroutineScope = rememberCoroutineScope()
    var likedTracks by remember { mutableStateOf<List<Track>>(emptyList()) }
    
    val state by allTracksViewModel.state.collectAsState()
    
    LaunchedEffect(artistName) {
        if (artistName.isNotBlank()) {
            try {
                allTracksViewModel.loadTracksByArtist(artistName)
            } catch (e: Exception) {
                println("Error loading tracks: ${e.message}")
                e.printStackTrace()
            }
        }
        // Загружаем лайкнутые треки
        likedTracks = TrackCache.loadTracks() ?: emptyList()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Back button overlay
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .padding(16.dp)
                .zIndex(1f)
        ) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = "Назад",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.Black)
            }
        } else if (state.error != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Ошибка загрузки",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = state.error ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { 
                        coroutineScope.launch {
                            allTracksViewModel.loadTracksByArtist(artistName)
                        }
                    }) {
                        Text("Повторить")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // Artist Header Section
                item {
                    ArtistHeaderSection(
                        artistName = artistName,
                        artistPhotoUrl = state.artistPhotoUrl,
                        onPlayClick = {
                            if (state.tracks.isNotEmpty()) {
                                onTrackClick(state.tracks.first())
                            }
                        },
                        onPauseClick = { /* TODO: Pause */ },
                        isPlaying = isPlaying
                    )
                }
                
                items(state.tracks) { track ->
                    TrackRow(
                        track = track,
                        isCurrentTrack = currentTrack?.id == track.id,
                        isPlaying = isPlaying && currentTrack?.id == track.id,
                        onClick = { onTrackClick(track) },
                        isLiked = likedTracks.any { it.id == track.id },
                        onLikeClick = {
                            coroutineScope.launch {
                                val cached = TrackCache.loadTracks()?.toMutableList() ?: mutableListOf()
                                if (likedTracks.none { it.id == track.id }) {
                                    if (track.id != null && cached.none { it.id == track.id }) {
                                        cached.add(track)
                                        TrackCache.saveTracks(cached)
                                    }
                                    likedTracks = likedTracks + track
                                } else {
                                    val updated = cached.filter { it.id != track.id }
                                    TrackCache.saveTracks(updated)
                                    likedTracks = likedTracks.filter { it.id != track.id }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ArtistHeaderSection(
    artistName: String,
    artistPhotoUrl: String?,
    onPlayClick: () -> Unit,
    onPauseClick: () -> Unit,
    isPlaying: Boolean = false
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        // Artist Photo Background
        artistPhotoUrl?.let { url ->
            PlatformImage(
                url = url,
                contentDescription = artistName,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } ?: run {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            )
        }
        
        // Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
        )
        
        // Artist Info
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text(
                text = artistName,
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        // Action Buttons
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Listen Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.Yellow)
                        .clickable { if (isPlaying) onPauseClick() else onPlayClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Пауза" else "Слушать",
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = "Слушать",
                    color = Color.White,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun TrackRow(
    track: Track,
    isCurrentTrack: Boolean,
    isPlaying: Boolean,
    onClick: () -> Unit,
    isLiked: Boolean = false,
    onLikeClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Album Cover
        track.coverUrl?.let { url ->
            PlatformImage(
                url = url,
                contentDescription = track.title,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
        } ?: run {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Gray)
            )
        }
        
        // Track Info
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        ) {
            Text(
                text = track.title ?: "",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = track.artist ?: "",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
        
        // Like Button
        IconButton(
            onClick = onLikeClick,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                Icons.Default.Favorite,
                contentDescription = "Нравится",
                tint = if (isLiked) Color(0xFFE94057) else Color.Gray,
                modifier = Modifier.size(16.dp)
            )
        }
    }
} 