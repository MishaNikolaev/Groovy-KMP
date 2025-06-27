package com.nmichail.groovy_kmp.data.remote

import com.nmichail.groovy_kmp.domain.models.Track
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.*

class TrackApi(private val client: HttpClient) {
    suspend fun getTracks(): List<Track> =
        client.get("/tracks").body()

    suspend fun getTrack(id: String): Track =
        client.get("/tracks/$id").body()

    suspend fun getTracksByAlbum(albumId: String): List<Track> =
        client.get("/tracks/album/$albumId").body()

    suspend fun getTracksByArtist(artist: String): List<Track> =
        client.get("/tracks/artist/$artist").body()

    suspend fun searchTracks(query: String): List<Track> =
        client.get("/tracks/search") {
            parameter("query", query)
        }.body()

    suspend fun getTopTracks(): List<Track> =
        client.get("/tracks/top").body()

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