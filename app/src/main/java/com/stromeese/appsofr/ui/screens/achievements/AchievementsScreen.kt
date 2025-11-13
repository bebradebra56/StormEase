package com.stromeese.appsofr.ui.screens.achievements

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stromeese.appsofr.data.database.AppDatabase
import com.stromeese.appsofr.data.model.Achievement
import com.stromeese.appsofr.data.repository.StormEaseRepository
import com.stromeese.appsofr.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreen(
    onNavigateBack: () -> Unit,
    viewModel: AchievementsViewModel = viewModel(
        factory = AchievementsViewModel.Factory(
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
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val repository = StormEaseRepository(
            AppDatabase.getDatabase(context).activityDao(),
            AppDatabase.getDatabase(context).achievementDao(),
            AppDatabase.getDatabase(context).dailyStatsDao()
        )
        repository.initializeAchievements()
    }

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
                        Column {
                            Text(
                                "Achievements",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Earn divine rewards",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                            )
                        }
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
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ElectricBlue)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header
                    item {
                        AchievementsHeader(
                            unlockedCount = uiState.achievements.count { it.isUnlocked },
                            totalCount = uiState.achievements.size
                        )
                    }

                    // Achievements List
                    items(uiState.achievements) { achievement ->
                        AchievementCard(achievement = achievement)
                    }

                    // Footer spacer
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun AchievementsHeader(
    unlockedCount: Int,
    totalCount: Int
) {
    OlympusCard(
        modifier = Modifier.fillMaxWidth(),
        glowColor = GoldenAccent
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "🏛️",
                style = MaterialTheme.typography.displayLarge
            )
            Text(
                "Olympus Achievements",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "$unlockedCount",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = GoldenAccent
                )
                Text(
                    "/ $totalCount",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Progress bar
            val progress = if (totalCount > 0) unlockedCount.toFloat() / totalCount else 0f
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = GoldenAccent,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }
    }
}

@Composable
fun AchievementCard(achievement: Achievement) {
    val scale by animateFloatAsState(
        targetValue = if (achievement.isUnlocked) 1f else 0.95f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    OlympusCard(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
        glowColor = if (achievement.isUnlocked) GoldenAccent else Color.Transparent
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon with glow effect
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(80.dp)
            ) {
                if (achievement.isUnlocked) {
                    GoldenGlow(
                        modifier = Modifier.size(100.dp),
                        isGlowing = true
                    )
                }
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            if (achievement.isUnlocked) {
                                Brush.radialGradient(
                                    colors = listOf(
                                        GoldenAccent.copy(alpha = 0.3f),
                                        GoldenAccent.copy(alpha = 0.1f)
                                    )
                                )
                            } else {
                                Brush.radialGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.surfaceVariant,
                                        MaterialTheme.colorScheme.surface
                                    )
                                )
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        getAchievementIcon(achievement.icon),
                        style = MaterialTheme.typography.displayMedium,
                        modifier = Modifier.scale(if (achievement.isUnlocked) 1f else 0.7f)
                    )
                }
            }

            // Details
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    achievement.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (achievement.isUnlocked) {
                        GoldenAccent
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
                Text(
                    achievement.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Progress
                if (!achievement.isUnlocked && achievement.maxProgress > 1) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Progress",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "${achievement.progress} / ${achievement.maxProgress}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = ElectricBlue
                            )
                        }
                        
                        AnimatedProgressBar(
                            progress = achievement.progress.toFloat() / achievement.maxProgress,
                            color = ElectricBlue
                        )
                    }
                } else if (achievement.isUnlocked) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "✓",
                            style = MaterialTheme.typography.titleSmall,
                            color = GoldenAccent
                        )
                        Text(
                            "Unlocked",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = GoldenAccent
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedProgressBar(
    progress: Float,
    color: Color
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "progress"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animatedProgress.coerceIn(0f, 1f))
                .fillMaxHeight()
                .clip(RoundedCornerShape(4.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            color.copy(alpha = 0.7f),
                            color
                        )
                    )
                )
        )
    }
}

fun getAchievementIcon(iconId: String): String {
    return when (iconId) {
        "harmony" -> "⚖️"
        "lightning" -> "⚡"
        "wisdom" -> "📜"
        "waves" -> "🌊"
        "sun" -> "☀️"
        "speed" -> "🏃"
        else -> "🏆"
    }
}

