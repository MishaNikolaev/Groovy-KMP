package com.nmichail.groovy_kmp.data.remote

import com.nmichail.groovy_kmp.domain.models.Album
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import kotlinx.serialization.json.Json

class AlbumApi(private val client: HttpClient) {
    suspend fun getAlbums(): List<Album> {
        return try {
            val response = client.get("/albums")
            val rawJson = response.bodyAsText()
            println("[AlbumApi] Starting getAlbums() request...")
            println("[AlbumApi] Raw response: $rawJson")
            
            val json = Json { ignoreUnknownKeys = true }
            val result = json.decodeFromString<List<Album>>(rawJson)
            println("[AlbumApi] Successfully got ${result.size} albums")
            result
        } catch (e: Exception) {
            println("[AlbumApi] Error getting albums: ${e.message}")
            emptyList()
        }
    }

    suspend fun getAlbum(id: String): Album {
        return try {
            val response = client.get("/albums/$id")
            val rawJson = response.bodyAsText()
            println("[AlbumApi] Getting album $id...")
            
            val json = Json { ignoreUnknownKeys = true }
            json.decodeFromString<Album>(rawJson)
        } catch (e: Exception) {
            println("[AlbumApi] Error getting album $id: ${e.message}")
            throw e
        }
    }

    suspend fun getAlbumsByArtist(artist: String): List<Album> {
        return try {
            val response = client.get("/albums/artist/$artist")
            val rawJson = response.bodyAsText()
            println("Raw albums by artist response for '$artist': $rawJson")

            if (rawJson.isBlank()) {
                return emptyList()
            }

            val json = Json { ignoreUnknownKeys = true }
            json.decodeFromString<List<Album>>(rawJson)
        } catch (e: Exception) {
            println("Error getting albums by artist '$artist': ${e.message}")
            emptyList()
        }
    }

    suspend fun searchAlbums(query: String): List<Album> {
        return try {
            val response = client.get("/albums/search") {
                parameter("query", query)
            }
            val rawJson = response.bodyAsText()
            println("[AlbumApi] Searching albums with query: $query")
            
            val json = Json { ignoreUnknownKeys = true }
            json.decodeFromString<List<Album>>(rawJson)
        } catch (e: Exception) {
            println("[AlbumApi] Error searching albums: ${e.message}")
            emptyList()
        }
    }

    suspend fun getAlbumsByGenre(genre: String): List<Album> {
        return try {
            val response = client.get("/albums/genre/$genre")
            val rawJson = response.bodyAsText()
            println("[AlbumApi] Getting albums by genre: $genre")
            
            val json = Json { ignoreUnknownKeys = true }
            json.decodeFromString<List<Album>>(rawJson)
        } catch (e: Exception) {
            println("[AlbumApi] Error getting albums by genre: ${e.message}")
            emptyList()
        }
    }

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