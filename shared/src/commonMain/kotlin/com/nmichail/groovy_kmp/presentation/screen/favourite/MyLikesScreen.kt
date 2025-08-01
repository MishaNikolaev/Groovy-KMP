package com.nmichail.groovy_kmp.presentation.screen.favourite

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import com.nmichail.groovy_kmp.domain.models.Album
import com.nmichail.groovy_kmp.data.local.TrackCache
import com.nmichail.groovy_kmp.data.local.AlbumCache
import com.nmichail.groovy_kmp.domain.repository.TrackRepository
import com.nmichail.groovy_kmp.domain.repository.AlbumRepository
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.shape.CircleShape

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MyLikesScreen(
    onBackClick: () -> Unit,
    onAlbumClick: (String) -> Unit = {}
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
                text = "My Liked Tracks",
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
                                try {
                                trackRepository.unlikeTrack(track.id!!)
                                likedTracks = likedTracks.filter { it.id != track.id }
                                TrackCache.saveTracks(likedTracks)
                                } catch (e: Exception) {
                                    println("Error unliking track: ${e.message}")
                                }
                            }
                        },
                        onTrackClick = {
                            val playerViewModel = getKoin().get<PlayerViewModel>()
                            coroutineScope.launch {
                                try {
                                playerViewModel.setPlaylist(likedTracks, "Liked Tracks")
                                playerViewModel.play(likedTracks, track)
                                } catch (e: Exception) {
                                    println("Error playing track: ${e.message}")
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

@Composable
fun LikedAlbumCard(
    album: Album,
    onLikeClick: () -> Unit,
    onAlbumClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAlbumClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.LightGray)
        ) {
            PlatformImage(
                url = album.coverUrl,
                contentDescription = album.title,
                modifier = Modifier.fillMaxSize(),
                onColorExtracted = null
            )
            
            // Кнопка лайка поверх обложки
            IconButton(
                onClick = onLikeClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(32.dp)
                    .background(Color(0x80000000), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Unlike",
                    tint = Color(0xFFE94057),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = album.title ?: "",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            maxLines = 2,
            textAlign = TextAlign.Center
        )
        Text(
            text = album.artist ?: "",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.Gray,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            ),
            maxLines = 1,
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MyLikedAlbumsScreen(
    onBackClick: () -> Unit,
    onAlbumClick: (String) -> Unit = {}
) {
    var likedAlbums by remember { mutableStateOf<List<Album>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    
    val albumRepository = remember { getKoin().get<AlbumRepository>() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val cachedAlbums = AlbumCache.loadAlbums()
                likedAlbums = cachedAlbums ?: emptyList()
            } catch (e: Exception) {
                likedAlbums = emptyList()
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
                text = "My Liked Albums",
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
        } else if (likedAlbums.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "You have no favourite albums",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(bottom = 40.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(likedAlbums) { album ->
                    LikedAlbumCard(
                        album = album,
                        onLikeClick = {
                            coroutineScope.launch {
                                try {
                                    albumRepository.unlikeAlbum(album.id!!)
                                    likedAlbums = likedAlbums.filter { it.id != album.id }
                                    AlbumCache.saveAlbums(likedAlbums)
                                } catch (e: Exception) {
                                    println("Error unliking album: ${e.message}")
                                }
                            }
                        },
                        onAlbumClick = {
                            album.id?.let { onAlbumClick(it) }
                        }
                    )
                }
            }
        }
    }
} 