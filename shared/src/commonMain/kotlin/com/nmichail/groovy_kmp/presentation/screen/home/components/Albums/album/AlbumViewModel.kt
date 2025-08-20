package com.nmichail.groovy_kmp.presentation.screen.home.components.Albums.album

import androidx.compose.ui.graphics.Color
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

    private var lastLoadedAlbumId: String? = null
    private var albumBackgroundColor: Color = Color(0xFFAAA287)
    private val albumColors = mutableMapOf<String, Color>()

    private val albumCache = mutableMapOf<String, AlbumWithTracks>()

    fun load(albumId: String) {
        albumCache[albumId]?.let {
            _state.value = it
            lastLoadedAlbumId = albumId
            return
        }

        if (lastLoadedAlbumId != albumId) {
            _state.value = null
        }

        viewModelScope.launch {
            try {
                val albumWithTracks = getAlbumWithTracksUseCase(albumId)
                if (albumWithTracks != null) {
                    _state.value = albumWithTracks
                    lastLoadedAlbumId = albumId
                    albumCache[albumId] = albumWithTracks
                    albumWithTracks.album.coverUrl?.let { url ->
                        val generatedColor = generateColorFromUrl(url)
                        albumBackgroundColor = generatedColor
                    }
                    println("[AlbumViewModel] Album loaded successfully: ${albumWithTracks.album.title}")
                } else {
                    println("[AlbumViewModel] Album not found: $albumId")
                    _state.value = null
                }
            } catch (e: Exception) {
                println("[AlbumViewModel] Error loading album $albumId: ${e.message}")
                e.printStackTrace()
                _state.value = null
            }
        }
    }

    fun getBackgroundColor(): Color = albumBackgroundColor

    fun setBackgroundColor(color: Color) {
        albumBackgroundColor = color
    }

    fun setAlbumColor(albumId: String, color: Color) {
        if (color == Color(0xFFAAA287)) {
            return
        }
        albumColors[albumId] = color
    }

    fun getAlbumCoverColor(albumId: String?): Color {
        if (albumId == null) return Color(0xFFAAA287)
        val album = albumCache[albumId]
        return Color(0xFFAAA287)
    }

    private fun generateColorFromUrl(url: String?): Color {
        if (url == null) return Color(0xFFAAA287)

        val hash = url.hashCode()
        val hue = (hash % 360).toFloat()

        val saturation = when {
            hash % 3 == 0 -> 0.4f
            hash % 3 == 1 -> 0.25f
            else -> 0.15f
        }

        val lightness = when {
            hash % 4 == 0 -> 0.35f
            hash % 4 == 1 -> 0.45f
            hash % 4 == 2 -> 0.55f
            else -> 0.4f
        }

        val c = (1 - kotlin.math.abs(2 * lightness - 1)) * saturation
        val x = c * (1 - kotlin.math.abs((hue / 60) % 2 - 1))
        val m = lightness - c / 2

        val (r, g, b) = when {
            hue < 60 -> Triple(c, x, 0f)
            hue < 120 -> Triple(x, c, 0f)
            hue < 180 -> Triple(0f, c, x)
            hue < 240 -> Triple(0f, x, c)
            hue < 300 -> Triple(x, 0f, c)
            else -> Triple(c, 0f, x)
        }

        return Color(
            red = (r + m).coerceIn(0f, 1f),
            green = (g + m).coerceIn(0f, 1f),
            blue = (b + m).coerceIn(0f, 1f),
            alpha = 0.9f
        )
    }
}