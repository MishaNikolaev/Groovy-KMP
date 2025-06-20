package com.nmichail.groovy_kmp.di

import LoginViewModel
import com.nmichail.groovy_kmp.data.remote.AuthApi
import com.nmichail.groovy_kmp.data.repositoryImpl.AuthRepositoryImpl
import com.nmichail.groovy_kmp.domain.repository.AuthRepository
import com.nmichail.groovy_kmp.domain.usecases.LoginUseCase
import io.ktor.client.*
import org.koin.dsl.module

val appModule = module {
    single { provideHttpClient() }
    single { AuthApi(get()) }
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single { LoginUseCase(get()) }
    factory { LoginViewModel(get()) }
}