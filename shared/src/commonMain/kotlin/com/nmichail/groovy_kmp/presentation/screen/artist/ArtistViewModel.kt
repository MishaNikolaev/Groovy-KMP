package com.nmichail.groovy_kmp.presentation.screen.artist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.nmichail.groovy_kmp.domain.models.Album
import com.nmichail.groovy_kmp.domain.models.Track
import com.nmichail.groovy_kmp.domain.repository.AlbumRepository
import com.nmichail.groovy_kmp.domain.repository.TrackRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class ArtistScreenState(
    val artistName: String = "",
    val artistPhotoUrl: String? = null,
    val tracks: List<Track> = emptyList(),
    val albums: List<Album> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ArtistViewModel(
    private val trackRepository: TrackRepository,
    private val albumRepository: AlbumRepository
) {
    private val _state = MutableStateFlow(ArtistScreenState())
    val state: StateFlow<ArtistScreenState> = _state.asStateFlow()

    fun loadArtistData(artistName: String) {
        _state.value = _state.value.copy(isLoading = true, error = null, artistName = artistName)
        
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val tracks = trackRepository.getTracksByArtist(artistName)
                val albums = albumRepository.getAlbumsByArtist(artistName)
                
                // Получаем фото артиста из первого альбома или трека
                val artistPhotoUrl = albums.firstOrNull()?.artistPhotoUrl ?: 
                                   tracks.firstOrNull()?.coverUrl
                
                _state.value = _state.value.copy(
                    tracks = tracks,
                    albums = albums,
                    artistPhotoUrl = artistPhotoUrl,
                    isLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message ?: "Ошибка загрузки данных",
                    isLoading = false
                )
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
} 