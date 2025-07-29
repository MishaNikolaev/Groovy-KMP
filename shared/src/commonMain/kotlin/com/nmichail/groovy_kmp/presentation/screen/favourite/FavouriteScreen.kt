package com.nmichail.groovy_kmp.presentation.screen.favourite

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.nmichail.groovy_kmp.presentation.screen.home.components.Artists.ArtistsSection
import com.nmichail.groovy_kmp.presentation.screen.home.components.Albums.PlatformImage
import com.nmichail.groovy_kmp.domain.models.Track
import com.nmichail.groovy_kmp.domain.models.Album
import com.nmichail.groovy_kmp.data.local.TrackCache
import com.nmichail.groovy_kmp.data.local.AlbumCache
import com.nmichail.groovy_kmp.domain.repository.TrackRepository
import com.nmichail.groovy_kmp.domain.repository.AlbumRepository
import org.koin.mp.KoinPlatform.getKoin
import kotlinx.coroutines.launch
import groovy_kmp.shared.generated.resources.*
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import com.nmichail.groovy_kmp.presentation.screen.player.PlayerViewModel
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.zIndex
import com.nmichail.groovy_kmp.data.manager.SessionManager

@OptIn(ExperimentalFoundationApi::class, ExperimentalResourceApi::class)
@Composable
fun FavouriteScreen(
    onMyLikesClick: () -> Unit = {},
    onAlbumsClick: () -> Unit = {}
) {
    var likedTracks by remember { mutableStateOf<List<Track>>(emptyList()) }
    var likedAlbums by remember { mutableStateOf<List<Album>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    
    val trackRepository = remember { getKoin().get<TrackRepository>() }
    val albumRepository = remember { getKoin().get<AlbumRepository>() }
    val sessionManager = remember { getKoin().get<SessionManager>() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val cachedTracks = TrackCache.loadTracks()
                val cachedAlbums = AlbumCache.loadAlbums()
                
                likedTracks = cachedTracks ?: emptyList()
                likedAlbums = cachedAlbums ?: emptyList()
                
                println("[FavouriteScreen] Loaded ${likedTracks.size} liked tracks and ${likedAlbums.size} liked albums from cache")
                
            } catch (e: Exception) {
                println("Error loading liked content from cache: ${e.message}")
                likedTracks = emptyList()
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
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(34.dp))
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onMyLikesClick() }
        ) {
            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = "Heart",
                tint = Color(0xFFE94057),
                modifier = Modifier.size(56.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "My likes",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowRight,
                        contentDescription = "Go",
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${likedTracks.size} tracks, ${likedAlbums.size} albums",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFFE94057))
            }
        } else if (likedTracks.isEmpty() && likedAlbums.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "You have no favourite tracks or albums",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )
            }
        } else {
            if (likedTracks.isNotEmpty()) {
                Text(
                    text = "Liked Tracks",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(horizontal = 0.dp)
                ) {
                    val chunkedTracks = likedTracks.chunked(3)
                    items(chunkedTracks.size) { columnIndex ->
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.width(280.dp)
                        ) {
                            chunkedTracks[columnIndex].forEach { track ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            val playerViewModel = getKoin().get<PlayerViewModel>()
                                            coroutineScope.launch {
                                                playerViewModel.setPlaylist(likedTracks, "Liked Tracks")
                                                playerViewModel.play(likedTracks, track)
                                            }
                                        }
                                        .padding(vertical = 8.dp, horizontal = 12.dp)
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
                                    IconButton(
                                        onClick = {
                                            coroutineScope.launch {
                                                trackRepository.unlikeTrack(track.id!!)
                                                likedTracks = likedTracks.filter { it.id != track.id }
                                                TrackCache.saveTracks(likedTracks)
                                            }
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Favorite,
                                            contentDescription = "Unlike",
                                            tint = Color(0xFFE94057),
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        Spacer(modifier = Modifier.height(36.dp))
        Text(
            text = "Also in your collection",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(18.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { }
            ) {
                Box(
                    modifier = Modifier.size(width = 80.dp, height = 76.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFE5BDBD))
                            .align(Alignment.TopEnd)
                    )
                    Image(
                        painter = painterResource(Res.drawable.avatar),
                        contentDescription = "Плейлист",
                        modifier = Modifier
                            .size(68.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .align(Alignment.BottomStart)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Text(
                        text = "Плейлисты",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium, fontSize = 18.sp),
                        maxLines = 1,
                        modifier = Modifier.basicMarquee(),
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "В автобус, В зал, Работа",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray, fontSize = 15.sp),
                        maxLines = 2
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAlbumsClick() }
            ) {
                Box(
                    modifier = Modifier.size(width = 80.dp, height = 76.dp)
                ) {
                    val lastLikedAlbum = likedAlbums.lastOrNull()
                    val backgroundColor = lastLikedAlbum?.coverColor?.let { Color(it) } ?: Color(0xFFD3D3D3)
                    
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(backgroundColor)
                            .align(Alignment.TopEnd)
                    )
                    
                    if (lastLikedAlbum != null) {
                        PlatformImage(
                            url = lastLikedAlbum.coverUrl,
                            contentDescription = "Альбом",
                            modifier = Modifier
                                .size(68.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .align(Alignment.BottomStart)
                        )
                    } else {
                        Image(
                            painter = painterResource(Res.drawable.Queen_The_Miracle_example),
                            contentDescription = "Альбом",
                            modifier = Modifier
                                .size(68.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .align(Alignment.BottomStart)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Text(
                        text = "Albums",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium, fontSize = 18.sp),
                        maxLines = 1,
                        modifier = Modifier.basicMarquee(),
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = likedAlbums.lastOrNull()?.title ?: "The Miracle",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray, fontSize = 15.sp),
                        maxLines = 2
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(36.dp))
        ArtistsSection(
            title = "Most listened to artists",
            artists = listOf(
                Pair("Queen", Res.drawable.queen_example),
                Pair("Wham", Res.drawable.wham_example),
                Pair("Queen", Res.drawable.queen_example)
            ),
            onArtistClick = { },
            onViewAllClick = { }
        )
        Spacer(modifier = Modifier.height(18.dp))
    }
}

@Composable
fun LikedTrackRow(
    track: Track,
    onLikeClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { /* TODO: play track */ }
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp))
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
fun LikedAlbumRow(
    album: Album,
    onLikeClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { /* TODO: navigate to album */ }
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF2F2F2)),
            contentAlignment = Alignment.Center
        ) {
            PlatformImage(
                url = album.coverUrl,
                contentDescription = album.title,
                modifier = Modifier.fillMaxSize(),
                onColorExtracted = null
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = album.title ?: "",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = album.artist ?: "",
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
