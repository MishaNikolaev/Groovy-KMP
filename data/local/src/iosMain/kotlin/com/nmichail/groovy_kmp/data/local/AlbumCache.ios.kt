package com.nmichail.groovy_kmp.data.local

import com.nmichail.groovy_kmp.domain.models.Album
import com.nmichail.groovy_kmp.domain.models.Track
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSString
import platform.Foundation.stringWithContentsOfFile
import platform.Foundation.writeToFile
import platform.Foundation.NSURL
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
actual object AlbumCache {
    private const val CACHE_FILE = "albums_cache.json"
    private fun getCachePath(): String {
        val urls = NSFileManager.defaultManager.URLsForDirectory(NSDocumentDirectory, NSUserDomainMask)
        val documentsDirectory = (urls[0] as? NSURL)?.path ?: return CACHE_FILE
        return documentsDirectory + "/" + CACHE_FILE
    }

    actual suspend fun saveAlbums(albums: List<Album>) {
        val json = Json.encodeToString(albums)
        val path = getCachePath()
        (json as NSString).writeToFile(path, true)
    }

    actual suspend fun loadAlbums(): List<Album>? {
        val path = getCachePath()
        val fileManager = NSFileManager.defaultManager
        if (!fileManager.fileExistsAtPath(path)) return null
        val json = NSString.stringWithContentsOfFile(path)
        return try {
            Json.decodeFromString(json as String)
        } catch (e: Exception) {
            null
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
actual object AllAlbumsCache {
    private const val CACHE_FILE = "all_albums_cache.json"
    private fun getCachePath(): String {
        val urls = NSFileManager.defaultManager.URLsForDirectory(NSDocumentDirectory, NSUserDomainMask)
        val documentsDirectory = (urls[0] as? NSURL)?.path ?: return CACHE_FILE
        return documentsDirectory + "/" + CACHE_FILE
    }

    actual suspend fun saveAllAlbums(albums: List<Album>) {
        val json = Json.encodeToString(albums)
        val path = getCachePath()
        (json as NSString).writeToFile(path, true)
    }

    actual suspend fun loadAllAlbums(): List<Album>? {
        val path = getCachePath()
        val fileManager = NSFileManager.defaultManager
        if (!fileManager.fileExistsAtPath(path)) return null
        val json = NSString.stringWithContentsOfFile(path)
        return try {
            Json.decodeFromString(json as String)
        } catch (e: Exception) {
            null
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
actual object TrackCache {
    private const val CACHE_FILE = "tracks_cache.json"
    private const val HISTORY_FILE = "tracks_history.json"
    private fun getCachePath(): String {
        val urls = NSFileManager.defaultManager.URLsForDirectory(NSDocumentDirectory, NSUserDomainMask)
        val documentsDirectory = (urls[0] as? NSURL)?.path ?: return CACHE_FILE
        return documentsDirectory + "/" + CACHE_FILE
    }
    private fun getHistoryPath(): String {
        val urls = NSFileManager.defaultManager.URLsForDirectory(NSDocumentDirectory, NSUserDomainMask)
        val documentsDirectory = (urls[0] as? NSURL)?.path ?: return HISTORY_FILE
        return documentsDirectory + "/" + HISTORY_FILE
    }

    actual suspend fun saveTracks(tracks: List<Track>) {
        val json = Json.encodeToString(tracks)
        val path = getCachePath()
        (json as NSString).writeToFile(path, true)
    }

    actual suspend fun loadTracks(): List<Track>? {
        val path = getCachePath()
        val fileManager = NSFileManager.defaultManager
        if (!fileManager.fileExistsAtPath(path)) return null
        val json = NSString.stringWithContentsOfFile(path)
        return try {
            Json.decodeFromString(json as String)
        } catch (e: Exception) {
            null
        }
    }

    actual suspend fun saveHistory(tracks: List<Track>) {
        val json = Json.encodeToString(tracks)
        val path = getHistoryPath()
        (json as NSString).writeToFile(path, true)
    }

    actual suspend fun loadHistory(): List<Track>? {
        val path = getHistoryPath()
        val fileManager = NSFileManager.defaultManager
        if (!fileManager.fileExistsAtPath(path)) return null
        val json = NSString.stringWithContentsOfFile(path)
        return try {
            Json.decodeFromString(json as String)
        } catch (e: Exception) {
            null
        }
    }

    actual suspend fun addToHistory(track: Track) {
        try {
            println("[TrackCache iOS] Adding track to history: ${track.title}, playedAt: ${track.playedAt}")
            val currentHistory = loadHistory()
            println("[TrackCache iOS] Current history size: ${currentHistory?.size ?: 0}")
            
            // Удаляем трек из истории, если он уже есть (чтобы переместить в начало)
            val filteredHistory = currentHistory?.filter { it.id != track.id } ?: emptyList()
            println("[TrackCache iOS] Filtered history size: ${filteredHistory.size}")
            
            // Добавляем трек в начало истории
            val newHistory = listOf(track) + filteredHistory
            // Ограничиваем историю 50 треками
            val limitedHistory = newHistory.take(50)
            saveHistory(limitedHistory)
            
            println("[TrackCache iOS] Saved history with ${limitedHistory.size} tracks")
        } catch (e: Exception) {
            println("[TrackCache iOS] Error adding track to history: ${e.message}")
            e.printStackTrace()
        }
    }
} 