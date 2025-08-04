package com.nmichail.groovy_kmp.presentation.screen.favourite

import com.nmichail.groovy_kmp.data.local.TrackCache
import com.nmichail.groovy_kmp.data.local.AlbumCache
import com.nmichail.groovy_kmp.domain.models.Track
import com.nmichail.groovy_kmp.domain.models.Album
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class MostListenedArtistsState(
    val artists: List<com.nmichail.groovy_kmp.presentation.screen.artists.ArtistInfo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class MostListenedArtistsViewModel {
    private val viewModelScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val _state = MutableStateFlow(MostListenedArtistsState())
    val state: StateFlow<MostListenedArtistsState> = _state

    fun loadMostListenedArtists() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            
            try {
                val history = TrackCache.loadHistory()
                val albums = AlbumCache.loadAlbums()
                println("[MostListenedArtistsViewModel] Loaded ${history?.size ?: 0} tracks from history and ${albums?.size ?: 0} albums")
                
                if (history.isNullOrEmpty()) {
                    _state.value = _state.value.copy(
                        artists = emptyList(),
                        isLoading = false
                    )
                    return@launch
                }
                
                // Подсчитываем количество прослушиваний для каждого артиста
                val artistPlayCount = mutableMapOf<String, Int>()
                val artistPhotos = mutableMapOf<String, String?>()
                
                // Сначала собираем фото артистов из альбомов (приоритет)
                albums?.forEach { album ->
                    album.artist?.let { artistName ->
                        if (!artistPhotos.containsKey(artistName) && !album.artistPhotoUrl.isNullOrBlank()) {
                            artistPhotos[artistName] = album.artistPhotoUrl
                            println("[MostListenedArtistsViewModel] Found artist photo for $artistName: ${album.artistPhotoUrl}")
                        }
                    }
                }
                
                // Затем подсчитываем прослушивания и добавляем фото из треков если нет в альбомах
                history.forEach { track ->
                    track.artist?.let { artistName ->
                        artistPlayCount[artistName] = (artistPlayCount[artistName] ?: 0) + 1
                        
                        // Добавляем фото артиста из трека только если нет фото из альбома
                        if (!artistPhotos.containsKey(artistName) && !track.coverUrl.isNullOrBlank()) {
                            artistPhotos[artistName] = track.coverUrl
                            println("[MostListenedArtistsViewModel] Using track cover for $artistName: ${track.coverUrl}")
                        }
                    }
                }
                
                // Сортируем артистов по количеству прослушиваний (по убыванию)
                val sortedArtists = artistPlayCount.entries
                    .sortedByDescending { it.value }
                    .map { (artistName, playCount) ->
                        println("[MostListenedArtistsViewModel] Artist: $artistName, plays: $playCount, photo: ${artistPhotos[artistName]}")
                        com.nmichail.groovy_kmp.presentation.screen.artists.ArtistInfo(
                            name = artistName,
                            photoUrl = artistPhotos[artistName]
                        )
                    }
                
                println("[MostListenedArtistsViewModel] Found ${sortedArtists.size} artists")
                _state.value = _state.value.copy(
                    artists = sortedArtists,
                    isLoading = false
                )
                
            } catch (e: Exception) {
                println("[MostListenedArtistsViewModel] Error loading most listened artists: ${e.message}")
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error"
                )
            }
        }
    }
} 