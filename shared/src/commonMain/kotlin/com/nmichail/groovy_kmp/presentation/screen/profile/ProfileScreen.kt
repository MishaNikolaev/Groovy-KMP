package com.nmichail.groovy_kmp.presentation.screen.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import groovy_kmp.shared.generated.resources.Res
import groovy_kmp.shared.generated.resources.profile_image_gray

@Composable
fun ProfileScreen(
    email: String?,
    username: String?,
    onLogout: () -> Unit,
    onBack: (() -> Unit)? = null
) {
    var showDialog by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(start = 8.dp, end = 8.dp, bottom = 0.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.width(24.dp))
            Text(
                text = "Profile",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp),
                color = Color.Black
            )
            IconButton(onClick = { showDialog = true }) {
                Icon(Icons.Filled.ExitToApp, contentDescription = "Logout", tint = Color.Black, modifier = Modifier.size(24.dp))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF23243A), Color(0xFF181926))
                    )
                ),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            brush = Brush.sweepGradient(
                                listOf(
                                    Color(0xFFE94057),
                                    Color(0xFF8A2387),
                                    Color(0xFF4A90E2),
                                    Color(0xFFE94057)
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(Res.drawable.profile_image_gray),
                        contentDescription = "Avatar",
                        modifier = Modifier.size(56.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = username ?: "User Name",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = email ?: "email@example.com",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFFB0B0B0), fontSize = 14.sp),
                        color = Color(0xFFB0B0B0)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Log out") },
                text = { Text("Are you sure you want to log out of your account??") },
                confirmButton = {
                    TextButton(onClick = {
                        showDialog = false
                        onLogout()
                    }) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("No")
                    }
                }
            )
        }
    }
}