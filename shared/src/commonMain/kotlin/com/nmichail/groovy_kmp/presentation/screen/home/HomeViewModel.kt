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

    fun load() {
        viewModelScope.launch {
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
            val loadedTracks = trackRepository.getTopTracks()
            _tracks.value = loadedTracks
        }
    }
}

object PlatformUtils {
    fun isAndroid(): Boolean = PlatformUtils::class.simpleName?.contains("android", true) == true
}