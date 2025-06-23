package com.nmichail.groovy_kmp.presentation

import LoginViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.nmichail.groovy_kmp.presentation.screen.login.LoginScreen
import com.nmichail.groovy_kmp.presentation.screen.register.RegisterScreen
import com.nmichail.groovy_kmp.presentation.screen.register.RegisterViewModel
import org.koin.mp.KoinPlatform.getKoin

@Composable
fun App() {
    var registerResult by remember { mutableStateOf<String?>(null) }
    val viewModel = getKoin().get<RegisterViewModel>()

    if (registerResult == null) {
        RegisterScreen(
            onRegister = { email, password, username ->
                viewModel.register(email, password, username) { isSuccess ->
                    registerResult = if (isSuccess) "success" else "fail"
                }
            },
            onLogin = {},
            isLoading = viewModel.isLoading,
            errorMessage = viewModel.errorMessage
        )
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(registerResult ?: "")
        }
    }
}