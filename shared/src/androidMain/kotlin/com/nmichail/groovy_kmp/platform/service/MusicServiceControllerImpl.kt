package com.nmichail.groovy_kmp.platform.service

import android.content.Context
import android.content.Intent
import com.nmichail.groovy_kmp.platform.service.MusicPlayerService
import com.nmichail.groovy_kmp.domain.MusicServiceController
import com.nmichail.groovy_kmp.domain.models.Track
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class MusicServiceControllerImpl(private val context: Context) : MusicServiceController {
    override suspend fun play(track: Track) {
        val intent = Intent(context, MusicPlayerService::class.java).apply {
            action = "PLAY"
            putExtra("track_json", Json.encodeToString(track))
        }
        context.startService(intent)
    }
    override suspend fun pause(track: Track) {
        val intent = Intent(context, MusicPlayerService::class.java).apply {
            action = "PAUSE"
            putExtra("track_json", Json.encodeToString(track))
        }
        context.startService(intent)
    }
    override suspend fun resume(track: Track) {
        val intent = Intent(context, MusicPlayerService::class.java).apply {
            action = "RESUME"
            putExtra("track_json", Json.encodeToString(track))
        }
        context.startService(intent)
    }
    override suspend fun next(track: Track) {
        val intent = Intent(context, MusicPlayerService::class.java).apply {
            action = "NEXT"
            putExtra("track_json", Json.encodeToString(track))
        }
        context.startService(intent)
    }
    override suspend fun previous(track: Track) {
        val intent = Intent(context, MusicPlayerService::class.java).apply {
            action = "PREVIOUS"
            putExtra("track_json", Json.encodeToString(track))
        }
        context.startService(intent)
    }
    override suspend fun seekTo(track: Track, position: Long) {
        val intent = Intent(context, MusicPlayerService::class.java).apply {
            action = "SEEK_TO"
            putExtra("track_json", Json.encodeToString(track))
            putExtra("seek_position", position)
        }
        context.startService(intent)
    }
} 