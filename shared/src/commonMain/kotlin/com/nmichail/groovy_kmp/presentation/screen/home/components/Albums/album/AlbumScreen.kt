package com.nmichail.groovy_kmp.presentation.screen.home.components.Albums.album

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nmichail.groovy_kmp.domain.models.AlbumWithTracks
import com.nmichail.groovy_kmp.domain.models.PlayerState
import com.nmichail.groovy_kmp.domain.models.Track
import com.nmichail.groovy_kmp.presentation.AlbumFontFamily
import com.nmichail.groovy_kmp.presentation.screen.home.components.Albums.AnimatedPlayingIndicator
import com.nmichail.groovy_kmp.presentation.screen.home.components.Albums.PlatformImage
import com.nmichail.groovy_kmp.presentation.screen.player.PlayerViewModel
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform.getKoin
import com.nmichail.groovy_kmp.domain.repository.AlbumRepository
import com.nmichail.groovy_kmp.data.local.AlbumCache
import com.nmichail.groovy_kmp.data.manager.SessionManager

fun generateAlbumColor(url: String?): Color {
    val hash = (url ?: "").hashCode()
    val hue = (hash % 360).toFloat()
    val saturation = when {
        hash % 3 == 0 -> 0.4f
        hash % 3 == 1 -> 0.25f
        else -> 0.15f
    }
    val lightness = when {
        hash % 4 == 0 -> 0.35f
        hash % 4 == 1 -> 0.45f
        hash % 4 == 2 -> 0.55f
        else -> 0.4f
    }
    val c = (1 - kotlin.math.abs(2 * lightness - 1)) * saturation
    val x = c * (1 - kotlin.math.abs((hue / 60) % 2 - 1))
    val m = lightness - c / 2
    val (r, g, b) = when {
        hue < 60 -> Triple(c, x, 0f)
        hue < 120 -> Triple(x, c, 0f)
        hue < 180 -> Triple(0f, c, x)
        hue < 240 -> Triple(0f, x, c)
        hue < 300 -> Triple(x, 0f, c)
        else -> Triple(c, 0f, x)
    }
    return Color(
        red = (r + m).coerceIn(0f, 1f),
        green = (g + m).coerceIn(0f, 1f),
        blue = (b + m).coerceIn(0f, 1f),
        alpha = 0.7f
    )
}


