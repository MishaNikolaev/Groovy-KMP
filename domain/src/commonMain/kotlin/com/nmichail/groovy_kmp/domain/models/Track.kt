package com.nmichail.groovy_kmp.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Track(
    val id: String? = null,
    val title: String? = null,
    val artist: String? = null,
    val artistId: String? = null,
    val albumId: String? = null,
    val coverUrl: String? = null,
    val duration: Int? = null,
    val storagePath: String? = null,
    val coverColor: Long? = null,
    val lyrics: Lyrics? = null,
    val videoUrl: String? = null,
    val playedAt: Long? = null
)