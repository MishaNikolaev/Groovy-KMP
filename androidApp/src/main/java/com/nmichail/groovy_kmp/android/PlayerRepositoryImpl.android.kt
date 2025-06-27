package com.nmichail.groovy_kmp.android

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat
import com.nmichail.groovy_kmp.domain.models.PlayerState
import com.nmichail.groovy_kmp.domain.models.Track
import com.nmichail.groovy_kmp.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PlayerRepositoryImpl(private val context: Context) : PlayerRepository {
    private val _state = MutableStateFlow<PlayerState>(PlayerState.Idle)
    override val state: StateFlow<PlayerState> = _state

    private val _currentTrack = MutableStateFlow<Track?>(null)
    override val currentTrack: StateFlow<Track?> = _currentTrack

    private val _progress = MutableStateFlow(0f)
    override val progress: StateFlow<Float> = _progress

    override suspend fun play(track: Track) {
        _currentTrack.value = track
        _state.value = PlayerState.Playing
        val intent = Intent(context, MusicPlayerService::class.java).apply {
            action = MusicPlayerService.ACTION_PLAY
            putExtra(MusicPlayerService.EXTRA_TRACK_URL, track.storagePath)
            putExtra(MusicPlayerService.EXTRA_TRACK_TITLE, track.title)
            putExtra(MusicPlayerService.EXTRA_TRACK_ARTIST, track.artist)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(context, intent)
        } else {
            context.startService(intent)
        }
    }
    override suspend fun pause() {
        _state.value = PlayerState.Paused
        val intent = Intent(context, MusicPlayerService::class.java).apply {
            action = MusicPlayerService.ACTION_PAUSE
        }
        context.startService(intent)
    }
    override suspend fun resume() {
        _state.value = PlayerState.Playing
        val intent = Intent(context, MusicPlayerService::class.java).apply {
            action = MusicPlayerService.ACTION_RESUME
        }
        context.startService(intent)
    }
    override suspend fun stop() {
        _state.value = PlayerState.Idle
        val intent = Intent(context, MusicPlayerService::class.java).apply {
            action = MusicPlayerService.ACTION_STOP
        }
        context.startService(intent)
    }
    override suspend fun seekTo(position: Float) {
        // Можно реализовать передачу позиции через Intent
    }
} 