@Composable
fun AlbumScreen(
    albumWithTracks: AlbumWithTracks,
    onBack: () -> Unit,
    onArtistClick: (String) -> Unit,
    onLikeClick: () -> Unit,
    onPlayClick: () -> Unit,
    onPauseClick: () -> Unit,
    onTrackClick: (trackId: String) -> Unit,
    userId: String? = null
) {
    val playerViewModel = remember { getKoin().get<PlayerViewModel>() }
    val playerInfo by playerViewModel.playerInfo.collectAsState()
    val isPlaying = playerInfo.state is PlayerState.Playing
    val currentTrack = playerInfo.track
    val albumRepository = remember { getKoin().get<AlbumRepository>() }
    var isAlbumLiked by remember(albumWithTracks.album.id) { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(albumWithTracks.album.id, userId) {
        if (userId != null && albumWithTracks.album.id != null) {
            val liked = albumRepository.getLikedAlbums(userId).any { it.id == albumWithTracks.album.id }
            isAlbumLiked = liked
        }
    }

    val albumViewModel = remember { getKoin().get<AlbumViewModel>() }
    val backgroundColor = albumViewModel.getBackgroundColor()

    val albumColor = remember(albumWithTracks.album.coverColor) {
        albumWithTracks.album.coverColor?.let { Color(it) } ?: Color(0xFFAAA287)
    }

    key(albumWithTracks.album.id) {
        if (albumColor == Color(0xFFAAA287)) {
            PlatformImage(
                url = albumWithTracks.album.coverUrl,
                contentDescription = null,
                modifier = Modifier.size(1.dp).alpha(0f),
                onColorExtracted = { color ->
                    albumWithTracks.album.id?.let {
                        albumViewModel.setAlbumColor(it, color)
                    }
                }
            )
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(450.dp)
                    .background(albumColor)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                ) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                        .align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.LightGray)
                    ) {
                        PlatformImage(
                            url = albumWithTracks.album.coverUrl,
                            contentDescription = albumWithTracks.album.title,
                            modifier = Modifier.fillMaxSize(),
                            onColorExtracted = { color ->
                                albumWithTracks.album.id?.let {
                                    albumViewModel.setAlbumColor(it, color)
                                }
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = albumWithTracks.album.title ?: "",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = AlbumFontFamily,
                            color = Color.White.copy(alpha = 0.85f)
                        ),
                        maxLines = 2
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 48.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        val author = albumWithTracks.album.artist ?: ""
                        val year = albumWithTracks.album.createdAt ?: ""
                        val authorYear = if (author.isNotBlank() && year.isNotBlank()) "$author · $year" else author + year
                        Row(
                            modifier = Modifier.clickable(enabled = author.isNotBlank()) { albumWithTracks.album.artist?.let { onArtistClick(it) } },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                            ) {
                                PlatformImage(
                                    url = albumWithTracks.album.artistPhotoUrl,
                                    contentDescription = albumWithTracks.album.artist,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = authorYear,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 14.sp,
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontFamily = AlbumFontFamily
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    if (!isAlbumLiked && albumWithTracks.album.id != null) {
                                        albumRepository.likeAlbum(albumWithTracks.album.id)
                                        // Обновить кэш после лайка
                                        // TODO: Получить userId из сессии
                                        val sessionManager = getKoin().get<SessionManager>()
                                        val session = sessionManager.getSession()
                                        val userId = session?.email // Используем email как userId
                                        if (userId != null) {
                                            val liked = albumRepository.getLikedAlbums(userId)
                                            AlbumCache.saveAlbums(liked)
                                        }
                                        isAlbumLiked = true
                                    } else if (isAlbumLiked && albumWithTracks.album.id != null) {
                                        albumRepository.unlikeAlbum(albumWithTracks.album.id)
                                        // Обновить кэш после дизлайка
                                        // TODO: Получить userId из сессии
                                        val sessionManager = getKoin().get<SessionManager>()
                                        val session = sessionManager.getSession()
                                        val userId = session?.email // Используем email как userId
                                        if (userId != null) {
                                            val liked = albumRepository.getLikedAlbums(userId)
                                            AlbumCache.saveAlbums(liked)
                                        }
                                        isAlbumLiked = false
                                    }
                                }
                            },
                            modifier = Modifier
                                .size(64.dp)
                                .background(Color(0x33AAAAAA), shape = CircleShape)
                        ) {
                            Icon(
                                imageVector = if (isAlbumLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = if (isAlbumLiked) "Unlike" else "Like",
                                tint = if (isAlbumLiked) Color.Red else Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(48.dp))
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    val firstTrack = albumWithTracks.tracks.firstOrNull()
                                    if (currentTrack?.id != firstTrack?.id) {
                                        playerViewModel.setPlaylist(albumWithTracks.tracks, albumWithTracks.album.title ?: "Unknown Album")
                                        firstTrack?.let { playerViewModel.play(albumWithTracks.tracks, it) }
                                    } else {
                                        if (isPlaying) {
                                            playerViewModel.pause(albumWithTracks.tracks, currentTrack ?: return@launch)
                                        } else {
                                            playerViewModel.resume(albumWithTracks.tracks, currentTrack ?: return@launch)
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .size(56.dp)
                                .background(Color(0xFFFFD600), shape = CircleShape)
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Color.Black,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(14.dp))
        }

        itemsIndexed(albumWithTracks.tracks) { index, track ->
            TrackRow(
                track = track,
                isPlaying = isPlaying && currentTrack?.id == track.id,
                index = index,
                onClick = {
                    coroutineScope.launch {
                        playerViewModel.setPlaylist(albumWithTracks.tracks, albumWithTracks.album.title ?: "Unknown Album")
                        playerViewModel.play(albumWithTracks.tracks, track)
                    }
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
                    }
    }
}

@Composable
fun TrackRow(track: Track, isPlaying: Boolean, index: Int, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 24.dp)
    ) {
        Box(Modifier.width(32.dp), contentAlignment = Alignment.Center) {
            if (isPlaying) {
                AnimatedPlayingIndicator()
            } else {
                Text(
                    text = "${index + 1}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Gray,
                        fontWeight = FontWeight.Normal,
                        fontSize = 17.sp
                    )
                )
            }
        }
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = track.title ?: "",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp
                )
            )
        }
    }
}