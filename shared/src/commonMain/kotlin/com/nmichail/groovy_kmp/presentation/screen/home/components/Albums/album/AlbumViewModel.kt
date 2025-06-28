package com.nmichail.groovy_kmp.presentation.screen.home.components.Albums.album

import com.nmichail.groovy_kmp.domain.models.AlbumWithTracks
import com.nmichail.groovy_kmp.domain.usecases.GetAlbumWithTracksUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AlbumViewModel(
    private val getAlbumWithTracksUseCase: GetAlbumWithTracksUseCase
) {
    private val viewModelScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val _state = MutableStateFlow<AlbumWithTracks?>(null)
    val state: StateFlow<AlbumWithTracks?> = _state

    fun load(albumId: String) {
        viewModelScope.launch {
            println("AlbumViewModel: loading album with id=$albumId")
            try {
                val albumWithTracks = getAlbumWithTracksUseCase(albumId)
                println("AlbumViewModel: loaded $albumWithTracks")
                _state.value = albumWithTracks
            } catch (e: Exception) {
                println("AlbumViewModel: error loading album: ${e.message}")
            }
        }
    }
}