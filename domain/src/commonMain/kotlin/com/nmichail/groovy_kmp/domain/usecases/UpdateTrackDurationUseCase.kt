package com.nmichail.groovy_kmp.domain.usecases

import com.nmichail.groovy_kmp.domain.repository.PlayerRepository

class UpdateTrackDurationUseCase(private val playerRepository: PlayerRepository) {
    suspend operator fun invoke(durationMs: Long) {
        println("[UpdateTrackDurationUseCase] Invoking with duration: $durationMs ms")
        playerRepository.updateCurrentTrackDuration(durationMs)
        println("[UpdateTrackDurationUseCase] Duration update completed")
    }
}