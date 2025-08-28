package com.nmichail.groovy_kmp.domain.repository

import com.nmichail.groovy_kmp.domain.models.Track

interface TrackRepository {
    suspend fun getTracks(): List<Track>
    suspend fun getTrack(id: String): Track?
    suspend fun getTracksByAlbum(albumId: String): List<Track>
    suspend fun getTracksByArtist(artist: String): List<Track>
    suspend fun searchTracks(query: String): List<Track>
    suspend fun getTopTracks(): List<Track>
    suspend fun getRecentTracks(): List<Track>
    suspend fun getLikedTracks(userId: String): List<Track>
    suspend fun likeTrack(id: String)
    suspend fun unlikeTrack(id: String)
    suspend fun playTrack(id: String)
    suspend fun getTrackPlayCount(id: String): Int
    suspend fun addToHistory(track: Track)
    suspend fun isTrackLiked(id: String): Boolean
} 