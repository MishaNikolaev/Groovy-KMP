package com.nmichail.groovy_kmp.data.local

import com.nmichail.groovy_kmp.domain.models.Album
import com.nmichail.groovy_kmp.domain.models.Track
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

expect object AlbumCache {
    suspend fun saveAlbums(albums: List<Album>)
    suspend fun loadAlbums(): List<Album>?
}

expect object AllAlbumsCache {
    suspend fun saveAllAlbums(albums: List<Album>)
    suspend fun loadAllAlbums(): List<Album>?
}

expect object TrackCache {
    suspend fun saveTracks(tracks: List<Track>)
    suspend fun loadTracks(): List<Track>?
} 