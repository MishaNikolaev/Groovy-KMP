package com.nmichail.groovy_kmp.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.nmichail.groovy_kmp.presentation.navigation.Navigation
import com.nmichail.groovy_kmp.presentation.screen.player.PlayerBar
import moe.tlaster.precompose.PreComposeApp

@Composable
fun App() {
    PreComposeApp {
        Box(modifier = Modifier.fillMaxSize()) {
            Navigation()
            Box(modifier = Modifier.fillMaxSize()) {
                Navigation()
                PlayerBar(modifier = Modifier.align(Alignment.BottomCenter))
            }
        }
    }
}