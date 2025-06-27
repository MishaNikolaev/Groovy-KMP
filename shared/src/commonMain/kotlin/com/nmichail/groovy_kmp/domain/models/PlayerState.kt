package com.nmichail.groovy_kmp.domain.models

sealed class PlayerState {
    object Idle : PlayerState()
    object Buffering : PlayerState()
    object Playing : PlayerState()
    object Paused : PlayerState()
    object Completed : PlayerState()
    data class Error(val message: String) : PlayerState()
} 