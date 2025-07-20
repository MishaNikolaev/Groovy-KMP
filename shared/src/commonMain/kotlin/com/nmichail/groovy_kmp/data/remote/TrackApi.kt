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
    suspend fun getTracks(): List<Track> =
        client.get("/tracks").body()

    suspend fun getTrack(id: String): Track =
        client.get("/tracks/$id").body()

    suspend fun getTracksByAlbum(albumId: String): List<Track> {
        val response = client.get("/tracks/album/$albumId").body<String>()
        println("[TrackApi] Raw response for albumId=$albumId: $response")
        return try {
            val tracks = Json.decodeFromString<List<Track>>(response)
            println("[TrackApi] Successfully parsed as array: ${tracks.size} tracks")
            tracks
        } catch (e: Exception) {
            println("[TrackApi] Failed to parse as array, trying as single object: ${e.message}")
            try {
                val singleTrack = Json.decodeFromString<Track>(response)
                println("[TrackApi] Successfully parsed as single object")
                listOf(singleTrack)
            } catch (e2: Exception) {
                println("[TrackApi] Failed to parse tracks response: $response")
                println("[TrackApi] Error: ${e2.message}")
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
            println("[TrackApi] Raw JSON: $rawJson")
            val json = Json { ignoreUnknownKeys = true }
            val jsonArray = Json.parseToJsonElement(rawJson).jsonArray
            println("[TrackApi] jsonArray.size = ${jsonArray.size}")
            val result = mutableListOf<Track>()
            for (element in jsonArray) {
                println("[TrackApi] Try parse: $element")
                try {
                    val track = json.decodeFromJsonElement<Track>(element)
                    println("[TrackApi] Parsed track id: ${track.id}")
                    result.add(track)
                } catch (e: Exception) {
                    println("[TrackApi] Error parsing track: $element\nError: ${e.message}")
                }
            }
            val found50 = result.any { it.id == "50" }
            if (!found50) {
                println("[TrackApi] Track with id=50 NOT PARSED!")
            }
            result
        } catch (e: Exception) {
            println("[TrackApi] Error parsing top tracks: ${e.message}")
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