package com.nmichail.groovy_kmp.presentation.screen.home.components.Playlists_Liked_Heard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nmichail.groovy_kmp.presentation.AlbumFontFamily
import groovy_kmp.shared.generated.resources.Res
import groovy_kmp.shared.generated.resources.playlist_example
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource


@Composable
fun PlaylistsLikedHeard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PlaylistPreview(
            title = "My likes",
            subtitle = "163 tracks",
            cover = Res.drawable.playlist_example,
            modifier = Modifier.weight(1f),
            onClick = { /* TODO */ }
        )
        PlaylistPreview(
            title = "History",
            subtitle = "d4vd, Al Arifin",
            cover = Res.drawable.playlist_example,
            modifier = Modifier.weight(1f),
            onClick = { /* TODO */ }
        )
    }
}

@Composable
fun PlaylistPreview(
    title: String,
    subtitle: String,
    cover: DrawableResource,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
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
                    .background(Color(0xFFE5BDBD))
                    .align(Alignment.BottomEnd)
            )
            Image(
                painter = painterResource(cover),
                contentDescription = title,
                modifier = Modifier
                    .size(68.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .align(Alignment.TopStart)
            )
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
