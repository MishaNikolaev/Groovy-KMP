package com.nmichail.groovy_kmp.data.repository

import com.nmichail.groovy_kmp.data.remote.TrackApi
import com.nmichail.groovy_kmp.domain.models.Track
import com.nmichail.groovy_kmp.domain.repository.TrackRepository

class TrackRepositoryImpl(private val api: TrackApi) : TrackRepository {
    override suspend fun getTracks(): List<Track> = api.getTracks()
    override suspend fun getTrack(id: String): Track? = api.getTrack(id)
    override suspend fun getTracksByAlbum(albumId: String): List<Track> = api.getTracksByAlbum(albumId)
    override suspend fun getTracksByArtist(artist: String): List<Track> = api.getTracksByArtist(artist)
    override suspend fun searchTracks(query: String): List<Track> = api.searchTracks(query)
    override suspend fun getTopTracks(): List<Track> = api.getTopTracks()
    override suspend fun getRecentTracks(): List<Track> = api.getRecentTracks()
    override suspend fun getLikedTracks(userId: String): List<Track> = api.getLikedTracks(userId)
    override suspend fun likeTrack(id: String) = api.likeTrack(id)
    override suspend fun unlikeTrack(id: String) = api.unlikeTrack(id)
    override suspend fun playTrack(id: String) = api.playTrack(id)
    override suspend fun getTrackPlayCount(id: String): Int = api.getTrackPlayCount(id)
} 