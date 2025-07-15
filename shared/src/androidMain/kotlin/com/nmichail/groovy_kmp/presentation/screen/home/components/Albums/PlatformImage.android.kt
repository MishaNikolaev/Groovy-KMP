package com.nmichail.groovy_kmp.presentation.screen.home.components.Albums

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.graphics.asAndroidBitmap
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import androidx.palette.graphics.Palette

@Composable
actual fun PlatformImage(
    url: String?, 
    contentDescription: String?, 
    modifier: Modifier,
    onColorExtracted: ((Color) -> Unit)?
) {
    if (url != null) {
        var extracted by remember { mutableStateOf(false) }
        val painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(url)
                .build()
        )
        val state = painter.state
        if (!extracted && state is AsyncImagePainter.State.Success && onColorExtracted != null) {
            val bitmap = (state.result.drawable as? android.graphics.drawable.BitmapDrawable)?.bitmap
            bitmap?.let {
                Palette.from(it).generate { palette ->
                    val dominant = palette?.getDominantSwatch()?.rgb
                    val vibrant = palette?.vibrantSwatch?.rgb
                    val muted = palette?.mutedSwatch?.rgb
                    val lightVibrant = palette?.lightVibrantSwatch?.rgb
                    val lightMuted = palette?.lightMutedSwatch?.rgb
                    val darkVibrant = palette?.darkVibrantSwatch?.rgb
                    val darkMuted = palette?.darkMutedSwatch?.rgb
                    android.util.Log.d("PaletteDebug", "dominant=$dominant vibrant=$vibrant muted=$muted lightVibrant=$lightVibrant lightMuted=$lightMuted darkVibrant=$darkVibrant darkMuted=$darkMuted")
                    val colorInt = vibrant ?: dominant ?: muted ?: lightVibrant ?: lightMuted ?: darkVibrant ?: darkMuted
                    android.util.Log.d("PaletteDebug", "selectedColor=$colorInt")
                    if (colorInt != null) {
                        onColorExtracted(Color(colorInt))
                    }
                }
            }
            extracted = true
        }
        Image(
            painter = painter,
            contentDescription = contentDescription,
            modifier = modifier.fillMaxSize()
        )
    } else {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.LightGray)
        )
    }
} 