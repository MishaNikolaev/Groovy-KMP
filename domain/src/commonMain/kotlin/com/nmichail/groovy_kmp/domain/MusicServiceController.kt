package com.nmichail.groovy_kmp.domain

import com.nmichail.groovy_kmp.domain.models.Track

interface MusicServiceController {
    suspend fun play(track: Track)
    suspend fun pause(track: Track)
    suspend fun resume(track: Track)
    suspend fun next(track: Track)
    suspend fun previous(track: Track)
    suspend fun seekTo(track: Track, position: Long)
} 