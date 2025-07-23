package com.nmichail.groovy_kmp.presentation.screen.home.components.Artists

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nmichail.groovy_kmp.presentation.AlbumFontFamily
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun ArtistCircle(name: String, imageRes: DrawableResource, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(150.dp)
            .clickable { onClick() }
    ) {
        Image(
            painter = painterResource(imageRes),
            contentDescription = name,
            modifier = Modifier
                .size(134.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                fontFamily = AlbumFontFamily
            ),
            maxLines = 2
        )
    }
}