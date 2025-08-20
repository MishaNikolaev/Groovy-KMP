package com.nmichail.groovy_kmp.data.repository

import com.nmichail.groovy_kmp.domain.models.PlaybackProgress
import com.nmichail.groovy_kmp.domain.models.PlayerInfo
import com.nmichail.groovy_kmp.domain.models.PlayerState
import com.nmichail.groovy_kmp.domain.models.RepeatMode
import com.nmichail.groovy_kmp.domain.models.Track
import com.nmichail.groovy_kmp.domain.repository.PlayerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlayerRepositoryImpl : PlayerRepository {

    private val scope = CoroutineScope(Dispatchers.Default)

    private val _playerInfo = MutableStateFlow(PlayerInfo())
    override val playerInfo: StateFlow<PlayerInfo> = _playerInfo.asStateFlow()

    private var progressJob: Job? = null

    override suspend fun setPlaylist(tracks: List<Track>, playlistName: String) {
        val currentTrack = tracks.firstOrNull()
        _playerInfo.update {
            it.copy(
                track = currentTrack,
                playlist = tracks,
                playlistName = playlistName
            )
        }

        if (currentTrack != null) {
            play(currentTrack)
        } else {
            stop()
        }
    }

    override suspend fun play(track: Track) {
        if (_playerInfo.value.playlist.none { it.id == track.id }) {
            _playerInfo.update {
                it.copy(
                    playlist = listOf(track),
                    playlistName = "Single Track"
                )
            }
        }

        _playerInfo.update {
            val isSameTrack = it.track?.id == track.id
            val currentPosition = if (isSameTrack) it.progress.currentPosition else 0L
            it.copy(
                state = PlayerState.Playing(track),
                track = track,
                progress = PlaybackProgress(currentPosition = currentPosition, totalDuration = it.progress.totalDuration)
            )
        }
        startProgressTracking()
    }

    override suspend fun pause() {
        val currentTrack = _playerInfo.value.track
        if (_playerInfo.value.state is PlayerState.Paused) {
            println("[PlayerRepositoryImpl] pause() skipped: already paused")
            return
        }
        println("[PlayerRepositoryImpl] pause() called, current state: ${_playerInfo.value.state}, track: ${currentTrack?.title}")
        if (_playerInfo.value.state is PlayerState.Playing && currentTrack != null) {
            _playerInfo.update { it.copy(state = PlayerState.Paused(currentTrack)) }
            stopProgressTracking()
        }
    }

    override suspend fun resume() {
        val currentTrack = _playerInfo.value.track
        println("[PlayerRepositoryImpl] resume() called, current state: ${_playerInfo.value.state}")
        if (_playerInfo.value.state is PlayerState.Paused && currentTrack != null) {
            _playerInfo.update { it.copy(state = PlayerState.Playing(currentTrack)) }
            println("[PlayerRepositoryImpl] state updated to Playing for track: ${currentTrack.title}")
            startProgressTracking()
        }
    }

    override suspend fun stop() {
        stopProgressTracking()
        _playerInfo.value = PlayerInfo()
    }

    override suspend fun skipToNext() {
        val currentPlayerInfo = _playerInfo.value
        val currentPlaylist = currentPlayerInfo.playlist
        if (currentPlaylist.isEmpty()) return

        val currentTrackIndex = currentPlaylist.indexOf(currentPlayerInfo.track)
        if (currentTrackIndex == -1) return

        val nextTrackIndex = if (currentPlayerInfo.isShuffleEnabled) {
            (0 until currentPlaylist.size).filter { it != currentTrackIndex }.random()
        } else {
            (currentTrackIndex + 1) % currentPlaylist.size
        }

        play(currentPlaylist[nextTrackIndex])
    }

    override suspend fun skipToPrevious() {
        val currentPlayerInfo = _playerInfo.value
        val currentPlaylist = currentPlayerInfo.playlist
        if (currentPlaylist.isEmpty()) return

        val currentTrackIndex = currentPlaylist.indexOf(currentPlayerInfo.track)
        if (currentTrackIndex == -1) return

        val previousTrackIndex = (currentTrackIndex - 1 + currentPlaylist.size) % currentPlaylist.size
        play(currentPlaylist[previousTrackIndex])
    }

    override suspend fun seekTo(position: Long) {
        val duration = _playerInfo.value.progress.totalDuration
        if (duration <= 0) return

        val clampedPosition = position.coerceIn(0, duration)
        _playerInfo.update {
            it.copy(progress = it.progress.copy(currentPosition = clampedPosition))
        }
        if (_playerInfo.value.state is PlayerState.Playing) {
            startProgressTracking()
        }
    }

    override suspend fun updateCurrentTrackDuration(duration: Long) {
        println("[PlayerRepositoryImpl] Received duration update request: $duration ms")
        if (duration > 0) {
            println("[PlayerRepositoryImpl] Updating duration: $duration ms")
            _playerInfo.update {
                it.copy(progress = it.progress.copy(totalDuration = duration))
            }
            println("[PlayerRepositoryImpl] Duration updated successfully. New totalDuration: ${_playerInfo.value.progress.totalDuration}")
        } else {
            println("[PlayerRepositoryImpl] Skipping duration update: duration is $duration")
        }
    }

    override suspend fun toggleShuffle() {
        _playerInfo.update { it.copy(isShuffleEnabled = !it.isShuffleEnabled) }
    }

    override suspend fun toggleRepeatMode() {
        val nextMode = when (_playerInfo.value.repeatMode) {
            RepeatMode.None -> RepeatMode.All
            RepeatMode.All -> RepeatMode.One
            RepeatMode.One -> RepeatMode.None
        }
        _playerInfo.update { it.copy(repeatMode = nextMode) }
    }

    fun updateTrackPosition(position: Long) {
        _playerInfo.update {
            it.copy(progress = it.progress.copy(currentPosition = position))
        }
        if (_playerInfo.value.state is PlayerState.Playing) {
            startProgressTracking()
        }
    }

    suspend fun playByIndex(index: Int) {
        val playlist = _playerInfo.value.playlist
        if (playlist.isNotEmpty() && index in playlist.indices) {
            play(playlist[index])
        }
    }

    private fun startProgressTracking() {
        stopProgressTracking()
        println("[PlayerRepositoryImpl] startProgressTracking: currentPosition=${_playerInfo.value.progress.currentPosition}, totalDuration=${_playerInfo.value.progress.totalDuration}")
        val currentInfo = _playerInfo.value
        val currentProgress = currentInfo.progress
        if (currentProgress.currentPosition >= currentProgress.totalDuration && currentProgress.totalDuration > 0) {
            println("[PlayerRepositoryImpl] startProgressTracking: not starting, already at end")
            return
        }
        progressJob = scope.launch {
            while (isActive) {
                delay(1000)
                val currentInfo = _playerInfo.value
                val currentProgress = currentInfo.progress

                if (currentInfo.state !is PlayerState.Playing) continue

                val newPosition = currentProgress.currentPosition + 1000
                if (currentProgress.totalDuration > 0 && newPosition >= currentProgress.totalDuration) {
                    withContext(Dispatchers.Main) {
                        println("[PlayerRepositoryImpl] handleTrackCompletion: newPosition=$newPosition, totalDuration=${currentProgress.totalDuration}")
                        handleTrackCompletion()
                        stopProgressTracking()
                    }
                } else {
                    _playerInfo.update {
                        it.copy(progress = it.progress.copy(currentPosition = newPosition))
                    }
                }
            }
        }
    }

    private fun stopProgressTracking() {
        println("[PlayerRepositoryImpl] stopProgressTracking")
        progressJob?.cancel()
    }

    private suspend fun handleTrackCompletion() {
        val currentInfo = _playerInfo.value
        println("[PlayerRepositoryImpl] handleTrackCompletion: state=${currentInfo.state}, position=${currentInfo.progress.currentPosition}, totalDuration=${currentInfo.progress.totalDuration}")
        when (currentInfo.repeatMode) {
            RepeatMode.One -> {
                seekTo(0)
                currentInfo.track?.let { play(it) }
            }
            RepeatMode.All -> {
                skipToNext()
            }
            RepeatMode.None -> {
                val currentPlaylist = currentInfo.playlist
                val currentTrackIndex = currentPlaylist.indexOf(currentInfo.track)
                if (currentTrackIndex < currentPlaylist.size - 1) {
                    skipToNext()
                } else {
                    if (_playerInfo.value.state is PlayerState.Playing) {
                        println("[PlayerRepositoryImpl] handleTrackCompletion: calling pause() at end of playlist")
                        pause()
                    }
                }
            }
        }
    }
} 