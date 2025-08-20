package com.nmichail.groovy_kmp.data.local

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

actual object LikesManager {
    private const val PREFS_NAME = "likes_preferences"
    private const val LIKED_TRACKS_KEY = "liked_tracks"
    
    private fun getSharedPreferences(): SharedPreferences {
        return ApplicationContextHolder.context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    actual suspend fun saveLikedTrack(trackId: String) = withContext(Dispatchers.IO) {
        try {
            val prefs = getSharedPreferences()
            val currentLikedTracks = getLikedTracks().toMutableSet()
            currentLikedTracks.add(trackId)
            val json = Json.encodeToString(currentLikedTracks.toList())
            prefs.edit().putString(LIKED_TRACKS_KEY, json).apply()
            println("[LikesManager] Saved liked track: $trackId")
        } catch (e: Exception) {
            println("[LikesManager] Error saving liked track: ${e.message}")
        }
    }
    
    actual suspend fun removeLikedTrack(trackId: String) = withContext(Dispatchers.IO) {
        try {
            val prefs = getSharedPreferences()
            val currentLikedTracks = getLikedTracks().toMutableSet()
            currentLikedTracks.remove(trackId)
            val json = Json.encodeToString(currentLikedTracks.toList())
            prefs.edit().putString(LIKED_TRACKS_KEY, json).apply()
            println("[LikesManager] Removed liked track: $trackId")
        } catch (e: Exception) {
            println("[LikesManager] Error removing liked track: ${e.message}")
        }
    }
    
    actual suspend fun isTrackLiked(trackId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val likedTracks = getLikedTracks()
            val isLiked = likedTracks.contains(trackId)
            println("[LikesManager] Track $trackId liked status: $isLiked")
            isLiked
        } catch (e: Exception) {
            println("[LikesManager] Error checking liked status: ${e.message}")
            false
        }
    }
    
    actual suspend fun getLikedTracks(): List<String> = withContext(Dispatchers.IO) {
        try {
            val prefs = getSharedPreferences()
            val json = prefs.getString(LIKED_TRACKS_KEY, "[]")
            val likedTracks = Json.decodeFromString<List<String>>(json ?: "[]")
            println("[LikesManager] Loaded ${likedTracks.size} liked tracks")
            likedTracks
        } catch (e: Exception) {
            println("[LikesManager] Error loading liked tracks: ${e.message}")
            emptyList()
        }
    }
} 