package com.nmichail.groovy_kmp.presentation.screen.home.components.recent

import com.nmichail.groovy_kmp.data.local.TrackCache
import com.nmichail.groovy_kmp.domain.models.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


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
                val history = TrackCache.loadHistory()
                println("[RecentTracksViewModel] Raw history loaded: ${history?.size ?: 0} tracks")
                history?.forEach { track ->
                    println("[RecentTracksViewModel] Track: ${track.title}, playedAt: ${track.playedAt}")
                }
                
                _tracks.value = history ?: emptyList()
                
                val groups = groupTracksByDate(history ?: emptyList())
                println("[RecentTracksViewModel] Created ${groups.size} groups")
                groups.forEach { group ->
                    println("[RecentTracksViewModel] Group: ${group.date}, tracks: ${group.tracks.size}")
                }
                _trackGroups.value = groups
                
                println("[RecentTracksViewModel] Loaded ${history?.size ?: 0} tracks from history, grouped into ${groups.size} days")
            } catch (e: Exception) {
                println("[RecentTracksViewModel] Error loading history: ${e.message}")
                e.printStackTrace()
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