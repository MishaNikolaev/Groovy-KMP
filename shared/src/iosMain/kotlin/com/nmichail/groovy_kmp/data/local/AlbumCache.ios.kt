package com.nmichail.groovy_kmp.data.local

import com.nmichail.groovy_kmp.domain.models.Album
import com.nmichail.groovy_kmp.domain.models.Track
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import platform.Foundation.*

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

actual object TrackCache {
    private const val CACHE_FILE = "tracks_cache.json"
    private fun getCachePath(): String {
        val urls = NSFileManager.defaultManager.URLsForDirectory(NSDocumentDirectory, NSUserDomainMask)
        val documentsDirectory = (urls[0] as? NSURL)?.path ?: return CACHE_FILE
        return documentsDirectory + "/" + CACHE_FILE
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
} 