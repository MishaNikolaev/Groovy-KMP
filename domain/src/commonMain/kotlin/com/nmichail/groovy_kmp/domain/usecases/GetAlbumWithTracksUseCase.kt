package com.nmichail.groovy_kmp.domain.usecases

import com.nmichail.groovy_kmp.domain.models.Album
import com.nmichail.groovy_kmp.domain.models.AlbumWithTracks
import com.nmichail.groovy_kmp.domain.models.Track
import com.nmichail.groovy_kmp.domain.repository.AlbumRepository
import com.nmichail.groovy_kmp.domain.repository.TrackRepository

class GetAlbumWithTracksUseCase(
    private val albumRepository: AlbumRepository,
    private val trackRepository: TrackRepository
) {
    suspend operator fun invoke(albumId: String): AlbumWithTracks? {
        try {
            println("[GetAlbumWithTracksUseCase] Loading album with id=$albumId")
            val album = albumRepository.getAlbum(albumId)
            println("[GetAlbumWithTracksUseCase] Album loaded: $album")
            val tracks = trackRepository.getTracksByAlbum(albumId)
            println("[GetAlbumWithTracksUseCase] Tracks loaded for albumId=$albumId: ${tracks.size} tracks")
            tracks.forEach { t ->
                println("[GetAlbumWithTracksUseCase] Track: id=${t.id}, albumId=${t.albumId}, title=${t.title}, duration=${t.duration}, artist=${t.artist}")
            }
            return if (album != null) AlbumWithTracks(album, tracks) else null
        } catch (e: Exception) {
            println("[GetAlbumWithTracksUseCase] ERROR loading album or tracks for albumId=$albumId: ${e.message}")
            e.printStackTrace()
            return null
        }
    }
} 