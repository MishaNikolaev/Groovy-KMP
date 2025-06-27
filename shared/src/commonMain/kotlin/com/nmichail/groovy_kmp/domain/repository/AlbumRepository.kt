package com.nmichail.groovy_kmp.domain.repository

import com.nmichail.groovy_kmp.domain.models.Album

interface AlbumRepository {
    suspend fun getAlbums(): List<Album>
    suspend fun getAlbum(id: String): Album?
    suspend fun getAlbumsByArtist(artist: String): List<Album>
    suspend fun searchAlbums(query: String): List<Album>
    suspend fun getAlbumsByGenre(genre: String): List<Album>
    suspend fun likeAlbum(id: String)
    suspend fun unlikeAlbum(id: String)
} 