package com.nmichail.groovy_kmp.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.nmichail.groovy_kmp.presentation.navigation.Navigation
import moe.tlaster.precompose.PreComposeApp

@Composable
fun App() {
    PreComposeApp {
        Box(modifier = Modifier.fillMaxSize()) {
            Navigation()
        }
    }
}