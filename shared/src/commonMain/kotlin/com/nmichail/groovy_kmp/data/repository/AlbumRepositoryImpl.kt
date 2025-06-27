package com.nmichail.groovy_kmp.data.repository

import com.nmichail.groovy_kmp.data.remote.AlbumApi
import com.nmichail.groovy_kmp.domain.models.Album
import com.nmichail.groovy_kmp.domain.repository.AlbumRepository

class AlbumRepositoryImpl(private val api: AlbumApi) : AlbumRepository {
    override suspend fun getAlbums(): List<Album> = api.getAlbums()
    override suspend fun getAlbum(id: String): Album? = api.getAlbum(id)
    override suspend fun getAlbumsByArtist(artist: String): List<Album> = api.getAlbumsByArtist(artist)
    override suspend fun searchAlbums(query: String): List<Album> = api.searchAlbums(query)
    override suspend fun getAlbumsByGenre(genre: String): List<Album> = api.getAlbumsByGenre(genre)
    override suspend fun likeAlbum(id: String) = api.likeAlbum(id)
    override suspend fun unlikeAlbum(id: String) = api.unlikeAlbum(id)
} 