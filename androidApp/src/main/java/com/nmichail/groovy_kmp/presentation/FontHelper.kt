package com.nmichail.groovy_kmp.presentation

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.nmichail.groovy_kmp.android.R

fun getAlbumFontFamily(): FontFamily {
    return FontFamily(
        Font(
            resId = R.font.ysmusic_headlinebold,
            weight = FontWeight.Normal
        )
    )
} 