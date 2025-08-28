package com.nmichail.groovy_kmp.presentation.screen.home.components.recent

// Data imports removed - these should be injected through DI
import com.nmichail.groovy_kmp.domain.models.Track
import com.nmichail.groovy_kmp.domain.repository.TrackRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform.getKoin


data class TrackGroup(
    val date: String,
    val tracks: List<Track>
)

class RecentTracksViewModel() {
    private val viewModelScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val _tracks = MutableStateFlow<List<Track>>(emptyList())
    val tracks: StateFlow<List<Track>> = _tracks
    
    private val _trackGroups = MutableStateFlow<List<TrackGroup>>(emptyList())
    val trackGroups: StateFlow<List<TrackGroup>> = _trackGroups

    fun load() {
        viewModelScope.launch {
            try {
                // Load recent tracks from local storage
                val trackRepository = getKoin().get<TrackRepository>()
                val recentTracks = trackRepository.getRecentTracks()
                println("[RecentTracksViewModel] Loaded ${recentTracks.size} recent tracks from local storage")
                
                _tracks.value = recentTracks
                _trackGroups.value = groupTracksByDate(recentTracks)
            } catch (e: Exception) {
                println("[RecentTracksViewModel] Error loading recent tracks: ${e.message}")
                _tracks.value = emptyList()
                _trackGroups.value = emptyList()
            }
        }
    }
    
    private fun groupTracksByDate(tracks: List<Track>): List<TrackGroup> {
        val filteredTracks = tracks.filter { it.playedAt != null }
        println("[RecentTracksViewModel] Filtered tracks with playedAt: ${filteredTracks.size}")
        
        val grouped = filteredTracks.groupBy { track ->
            "Recent" // Упрощаем группировку
        }
        
        val result = grouped.map { (date, tracksList) ->
            TrackGroup(date = date, tracks = tracksList)
        }.sortedByDescending { group ->
            when (group.date) {
                "Today" -> 0
                "Yesterday" -> 1
                else -> {
                    val firstTrack = group.tracks.firstOrNull()
                    firstTrack?.playedAt ?: 0L
                }
            }
        }
        
        println("[RecentTracksViewModel] Final result: ${result.size} groups")
        result.forEach { group ->
            println("[RecentTracksViewModel] Group '${group.date}': ${group.tracks.size} tracks")
        }
        
        return result
    }
    

} 