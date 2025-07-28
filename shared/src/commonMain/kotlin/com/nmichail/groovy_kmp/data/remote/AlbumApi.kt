package com.nmichail.groovy_kmp.data.remote

import com.nmichail.groovy_kmp.domain.models.Album
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.*

class AlbumApi(private val client: HttpClient) {
    suspend fun getAlbums(): List<Album> =
        client.get("/albums").body()

    suspend fun getAlbum(id: String): Album =
        client.get("/albums/$id").body()

    suspend fun getAlbumsByArtist(artist: String): List<Album> =
        client.get("/albums/artist/$artist").body()

    suspend fun searchAlbums(query: String): List<Album> =
        client.get("/albums/search") {
            parameter("query", query)
        }.body()

    suspend fun getAlbumsByGenre(genre: String): List<Album> =
        client.get("/albums/genre/$genre").body()

    suspend fun likeAlbum(id: String) {
        client.post("/albums/$id/like")
    }

    suspend fun unlikeAlbum(id: String) {
        client.delete("/albums/$id/like")
    }

    suspend fun getLikedAlbums(userId: String): List<Album> =
        client.get("/albums/liked/$userId").body()
} 