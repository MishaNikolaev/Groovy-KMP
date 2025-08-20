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
    
    single<PlayerRepository> { PlayerRepositoryImpl() }
    
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
    factory { PlayerViewModel(get(), get()) }
    factory { RecentTracksViewModel() }
} 