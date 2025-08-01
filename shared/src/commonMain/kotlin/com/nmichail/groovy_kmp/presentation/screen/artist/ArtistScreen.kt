package com.nmichail.groovy_kmp.presentation.screen.artist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nmichail.groovy_kmp.domain.models.Album
import com.nmichail.groovy_kmp.domain.models.Track
import com.nmichail.groovy_kmp.presentation.screen.home.components.Albums.PlatformImage
import com.nmichail.groovy_kmp.presentation.screen.player.PlayerViewModel
import org.koin.mp.KoinPlatform.getKoin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistScreen(
    artistName: String,
    onBackClick: () -> Unit,
    onTrackClick: (Track) -> Unit,
    onAlbumClick: (Album) -> Unit,
    onPlayClick: () -> Unit,
    onPauseClick: () -> Unit
) {
    val koin = getKoin()
    val artistViewModel = remember { koin.get<ArtistViewModel>() }
    val playerViewModel = remember { koin.get<PlayerViewModel>() }
    val playerInfo by playerViewModel.playerInfo.collectAsState()
    
    val state by artistViewModel.state.collectAsState()
    
    LaunchedEffect(artistName) {
        artistViewModel.loadArtistData(artistName)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Search */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Поиск")
                    }
                    IconButton(onClick = { /* TODO: More options */ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Еще")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Artist Header Section
            item {
                ArtistHeaderSection(
                    artistName = state.artistName,
                    artistPhotoUrl = state.artistPhotoUrl,
                    onPlayClick = onPlayClick,
                    onPauseClick = onPauseClick
                )
            }
            
            // Popular Tracks Section
            if (state.tracks.isNotEmpty()) {
                item {
                    PopularTracksSection(
                        tracks = state.tracks,
                        onTrackClick = onTrackClick,
                        currentTrack = playerInfo.track,
                        isPlaying = playerInfo.state is com.nmichail.groovy_kmp.domain.models.PlayerState.Playing
                    )
                }
            }
            
            // Albums Section
            if (state.albums.isNotEmpty()) {
                item {
                    AlbumsSection(
                        albums = state.albums,
                        onAlbumClick = onAlbumClick
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
    onPauseClick: () -> Unit
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
                modifier = Modifier.fillMaxSize()
            )
        } ?: run {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Gray)
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
                text = artistName.uppercase(),
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "6 476 726 за месяц",
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        
        // Action Buttons
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Like Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.Gray.copy(alpha = 0.8f))
                        .clickable { /* TODO: Like artist */ },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = "Нравится",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = "8 010 908",
                    color = Color.White,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            
            // Trailer Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.Gray.copy(alpha = 0.8f))
                        .clickable { /* TODO: Show trailer */ },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = "Трейлер",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = "Трейлер",
                    color = Color.White,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            
            // Listen Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.Yellow)
                        .clickable { onPlayClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "Слушать",
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
    
    // Audiobook Link
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { /* TODO: Open audiobook */ },
            shape = RoundedCornerShape(8.dp),
            color = Color.DarkGray
        ) {
            Text(
                text = "Аудиокнига о $artistName",
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
private fun PopularTracksSection(
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit,
    currentTrack: Track?,
    isPlaying: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Популярные треки",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "Показать все",
                modifier = Modifier.clickable { /* TODO: Show all tracks */ }
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        tracks.take(3).forEach { track ->
            TrackRow(
                track = track,
                isCurrentTrack = currentTrack?.id == track.id,
                isPlaying = isPlaying && currentTrack?.id == track.id,
                onClick = { onTrackClick(track) }
            )
        }
    }
}

@Composable
private fun TrackRow(
    track: Track,
    isCurrentTrack: Boolean,
    isPlaying: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
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
        
        // Action Buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = { /* TODO: Like track */ },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = "Нравится",
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
            }
            
            IconButton(
                onClick = onClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    if (isCurrentTrack && isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isCurrentTrack && isPlaying) "Пауза" else "Воспроизвести",
                    tint = Color.Black,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun AlbumsSection(
    albums: List<Album>,
    onAlbumClick: (Album) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Альбомы",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(albums) { album ->
                AlbumCard(
                    album = album,
                    onClick = { onAlbumClick(album) }
                )
            }
        }
    }
}

@Composable
private fun AlbumCard(
    album: Album,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(120.dp)
            .clickable { onClick() }
    ) {
        album.coverUrl?.let { url ->
            PlatformImage(
                url = url,
                contentDescription = album.title,
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        } ?: run {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Gray)
            )
        }
        
        Text(
            text = album.title ?: "",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 4.dp)
        )
        
        Text(
            text = album.artist ?: "",
            fontSize = 10.sp,
            color = Color.Gray,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
} 