package com.nmichail.groovy_kmp.presentation.screen.player

import com.nmichail.groovy_kmp.domain.MusicServiceController
import com.nmichail.groovy_kmp.domain.models.PlayerInfo
import com.nmichail.groovy_kmp.domain.models.PlayerState
import com.nmichail.groovy_kmp.domain.models.Track
import com.nmichail.groovy_kmp.domain.usecases.PlayerUseCases
import com.nmichail.groovy_kmp.domain.repository.TrackRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class PlayerViewModel(
    private val playerUseCases: PlayerUseCases,
    private val musicServiceController: MusicServiceController,
    private val trackRepository: TrackRepository
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
            println("[PlayerViewModel] observePlayerInfo: state=${info.state}, position=${info.progress.currentPosition}, duration=${info.progress.totalDuration}")
            if (info.state is PlayerState.Playing) println("[PlayerViewModel] STATE = PLAYING for track: ${info.track?.title}")
            if (info.state is PlayerState.Paused) println("[PlayerViewModel] STATE = PAUSED for track: ${info.track?.title}")
            _playerInfo.value = info
            _currentPosition.value = info.progress.currentPosition
            if (info.progress.totalDuration > 0) {
                _progress.value =
                    info.progress.currentPosition.toFloat() / info.progress.totalDuration
                println("[PlayerViewModel] Progress calculated: ${_progress.value} (${info.progress.currentPosition}/${info.progress.totalDuration})")
            } else {
                _progress.value = 0f
                println("[PlayerViewModel] Duration is 0, progress set to 0")
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
            val currentTrack = playerInfo.track
            if (currentTrack != null) {
                viewModelScope.launch {
                    musicServiceController.seekTo(currentTrack, newPosition)
                    playerUseCases.seekTo(newPosition)
                }
            }
        }
    }

    fun play(playlist: List<Track>, track: Track) {
        // Сбрасываем прогресс при начале воспроизведения нового трека
        _currentPosition.value = 0L
        _progress.value = 0f
        
        viewModelScope.launch { 
            musicServiceController.play(track)
            playerUseCases.playTrack(track)
            
            // Add track to history
            try {
                trackRepository.addToHistory(track)
                println("[PlayerViewModel] Added track to history: ${track.title}")
            } catch (e: Exception) {
                println("[PlayerViewModel] Error adding track to history: ${e.message}")
            }
        }
    }

    fun pause(playlist: List<Track>, track: Track) {
        println("[PlayerViewModel] pause() called, playlist size: ${playlist.size}, track: ${track.title}")
        viewModelScope.launch { 
            musicServiceController.pause(track)
            playerUseCases.pauseTrack()
        }
    }

    fun resume(playlist: List<Track>, track: Track) {
        println("[PlayerViewModel] resume() called, playlist size: ${playlist.size}, track: ${track.title}")
        viewModelScope.launch {
            musicServiceController.resume(track)
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
        val nextTrack = playlist.getOrNull(nextIndex)
        if (nextTrack != null) {
            // Сбрасываем прогресс при переходе к следующему треку
            _currentPosition.value = 0L
            _progress.value = 0f
            viewModelScope.launch { 
                musicServiceController.next(track)
                playerUseCases.playTrack(nextTrack)
                
                // Add track to history
                try {
                    trackRepository.addToHistory(nextTrack)
                    println("[PlayerViewModel] Added track to history (next): ${nextTrack.title}")
                } catch (e: Exception) {
                    println("[PlayerViewModel] Error adding track to history (next): ${e.message}")
                }
            }
        }
    }

    fun skipToPrevious(playlist: List<Track>, track: Track) {
        val index = playlist.indexOfFirst { it.id == track.id }
        val prevIndex = if (index == -1) 0 else (index - 1 + playlist.size) % playlist.size
        val prevTrack = playlist.getOrNull(prevIndex)
        if (prevTrack != null) {
            // Сбрасываем прогресс при переходе к предыдущему треку
            _currentPosition.value = 0L
            _progress.value = 0f
            viewModelScope.launch { 
                musicServiceController.previous(track)
                playerUseCases.playTrack(prevTrack)
                
                // Add track to history
                try {
                    trackRepository.addToHistory(prevTrack)
                    println("[PlayerViewModel] Added track to history (previous): ${prevTrack.title}")
                } catch (e: Exception) {
                    println("[PlayerViewModel] Error adding track to history (previous): ${e.message}")
                }
            }
        }
    }

    fun updateTrackDuration(duration: Long) {
        val currentTrack = _playerInfo.value.track
        println("[PlayerViewModel] updateTrackDuration called: duration=$duration ms, track=${currentTrack?.title}")
        if (currentTrack != null && duration > 0) {
            println("[PlayerViewModel] Updating track duration: $duration ms for track: ${currentTrack.title}")
            viewModelScope.launch {
                playerUseCases.updateTrackDuration(duration)
                println("[PlayerViewModel] Duration update completed for track: ${currentTrack.title}")
            }
        } else {
            println("[PlayerViewModel] Skipping duration update: duration=$duration, track=${currentTrack?.title}")
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
        // PlayerRepositoryImpl reference removed - should be handled through proper interface
    }

    fun playFromAlbum(playlist: List<Track>, track: Track, playlistName: String) {
        setPlaylist(playlist, playlistName)
        
        _currentPosition.value = 0L
        _progress.value = 0f
        
        viewModelScope.launch {
            musicServiceController.play(track)
        }
    }
}