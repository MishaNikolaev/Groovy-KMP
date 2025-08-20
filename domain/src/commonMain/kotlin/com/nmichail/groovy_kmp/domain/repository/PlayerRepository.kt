package com.nmichail.groovy_kmp.domain.repository

import com.nmichail.groovy_kmp.domain.models.PlayerInfo
import com.nmichail.groovy_kmp.domain.models.Track
import kotlinx.coroutines.flow.StateFlow

interface PlayerRepository {
    val playerInfo: StateFlow<PlayerInfo>

    suspend fun setPlaylist(tracks: List<Track>, playlistName: String)
    suspend fun play(track: Track)
    suspend fun pause()
    suspend fun resume()
    suspend fun stop()
    suspend fun skipToNext()
    suspend fun skipToPrevious()
    suspend fun seekTo(position: Long)
    suspend fun updateCurrentTrackDuration(duration: Long)
    suspend fun toggleShuffle()
    suspend fun toggleRepeatMode()
} 