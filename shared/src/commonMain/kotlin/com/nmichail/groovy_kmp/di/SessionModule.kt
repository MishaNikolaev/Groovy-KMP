package com.nmichail.groovy_kmp.di

import com.nmichail.groovy_kmp.data.local.datasource.InMemoryUserSessionDataSource
import com.nmichail.groovy_kmp.data.manager.SessionManager
import com.nmichail.groovy_kmp.data.local.datasource.UserSessionDataSource
import com.nmichail.groovy_kmp.data.remote.AlbumApi
import com.nmichail.groovy_kmp.data.remote.TrackApi
import com.nmichail.groovy_kmp.data.repository.AlbumRepositoryImpl
import com.nmichail.groovy_kmp.data.repository.TrackRepositoryImpl
import com.nmichail.groovy_kmp.domain.repository.AlbumRepository
import com.nmichail.groovy_kmp.domain.repository.TrackRepository
import com.nmichail.groovy_kmp.domain.usecases.GetAlbumWithTracksUseCase
import com.nmichail.groovy_kmp.presentation.screen.home.components.Albums.album.AlbumViewModel
import com.nmichail.groovy_kmp.presentation.screen.home.HomeViewModel
import org.koin.dsl.module

val sessionModuleCommon = module {
    single<UserSessionDataSource> { InMemoryUserSessionDataSource() }
    single { SessionManager(get()) }
    single { AlbumApi(get()) }
    single { TrackApi(get()) }
    single<AlbumRepository> { AlbumRepositoryImpl(get()) }
    single<TrackRepository> { TrackRepositoryImpl(get()) }
    single { GetAlbumWithTracksUseCase(get(), get()) }
    single { AlbumViewModel(get()) }
    factory { HomeViewModel(get(), get()) }
} 