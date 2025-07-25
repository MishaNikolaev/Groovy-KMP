package com.nmichail.groovy_kmp.presentation.screen.home.components.recent

import com.nmichail.groovy_kmp.data.remote.TrackApi
import com.nmichail.groovy_kmp.domain.models.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RecentTracksViewModel(
    private val trackApi: TrackApi
) {
    private val viewModelScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val _tracks = MutableStateFlow<List<Track>>(emptyList())
    val tracks: StateFlow<List<Track>> = _tracks

    fun load() {
        viewModelScope.launch {
            val loaded = trackApi.getRecentTracks()
            _tracks.value = loaded
        }
    }
} 