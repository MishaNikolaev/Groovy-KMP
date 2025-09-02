package com.nmichail.groovy_kmp.di

import com.nmichail.groovy_kmp.data.remote.AuthApi
import com.nmichail.groovy_kmp.data.repository.AuthRepositoryImpl
import com.nmichail.groovy_kmp.data.repository.RegisterRepositoryImpl
import com.nmichail.groovy_kmp.domain.repository.AuthRepository
import com.nmichail.groovy_kmp.domain.repository.RegisterRepository
import com.nmichail.groovy_kmp.domain.usecases.LoginUseCase
import com.nmichail.groovy_kmp.domain.usecases.RegisterUseCase
import com.nmichail.groovy_kmp.data.remote.provideHttpClient
import com.nmichail.groovy_kmp.android.session.AndroidSessionViewModel
import com.nmichail.groovy_kmp.data.manager.SessionManager
import com.nmichail.groovy_kmp.feature.auth.login.LoginViewModel
import com.nmichail.groovy_kmp.feature.auth.register.RegisterViewModel
import com.nmichail.groovy_kmp.feature.core.viewmodel.SessionViewModel
import org.koin.dsl.module

val appModule = module {
    single { provideHttpClient() }
    single { AuthApi(get()) }

    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single { LoginUseCase(get()) }
    single<SessionViewModel> { AndroidSessionViewModel(get<SessionManager>()) }
    factory { LoginViewModel(get(), get()) }

    factory { RegisterViewModel(get()) }
    single<RegisterRepository> { RegisterRepositoryImpl(get()) }
    single { RegisterUseCase(get()) }
}

