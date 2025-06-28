package com.nmichail.groovy_kmp.presentation.screen.home.components.Albums

import androidx.compose.runtime.Composable
import coil.compose.AsyncImage
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.graphics.Color

@Composable
actual fun PlatformImage(url: String?, contentDescription: String?, modifier: Modifier) {
    if (url != null) { // it's for loading images, vou
        AsyncImage(
            model = url,
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize()
        )
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray)
        )
    }
} 