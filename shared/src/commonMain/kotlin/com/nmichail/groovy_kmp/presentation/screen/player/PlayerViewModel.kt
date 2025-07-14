package com.nmichail.groovy_kmp.presentation.screen.player

import com.nmichail.groovy_kmp.data.repository.PlayerRepositoryImpl
import com.nmichail.groovy_kmp.domain.MusicServiceController
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
        println("[PlayerViewModel] setPlaylist called with playlistName: $playlistName, tracks: ${tracks.map { it.title }}")
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
        }
    }

    fun play(playlist: List<Track>, track: Track) {
        val index = playlist.indexOfFirst { it.id == track.id }
        musicServiceController.play(playlist, if (index == -1) 0 else index)
        viewModelScope.launch { playerUseCases.playTrack(track) }
    }

    fun pause(playlist: List<Track>, track: Track) {
        val index = playlist.indexOfFirst { it.id == track.id }
        musicServiceController.pause(playlist, if (index == -1) 0 else index)
        viewModelScope.launch { playerUseCases.pauseTrack() }
    }

    fun resume(playlist: List<Track>, track: Track) {
        val index = playlist.indexOfFirst { it.id == track.id }
        musicServiceController.resume(playlist, if (index == -1) 0 else index)
        viewModelScope.launch { playerUseCases.resumeTrack() }
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