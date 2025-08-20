package com.nmichail.groovy_kmp.data.remote

import com.nmichail.groovy_kmp.domain.models.Album
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import kotlinx.serialization.json.Json
import com.nmichail.groovy_kmp.data.remote.getServerHost

class AlbumApi(private val client: HttpClient) {
    private val baseUrl = getServerHost()

    suspend fun getAlbums(): List<Album> {
        return try {
            println("[AlbumApi] Starting getAlbums() request to $baseUrl/albums")
            
            // First check if server is accessible
            val isConnected = checkServerConnectivity(client, baseUrl)
            if (!isConnected) {
                println("[AlbumApi] Server is not accessible, returning empty list")
                return emptyList()
            }
            
            val response = client.get("$baseUrl/albums")
            val rawJson = response.bodyAsText()
            println("[AlbumApi] Raw response: $rawJson")
            
            val json = Json { ignoreUnknownKeys = true }
            val result = json.decodeFromString<List<Album>>(rawJson)
            println("[AlbumApi] Successfully got ${result.size} albums")
            result
        } catch (e: Exception) {
            println("[AlbumApi] Error getting albums: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getAlbum(id: String): Album {
        return try {
            println("[AlbumApi] Getting album $id from $baseUrl/albums/$id")
            val response = client.get("$baseUrl/albums/$id")
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
            println("[AlbumApi] Getting albums by artist '$artist' from $baseUrl/albums/artist/$artist")
            val response = client.get("$baseUrl/albums/artist/$artist")
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
            println("[AlbumApi] Searching albums with query: $query from $baseUrl/albums/search")
            val response = client.get("$baseUrl/albums/search") {
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
            println("[AlbumApi] Getting albums by genre: $genre from $baseUrl/albums/genre/$genre")
            val response = client.get("$baseUrl/albums/genre/$genre")
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
        println("[AlbumApi] Liking album with id: $id at $baseUrl/albums/$id/like")
        try {
            client.post("$baseUrl/albums/$id/like")
            println("[AlbumApi] Successfully liked album: $id")
        } catch (e: Exception) {
            println("[AlbumApi] Error liking album $id: ${e.message}")
            throw e
        }
    }

    suspend fun unlikeAlbum(id: String) {
        println("[AlbumApi] Unliking album with id: $id at $baseUrl/albums/$id/like")
        try {
            client.delete("$baseUrl/albums/$id/like")
            println("[AlbumApi] Successfully unliked album: $id")
        } catch (e: Exception) {
            println("[AlbumApi] Error unliking album $id: ${e.message}")
            throw e
        }
    }

    suspend fun getLikedAlbums(userId: String): List<Album> {
        println("[AlbumApi] Getting liked albums for userId: $userId from $baseUrl/albums/liked/$userId")
        try {
            val response = client.get("$baseUrl/albums/liked/$userId")
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