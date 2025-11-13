package com.stromeese.appsofr.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.stromeese.appsofr.data.model.Activity
import com.stromeese.appsofr.data.model.ActivityCategory
import com.stromeese.appsofr.data.model.Emotion
import com.stromeese.appsofr.data.repository.StormEaseRepository
import com.stromeese.appsofr.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToAddActivity: () -> Unit,
    onNavigateToStatistics: () -> Unit,
    onNavigateToAchievements: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToArchive: () -> Unit,
    viewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.Factory(
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

        // Lightning effect
        AnimatedVisibility(
            visible = uiState.showLightning,
            enter = fadeIn(animationSpec = tween(200)),
            exit = fadeOut(animationSpec = tween(500))
        ) {
            LightningStrike(isVisible = true)
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(1000)
                viewModel.dismissLightning()
            }
        }

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                "StormEase",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Under Zeus's Protection",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    ),
                    actions = {
                        IconButton(onClick = onNavigateToSettings) {
                            Icon(
                                Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                )
            },
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Date and Greeting
                item {
                    GreetingCard(date = uiState.currentDate)
                }

                // Add Activity Button (prominent)
                item {
                    AddActivityButton(onClick = onNavigateToAddActivity)
                }

                // Balance Indicator
                item {
                    BalanceIndicator(
                        workMinutes = uiState.todayStats?.workMinutes ?: 0,
                        restMinutes = uiState.todayStats?.restMinutes ?: 0,
                        personalMinutes = uiState.todayStats?.personalMinutes ?: 0,
                        balanceScore = uiState.todayStats?.balanceScore ?: 0f
                    )
                }

                // Quick Stats
                item {
                    QuickStatsRow(
                        totalMinutes = (uiState.todayStats?.workMinutes ?: 0) +
                                (uiState.todayStats?.restMinutes ?: 0) +
                                (uiState.todayStats?.personalMinutes ?: 0),
                        balanceScore = uiState.todayStats?.balanceScore ?: 0f,
                        activityCount = uiState.recentActivities.size
                    )
                }

                // Navigation Cards
                item {
                    NavigationCards(
                        onNavigateToStatistics = onNavigateToStatistics,
                        onNavigateToAchievements = onNavigateToAchievements,
                        onNavigateToArchive = onNavigateToArchive
                    )
                }

                // Recent Activities
                if (uiState.recentActivities.isNotEmpty()) {
                    item {
                        Text(
                            "Recent Activities",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    items(uiState.recentActivities) { activity ->
                        ActivityCard(activity)
                    }
                }
            }
        }
    }
}

