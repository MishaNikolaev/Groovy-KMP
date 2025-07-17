package com.nmichail.groovy_kmp.presentation.screen.home

import com.nmichail.groovy_kmp.data.local.AlbumCache
import com.nmichail.groovy_kmp.domain.models.Album
import com.nmichail.groovy_kmp.domain.models.Track
import com.nmichail.groovy_kmp.domain.repository.AlbumRepository
import com.nmichail.groovy_kmp.domain.repository.TrackRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
            // 1. Сначала пробуем загрузить из кэша
            val cachedAlbums = withContext(Dispatchers.Default) {
                AlbumCache.loadAlbums()
            }
            if (cachedAlbums != null && cachedAlbums.isNotEmpty()) {
                _albums.value = cachedAlbums
            }
            // 2. Потом обновляем из сети
            val loadedAlbums = albumRepository.getAlbums()
            _albums.value = loadedAlbums
            // 3. Сохраняем в кэш
            withContext(Dispatchers.Default) {
                AlbumCache.saveAlbums(loadedAlbums)
            }
            val loadedTracks = trackRepository.getTopTracks()
            _tracks.value = loadedTracks
        }
    }
}

object PlatformUtils {
    fun isAndroid(): Boolean = PlatformUtils::class.simpleName?.contains("android", true) == true
}