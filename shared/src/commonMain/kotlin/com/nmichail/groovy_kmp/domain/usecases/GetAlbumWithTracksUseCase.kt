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
        val album = albumRepository.getAlbum(albumId) ?: return null
        val tracks = trackRepository.getTracksByAlbum(albumId)
        return AlbumWithTracks(album, tracks)
    }
} 