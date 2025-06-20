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

@Composable
fun App(viewModel: LoginViewModel) {
    var loginResult by remember { mutableStateOf<String?>(null) }

    if (loginResult == null) {
        LoginScreen(
            onSignIn = { email, password ->
                viewModel.login(email, password) { isSuccess ->
                    loginResult = if (isSuccess) "success" else "fail"
                }
            },
            onCreateAccount = {},
            isLoading = viewModel.isLoading,
            errorMessage = viewModel.errorMessage
        )
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(loginResult ?: "")
        }
    }
}