@Composable
fun AddActivityButton(onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
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
            .height(72.dp)
            .scale(scale),
        colors = ButtonDefaults.buttonColors(
            containerColor = ElectricBlue
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 8.dp,
            pressedElevation = 12.dp
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = glowAlpha)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    tint = ElectricBlue,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                "⚡ Add New Activity",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun GreetingCard(date: String) {
    OlympusCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val greeting = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
                in 0..11 -> "Good Morning"
                in 12..17 -> "Good Afternoon"
                else -> "Good Evening"
            }
            
            Text(
                greeting,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                formatDate(date),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun BalanceIndicator(
    workMinutes: Int,
    restMinutes: Int,
    personalMinutes: Int,
    balanceScore: Float
) {
    val totalMinutes = workMinutes + restMinutes + personalMinutes
    
    OlympusCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Today's Balance",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Circular progress
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(200.dp)
            ) {
                if (totalMinutes > 0) {
                    BalanceCircle(
                        workPercent = workMinutes.toFloat() / totalMinutes,
                        restPercent = restMinutes.toFloat() / totalMinutes,
                        personalPercent = personalMinutes.toFloat() / totalMinutes
                    )
                } else {
                    Text(
                        "No activities yet",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                LegendItem(
                    icon = "⚡",
                    label = "Work",
                    minutes = workMinutes,
                    color = ElectricBlue
                )
                LegendItem(
                    icon = "☁️",
                    label = "Rest",
                    minutes = restMinutes,
                    color = SecondaryGrayBlue
                )
                LegendItem(
                    icon = "💎",
                    label = "Personal",
                    minutes = personalMinutes,
                    color = GoldenAccent
                )
            }
        }
    }
}

@Composable
fun BalanceCircle(
    workPercent: Float,
    restPercent: Float,
    personalPercent: Float
) {
    val animatedWork by animateFloatAsState(
        targetValue = workPercent,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "work"
    )
    val animatedRest by animateFloatAsState(
        targetValue = restPercent,
        animationSpec = tween(1000, delayMillis = 300, easing = FastOutSlowInEasing),
        label = "rest"
    )
    val animatedPersonal by animateFloatAsState(
        targetValue = personalPercent,
        animationSpec = tween(1000, delayMillis = 600, easing = FastOutSlowInEasing),
        label = "personal"
    )

    Box(
        modifier = Modifier
            .size(180.dp)
            .clip(CircleShape)
            .background(
                brush = Brush.sweepGradient(
                    0f to ElectricBlue,
                    animatedWork to ElectricBlue,
                    animatedWork to SecondaryGrayBlue,
                    animatedWork + animatedRest to SecondaryGrayBlue,
                    animatedWork + animatedRest to GoldenAccent,
                    1f to GoldenAccent
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "⚖️",
                style = MaterialTheme.typography.displayLarge
            )
        }
    }
}

@Composable
fun LegendItem(
    icon: String,
    label: String,
    minutes: Int,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Text(
                icon,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            "${minutes}m",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun QuickStatsRow(
    totalMinutes: Int,
    balanceScore: Float,
    activityCount: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            icon = "⏱️",
            value = "${totalMinutes / 60}h ${totalMinutes % 60}m",
            label = "Total Time"
        )
        StatCard(
            modifier = Modifier.weight(1f),
            icon = "⚖️",
            value = "${balanceScore.toInt()}%",
            label = "Balance"
        )
        StatCard(
            modifier = Modifier.weight(1f),
            icon = "📊",
            value = "$activityCount",
            label = "Activities"
        )
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    icon: String,
    value: String,
    label: String
) {
    OlympusCard(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                icon,
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun NavigationCards(
    onNavigateToStatistics: () -> Unit,
    onNavigateToAchievements: () -> Unit,
    onNavigateToArchive: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        NavCard(
            icon = "📈",
            title = "Statistics",
            description = "View your analytics",
            onClick = onNavigateToStatistics
        )
        NavCard(
            icon = "🏆",
            title = "Achievements",
            description = "Track your progress",
            onClick = onNavigateToAchievements
        )
        NavCard(
            icon = "📜",
            title = "Archive",
            description = "Browse past days",
            onClick = onNavigateToArchive
        )
    }
}

@Composable
fun NavCard(
    icon: String,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    OlympusCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
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
            Icon(
                Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ActivityCard(activity: Activity) {
    OlympusCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(getCategoryColor(activity.category).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    getCategoryIcon(activity.category),
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    activity.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "${activity.durationMinutes}m",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        getEmotionIcon(activity.emotion),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

fun getCategoryIcon(category: ActivityCategory): String {
    return when (category) {
        ActivityCategory.WORK -> "⚡"
        ActivityCategory.REST -> "☁️"
        ActivityCategory.PERSONAL -> "💎"
    }
}

fun getCategoryColor(category: ActivityCategory): Color {
    return when (category) {
        ActivityCategory.WORK -> ElectricBlue
        ActivityCategory.REST -> SecondaryGrayBlue
        ActivityCategory.PERSONAL -> GoldenAccent
    }
}

fun getEmotionIcon(emotion: Emotion): String {
    return when (emotion) {
        Emotion.HAPPY -> "😊"
        Emotion.NEUTRAL -> "😐"
        Emotion.SAD -> "😞"
    }
}

fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val outputFormat = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.US)
        val date = inputFormat.parse(dateString)
        date?.let { outputFormat.format(it) } ?: dateString
    } catch (e: Exception) {
        dateString
    }
}

