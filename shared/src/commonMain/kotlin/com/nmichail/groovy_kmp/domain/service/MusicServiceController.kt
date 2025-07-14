package com.nmichail.groovy_kmp.domain

import com.nmichail.groovy_kmp.domain.models.Track

interface MusicServiceController {
    fun play(playlist: List<Track>, index: Int)
    fun pause(playlist: List<Track>, index: Int)
    fun resume(playlist: List<Track>, index: Int)
    fun next(playlist: List<Track>, index: Int)
    fun previous(playlist: List<Track>, index: Int)
    fun seekTo(playlist: List<Track>, index: Int, position: Long)
} 