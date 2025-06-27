package com.nmichail.groovy_kmp.presentation.screen.player

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nmichail.groovy_kmp.domain.models.PlayerState
import org.koin.mp.KoinPlatform.getKoin
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause

@Composable
fun PlayerBar(
    modifier: Modifier = Modifier
) {
    val viewModel = getKoin().get<PlayerViewModel>()
    val track by viewModel.currentTrack.collectAsState()
    val state by viewModel.state.collectAsState()
    val progress by viewModel.progress.collectAsState()

    if (track != null) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(Color(0xFFF8F8FC))
                .padding(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(track?.title ?: "", style = MaterialTheme.typography.bodyLarge)
                    Text(track?.artist ?: "", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                IconButton(onClick = {
                    when (state) {
                        PlayerState.Playing -> viewModel.pause()
                        PlayerState.Paused, PlayerState.Idle, PlayerState.Completed -> viewModel.resume()
                        else -> {}
                    }
                }) {
                    if (state == PlayerState.Playing) {
                        Icon(Icons.Filled.Pause, contentDescription = "Pause")
                    } else {
                        Icon(Icons.Filled.PlayArrow, contentDescription = "Play")
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(Color.LightGray)
                    .align(Alignment.BottomStart)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .height(2.dp)
                        .background(Color(0xFFE94057))
                )
            }
        }
    }
} 