package com.nmichail.groovy_kmp.data.local

expect object AlbumLikesManager {
    suspend fun saveLikedAlbum(albumId: String)
    suspend fun removeLikedAlbum(albumId: String)
    suspend fun isAlbumLiked(albumId: String): Boolean
    suspend fun getLikedAlbums(): List<String>
} 