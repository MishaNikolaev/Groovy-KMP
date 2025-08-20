package com.nmichail.groovy_kmp.data.local

import com.nmichail.groovy_kmp.domain.models.Album

expect object AlbumCache {
    suspend fun saveAlbums(albums: List<Album>)
    suspend fun loadAlbums(): List<Album>?
}

expect object AllAlbumsCache {
    suspend fun saveAllAlbums(albums: List<Album>)
    suspend fun loadAllAlbums(): List<Album>?
} 