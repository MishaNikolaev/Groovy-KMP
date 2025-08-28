package com.nmichail.groovy_kmp.data.local

import platform.Foundation.NSUserDefaults
import platform.Foundation.NSJSONSerialization
import platform.Foundation.NSJSONWritingPrettyPrinted
import platform.Foundation.NSJSONReadingMutableContainers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
actual object AlbumLikesManager {
    private const val LIKED_ALBUMS_KEY = "liked_albums"
    
    private fun getUserDefaults(): NSUserDefaults {
        return NSUserDefaults.standardUserDefaults
    }
    
    actual suspend fun saveLikedAlbum(albumId: String) = withContext(Dispatchers.Default) {
        try {
            val userDefaults = getUserDefaults()
            val currentLikedAlbums = getLikedAlbums().toMutableSet()
            currentLikedAlbums.add(albumId)
            val jsonData = NSJSONSerialization.dataWithJSONObject(
                currentLikedAlbums.toList(),
                NSJSONWritingPrettyPrinted,
                null
            )
            userDefaults.setObject(jsonData, LIKED_ALBUMS_KEY)
            userDefaults.synchronize()
            println("[AlbumLikesManager] Saved liked album: $albumId")
        } catch (e: Exception) {
            println("[AlbumLikesManager] Error saving liked album: ${e.message}")
        }
    }
    
    actual suspend fun removeLikedAlbum(albumId: String) = withContext(Dispatchers.Default) {
        try {
            val userDefaults = getUserDefaults()
            val currentLikedAlbums = getLikedAlbums().toMutableSet()
            currentLikedAlbums.remove(albumId)
            val jsonData = NSJSONSerialization.dataWithJSONObject(
                currentLikedAlbums.toList(),
                NSJSONWritingPrettyPrinted,
                null
            )
            userDefaults.setObject(jsonData, LIKED_ALBUMS_KEY)
            userDefaults.synchronize()
            println("[AlbumLikesManager] Removed liked album: $albumId")
        } catch (e: Exception) {
            println("[AlbumLikesManager] Error removing liked album: ${e.message}")
        }
    }
    
    actual suspend fun isAlbumLiked(albumId: String): Boolean = withContext(Dispatchers.Default) {
        try {
            val likedAlbums = getLikedAlbums()
            val isLiked = likedAlbums.contains(albumId)
            println("[AlbumLikesManager] Album $albumId liked status: $isLiked")
            isLiked
        } catch (e: Exception) {
            println("[AlbumLikesManager] Error checking liked status: ${e.message}")
            false
        }
    }
    
    actual suspend fun getLikedAlbums(): List<String> = withContext(Dispatchers.Default) {
        try {
            val userDefaults = getUserDefaults()
            val jsonData = userDefaults.objectForKey(LIKED_ALBUMS_KEY) as? platform.Foundation.NSData
            if (jsonData != null) {
                val jsonArray = NSJSONSerialization.JSONObjectWithData(
                    jsonData,
                    NSJSONReadingMutableContainers,
                    null
                ) as? List<*>
                val likedAlbums = jsonArray?.filterIsInstance<String>() ?: emptyList()
                println("[AlbumLikesManager] Loaded ${likedAlbums.size} liked albums")
                likedAlbums
            } else {
                println("[AlbumLikesManager] No liked albums found")
                emptyList()
            }
        } catch (e: Exception) {
            println("[AlbumLikesManager] Error loading liked albums: ${e.message}")
            emptyList()
        }
    }
} 