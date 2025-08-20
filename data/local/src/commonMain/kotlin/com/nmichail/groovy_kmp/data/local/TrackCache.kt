package com.nmichail.groovy_kmp.data.local

import com.nmichail.groovy_kmp.domain.models.Track

expect object TrackCache {
    suspend fun saveTracks(tracks: List<Track>)
    suspend fun loadTracks(): List<Track>?
    suspend fun saveHistory(tracks: List<Track>)
    suspend fun loadHistory(): List<Track>?
    suspend fun addToHistory(track: Track)
} 