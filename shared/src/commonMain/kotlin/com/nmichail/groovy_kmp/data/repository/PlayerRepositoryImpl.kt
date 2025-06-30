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
            it.copy(
                state = PlayerState.Playing(track),
                track = track,
                progress = PlaybackProgress(currentPosition = 0L, totalDuration = it.progress.totalDuration)
            )
        }
        startProgressTracking()
    }

    override suspend fun pause() {
        val currentTrack = _playerInfo.value.track
        if (_playerInfo.value.state is PlayerState.Playing && currentTrack != null) {
            _playerInfo.update { it.copy(state = PlayerState.Paused(currentTrack)) }
            stopProgressTracking()
        }
    }

    override suspend fun resume() {
        val currentTrack = _playerInfo.value.track
        if (_playerInfo.value.state is PlayerState.Paused && currentTrack != null) {
            _playerInfo.update { it.copy(state = PlayerState.Playing(currentTrack)) }
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
    }

    override suspend fun updateCurrentTrackDuration(duration: Long) {
        if (duration > 0) {
            _playerInfo.update {
                it.copy(progress = it.progress.copy(totalDuration = duration))
            }
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

    private fun startProgressTracking() {
        stopProgressTracking()
        progressJob = scope.launch {
            while (isActive) {
                delay(1000)
                val currentInfo = _playerInfo.value
                val currentProgress = currentInfo.progress

                if (currentInfo.state !is PlayerState.Playing) continue

                val newPosition = currentProgress.currentPosition + 1000
                if (currentProgress.totalDuration > 0 && newPosition >= currentProgress.totalDuration) {
                    withContext(Dispatchers.Main) {
                        handleTrackCompletion()
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
        progressJob?.cancel()
    }

    private suspend fun handleTrackCompletion() {
        val currentInfo = _playerInfo.value
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
                    pause()
                }
            }
        }
    }
}