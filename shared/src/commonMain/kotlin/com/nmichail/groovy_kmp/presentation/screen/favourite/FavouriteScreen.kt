package com.nmichail.groovy_kmp.presentation.screen.favourite

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import groovy_kmp.shared.generated.resources.Res
import groovy_kmp.shared.generated.resources.groovy_logo
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.ui.graphics.vector.ImageVector
import groovy_kmp.shared.generated.resources.Queen_The_Miracle_example
import groovy_kmp.shared.generated.resources.avatar
import groovy_kmp.shared.generated.resources.queen_example
import org.jetbrains.compose.resources.DrawableResource
import androidx.compose.foundation.shape.RoundedCornerShape
import groovy_kmp.shared.generated.resources.like_profile
import groovy_kmp.shared.generated.resources.logo_groovy_vou

@OptIn(ExperimentalResourceApi::class)
@Composable
fun FavouriteScreen() {
    val onHeaderClick = { /* TODO: обработка клика по заголовку */ }
    val tracks = listOf(
        TrackUi("Электричка", "КИНО", Res.drawable.queen_example),
        TrackUi("Here With Me", "d4vd", Res.drawable.avatar),
        TrackUi("We Are The Champions", "Queen", Res.drawable.Queen_The_Miracle_example),
        TrackUi("Another One Bites The Dust", "Queen", Res.drawable.queen_example),
        TrackUi("Somebody to Love", "Queen", Res.drawable.queen_example),
        TrackUi("Song 6", "Artist 6", Res.drawable.avatar)
    )
    val chunkedTracks = tracks.chunked(3)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(34.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onHeaderClick() }
        ) {
            Image(
                painter = painterResource(Res.drawable.like_profile),
                contentDescription = "Heart",
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
                    text = "164 tracks",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 0.dp, end = 16.dp)
        ) {
            items(chunkedTracks) { columnTracks ->
                Column(
                    modifier = Modifier.width(320.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    columnTracks.forEach { track ->
                        TrackCard(track)
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
            }
        }
    }
}

