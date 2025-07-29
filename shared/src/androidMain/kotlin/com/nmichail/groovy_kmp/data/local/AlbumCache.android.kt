package com.nmichail.groovy_kmp.data.local

import android.content.Context
import com.nmichail.groovy_kmp.domain.models.Album
import com.nmichail.groovy_kmp.domain.models.Track
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

object ApplicationContextHolder {
    lateinit var context: Context
}

actual object AlbumCache {
    private const val CACHE_FILE = "albums_cache.json"
    private fun getCacheFile(): File = File(ApplicationContextHolder.context.filesDir, CACHE_FILE)

    actual suspend fun saveAlbums(albums: List<Album>) {
        val json = Json.encodeToString(albums)
        getCacheFile().writeText(json)
    }

    actual suspend fun loadAlbums(): List<Album>? {
        val file = getCacheFile()
        if (!file.exists()) return null
        val json = file.readText()
        return try {
            Json.decodeFromString(json)
        } catch (e: Exception) {
            null
        }
    }
}

actual object AllAlbumsCache {
    private const val CACHE_FILE = "all_albums_cache.json"
    private fun getCacheFile(): File = File(ApplicationContextHolder.context.filesDir, CACHE_FILE)

    actual suspend fun saveAllAlbums(albums: List<Album>) {
        val json = Json.encodeToString(albums)
        getCacheFile().writeText(json)
    }

    actual suspend fun loadAllAlbums(): List<Album>? {
        val file = getCacheFile()
        if (!file.exists()) return null
        val json = file.readText()
        return try {
            Json.decodeFromString(json)
        } catch (e: Exception) {
            null
        }
    }
}

actual object TrackCache {
    private const val CACHE_FILE = "tracks_cache.json"
    private const val HISTORY_FILE = "tracks_history.json"
    private fun getCacheFile(): File = File(ApplicationContextHolder.context.filesDir, CACHE_FILE)
    private fun getHistoryFile(): File = File(ApplicationContextHolder.context.filesDir, HISTORY_FILE)

    actual suspend fun saveTracks(tracks: List<Track>) {
        val json = Json.encodeToString(tracks)
        getCacheFile().writeText(json)
    }

    actual suspend fun loadTracks(): List<Track>? {
        val file = getCacheFile()
        if (!file.exists()) return null
        val json = file.readText()
        return try {
            Json.decodeFromString(json)
        } catch (e: Exception) {
            null
        }
    }

    actual suspend fun saveHistory(tracks: List<Track>) {
        val json = Json.encodeToString(tracks)
        getHistoryFile().writeText(json)
    }

    actual suspend fun loadHistory(): List<Track>? {
        val file = getHistoryFile()
        if (!file.exists()) return null
        val json = file.readText()
        return try {
            Json.decodeFromString(json)
        } catch (e: Exception) {
            null
        }
    }

    actual suspend fun addToHistory(track: Track) {
        try {
            println("[TrackCache Android] Adding track to history: ${track.title}, playedAt: ${track.playedAt}")
            val currentHistory = loadHistory()
            println("[TrackCache Android] Current history size: ${currentHistory?.size ?: 0}")

            // Удаляем трек из истории, если он уже есть (чтобы переместить в начало)
            val filteredHistory = currentHistory?.filter { it.id != track.id } ?: emptyList()
            println("[TrackCache Android] Filtered history size: ${filteredHistory.size}")

            // Добавляем трек в начало истории
            val newHistory = listOf(track) + filteredHistory
            // Ограничиваем историю 50 треками
            val limitedHistory = newHistory.take(50)
            saveHistory(limitedHistory)

            println("[TrackCache Android] Saved history with ${limitedHistory.size} tracks")
        } catch (e: Exception) {
            println("[TrackCache Android] Error adding track to history: ${e.message}")
            e.printStackTrace()
        }
    }
} 