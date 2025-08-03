package com.nmichail.groovy_kmp.presentation.screen.home.components.Albums

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nmichail.groovy_kmp.presentation.AlbumFontFamily

@Composable
expect fun PlatformImage(
    url: String?, 
    contentDescription: String?, 
    modifier: Modifier = Modifier,
    onColorExtracted: ((Color) -> Unit)? = null,
    contentScale: androidx.compose.ui.layout.ContentScale? = null
)

@Composable
fun AlbumsSection(
    albums: List<AlbumUi>,
    onAlbumClick: (AlbumUi) -> Unit,
    onViewAllClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Albums",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    fontFamily = AlbumFontFamily
                ),
                modifier = Modifier.weight(1f)
            )
            Row(
                modifier = Modifier
                    .clickable { onViewAllClick() }
                    .padding(start = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "View All",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Medium,
                        fontFamily = AlbumFontFamily
                    )
                )
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "Expand",
                    tint = Color.DarkGray
                )
            }
            Spacer(modifier = Modifier.height(44.dp))

        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            albums.forEach { album ->
                AlbumCard(album, onClick = { onAlbumClick(album) })
            }
        }
    }
}

data class AlbumUi(
    val title: String,
    val artist: String,
    val coverUrl: String?,
    val id: String? = null
)

@Composable
fun AlbumCard(album: AlbumUi, onClick: () -> Unit) {
    println("AlbumCard: title=${album.title}, artist=${album.artist}, coverUrl=${album.coverUrl}")
    Column(
        modifier = Modifier
            .width(140.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.LightGray)
        ) {
            PlatformImage(album.coverUrl, album.title)
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = album.title,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                fontFamily = AlbumFontFamily
            ),
            maxLines = 1
        )
        Text(
            text = album.artist,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            ),
            maxLines = 1
        )
    }
}
