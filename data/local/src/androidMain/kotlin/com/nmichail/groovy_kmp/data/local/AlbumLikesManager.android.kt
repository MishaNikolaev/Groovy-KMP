package com.nmichail.groovy_kmp.data.local

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

actual object AlbumLikesManager {
    private const val PREFS_NAME = "album_likes_preferences"
    private const val LIKED_ALBUMS_KEY = "liked_albums"
    
    private fun getSharedPreferences(): SharedPreferences {
        return ApplicationContextHolder.context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    actual suspend fun saveLikedAlbum(albumId: String) = withContext(Dispatchers.IO) {
        try {
            val prefs = getSharedPreferences()
            val currentLikedAlbums = getLikedAlbums().toMutableSet()
            currentLikedAlbums.add(albumId)
            val json = Json.encodeToString(currentLikedAlbums.toList())
            prefs.edit().putString(LIKED_ALBUMS_KEY, json).apply()
            println("[AlbumLikesManager] Saved liked album: $albumId")
        } catch (e: Exception) {
            println("[AlbumLikesManager] Error saving liked album: ${e.message}")
        }
    }
    
    actual suspend fun removeLikedAlbum(albumId: String) = withContext(Dispatchers.IO) {
        try {
            val prefs = getSharedPreferences()
            val currentLikedAlbums = getLikedAlbums().toMutableSet()
            currentLikedAlbums.remove(albumId)
            val json = Json.encodeToString(currentLikedAlbums.toList())
            prefs.edit().putString(LIKED_ALBUMS_KEY, json).apply()
            println("[AlbumLikesManager] Removed liked album: $albumId")
        } catch (e: Exception) {
            println("[AlbumLikesManager] Error removing liked album: ${e.message}")
        }
    }
    
    actual suspend fun isAlbumLiked(albumId: String): Boolean = withContext(Dispatchers.IO) {
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
    
    actual suspend fun getLikedAlbums(): List<String> = withContext(Dispatchers.IO) {
        try {
            val prefs = getSharedPreferences()
            val json = prefs.getString(LIKED_ALBUMS_KEY, "[]")
            val likedAlbums = Json.decodeFromString<List<String>>(json ?: "[]")
            println("[AlbumLikesManager] Loaded ${likedAlbums.size} liked albums")
            likedAlbums
        } catch (e: Exception) {
            println("[AlbumLikesManager] Error loading liked albums: ${e.message}")
            emptyList()
        }
    }
} 