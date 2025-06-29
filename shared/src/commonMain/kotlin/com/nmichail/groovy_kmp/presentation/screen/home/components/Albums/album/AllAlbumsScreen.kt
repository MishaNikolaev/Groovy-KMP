package com.nmichail.groovy_kmp.presentation.screen.home.components.Albums.album

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nmichail.groovy_kmp.presentation.screen.home.HomeViewModel
import org.koin.mp.KoinPlatform.getKoin
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.text.style.TextAlign
import com.nmichail.groovy_kmp.presentation.AlbumFontFamily
import com.nmichail.groovy_kmp.presentation.screen.home.components.Albums.AlbumUi
import com.nmichail.groovy_kmp.presentation.screen.home.components.Albums.PlatformImage

@Composable
fun AllAlbumsScreen(
    onBack: () -> Unit,
    onAlbumClick: (AlbumUi) -> Unit
) {
    val viewModel = remember { getKoin().get<HomeViewModel>() }
    val albums by viewModel.albums.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    
    LaunchedEffect(viewModel) {
        viewModel.load()
    }
    
    val filteredAlbums = albums
        .map { AlbumUi(it.title ?: "", it.artist ?: "", it.coverUrl, it.id) }
        .filter { album ->
            searchQuery.isEmpty() || 
            album.title.contains(searchQuery, ignoreCase = true) ||
            album.artist.contains(searchQuery, ignoreCase = true)
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "All Albums",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    fontFamily = AlbumFontFamily
                )
            )
        }
        
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text("Search albums...") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFE94057),
                unfocusedBorderColor = Color.Gray
            ),
            shape = RoundedCornerShape(12.dp)
        )
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(filteredAlbums) { album ->
                AlbumGridCard(album = album, onClick = { onAlbumClick(album) })
            }
        }
    }
}

@Composable
fun AlbumGridCard(album: AlbumUi, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.LightGray)
        ) {
            PlatformImage(album.coverUrl, album.title)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = album.title,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = AlbumFontFamily
            ),
            maxLines = 2,
            textAlign = TextAlign.Center
        )
        Text(
            text = album.artist,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.Gray,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            ),
            maxLines = 1,
            textAlign = TextAlign.Center
        )
    }
} 