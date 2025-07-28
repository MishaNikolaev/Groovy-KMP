package com.nmichail.groovy_kmp.presentation.screen.favourite

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nmichail.groovy_kmp.presentation.screen.home.components.Albums.PlatformImage
import com.nmichail.groovy_kmp.domain.models.Track
import com.nmichail.groovy_kmp.data.local.TrackCache
import com.nmichail.groovy_kmp.domain.repository.TrackRepository
import com.nmichail.groovy_kmp.presentation.screen.player.PlayerViewModel
import org.koin.mp.KoinPlatform.getKoin
import kotlinx.coroutines.launch
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.zIndex

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MyLikesScreen(
    onBackClick: () -> Unit
) {
    var likedTracks by remember { mutableStateOf<List<Track>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    
    val trackRepository = remember { getKoin().get<TrackRepository>() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val cachedTracks = TrackCache.loadTracks()
                likedTracks = cachedTracks ?: emptyList()
            } catch (e: Exception) {
                likedTracks = emptyList()
            } finally {
                isLoading = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(34.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "My Likes",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFFE94057))
            }
        } else if (likedTracks.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "You have no favourite tracks",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 40.dp)
            ) {
                items(likedTracks) { track ->
                    LikedTrackRow(
                        track = track,
                        onLikeClick = {
                            coroutineScope.launch {
                                trackRepository.unlikeTrack(track.id!!)
                                likedTracks = likedTracks.filter { it.id != track.id }
                                TrackCache.saveTracks(likedTracks)
                            }
                        },
                        onTrackClick = {
                            val playerViewModel = getKoin().get<PlayerViewModel>()
                            coroutineScope.launch {
                                playerViewModel.setPlaylist(likedTracks, "Liked Tracks")
                                playerViewModel.play(likedTracks, track)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LikedTrackRow(
    track: Track,
    onLikeClick: () -> Unit,
    onTrackClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onTrackClick() }
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF2F2F2)),
            contentAlignment = Alignment.Center
        ) {
            PlatformImage(
                url = track.coverUrl,
                contentDescription = track.title,
                modifier = Modifier.fillMaxSize(),
                onColorExtracted = null
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = track.title ?: "",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = track.artist ?: "",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Gray
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        IconButton(onClick = onLikeClick) {
            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = "Unlike",
                tint = Color(0xFFE94057),
                modifier = Modifier.size(28.dp)
            )
        }
    }
} 