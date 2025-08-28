package com.nmichail.groovy_kmp.data.remote

import com.nmichail.groovy_kmp.domain.models.Track
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import kotlinx.serialization.json.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import com.nmichail.groovy_kmp.data.remote.getServerHost

class TrackApi(private val client: HttpClient) {
    private val baseUrl = getServerHost()

    suspend fun getTracks(): List<Track> {
        return try {
            println("[TrackApi] Getting all tracks from $baseUrl/tracks")
            
            // First check if server is accessible
            val isConnected = checkServerConnectivity(client, baseUrl)
            if (!isConnected) {
                println("[TrackApi] Server is not accessible, returning empty list")
                return emptyList()
            }
            
            val response = client.get("$baseUrl/tracks")
            val rawJson = response.bodyAsText()
            println("[TrackApi] Raw response: $rawJson")
            
            val json = Json { ignoreUnknownKeys = true }
            val tracks = json.decodeFromString<List<Track>>(rawJson)
            println("[TrackApi] Successfully parsed ${tracks.size} tracks")
            tracks
        } catch (e: Exception) {
            println("[TrackApi] Error getting tracks: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getTrack(id: String): Track =
        client.get("$baseUrl/tracks/$id").body()

    suspend fun getTracksByAlbum(albumId: String): List<Track> {
        val response = client.get("$baseUrl/tracks/album/$albumId").body<String>()
        return try {
            val tracks = Json.decodeFromString<List<Track>>(response)
            tracks
        } catch (e: Exception) {
            try {
                val singleTrack = Json.decodeFromString<Track>(response)
                listOf(singleTrack)
            } catch (e2: Exception) {
                emptyList()
            }
        }
    }

    suspend fun getTracksByArtist(artist: String): List<Track> =
        client.get("$baseUrl/tracks/artist/$artist").body()

    suspend fun searchTracks(query: String): List<Track> =
        client.get("$baseUrl/tracks/search") {
            parameter("query", query)
        }.body()

    suspend fun getTopTracks(): List<Track> {
        return try {
            println("[TrackApi] Getting top tracks from $baseUrl/tracks/top")
            val response = client.get("$baseUrl/tracks/top")
            val rawJson = response.bodyAsText()
            val json = Json { ignoreUnknownKeys = true }
            val jsonArray = Json.parseToJsonElement(rawJson).jsonArray
            val result = mutableListOf<Track>()
            for (element in jsonArray) {
                try {
                    val track = json.decodeFromJsonElement<Track>(element)
                    result.add(track)
                } catch (e: Exception) {
                }
            }
            result
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getRecentTracks(): List<Track> =
        client.get("$baseUrl/tracks/recent").body()

    suspend fun getLikedTracks(userId: String): List<Track> =
        client.get("$baseUrl/tracks/liked/$userId").body()

    suspend fun likeTrack(id: String) {
        client.post("$baseUrl/tracks/$id/like")
    }

    suspend fun unlikeTrack(id: String) {
        client.delete("$baseUrl/tracks/$id/like")
    }

    suspend fun playTrack(id: String) {
        client.post("$baseUrl/tracks/$id/play")
    }

    suspend fun getTrackPlayCount(id: String): Int =
        client.get("$baseUrl/tracks/$id/play").body()
} 