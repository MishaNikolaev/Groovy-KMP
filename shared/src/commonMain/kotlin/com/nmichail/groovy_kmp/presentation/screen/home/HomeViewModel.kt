package com.nmichail.groovy_kmp.presentation.screen.home

import com.nmichail.groovy_kmp.data.local.AllAlbumsCache
import com.nmichail.groovy_kmp.domain.models.Album
import com.nmichail.groovy_kmp.domain.models.Track
import com.nmichail.groovy_kmp.domain.repository.AlbumRepository
import com.nmichail.groovy_kmp.domain.repository.TrackRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel(
    private val albumRepository: AlbumRepository,
    private val trackRepository: TrackRepository
) {
    private val viewModelScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val _albums = MutableStateFlow<List<Album>>(emptyList())
    val albums: StateFlow<List<Album>> = _albums

    private val _tracks = MutableStateFlow<List<Track>>(emptyList())
    val tracks: StateFlow<List<Track>> = _tracks

    private val _artists = MutableStateFlow<List<com.nmichail.groovy_kmp.presentation.screen.artists.ArtistInfo>>(emptyList())
    val artists: StateFlow<List<com.nmichail.groovy_kmp.presentation.screen.artists.ArtistInfo>> = _artists

    fun load() {
        viewModelScope.launch {
            // Загружаем альбомы
            try {
                val cachedAlbums = withContext(Dispatchers.Default) {
                    AllAlbumsCache.loadAllAlbums()
                }
                if (cachedAlbums != null && cachedAlbums.isNotEmpty()) {
                    _albums.value = cachedAlbums
                }
                val loadedAlbums = albumRepository.getAlbums()
                _albums.value = loadedAlbums
                withContext(Dispatchers.Default) {
                    AllAlbumsCache.saveAllAlbums(loadedAlbums)
                }
            } catch (e: Exception) {
                println("Error loading albums: ${e.message}")
            }
            
            try {
                val loadedTracks = trackRepository.getTopTracks()
                _tracks.value = loadedTracks
            } catch (e: Exception) {
                println("Error loading top tracks: ${e.message}")
            }
            
            try {
                val allTracks = trackRepository.getTracks()
                
                // Создаем Map для хранения фоток артистов
                val artistPhotos = mutableMapOf<String, String?>()
                
                // Собираем фотки из альбомов
                _albums.value.forEach { album ->
                    album.artist?.let { artistName ->
                        if (!artistPhotos.containsKey(artistName)) {
                            artistPhotos[artistName] = album.artistPhotoUrl
                        }
                    }
                }
                
                // Собираем фотки из треков (если нет в альбомах)
                allTracks.forEach { track ->
                    track.artist?.let { artistName ->
                        if (!artistPhotos.containsKey(artistName)) {
                            artistPhotos[artistName] = track.coverUrl
                        }
                    }
                }
                
                val allArtists = artistPhotos.map { (name, photoUrl) ->
                    com.nmichail.groovy_kmp.presentation.screen.artists.ArtistInfo(name, photoUrl)
                }.sortedBy { it.name }
                
                println("[HomeViewModel] Loaded ${allArtists.size} artists:")
                allArtists.forEach { artist ->
                    println("[HomeViewModel] Artist: ${artist.name}, Photo: ${artist.photoUrl}")
                }
                
                _artists.value = allArtists
            } catch (e: Exception) {
                println("Error loading artists: ${e.message}")
            }
        }
    }
}

object PlatformUtils {
    fun isAndroid(): Boolean = PlatformUtils::class.simpleName?.contains("android", true) == true
}