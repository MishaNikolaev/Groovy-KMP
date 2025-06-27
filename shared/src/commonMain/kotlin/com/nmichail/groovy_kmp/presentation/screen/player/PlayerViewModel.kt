package com.nmichail.groovy_kmp.presentation.screen.player

import com.nmichail.groovy_kmp.domain.models.PlayerState
import com.nmichail.groovy_kmp.domain.models.Track
import com.nmichail.groovy_kmp.domain.repository.PlayerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playerRepository: PlayerRepository
) {
    private val viewModelScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    val state: StateFlow<PlayerState> = playerRepository.state
    val currentTrack: StateFlow<Track?> = playerRepository.currentTrack
    val progress: StateFlow<Float> = playerRepository.progress

    fun play(track: Track) {
        viewModelScope.launch { playerRepository.play(track) }
    }
    fun pause() {
        viewModelScope.launch { playerRepository.pause() }
    }
    fun resume() {
        viewModelScope.launch { playerRepository.resume() }
    }
    fun stop() {
        viewModelScope.launch { playerRepository.stop() }
    }
    fun seekTo(position: Float) {
        viewModelScope.launch { playerRepository.seekTo(position) }
    }
} 