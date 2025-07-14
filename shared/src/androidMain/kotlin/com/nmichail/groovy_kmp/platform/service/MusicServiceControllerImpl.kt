package com.nmichail.groovy_kmp.platform.service

import android.content.Context
import android.content.Intent
import com.nmichail.groovy_kmp.platform.service.MusicPlayerService
import com.nmichail.groovy_kmp.domain.MusicServiceController
import com.nmichail.groovy_kmp.domain.models.Track
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class MusicServiceControllerImpl(private val context: Context) : MusicServiceController {
    override fun play(playlist: List<Track>, index: Int) {
        val playlistJson = Json.encodeToString(playlist)
        val intent = Intent(context, MusicPlayerService::class.java).apply {
            action = "PLAY"
            putExtra("playlist_json", playlistJson)
            putExtra("track_index", index)
        }
        context.startService(intent)
    }
    override fun pause(playlist: List<Track>, index: Int) {
        val playlistJson = Json.encodeToString(playlist)
        val intent = Intent(context, MusicPlayerService::class.java).apply {
            action = "PAUSE"
            putExtra("playlist_json", playlistJson)
            putExtra("track_index", index)
        }
        context.startService(intent)
    }
    override fun resume(playlist: List<Track>, index: Int) {
        val playlistJson = Json.encodeToString(playlist)
        val intent = Intent(context, MusicPlayerService::class.java).apply {
            action = "RESUME"
            putExtra("playlist_json", playlistJson)
            putExtra("track_index", index)
        }
        context.startService(intent)
    }
    override fun next(playlist: List<Track>, index: Int) {
        val playlistJson = Json.encodeToString(playlist)
        val intent = Intent(context, MusicPlayerService::class.java).apply {
            action = "NEXT"
            putExtra("playlist_json", playlistJson)
            putExtra("track_index", index)
        }
        context.startService(intent)
    }
    override fun previous(playlist: List<Track>, index: Int) {
        val playlistJson = Json.encodeToString(playlist)
        val intent = Intent(context, MusicPlayerService::class.java).apply {
            action = "PREVIOUS"
            putExtra("playlist_json", playlistJson)
            putExtra("track_index", index)
        }
        context.startService(intent)
    }
    override fun seekTo(playlist: List<Track>, index: Int, position: Long) {
        val playlistJson = Json.encodeToString(playlist)
        val intent = Intent(context, MusicPlayerService::class.java).apply {
            action = "SEEK_TO"
            putExtra("playlist_json", playlistJson)
            putExtra("track_index", index)
            putExtra("seek_position", position)
        }
        context.startService(intent)
    }
} 