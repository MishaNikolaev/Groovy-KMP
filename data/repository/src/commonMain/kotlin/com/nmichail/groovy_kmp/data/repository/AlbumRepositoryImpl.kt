package com.nmichail.groovy_kmp.data.repository

import com.nmichail.groovy_kmp.data.remote.AlbumApi
import com.nmichail.groovy_kmp.data.local.AlbumCache
import com.nmichail.groovy_kmp.data.local.AlbumLikesManager
import com.nmichail.groovy_kmp.domain.models.Album
import com.nmichail.groovy_kmp.domain.repository.AlbumRepository

class AlbumRepositoryImpl(private val api: AlbumApi) : AlbumRepository {
    override suspend fun getAlbums(): List<Album> {
        return try {
            val albums = api.getAlbums()
            AlbumCache.saveAlbums(albums)
            albums
        } catch (e: Exception) {
            println("[AlbumRepositoryImpl] Error fetching albums from API: ${e.message}")
            AlbumCache.loadAlbums() ?: emptyList()
        }
    }
    
    override suspend fun getAlbum(id: String): Album? = api.getAlbum(id)
    
    override suspend fun getAlbumsByArtist(artist: String): List<Album> = api.getAlbumsByArtist(artist)
    
    override suspend fun searchAlbums(query: String): List<Album> = api.searchAlbums(query)
    
    override suspend fun getAlbumsByGenre(genre: String): List<Album> = api.getAlbumsByGenre(genre)
    
    override suspend fun likeAlbum(id: String) {
        try {
            api.likeAlbum(id)
            AlbumLikesManager.saveLikedAlbum(id)
            println("[AlbumRepositoryImpl] Liked album: $id")
        } catch (e: Exception) {
            println("[AlbumRepositoryImpl] Error liking album $id: ${e.message}")
            // Still save locally for offline mode
            AlbumLikesManager.saveLikedAlbum(id)
        }
    }
    
    override suspend fun unlikeAlbum(id: String) {
        try {
            api.unlikeAlbum(id)
            AlbumLikesManager.removeLikedAlbum(id)
            println("[AlbumRepositoryImpl] Unliked album: $id")
        } catch (e: Exception) {
            println("[AlbumRepositoryImpl] Error unliking album $id: ${e.message}")
            // Still remove locally for offline mode
            AlbumLikesManager.removeLikedAlbum(id)
        }
    }
    
    override suspend fun getLikedAlbums(userId: String): List<Album> = api.getLikedAlbums(userId)
    
    override suspend fun isAlbumLiked(id: String): Boolean {
        return AlbumLikesManager.isAlbumLiked(id)
    }
} 