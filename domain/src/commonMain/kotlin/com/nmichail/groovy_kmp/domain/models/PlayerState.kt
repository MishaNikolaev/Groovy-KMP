package com.nmichail.groovy_kmp.domain.models

sealed interface PlayerState {
    data object Idle : PlayerState
    data object Loading : PlayerState
    data class Playing(val track: Track) : PlayerState
    data class Paused(val track: Track) : PlayerState
    data class Stopped(val track: Track) : PlayerState
    data class Error(val message: String) : PlayerState
}

enum class RepeatMode {
    None,
    One,
    All
} 