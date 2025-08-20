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

class TrackApi(private val client: HttpClient) {
    suspend fun getTracks(): List<Track> {
        return try {
            val response = client.get("/tracks")
            val rawJson = response.bodyAsText()
            println("[TrackApi] Getting all tracks...")
            
            val json = Json { ignoreUnknownKeys = true }
            json.decodeFromString<List<Track>>(rawJson)
        } catch (e: Exception) {
            println("[TrackApi] Error getting tracks: ${e.message}")
            emptyList()
        }
    }

    suspend fun getTrack(id: String): Track =
        client.get("/tracks/$id").body()

    suspend fun getTracksByAlbum(albumId: String): List<Track> {
        val response = client.get("/tracks/album/$albumId").body<String>()
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
        client.get("/tracks/artist/$artist").body()

    suspend fun searchTracks(query: String): List<Track> =
        client.get("/tracks/search") {
            parameter("query", query)
        }.body()

    suspend fun getTopTracks(): List<Track> {
        return try {
            val response = client.get("/tracks/top")
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
        client.get("/tracks/recent").body()

    suspend fun getLikedTracks(userId: String): List<Track> =
        client.get("/tracks/liked/$userId").body()

    suspend fun likeTrack(id: String) {
        client.post("/tracks/$id/like")
    }

    suspend fun unlikeTrack(id: String) {
        client.delete("/tracks/$id/like")
    }

    suspend fun playTrack(id: String) {
        client.post("/tracks/$id/play")
    }

    suspend fun getTrackPlayCount(id: String): Int =
        client.get("/tracks/$id/play").body()
} 