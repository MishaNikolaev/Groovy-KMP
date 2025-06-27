package com.nmichail.groovy_kmp.presentation.screen.home

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
            println("Start loading albums")
            val loadedAlbums = albumRepository.getAlbums()
            println("Loaded albums: $loadedAlbums")
            _albums.value = loadedAlbums

            val loadedTracks = trackRepository.getTopTracks()
            println("Loaded tracks: $loadedTracks")
            _tracks.value = loadedTracks
        }
    }
}