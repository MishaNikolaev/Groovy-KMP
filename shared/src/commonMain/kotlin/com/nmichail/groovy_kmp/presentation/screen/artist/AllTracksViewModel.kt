package com.nmichail.groovy_kmp.presentation.screen.artist

import com.nmichail.groovy_kmp.domain.models.Track
import com.nmichail.groovy_kmp.domain.models.Album
import com.nmichail.groovy_kmp.domain.repository.TrackRepository
import com.nmichail.groovy_kmp.domain.repository.AlbumRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AllTracksScreenState(
    val tracks: List<Track> = emptyList(),
    val artistPhotoUrl: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class AllTracksViewModel(
    private val trackRepository: TrackRepository,
    private val albumRepository: AlbumRepository
) {
    private val _state = MutableStateFlow(AllTracksScreenState())
    val state: StateFlow<AllTracksScreenState> = _state.asStateFlow()

    suspend fun loadTracksByArtist(artistName: String) {
        _state.value = _state.value.copy(isLoading = true, error = null)
        try {
            val tracks = trackRepository.getTracksByArtist(artistName)
            val albums = albumRepository.getAlbumsByArtist(artistName)
            
            val artistPhotoUrl = albums.firstOrNull()?.artistPhotoUrl ?: 
                               tracks.firstOrNull()?.coverUrl
            
            _state.value = _state.value.copy(
                tracks = tracks,
                artistPhotoUrl = artistPhotoUrl,
                isLoading = false
            )
        } catch (e: Exception) {
            _state.value = _state.value.copy(
                error = e.message ?: "Ошибка загрузки треков",
                isLoading = false
            )
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
} 