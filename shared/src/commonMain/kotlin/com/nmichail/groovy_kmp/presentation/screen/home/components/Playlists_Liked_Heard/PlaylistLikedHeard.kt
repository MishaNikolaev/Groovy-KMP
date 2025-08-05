package com.nmichail.groovy_kmp.presentation.screen.home.components.Playlists_Liked_Heard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nmichail.groovy_kmp.domain.models.Track
import com.nmichail.groovy_kmp.domain.models.Album
import com.nmichail.groovy_kmp.presentation.AlbumFontFamily
import groovy_kmp.shared.generated.resources.Res
import groovy_kmp.shared.generated.resources.playlist_example
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import com.nmichail.groovy_kmp.presentation.screen.home.components.Albums.PlatformImage
import com.nmichail.groovy_kmp.data.local.TrackCache
import com.nmichail.groovy_kmp.data.local.AlbumCache
import kotlinx.coroutines.launch

@Composable
fun PlaylistsLikedHeard(
    onHistoryClick: () -> Unit = {},
    onMyLikesClick: () -> Unit = {},
    lastPlayedTrack: Track? = null
) {
    var likedTracks by remember { mutableStateOf<List<Track>>(emptyList()) }
    var likedAlbums by remember { mutableStateOf<List<Album>>(emptyList()) }
    var lastLikedAlbum by remember { mutableStateOf<Album?>(null) }
    var lastPlayedTrackFromHistory by remember { mutableStateOf<Track?>(null) }
    
    LaunchedEffect(Unit) {
        val cachedTracks = TrackCache.loadTracks()
        val cachedAlbums = AlbumCache.loadAlbums()
        val history = TrackCache.loadHistory()
        
        likedTracks = cachedTracks ?: emptyList()
        likedAlbums = cachedAlbums ?: emptyList()
        lastLikedAlbum = likedAlbums.lastOrNull()
        lastPlayedTrackFromHistory = history?.firstOrNull()

        history?.forEachIndexed { index, track ->
            println("[PlaylistLikedHeard] Track $index: ${track.title}, coverColor: ${track.coverColor}, coverUrl: ${track.coverUrl}, playedAt: ${track.playedAt}")
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PlaylistPreview(
            title = "My likes",
            subtitle = "${likedTracks.size} tracks, ${likedAlbums.size} albums",
            coverUrl = lastLikedAlbum?.coverUrl,
            coverColor = lastLikedAlbum?.coverColor,
            modifier = Modifier.weight(1f),
            onClick = onMyLikesClick
        )
        var localCoverColor by remember(lastPlayedTrackFromHistory?.id) { mutableStateOf<Long?>(lastPlayedTrackFromHistory?.coverColor) }
        PlaylistPreview(
            title = "History",
            subtitle = lastPlayedTrackFromHistory?.artist ?: "No recent tracks",
            coverUrl = lastPlayedTrackFromHistory?.coverUrl,
            coverColor = localCoverColor,
            modifier = Modifier.weight(1f),
            onClick = onHistoryClick,
            onColorExtracted = { color ->
                localCoverColor = color.value.toLong()
                println("[PlaylistLikedHeard] Extracted color for history: ${color.value}")
            }
        )
    }
}

@Composable
fun PlaylistPreview(
    title: String,
    subtitle: String,
    cover: DrawableResource? = null,
    coverUrl: String? = null,
    coverColor: Long? = null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onColorExtracted: ((Color) -> Unit)? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier.size(width = 80.dp, height = 76.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(coverColor?.let { Color(it) } ?: Color(0xFFE5BDBD))
                    .align(Alignment.BottomEnd)
            ) {
                if (coverColor != null) {
                    println("[PlaylistPreview] Using coverColor: $coverColor for title: $title")
                } else {
                    println("[PlaylistPreview] No coverColor for title: $title, using default")
                }
            }
            if (coverUrl != null) {
                PlatformImage(
                    url = coverUrl,
                    contentDescription = title,
                    modifier = Modifier
                        .size(68.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .align(Alignment.TopStart),
                    onColorExtracted = onColorExtracted
                )
            } else if (cover != null) {
                Image(
                    painter = painterResource(cover),
                    contentDescription = title,
                    modifier = Modifier
                        .size(68.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .align(Alignment.TopStart)
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = AlbumFontFamily
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Gray
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

        }
    }
}
