package com.nmichail.groovy_kmp.feature.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
// Resources removed to avoid circular dependency
import androidx.compose.material.icons.filled.Check
import androidx.compose.ui.draw.alpha
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun ProfileScreen(
    email: String?,
    username: String?,
    onLogout: () -> Unit,
    onBack: (() -> Unit)? = null
) {
    var showDialog by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
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
                .background(Color.White),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp)
                    .clickable { /* TODO: обработка клика по My likes */ },
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
                    // Avatar placeholder - removed resource dependency
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color.Gray)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = username ?: "User Name",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = email ?: "email@example.com",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFFB0B0B0), fontSize = 14.sp, fontWeight = FontWeight.Medium),
                        color = Color(0xFFB0B0B0)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Theme",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, fontSize = 22.sp),
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .border(
                            width = 1.5.dp,
                            color = Color(0xFFB0B0B0),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    shadowElevation = 1.dp,
                    color = Color.White,
                    onClick = { /* TODO: выбрать светлую тему */ }
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("☀️", fontSize = 28.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Light", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium, fontSize = 15.sp))
                    }
                }
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .border(
                            width = 1.5.dp,
                            color = Color(0xFF444444),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    shadowElevation = 1.dp,
                    color = Color.White,
                    onClick = { /* TODO: выбрать тёмную тему */ }
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🌑",
                            fontSize = 28.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Dark", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium, fontSize = 15.sp))
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Choose your color",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, fontSize = 22.sp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("🎨", fontSize = 22.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))

            var selectedColor by remember { mutableStateOf("dark_gray") }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ColorRowOption(
                    text = "Dark Gray",
                    color = Color.DarkGray,
                    isSelected = selectedColor == "dark_gray",
                    onClick = { selectedColor = "dark_gray" }
                )
                ColorRowOption(
                    text = "Green",
                    color = Color(0xFF4CAF50),
                    isSelected = selectedColor == "green",
                    onClick = { selectedColor = "green" }
                )
                ColorRowOption(
                    text = "Blue",
                    color = Color(0xFF2196F3),
                    isSelected = selectedColor == "blue",
                    onClick = { selectedColor = "blue" }
                )
                ColorRowOption(
                    text = "Purple",
                    color = Color(0xFF673AB7),
                    isSelected = selectedColor == "purple",
                    onClick = { selectedColor = "purple" }
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            SettingsRow(
                text = "Edit photo and name",
                onClick = { /* TODO: Navigate to edit profile screen */ }
            )
            Divider(color = Color.LightGray.copy(alpha = 0.5f))
            SettingsRow(
                text = "Delete account",
                color = MaterialTheme.colorScheme.error,
                onClick = { /* TODO: Show delete confirmation */ }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Log out", fontWeight = FontWeight.Bold) },
                text = { Text("Are you sure you want to log out of your account???") },
                confirmButton = {
                    TextButton(onClick = {
                        showDialog = false
                        onLogout()
                    }) {
                        Text("Yes", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("No", color = Color.Black)
                    }
                },
                containerColor = Color.White
            )
        }
    }
}
