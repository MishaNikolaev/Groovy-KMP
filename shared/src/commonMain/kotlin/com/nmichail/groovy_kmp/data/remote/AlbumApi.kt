package com.nmichail.groovy_kmp.data.remote

import com.nmichail.groovy_kmp.domain.models.Album
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import kotlinx.serialization.json.Json

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
        println("[AlbumApi] Liking album with id: $id")
        try {
            client.post("/albums/$id/like")
            println("[AlbumApi] Successfully liked album: $id")
        } catch (e: Exception) {
            println("[AlbumApi] Error liking album $id: ${e.message}")
            throw e
        }
    }

    suspend fun unlikeAlbum(id: String) {
        println("[AlbumApi] Unliking album with id: $id")
        try {
            client.delete("/albums/$id/like")
            println("[AlbumApi] Successfully unliked album: $id")
        } catch (e: Exception) {
            println("[AlbumApi] Error unliking album $id: ${e.message}")
            throw e
        }
    }

    suspend fun getLikedAlbums(userId: String): List<Album> {
        println("[AlbumApi] Getting liked albums for userId: $userId")
        try {
            val response = client.get("/albums/liked/$userId")
            val responseText = response.bodyAsText()
            println("[AlbumApi] Raw response: $responseText")
            
            return try {
                Json.decodeFromString<List<Album>>(responseText)
            } catch (e: Exception) {
                println("[AlbumApi] Failed to parse as JSON array, trying as single object: ${e.message}")
                try {
                    val singleAlbum = Json.decodeFromString<Album>(responseText)
                    listOf(singleAlbum)
                } catch (e2: Exception) {
                    println("[AlbumApi] Failed to parse as single object: ${e2.message}")
                    emptyList()
                }
            }
        } catch (e: Exception) {
            println("[AlbumApi] Error getting liked albums for userId $userId: ${e.message}")
            return emptyList()
        }
    }
} 