package com.nmichail.groovy_kmp.di

import LoginViewModel
import com.nmichail.groovy_kmp.data.remote.AuthApi
import com.nmichail.groovy_kmp.data.repositoryImpl.AuthRepositoryImpl
import com.nmichail.groovy_kmp.data.repositoryImpl.RegisterRepositoryImpl
import com.nmichail.groovy_kmp.domain.repository.AuthRepository
import com.nmichail.groovy_kmp.domain.repository.RegisterRepository
import com.nmichail.groovy_kmp.domain.usecases.LoginUseCase
import com.nmichail.groovy_kmp.domain.usecases.RegisterUseCase
import com.nmichail.groovy_kmp.presentation.screen.register.RegisterViewModel
import io.ktor.client.*
import org.koin.dsl.module

val appModule = module {
    single { provideHttpClient() }
    single { AuthApi(get()) }

    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single { LoginUseCase(get()) }
    factory { LoginViewModel(get()) }

    factory { RegisterViewModel(get()) }
    single<RegisterRepository> { RegisterRepositoryImpl(get()) }
    single { RegisterUseCase(get()) }
}

val allModules = listOf(appModule, sessionModuleCommon, playerModule)