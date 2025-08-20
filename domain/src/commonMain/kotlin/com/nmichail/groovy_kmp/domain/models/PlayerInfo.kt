package com.nmichail.groovy_kmp.domain.models

import com.nmichail.groovy_kmp.domain.models.Track

data class PlayerInfo(
    val track: Track? = null,
    val state: PlayerState = PlayerState.Idle,
    val progress: PlaybackProgress = PlaybackProgress(),
    val playlist: List<Track> = emptyList(),
    val playlistName: String = "",
    val isShuffleEnabled: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.None
) 