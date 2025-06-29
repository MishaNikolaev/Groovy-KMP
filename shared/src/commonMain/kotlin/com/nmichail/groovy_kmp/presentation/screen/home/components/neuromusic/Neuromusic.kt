package com.nmichail.groovy_kmp.presentation.screen.home.components.neuromusic

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import com.nmichail.groovy_kmp.presentation.AlbumFontFamily


@Composable
fun GenreCard(text: String, brush: Brush, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(32.dp))
            .clickable { /* TODO: handle genre click */ },
        color = Color.Transparent,
        shadowElevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush, shape = RoundedCornerShape(32.dp)),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        fontFamily = AlbumFontFamily
                    ),
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun GenresCarousel() {
    val genreList = listOf(
        Triple("Calmness", Brush.horizontalGradient(listOf(Color(0xFF4A90E2), Color(0xFF50E3C2))), 0),
        Triple("Inspiration", Brush.horizontalGradient(listOf(Color(0xFFB36AE2), Color(0xFF50E3C2))), 1),
        Triple("Focus", Brush.horizontalGradient(listOf(Color(0xFF50E3C2), Color(0xFF4A90E2))), 2)
    )
    val cardWidth = 320.dp
    val cardSpacing = 16.dp
    val listState = rememberLazyListState()
    val centerIndex = 1

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
    ) {
        val boxWidthPx = with(LocalDensity.current) { maxWidth.toPx() }
        val cardWidthPx = with(LocalDensity.current) { cardWidth.toPx() }
        val visibleEdge = 32.dp
        val visibleEdgePx = with(LocalDensity.current) { visibleEdge.toPx() }
        val horizontalPadding = ((boxWidthPx - cardWidthPx) / 2 - visibleEdgePx).coerceAtLeast(0f)
        val horizontalPaddingDp = with(LocalDensity.current) { horizontalPadding.toDp() }

        LaunchedEffect(Unit) {
            listState.scrollToItem(centerIndex)
        }

        LazyRow(
            state = listState,
            contentPadding = PaddingValues(horizontal = horizontalPaddingDp),
            horizontalArrangement = Arrangement.spacedBy(cardSpacing)
        ) {
            items(genreList.size) { idx ->
                val (text, brush, _) = genreList[idx]
                GenreCard(
                    text = text,
                    brush = brush,
                    modifier = Modifier
                        .width(cardWidth)
                        .height(64.dp)
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(32.dp))
}