package com.nmichail.groovy_kmp.presentation.screen.artists

import com.nmichail.groovy_kmp.domain.models.Album
import com.nmichail.groovy_kmp.domain.models.Track
import com.nmichail.groovy_kmp.domain.repository.AlbumRepository
import com.nmichail.groovy_kmp.domain.repository.TrackRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class ArtistInfo(
    val name: String,
    val photoUrl: String?
)

data class AllArtistsScreenState(
    val artists: List<ArtistInfo> = emptyList(),
    val filteredArtists: List<ArtistInfo> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class AllArtistsViewModel(
    private val albumRepository: AlbumRepository,
    private val trackRepository: TrackRepository
) {
    private val viewModelScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val _state = MutableStateFlow(AllArtistsScreenState())
    val state: StateFlow<AllArtistsScreenState> = _state

    fun loadArtists() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            
            try {
                val albums = albumRepository.getAlbums()
                val tracks = trackRepository.getTracks()
                
                val artistPhotos = mutableMapOf<String, String?>()
                
                albums.forEach { album ->
                    album.artist?.let { artistName ->
                        if (!artistPhotos.containsKey(artistName)) {
                            artistPhotos[artistName] = album.artistPhotoUrl
                        }
                    }
                }
                
                tracks.forEach { track ->
                    track.artist?.let { artistName ->
                        if (!artistPhotos.containsKey(artistName)) {
                            artistPhotos[artistName] = track.coverUrl
                        }
                    }
                }
                
                val allArtists = artistPhotos.map { (name, photoUrl) ->
                    ArtistInfo(name, photoUrl)
                }.sortedBy { it.name }
                
                println("[AllArtistsViewModel] Loaded ${allArtists.size} artists:")
                allArtists.forEach { artist ->
                    println("[AllArtistsViewModel] Artist: ${artist.name}, Photo: ${artist.photoUrl}")
                }
                
                _state.value = _state.value.copy(
                    artists = allArtists,
                    filteredArtists = allArtists,
                    isLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error"
                )
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        val filtered = if (query.isBlank()) {
            _state.value.artists
        } else {
            _state.value.artists.filter { 
                it.name.contains(query, ignoreCase = true) 
            }
        }
        
        _state.value = _state.value.copy(
            searchQuery = query,
            filteredArtists = filtered
        )
    }
} 