package com.nmichail.groovy_kmp.presentation.screen.player

import com.nmichail.groovy_kmp.data.repository.PlayerRepositoryImpl
import com.nmichail.groovy_kmp.domain.MusicServiceController
import com.nmichail.groovy_kmp.domain.models.PlayerInfo
import com.nmichail.groovy_kmp.domain.models.PlayerState
import com.nmichail.groovy_kmp.domain.models.Track
import com.nmichail.groovy_kmp.domain.usecases.PlayerUseCases
import com.nmichail.groovy_kmp.data.local.TrackCache
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class PlayerViewModel(
    private val playerUseCases: PlayerUseCases,
    private val musicServiceController: MusicServiceController
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
            println("[PlayerViewModel] observePlayerInfo: state=${info.state}, position=${info.progress.currentPosition}")
            if (info.state is PlayerState.Playing) println("[PlayerViewModel] STATE = PLAYING for track: ${info.track?.title}")
            if (info.state is PlayerState.Paused) println("[PlayerViewModel] STATE = PAUSED for track: ${info.track?.title}")
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
        val playerInfo = _playerInfo.value
        val duration = playerInfo.progress.totalDuration
        if (duration > 0) {
            val newPosition = (duration * newProgress).toLong()
            _currentPosition.value = newPosition
            val playlist = playerInfo.playlist
            val index = playlist.indexOfFirst { it.id == playerInfo.track?.id }
            musicServiceController.seekTo(playlist, if (index == -1) 0 else index, newPosition)
            viewModelScope.launch {
                playerUseCases.seekTo(newPosition)
            }
        }
    }

    fun play(playlist: List<Track>, track: Track) {
        val index = playlist.indexOfFirst { it.id == track.id }
        musicServiceController.play(playlist, if (index == -1) 0 else index)
        viewModelScope.launch { 
            playerUseCases.playTrack(track)
            try {
                val trackWithTimestamp = track.copy(playedAt = 0L)
                TrackCache.addToHistory(trackWithTimestamp)
                println("[PlayerViewModel] Added track '${track.title}' to history")
            } catch (e: Exception) {
                println("[PlayerViewModel] Error adding track to history: ${e.message}")
            }
        }
    }

    fun pause(playlist: List<Track>, track: Track) {
        println("[PlayerViewModel] pause() called, playlist size: ${playlist.size}, track: ${track.title}")
        val index = playlist.indexOfFirst { it.id == track.id }
        musicServiceController.pause(playlist, if (index == -1) 0 else index)
        viewModelScope.launch { playerUseCases.pauseTrack() }
    }

    fun resume(playlist: List<Track>, track: Track) {
        println("[PlayerViewModel] resume() called, playlist size: ${playlist.size}, track: ${track.title}")
        val index = playlist.indexOfFirst { it.id == track.id }
        musicServiceController.resume(playlist, if (index == -1) 0 else index)
        viewModelScope.launch {
            println("[PlayerViewModel] launching playerUseCases.resumeTrack() for track: ${track.title}")
            playerUseCases.resumeTrack()
        }
    }

    fun stop() {
        viewModelScope.launch {
            playerUseCases.stopTrack()
        }
    }

    fun skipToNext(playlist: List<Track>, track: Track) {
        val index = playlist.indexOfFirst { it.id == track.id }
        val nextIndex = if (index == -1) 0 else (index + 1) % playlist.size
        musicServiceController.next(playlist, nextIndex)
        val nextTrack = playlist.getOrNull(nextIndex)
        if (nextTrack != null) {
            viewModelScope.launch { playerUseCases.playTrack(nextTrack) }
        }
    }

    fun skipToPrevious(playlist: List<Track>, track: Track) {
        val index = playlist.indexOfFirst { it.id == track.id }
        val prevIndex = if (index == -1) 0 else (index - 1 + playlist.size) % playlist.size
        musicServiceController.previous(playlist, prevIndex)
        val prevTrack = playlist.getOrNull(prevIndex)
        if (prevTrack != null) {
            viewModelScope.launch { playerUseCases.playTrack(prevTrack) }
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

    fun updateTrackPosition(position: Long) {
        (playerUseCases as? PlayerRepositoryImpl)?.updateTrackPosition(position)
    }

    fun playFromAlbum(playlist: List<Track>, track: Track, playlistName: String) {
        setPlaylist(playlist, playlistName)
        val index = playlist.indexOfFirst { it.id == track.id }
        musicServiceController.play(playlist, if (index == -1) 0 else index)
    }
}