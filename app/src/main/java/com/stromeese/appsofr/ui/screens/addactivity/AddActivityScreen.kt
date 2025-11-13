package com.stromeese.appsofr.ui.screens.addactivity

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stromeese.appsofr.data.database.AppDatabase
import com.stromeese.appsofr.data.model.ActivityCategory
import com.stromeese.appsofr.data.model.Emotion
import com.stromeese.appsofr.data.repository.StormEaseRepository
import com.stromeese.appsofr.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddActivityScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddActivityViewModel = viewModel(
        factory = AddActivityViewModel.Factory(
            StormEaseRepository(
                AppDatabase.getDatabase(LocalContext.current).activityDao(),
                AppDatabase.getDatabase(LocalContext.current).achievementDao(),
                AppDatabase.getDatabase(LocalContext.current).dailyStatsDao()
            )
        )
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val isDarkTheme = MaterialTheme.colorScheme.background == NightOlympusBackground

    Box(modifier = Modifier.fillMaxSize()) {
        // Background
        OlympusSkyBackground(isDarkTheme = isDarkTheme)
        FloatingClouds(isDarkTheme = isDarkTheme)
        if (isDarkTheme) {
            Starfield()
        }

        // Success animation
        AnimatedVisibility(
            visible = uiState.showSuccessAnimation,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                LightningStrike(isVisible = true)
                Text(
                    "⚡",
                    style = MaterialTheme.typography.displayLarge,
                    modifier = Modifier.scale(3f)
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Fixed TopBar
            TopAppBar(
                title = {
                    Text(
                        "Add Activity",
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
                    containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.95f)
                )
            )
            
            // Scrollable content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Activity Name Input
                OlympusCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Activity Name",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        OutlinedTextField(
                            value = uiState.activityName,
                            onValueChange = { viewModel.updateActivityName(it) },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("What did you do?") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricBlue,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            ),
                            isError = uiState.errorMessage != null
                        )
                        if (uiState.errorMessage != null) {
                            Text(
                                uiState.errorMessage!!,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                // Category Selection
                OlympusCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Category",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CategoryButton(
                                modifier = Modifier.weight(1f),
                                category = ActivityCategory.WORK,
                                icon = "⚡",
                                label = "Work",
                                isSelected = uiState.selectedCategory == ActivityCategory.WORK,
                                onClick = { viewModel.updateCategory(ActivityCategory.WORK) }
                            )
                            CategoryButton(
                                modifier = Modifier.weight(1f),
                                category = ActivityCategory.REST,
                                icon = "☁️",
                                label = "Rest",
                                isSelected = uiState.selectedCategory == ActivityCategory.REST,
                                onClick = { viewModel.updateCategory(ActivityCategory.REST) }
                            )
                            CategoryButton(
                                modifier = Modifier.weight(1f),
                                category = ActivityCategory.PERSONAL,
                                icon = "💎",
                                label = "Personal",
                                isSelected = uiState.selectedCategory == ActivityCategory.PERSONAL,
                                onClick = { viewModel.updateCategory(ActivityCategory.PERSONAL) }
                            )
                        }
                    }
                }

                // Duration Input
                OlympusCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Duration",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                "${uiState.durationMinutes} minutes",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = ElectricBlue
                            )
                        }
                        
                        Slider(
                            value = uiState.durationMinutes.toFloat(),
                            onValueChange = { viewModel.updateDuration(it.toInt()) },
                            valueRange = 5f..240f,
                            steps = 47,
                            colors = SliderDefaults.colors(
                                thumbColor = ElectricBlue,
                                activeTrackColor = ElectricBlue,
                                inactiveTrackColor = MaterialTheme.colorScheme.outline
                            )
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            QuickDurationButton(
                                minutes = 15,
                                onClick = { viewModel.updateDuration(15) }
                            )
                            QuickDurationButton(
                                minutes = 30,
                                onClick = { viewModel.updateDuration(30) }
                            )
                            QuickDurationButton(
                                minutes = 60,
                                onClick = { viewModel.updateDuration(60) }
                            )
                            QuickDurationButton(
                                minutes = 120,
                                onClick = { viewModel.updateDuration(120) }
                            )
                        }
                    }
                }

                // Emotion Selection
                OlympusCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "How did it feel?",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            EmotionButton(
                                emotion = Emotion.HAPPY,
                                icon = "😊",
                                label = "Happy",
                                isSelected = uiState.selectedEmotion == Emotion.HAPPY,
                                onClick = { viewModel.updateEmotion(Emotion.HAPPY) }
                            )
                            EmotionButton(
                                emotion = Emotion.NEUTRAL,
                                icon = "😐",
                                label = "Neutral",
                                isSelected = uiState.selectedEmotion == Emotion.NEUTRAL,
                                onClick = { viewModel.updateEmotion(Emotion.NEUTRAL) }
                            )
                            EmotionButton(
                                emotion = Emotion.SAD,
                                icon = "😞",
                                label = "Sad",
                                isSelected = uiState.selectedEmotion == Emotion.SAD,
                                onClick = { viewModel.updateEmotion(Emotion.SAD) }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Save Button - Now scrollable
                SaveButtonScrollable(
                    isSaving = uiState.isSaving,
                    onClick = { viewModel.saveActivity(onNavigateBack) }
                )
                
                // Extra padding for system navigation
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun SaveButtonScrollable(
    isSaving: Boolean,
    onClick: () -> Unit
) {
    // Pulsating animation
    val infiniteTransition = rememberInfiniteTransition(label = "save_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .scale(if (!isSaving) scale else 1f)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = ElectricBlue.copy(alpha = glowAlpha)
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = ElectricBlue,
            contentColor = Color.White,
            disabledContainerColor = ElectricBlue.copy(alpha = 0.6f),
            disabledContentColor = Color.White.copy(alpha = 0.6f)
        ),
        shape = RoundedCornerShape(16.dp),
        enabled = !isSaving,
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 8.dp,
            pressedElevation = 12.dp,
            disabledElevation = 4.dp
        )
    ) {
        if (isSaving) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(28.dp),
                    color = Color.White,
                    strokeWidth = 3.dp
                )
                Text(
                    "Saving...",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "⚡",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
                Text(
                    "Save Activity",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun CategoryButton(
    modifier: Modifier = Modifier,
    category: ActivityCategory,
    icon: String,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    Column(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) {
                    getCategoryColor(category).copy(alpha = 0.2f)
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            )
            .border(
                width = 2.dp,
                color = if (isSelected) getCategoryColor(category) else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            icon,
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) getCategoryColor(category) else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun QuickDurationButton(
    minutes: Int,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.height(36.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = ElectricBlue
        )
    ) {
        Text(
            "${minutes}m",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun EmotionButton(
    emotion: Emotion,
    icon: String,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    Column(
        modifier = Modifier
            .scale(scale)
            .clip(CircleShape)
            .background(
                if (isSelected) {
                    GoldenAccent.copy(alpha = 0.2f)
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            )
            .border(
                width = 2.dp,
                color = if (isSelected) GoldenAccent else Color.Transparent,
                shape = CircleShape
            )
            .clickable(onClick = onClick)
            .padding(16.dp)
            .size(64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            icon,
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) GoldenAccent else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

fun getCategoryColor(category: ActivityCategory): Color {
    return when (category) {
        ActivityCategory.WORK -> ElectricBlue
        ActivityCategory.REST -> SecondaryGrayBlue
        ActivityCategory.PERSONAL -> GoldenAccent
    }
}

