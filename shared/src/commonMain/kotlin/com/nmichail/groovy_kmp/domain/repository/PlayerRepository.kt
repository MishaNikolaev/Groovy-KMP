package com.nmichail.groovy_kmp.domain.repository

import com.nmichail.groovy_kmp.domain.models.PlayerState
import com.nmichail.groovy_kmp.domain.models.Track
import kotlinx.coroutines.flow.StateFlow

interface PlayerRepository {
    val state: StateFlow<PlayerState>
    val currentTrack: StateFlow<Track?>
    val progress: StateFlow<Float> 

    suspend fun play(track: Track)
    suspend fun pause()
    suspend fun resume()
    suspend fun stop()
    suspend fun seekTo(position: Float) 
} 