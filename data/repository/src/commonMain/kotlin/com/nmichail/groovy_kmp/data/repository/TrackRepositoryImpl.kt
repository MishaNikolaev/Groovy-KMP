package com.nmichail.groovy_kmp.data.repository

import com.nmichail.groovy_kmp.data.remote.TrackApi
import com.nmichail.groovy_kmp.data.local.TrackCache
import com.nmichail.groovy_kmp.data.local.LikesManager
import com.nmichail.groovy_kmp.domain.models.Track
import com.nmichail.groovy_kmp.domain.repository.TrackRepository

class TrackRepositoryImpl(private val api: TrackApi) : TrackRepository {
    override suspend fun getTracks(): List<Track> {
        return try {
            val tracks = api.getTracks()
            TrackCache.saveTracks(tracks)
            tracks
        } catch (e: Exception) {
            println("[TrackRepositoryImpl] Error fetching tracks from API: ${e.message}")
            TrackCache.loadTracks() ?: emptyList()
        }
    }
    
    override suspend fun getTrack(id: String): Track? = api.getTrack(id)
    
    override suspend fun getTracksByAlbum(albumId: String): List<Track> = api.getTracksByAlbum(albumId)
    
    override suspend fun getTracksByArtist(artist: String): List<Track> = api.getTracksByArtist(artist)
    
    override suspend fun searchTracks(query: String): List<Track> = api.searchTracks(query)
    
    override suspend fun getTopTracks(): List<Track> = api.getTopTracks()
    
    override suspend fun getRecentTracks(): List<Track> {
        return try {
            // Always load from local storage for history
            val tracks = TrackCache.loadHistory() ?: emptyList()
            println("[TrackRepositoryImpl] Loaded ${tracks.size} recent tracks from local storage")
            tracks
        } catch (e: Exception) {
            println("[TrackRepositoryImpl] Error loading recent tracks from local storage: ${e.message}")
            emptyList()
        }
    }
    
    override suspend fun getLikedTracks(userId: String): List<Track> = api.getLikedTracks(userId)
    
    override suspend fun likeTrack(id: String) {
        try {
            api.likeTrack(id)
            LikesManager.saveLikedTrack(id)
            println("[TrackRepositoryImpl] Liked track: $id")
        } catch (e: Exception) {
            println("[TrackRepositoryImpl] Error liking track $id: ${e.message}")
            // Still save locally for offline mode
            LikesManager.saveLikedTrack(id)
        }
    }
    
    override suspend fun unlikeTrack(id: String) {
        try {
            api.unlikeTrack(id)
            LikesManager.removeLikedTrack(id)
            println("[TrackRepositoryImpl] Unliked track: $id")
        } catch (e: Exception) {
            println("[TrackRepositoryImpl] Error unliking track $id: ${e.message}")
            // Still remove locally for offline mode
            LikesManager.removeLikedTrack(id)
        }
    }
    
    override suspend fun playTrack(id: String) {
        api.playTrack(id)
        // Note: History is added separately via addToHistory method
    }
    
    override suspend fun addToHistory(track: Track) {
        try {
            // Set current timestamp using platform-specific implementation
            val trackWithTimestamp = track.copy(playedAt = getCurrentTimeMillis())
            TrackCache.addToHistory(trackWithTimestamp)
            println("[TrackRepositoryImpl] Added track to history: ${track.title}")
        } catch (e: Exception) {
            println("[TrackRepositoryImpl] Error adding track to history: ${e.message}")
        }
    }
    
    private fun getCurrentTimeMillis(): Long {
        return kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
    }
    
    override suspend fun isTrackLiked(id: String): Boolean {
        return LikesManager.isTrackLiked(id)
    }
    
    override suspend fun getTrackPlayCount(id: String): Int = api.getTrackPlayCount(id)
} 