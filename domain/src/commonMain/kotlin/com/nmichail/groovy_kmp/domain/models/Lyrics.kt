package com.nmichail.groovy_kmp.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Lyrics(
    val lines: List<LyricLine> = emptyList()
)

@Serializable
data class LyricLine(
    val timeMs: Long = 0L,
    val text: String = ""
)