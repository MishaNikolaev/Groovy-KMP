package com.nmichail.groovy_kmp.data.local

import com.nmichail.groovy_kmp.domain.models.Album
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

expect object AlbumCache {
    suspend fun saveAlbums(albums: List<Album>)
    suspend fun loadAlbums(): List<Album>?
} 