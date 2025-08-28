package com.nmichail.groovy_kmp.data.local

expect object LikesManager {
    suspend fun saveLikedTrack(trackId: String)
    suspend fun removeLikedTrack(trackId: String)
    suspend fun isTrackLiked(trackId: String): Boolean
    suspend fun getLikedTracks(): List<String>
} 