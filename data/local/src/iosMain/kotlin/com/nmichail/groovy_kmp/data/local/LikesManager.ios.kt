package com.nmichail.groovy_kmp.data.local

import platform.Foundation.NSUserDefaults
import platform.Foundation.NSJSONSerialization
import platform.Foundation.NSJSONWritingPrettyPrinted
import platform.Foundation.NSJSONReadingMutableContainers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual object LikesManager {
    private const val LIKED_TRACKS_KEY = "liked_tracks"
    
    private fun getUserDefaults(): NSUserDefaults {
        return NSUserDefaults.standardUserDefaults
    }
    
    actual suspend fun saveLikedTrack(trackId: String) = withContext(Dispatchers.Default) {
        try {
            val userDefaults = getUserDefaults()
            val currentLikedTracks = getLikedTracks().toMutableSet()
            currentLikedTracks.add(trackId)
            val jsonData = NSJSONSerialization.dataWithJSONObject(
                currentLikedTracks.toList(),
                NSJSONWritingPrettyPrinted,
                null
            )
            userDefaults.setObject(jsonData, LIKED_TRACKS_KEY)
            userDefaults.synchronize()
            println("[LikesManager] Saved liked track: $trackId")
        } catch (e: Exception) {
            println("[LikesManager] Error saving liked track: ${e.message}")
        }
    }
    
    actual suspend fun removeLikedTrack(trackId: String) = withContext(Dispatchers.Default) {
        try {
            val userDefaults = getUserDefaults()
            val currentLikedTracks = getLikedTracks().toMutableSet()
            currentLikedTracks.remove(trackId)
            val jsonData = NSJSONSerialization.dataWithJSONObject(
                currentLikedTracks.toList(),
                NSJSONWritingPrettyPrinted,
                null
            )
            userDefaults.setObject(jsonData, LIKED_TRACKS_KEY)
            userDefaults.synchronize()
            println("[LikesManager] Removed liked track: $trackId")
        } catch (e: Exception) {
            println("[LikesManager] Error removing liked track: ${e.message}")
        }
    }
    
    actual suspend fun isTrackLiked(trackId: String): Boolean = withContext(Dispatchers.Default) {
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
    
    actual suspend fun getLikedTracks(): List<String> = withContext(Dispatchers.Default) {
        try {
            val userDefaults = getUserDefaults()
            val jsonData = userDefaults.objectForKey(LIKED_TRACKS_KEY) as? platform.Foundation.NSData
            if (jsonData != null) {
                val jsonArray = NSJSONSerialization.JSONObjectWithData(
                    jsonData,
                    NSJSONReadingMutableContainers,
                    null
                ) as? List<*>
                val likedTracks = jsonArray?.filterIsInstance<String>() ?: emptyList()
                println("[LikesManager] Loaded ${likedTracks.size} liked tracks")
                likedTracks
            } else {
                println("[LikesManager] No liked tracks found")
                emptyList()
            }
        } catch (e: Exception) {
            println("[LikesManager] Error loading liked tracks: ${e.message}")
            emptyList()
        }
    }
} 