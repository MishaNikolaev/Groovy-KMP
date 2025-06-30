package com.nmichail.groovy_kmp.domain.models

data class PlaybackProgress(
    val currentPosition: Long = 0L,
    val totalDuration: Long = 0L
) 