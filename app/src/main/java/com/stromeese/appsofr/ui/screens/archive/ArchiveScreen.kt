package com.stromeese.appsofr.ui.screens.archive

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
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
import com.stromeese.appsofr.data.model.ActivityCategory
import com.stromeese.appsofr.data.model.DailyStats
import com.stromeese.appsofr.data.repository.StormEaseRepository
import com.stromeese.appsofr.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArchiveScreen(
    onNavigateBack: () -> Unit,
    viewModel: ArchiveViewModel = viewModel(
        factory = ArchiveViewModel.Factory(
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

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                "Archive",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Browse past days",
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
                    // Archive header
                    item {
                        ArchiveHeader(totalDays = uiState.dailyStats.size)
                    }

                    // Calendar/List of days
                    if (uiState.dailyStats.isEmpty()) {
                        item {
                            EmptyArchiveCard()
                        }
                    } else {
                        items(uiState.dailyStats) { stats ->
                            DayCard(
                                stats = stats,
                                isSelected = stats.date == uiState.selectedDate,
                                onClick = {
                                    if (stats.date == uiState.selectedDate) {
                                        viewModel.clearSelection()
                                    } else {
                                        viewModel.selectDate(stats.date)
                                    }
                                }
                            )
                        }
                    }

                    // Selected day details
                    if (uiState.selectedDate != null) {
                        item {
                            DayDetailsCard(
                                date = uiState.selectedDate!!,
                                activities = uiState.selectedDayActivities,
                                onClose = { viewModel.clearSelection() }
                            )
                        }
                    }

                    // Bottom spacer
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ArchiveHeader(totalDays: Int) {
    OlympusCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "📜",
                style = MaterialTheme.typography.displayMedium
            )
            Column {
                Text(
                    "Historical Records",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "$totalDays days recorded",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun DayCard(
    stats: DailyStats,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    OlympusCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        glowColor = if (isSelected) ElectricBlue else Color.Transparent
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        formatDateHeader(stats.date),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        formatDateSubtitle(stats.date),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Status Icon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            if (stats.isBalanced) {
                                GoldenAccent.copy(alpha = 0.2f)
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (stats.isBalanced) "⚖️" else "📊",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }

            // Time breakdown
            val totalMinutes = stats.workMinutes + stats.restMinutes + stats.personalMinutes
            if (totalMinutes > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MiniStatItem(
                        icon = "⚡",
                        minutes = stats.workMinutes,
                        color = ElectricBlue
                    )
                    MiniStatItem(
                        icon = "☁️",
                        minutes = stats.restMinutes,
                        color = SecondaryGrayBlue
                    )
                    MiniStatItem(
                        icon = "💎",
                        minutes = stats.personalMinutes,
                        color = GoldenAccent
                    )
                }

                // Balance bar
                if (stats.isBalanced) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "⚖️",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            "Balanced • ${stats.balanceScore.toInt()}%",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = GoldenAccent
                        )
                    }
                }
            } else {
                Text(
                    "No activities recorded",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun MiniStatItem(
    icon: String,
    minutes: Int,
    color: Color
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            icon,
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            "${minutes}m",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun DayDetailsCard(
    date: String,
    activities: List<com.stromeese.appsofr.data.model.Activity>,
    onClose: () -> Unit
) {
    OlympusCard(
        modifier = Modifier.fillMaxWidth(),
        glowColor = ElectricBlue
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        formatDateHeader(date),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "${activities.size} activities",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onClose) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            OlympusDivider()

            if (activities.isEmpty()) {
                Text(
                    "No activities on this day",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            } else {
                activities.forEach { activity ->
                    ActivityDetailItem(activity = activity)
                }
            }
        }
    }
}

@Composable
fun ActivityDetailItem(
    activity: com.stromeese.appsofr.data.model.Activity
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(getCategoryColor(activity.category).copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                getCategoryIcon(activity.category),
                style = MaterialTheme.typography.titleMedium
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                activity.name,
                style = MaterialTheme.typography.titleSmall,
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
                Text(
                    formatTime(activity.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun EmptyArchiveCard() {
    OlympusCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                "📜",
                style = MaterialTheme.typography.displayLarge
            )
            Text(
                "No History Yet",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                "Start logging activities to build your archive",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

fun getCategoryColor(category: ActivityCategory): Color {
    return when (category) {
        ActivityCategory.WORK -> ElectricBlue
        ActivityCategory.REST -> SecondaryGrayBlue
        ActivityCategory.PERSONAL -> GoldenAccent
    }
}

fun getCategoryIcon(category: ActivityCategory): String {
    return when (category) {
        ActivityCategory.WORK -> "⚡"
        ActivityCategory.REST -> "☁️"
        ActivityCategory.PERSONAL -> "💎"
    }
}

fun getEmotionIcon(emotion: com.stromeese.appsofr.data.model.Emotion): String {
    return when (emotion) {
        com.stromeese.appsofr.data.model.Emotion.HAPPY -> "😊"
        com.stromeese.appsofr.data.model.Emotion.NEUTRAL -> "😐"
        com.stromeese.appsofr.data.model.Emotion.SAD -> "😞"
    }
}

fun formatDateHeader(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val outputFormat = SimpleDateFormat("MMMM dd", Locale.US)
        val date = inputFormat.parse(dateString)
        date?.let { outputFormat.format(it) } ?: dateString
    } catch (e: Exception) {
        dateString
    }
}

fun formatDateSubtitle(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val outputFormat = SimpleDateFormat("EEEE, yyyy", Locale.US)
        val date = inputFormat.parse(dateString)
        date?.let { outputFormat.format(it) } ?: dateString
    } catch (e: Exception) {
        dateString
    }
}

fun formatTime(timestamp: Long): String {
    val format = SimpleDateFormat("HH:mm", Locale.US)
    return format.format(Date(timestamp))
}

