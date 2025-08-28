package com.nmichail.groovy_kmp.domain.usecases

import com.nmichail.groovy_kmp.domain.models.PlayerInfo
import com.nmichail.groovy_kmp.domain.models.Track
import com.nmichail.groovy_kmp.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.StateFlow

data class PlayerUseCases(
    val playTrack: PlayTrackUseCase,
    val pauseTrack: PauseTrackUseCase,
    val resumeTrack: ResumeTrackUseCase,
    val stopTrack: StopTrackUseCase,
    val skipToNext: SkipToNextUseCase,
    val skipToPrevious: SkipToPreviousUseCase,
    val seekTo: SeekToUseCase,
    val updateTrackDuration: UpdateTrackDurationUseCase,
    val toggleShuffle: ToggleShuffleUseCase,
    val toggleRepeatMode: ToggleRepeatModeUseCase,
    val getPlayerInfo: GetPlayerInfoFlowUseCase,
    val setPlaylist: SetPlaylistUseCase
)

class GetPlayerInfoFlowUseCase(private val playerRepository: PlayerRepository) {
    operator fun invoke(): StateFlow<PlayerInfo> = playerRepository.playerInfo
}

class SetPlaylistUseCase(private val playerRepository: PlayerRepository) {
    suspend operator fun invoke(tracks: List<Track>, playlistName: String) =
        playerRepository.setPlaylist(tracks, playlistName)
}

class PlayTrackUseCase(private val playerRepository: PlayerRepository) {
    suspend operator fun invoke(track: Track) = playerRepository.play(track)
}

class PauseTrackUseCase(private val playerRepository: PlayerRepository) {
    suspend operator fun invoke() = playerRepository.pause()
}

class ResumeTrackUseCase(private val playerRepository: PlayerRepository) {
    suspend operator fun invoke() = playerRepository.resume()
}

class StopTrackUseCase(private val playerRepository: PlayerRepository) {
    suspend operator fun invoke() = playerRepository.stop()
}

class SkipToNextUseCase(private val playerRepository: PlayerRepository) {
    suspend operator fun invoke() = playerRepository.skipToNext()
}

class SkipToPreviousUseCase(private val playerRepository: PlayerRepository) {
    suspend operator fun invoke() = playerRepository.skipToPrevious()
}

class SeekToUseCase(private val playerRepository: PlayerRepository) {
    suspend operator fun invoke(position: Long) = playerRepository.seekTo(position)
}


class ToggleShuffleUseCase(private val playerRepository: PlayerRepository) {
    suspend operator fun invoke() = playerRepository.toggleShuffle()
}

class ToggleRepeatModeUseCase(private val playerRepository: PlayerRepository) {
    suspend operator fun invoke() = playerRepository.toggleRepeatMode()
}