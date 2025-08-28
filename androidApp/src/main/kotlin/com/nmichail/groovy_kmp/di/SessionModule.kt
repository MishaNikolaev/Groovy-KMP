package com.nmichail.groovy_kmp.di

import com.nmichail.groovy_kmp.data.local.datasource.UserSessionDataSource
import com.nmichail.groovy_kmp.data.manager.SessionManager
import com.nmichail.groovy_kmp.data.remote.AlbumApi
import com.nmichail.groovy_kmp.data.remote.TrackApi
import com.nmichail.groovy_kmp.data.repository.AlbumRepositoryImpl
import com.nmichail.groovy_kmp.data.repository.TrackRepositoryImpl
import com.nmichail.groovy_kmp.domain.repository.AlbumRepository
import com.nmichail.groovy_kmp.domain.repository.TrackRepository
import com.nmichail.groovy_kmp.domain.usecases.GetAlbumWithTracksUseCase
import com.nmichail.groovy_kmp.presentation.screen.home.components.Albums.album.AlbumViewModel
import com.nmichail.groovy_kmp.presentation.screen.home.HomeViewModel
import com.nmichail.groovy_kmp.presentation.screen.home.components.recent.RecentTracksViewModel
import com.nmichail.groovy_kmp.presentation.screen.artist.ArtistViewModel
import com.nmichail.groovy_kmp.presentation.screen.artist.AllTracksViewModel
import com.nmichail.groovy_kmp.presentation.screen.artists.AllArtistsViewModel
import com.nmichail.groovy_kmp.presentation.screen.favourite.MostListenedArtistsViewModel
import com.nmichail.groovy_kmp.android.local.datasource.UserSessionDataSourceImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val sessionModuleCommon = module {
    single<UserSessionDataSource> { UserSessionDataSourceImpl(androidContext()) }
    single { SessionManager(get()) }
    single { AlbumApi(get()) }
    single { TrackApi(get()) }
    single<AlbumRepository> { AlbumRepositoryImpl(get()) }
    single<TrackRepository> { TrackRepositoryImpl(get()) }
    single { GetAlbumWithTracksUseCase(get(), get()) }
    single { AlbumViewModel(get()) }
    single { RecentTracksViewModel() }
    factory { HomeViewModel(get(), get()) }
    factory { ArtistViewModel(get(), get()) }
    factory { AllTracksViewModel(get(), get()) }
    factory { AllArtistsViewModel(get(), get()) }
    factory { MostListenedArtistsViewModel() }
} 