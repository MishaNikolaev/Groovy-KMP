package com.nmichail.groovy_kmp.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Album(
    val id: String? = null,
    val title: String? = null,
    val artist: String? = null,
    val coverUrl: String? = null,
    val createdAt: String? = null,
    val genre: String? = null,
    val artistPhotoUrl: String? = null
) 