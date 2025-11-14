package com.stromeese.appsofr.ui.screens.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stromeese.appsofr.data.database.AppDatabase
import com.stromeese.appsofr.data.preferences.SoundType
import com.stromeese.appsofr.data.preferences.UserPreferences
import com.stromeese.appsofr.data.repository.StormEaseRepository
import com.stromeese.appsofr.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModel.Factory(
            UserPreferences(LocalContext.current),
            StormEaseRepository(
                AppDatabase.getDatabase(LocalContext.current).activityDao(),
                AppDatabase.getDatabase(LocalContext.current).achievementDao(),
                AppDatabase.getDatabase(LocalContext.current).dailyStatsDao()
            )
        )
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val isDarkTheme = uiState.isDarkTheme
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        // Background
        OlympusSkyBackground(isDarkTheme = isDarkTheme)
        FloatingClouds(isDarkTheme = isDarkTheme)
        if (isDarkTheme) {
            Starfield()
        }

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Settings",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Theme Section
                item {
                    SectionHeader(title = "Appearance", icon = "🎨")
                }

                item {
                    ThemeSettingCard(
                        isDarkTheme = uiState.isDarkTheme,
                        onToggleTheme = { viewModel.toggleTheme() }
                    )
                }


                // Data Section
                item {
                    SectionHeader(title = "Data", icon = "💾")
                }

                item {
                    DangerCard(
                        title = "Reset All Data",
                        description = "Delete all activities and achievements",
                        icon = "⚠️",
                        onAction = { viewModel.showResetDialog() }
                    )
                }

                // About Section
                item {
                    SectionHeader(title = "About", icon = "ℹ️")
                }

                item {
                    DangerCard(
                        title = "Privacy Policy",
                        description = "Tap to Read",
                        icon = "⚖\uFE0F",
                        onAction = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://sttormease.com/privacy-policy.html"))
                            context.startActivity(intent)
                        }
                    )
                }

                item {
                    AboutCard()
                }

                // Bottom spacer
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        // Reset Dialog
        if (uiState.showResetDialog) {
            ResetDataDialog(
                onConfirm = { viewModel.resetAllData() },
                onDismiss = { viewModel.dismissResetDialog() },
                isResetting = uiState.isResetting
            )
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    icon: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Text(
            icon,
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun ThemeSettingCard(
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    OlympusCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Theme",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    if (isDarkTheme) "Night of Olympus" else "Day of Olympus",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ThemeButton(
                    icon = "☀️",
                    isSelected = !isDarkTheme,
                    onClick = { if (isDarkTheme) onToggleTheme() }
                )
                ThemeButton(
                    icon = "🌙",
                    isSelected = isDarkTheme,
                    onClick = { if (!isDarkTheme) onToggleTheme() }
                )
            }
        }
    }
}

@Composable
fun ThemeButton(
    icon: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) {
                    ElectricBlue.copy(alpha = 0.2f)
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            icon,
            style = MaterialTheme.typography.headlineMedium
        )
    }
}

@Composable
fun SettingToggleCard(
    title: String,
    description: String,
    icon: String,
    isEnabled: Boolean,
    onToggle: () -> Unit
) {
    OlympusCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                icon,
                style = MaterialTheme.typography.displaySmall
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Switch(
                checked = isEnabled,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = LightningWhite,
                    checkedTrackColor = ElectricBlue,
                    uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    }
}

@Composable
fun SoundTypeCard(
    selectedType: String,
    isEnabled: Boolean,
    onTypeSelected: (String) -> Unit
) {
    AnimatedVisibility(
        visible = isEnabled,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        OlympusCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Sound Type",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SoundTypeItem(
                        type = SoundType.THUNDER,
                        isSelected = selectedType == "thunder",
                        onClick = { onTypeSelected("thunder") }
                    )
                    SoundTypeItem(
                        type = SoundType.WIND,
                        isSelected = selectedType == "wind",
                        onClick = { onTypeSelected("wind") }
                    )
                    SoundTypeItem(
                        type = SoundType.SILENT,
                        isSelected = selectedType == "silent",
                        onClick = { onTypeSelected("silent") }
                    )
                }
            }
        }
    }
}

@Composable
fun SoundTypeItem(
    type: SoundType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isSelected) {
                    ElectricBlue.copy(alpha = 0.1f)
                } else {
                    Color.Transparent
                }
            )
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = ElectricBlue,
                unselectedColor = MaterialTheme.colorScheme.outline
            )
        )
        Text(
            type.displayName,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) {
                ElectricBlue
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )
    }
}

@Composable
fun DangerCard(
    title: String,
    description: String,
    icon: String,
    onAction: () -> Unit
) {
    OlympusCard(
        modifier = Modifier.fillMaxWidth(),
        glowColor = Color.Red.copy(alpha = 0.3f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onAction),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                icon,
                style = MaterialTheme.typography.displaySmall
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Red
                )
                Text(
                    description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Icon(
                Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.Red
            )
        }
    }
}

@Composable
fun AboutCard() {
    OlympusCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "⚡",
                style = MaterialTheme.typography.displayLarge
            )
            Text(
                "StormEase",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                "Version 1.0",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                "Balance your life under Zeus's protection",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ResetDataDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    isResetting: Boolean
) {
    AlertDialog(
        onDismissRequest = { if (!isResetting) onDismiss() },
        icon = {
            Text("⚠️", style = MaterialTheme.typography.displayMedium)
        },
        title = {
            Text(
                "Reset All Data?",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                "This will permanently delete all your activities, statistics, and achievements. This action cannot be undone.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isResetting,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White
                )
            ) {
                if (isResetting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White
                    )
                } else {
                    Text("Reset")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isResetting
            ) {
                Text("Cancel")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    )
}

