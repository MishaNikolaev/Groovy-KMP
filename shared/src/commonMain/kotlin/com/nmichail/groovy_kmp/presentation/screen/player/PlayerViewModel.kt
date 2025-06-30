package com.nmichail.groovy_kmp.presentation.screen.player

import com.nmichail.groovy_kmp.domain.models.PlayerInfo
import com.nmichail.groovy_kmp.domain.models.Track
import com.nmichail.groovy_kmp.domain.usecases.PlayerUseCases
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playerUseCases: PlayerUseCases
) {
    private val viewModelScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val _playerInfo = MutableStateFlow(PlayerInfo())
    val playerInfo = _playerInfo.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition = _currentPosition.asStateFlow()

    private val _progress = MutableStateFlow(0f)
    val progress = _progress.asStateFlow()

    init {
        observePlayerInfo()
    }

    fun clear() {
        viewModelScope.cancel()
    }

    private fun observePlayerInfo() {
        playerUseCases.getPlayerInfo().onEach { info ->
            _playerInfo.value = info
            _currentPosition.value = info.progress.currentPosition
            if (info.progress.totalDuration > 0) {
                _progress.value =
                    info.progress.currentPosition.toFloat() / info.progress.totalDuration
            } else {
                _progress.value = 0f
            }
        }.launchIn(viewModelScope)
    }

    fun setPlaylist(tracks: List<Track>, playlistName: String) {
        viewModelScope.launch {
            playerUseCases.setPlaylist(tracks, playlistName)
        }
    }

    fun onTrackProgressChanged(newProgress: Float) {
        viewModelScope.launch {
            val playerInfo = _playerInfo.value
            val duration = playerInfo.progress.totalDuration
            if (duration > 0) {
                val newPosition = (duration * newProgress).toLong()
                _currentPosition.value = newPosition
                playerUseCases.seekTo(newPosition)
            }
        }
    }

    fun play(track: Track) {
        viewModelScope.launch {
            playerUseCases.playTrack(track)
        }
    }

    fun pause() {
        viewModelScope.launch {
            playerUseCases.pauseTrack()
        }
    }

    fun resume() {
        viewModelScope.launch {
            playerUseCases.resumeTrack()
        }
    }

    fun stop() {
        viewModelScope.launch {
            playerUseCases.stopTrack()
        }
    }

    fun skipToNext() {
        viewModelScope.launch {
            playerUseCases.skipToNext()
        }
    }

    fun skipToPrevious() {
        viewModelScope.launch {
            playerUseCases.skipToPrevious()
        }
    }

    fun updateTrackDuration(duration: Long) {
        viewModelScope.launch {
            playerUseCases.updateTrackDuration(duration)
        }
    }

    fun toggleShuffle() {
        viewModelScope.launch {
            playerUseCases.toggleShuffle()
        }
    }

    fun toggleRepeatMode() {
        viewModelScope.launch {
            playerUseCases.toggleRepeatMode()
        }
    }
}