package com.nmichail.groovy_kmp.presentation.screen.home.components.Artists

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nmichail.groovy_kmp.presentation.AlbumFontFamily
import org.jetbrains.compose.resources.DrawableResource


@Composable
fun ArtistsSection(
    title: String,
    artists: List<Pair<String, DrawableResource>>,
    onArtistClick: (String) -> Unit,
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
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    fontFamily = AlbumFontFamily
                ),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.height(32.dp))

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
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            artists.forEach { (name, imageRes) ->
                ArtistCircle(name, imageRes) { onArtistClick(name) }
            }
        }
    }
}