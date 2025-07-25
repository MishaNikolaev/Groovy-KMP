package com.nmichail.groovy_kmp.data.local

import android.content.Context
import com.nmichail.groovy_kmp.domain.models.Album
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