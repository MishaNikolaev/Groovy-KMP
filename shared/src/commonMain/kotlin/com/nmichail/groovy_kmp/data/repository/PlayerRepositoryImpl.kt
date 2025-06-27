package com.nmichail.groovy_kmp.data.repository

import com.nmichail.groovy_kmp.domain.models.PlayerState
import com.nmichail.groovy_kmp.domain.models.Track
import com.nmichail.groovy_kmp.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PlayerRepositoryImpl : PlayerRepository {
    private val _state = MutableStateFlow<PlayerState>(PlayerState.Idle)
    override val state: StateFlow<PlayerState> = _state

    private val _currentTrack = MutableStateFlow<Track?>(null)
    override val currentTrack: StateFlow<Track?> = _currentTrack

    private val _progress = MutableStateFlow(0f)
    override val progress: StateFlow<Float> = _progress

    override suspend fun play(track: Track) {
        _currentTrack.value = track
        _state.value = PlayerState.Playing
        _progress.value = 0f
    }

    override suspend fun pause() {
        _state.value = PlayerState.Paused
    }

    override suspend fun resume() {
        _state.value = PlayerState.Playing
    }

    override suspend fun stop() {
        _state.value = PlayerState.Idle
        _currentTrack.value = null
        _progress.value = 0f
    }

    override suspend fun seekTo(position: Float) {
        _progress.value = position
    }
} 