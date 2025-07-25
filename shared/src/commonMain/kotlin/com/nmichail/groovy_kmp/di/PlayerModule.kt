package com.nmichail.groovy_kmp.di

import com.nmichail.groovy_kmp.data.repository.PlayerRepositoryImpl
import com.nmichail.groovy_kmp.domain.MusicServiceController
import com.nmichail.groovy_kmp.domain.models.Track
import com.nmichail.groovy_kmp.domain.repository.PlayerRepository
import com.nmichail.groovy_kmp.domain.usecases.*
import com.nmichail.groovy_kmp.presentation.screen.player.PlayerViewModel
import com.nmichail.groovy_kmp.presentation.screen.home.components.recent.RecentTracksViewModel
import org.koin.dsl.module

val playerModule = module {
    
    // Repository
    single<PlayerRepository> { PlayerRepositoryImpl() }
    
    // Use Cases
    factory { PlayTrackUseCase(get()) }
    factory { PauseTrackUseCase(get()) }
    factory { ResumeTrackUseCase(get()) }
    factory { StopTrackUseCase(get()) }
    factory { SkipToNextUseCase(get()) }
    factory { SkipToPreviousUseCase(get()) }
    factory { SeekToUseCase(get()) }
    factory { UpdateTrackDurationUseCase(get()) }
    factory { ToggleShuffleUseCase(get()) }
    factory { ToggleRepeatModeUseCase(get()) }
    factory { GetPlayerInfoFlowUseCase(get()) }
    factory { SetPlaylistUseCase(get()) }
    
    // ViewModels
    factory {
        PlayerUseCases(
            playTrack = get(),
            pauseTrack = get(),
            resumeTrack = get(),
            stopTrack = get(),
            skipToNext = get(),
            skipToPrevious = get(),
            seekTo = get(),
            updateTrackDuration = get(),
            toggleShuffle = get(),
            toggleRepeatMode = get(),
            getPlayerInfo = get(),
            setPlaylist = get()
        )
    }
    single<MusicServiceController> { object : MusicServiceController {
        override fun play(playlist: List<Track>, index: Int) {}
        override fun pause(playlist: List<Track>, index: Int) {}
        override fun resume(playlist: List<Track>, index: Int) {}
        override fun next(playlist: List<Track>, index: Int) {}
        override fun previous(playlist: List<Track>, index: Int) {}
        override fun seekTo(playlist: List<Track>, index: Int, position: Long) {}
    } }
    factory { PlayerViewModel(get(), get()) }
    factory { RecentTracksViewModel(get()) }
} 