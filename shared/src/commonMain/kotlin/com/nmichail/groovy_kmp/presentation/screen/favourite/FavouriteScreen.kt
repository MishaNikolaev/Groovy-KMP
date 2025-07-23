package com.nmichail.groovy_kmp.presentation.screen.favourite

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
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
import com.nmichail.groovy_kmp.presentation.screen.home.components.Artists.ArtistsSection
import groovy_kmp.shared.generated.resources.*
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class, ExperimentalFoundationApi::class)
@Composable
fun FavouriteScreen() {
    val onHeaderClick = { /* TODO: обработка клика по заголовку */ }
    val tracks = listOf(
        TrackUi("Электричка", "КИНО", Res.drawable.queen_example),
        TrackUi("Here With Me", "d4vd", Res.drawable.avatar),
        TrackUi("We Are The Champions", "Queen", Res.drawable.Queen_The_Miracle_example),
        TrackUi("Another One Bites The Dust", "Queen", Res.drawable.queen_example),
        TrackUi("Somebody to Love", "Queen", Res.drawable.queen_example),
        TrackUi("This is a very long track title to demonstrate the marquee effect", "A-HA", Res.drawable.avatar)
    )
    val chunkedTracks = tracks.chunked(3)
    val pagerState = rememberPagerState(pageCount = { chunkedTracks.size })

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
                    text = "${tracks.size} tracks",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        if (chunkedTracks.isEmpty()) {
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
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(start = 0.dp, end = 40.dp),
                pageSpacing = 16.dp
            ) { page ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    chunkedTracks[page].forEach { track ->
                        TrackCard(track)
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
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
                    .clickable { /* TODO: обработка клика по плейлисту */ }
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
                    .clickable { /* TODO: обработка клика по альбому */ }
            ) {
                Box(
                    modifier = Modifier.size(width = 80.dp, height = 76.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFD3D3D3))
                            .align(Alignment.TopEnd)
                    )
                    Image(
                        painter = painterResource(Res.drawable.Queen_The_Miracle_example),
                        contentDescription = "Альбом",
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
                        text = "Альбомы",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium, fontSize = 18.sp),
                        maxLines = 1,
                        modifier = Modifier.basicMarquee(),
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "The Miracle",
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
            onArtistClick = { /* TODO */ },
            onViewAllClick = { /* TODO */ }
        )
        Spacer(modifier = Modifier.height(18.dp))
    }
}
