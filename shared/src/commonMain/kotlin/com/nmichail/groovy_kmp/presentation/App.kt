package com.nmichail.groovy_kmp.presentation

import androidx.compose.runtime.Composable
import com.nmichail.groovy_kmp.presentation.navigation.Navigation
import moe.tlaster.precompose.PreComposeApp

@Composable
fun App() {
    PreComposeApp {
        Navigation()
    }
}