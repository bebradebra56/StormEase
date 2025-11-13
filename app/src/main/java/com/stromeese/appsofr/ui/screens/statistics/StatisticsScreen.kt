package com.stromeese.appsofr.ui.screens.statistics

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stromeese.appsofr.data.database.AppDatabase
import com.stromeese.appsofr.data.model.ActivityCategory
import com.stromeese.appsofr.data.repository.StormEaseRepository
import com.stromeese.appsofr.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    onNavigateBack: () -> Unit,
    viewModel: StatisticsViewModel = viewModel(
        factory = StatisticsViewModel.Factory(
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
                        Text(
                            "Statistics",
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
                // Period Tabs
                item {
                    PeriodSelector(
                        selectedPeriod = uiState.selectedPeriod,
                        onPeriodSelected = { viewModel.selectPeriod(it) }
                    )
                }

                // Chart
                item {
                    if (uiState.categoryData.isNotEmpty()) {
                        StatisticsChart(
                            categoryData = uiState.categoryData,
                            totalMinutes = uiState.totalMinutes
                        )
                    } else {
                        EmptyStateCard()
                    }
                }

                // Balance Score
                item {
                    if (uiState.totalMinutes > 0) {
                        BalanceScoreCard(
                            balanceScore = uiState.balanceScore,
                            isBalanced = uiState.isBalanced
                        )
                    }
                }

                // Recommendation
                item {
                    if (uiState.totalMinutes > 0) {
                        RecommendationCard(recommendation = uiState.recommendation)
                    }
                }

                // Activities List
                if (uiState.activities.isNotEmpty()) {
                    item {
                        Text(
                            "Activity History",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    items(uiState.activities) { activity ->
                        ActivityListItem(
                            name = activity.name,
                            category = activity.category,
                            duration = activity.durationMinutes,
                            date = activity.date
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PeriodSelector(
    selectedPeriod: TimePeriod,
    onPeriodSelected: (TimePeriod) -> Unit
) {
    OlympusCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PeriodTab(
                modifier = Modifier.weight(1f),
                label = "Day",
                isSelected = selectedPeriod == TimePeriod.DAY,
                onClick = { onPeriodSelected(TimePeriod.DAY) }
            )
            PeriodTab(
                modifier = Modifier.weight(1f),
                label = "Week",
                isSelected = selectedPeriod == TimePeriod.WEEK,
                onClick = { onPeriodSelected(TimePeriod.WEEK) }
            )
            PeriodTab(
                modifier = Modifier.weight(1f),
                label = "Month",
                isSelected = selectedPeriod == TimePeriod.MONTH,
                onClick = { onPeriodSelected(TimePeriod.MONTH) }
            )
        }
    }
}

@Composable
fun PeriodTab(
    modifier: Modifier = Modifier,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) ElectricBlue else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun StatisticsChart(
    categoryData: List<CategoryData>,
    totalMinutes: Int
) {
    OlympusCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Lightning and Clouds",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Animated Bar Chart
            AnimatedBarChart(categoryData = categoryData)

            Spacer(modifier = Modifier.height(8.dp))

            // Legend with percentages
            categoryData.forEach { data ->
                CategoryLegendItem(
                    category = data.category,
                    minutes = data.minutes,
                    percentage = data.percentage
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Total: ${totalMinutes / 60}h ${totalMinutes % 60}m",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun AnimatedBarChart(categoryData: List<CategoryData>) {
    val workData = categoryData.find { it.category == ActivityCategory.WORK }
    val restData = categoryData.find { it.category == ActivityCategory.REST }
    val personalData = categoryData.find { it.category == ActivityCategory.PERSONAL }

    val workHeight by animateFloatAsState(
        targetValue = (workData?.percentage ?: 0f) / 100f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "work"
    )
    val restHeight by animateFloatAsState(
        targetValue = (restData?.percentage ?: 0f) / 100f,
        animationSpec = tween(1000, delayMillis = 300, easing = FastOutSlowInEasing),
        label = "rest"
    )
    val personalHeight by animateFloatAsState(
        targetValue = (personalData?.percentage ?: 0f) / 100f,
        animationSpec = tween(1000, delayMillis = 600, easing = FastOutSlowInEasing),
        label = "personal"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        BarItem(
            modifier = Modifier.weight(1f),
            height = workHeight,
            color = ElectricBlue,
            icon = "⚡",
            label = "Work"
        )
        BarItem(
            modifier = Modifier.weight(1f),
            height = restHeight,
            color = SecondaryGrayBlue,
            icon = "☁️",
            label = "Rest"
        )
        BarItem(
            modifier = Modifier.weight(1f),
            height = personalHeight,
            color = GoldenAccent,
            icon = "💎",
            label = "Personal"
        )
    }
}

@Composable
fun BarItem(
    modifier: Modifier = Modifier,
    height: Float,
    color: Color,
    icon: String,
    label: String
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Box(
            modifier = Modifier
                .width(60.dp)
                .fillMaxHeight(height.coerceIn(0.1f, 1f))
                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            color.copy(alpha = 0.6f),
                            color
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            if (height > 0.2f) {
                Text(
                    icon,
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun CategoryLegendItem(
    category: ActivityCategory,
    minutes: Int,
    percentage: Float
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(getCategoryColor(category))
            )
            Text(
                getCategoryName(category),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "${minutes / 60}h ${minutes % 60}m",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                "${percentage.toInt()}%",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = getCategoryColor(category)
            )
        }
    }
}

@Composable
fun BalanceScoreCard(
    balanceScore: Float,
    isBalanced: Boolean
) {
    OlympusCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Balance Score",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(120.dp)
            ) {
                CircularBalanceIndicator(score = balanceScore)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "${balanceScore.toInt()}%",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = getScoreColor(balanceScore)
                    )
                    Text(
                        if (isBalanced) "⚖️" else "⚠️",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }
            
            Text(
                when {
                    balanceScore >= 80 -> "Excellent Balance!"
                    balanceScore >= 60 -> "Good Balance"
                    balanceScore >= 40 -> "Fair Balance"
                    else -> "Needs Improvement"
                },
                style = MaterialTheme.typography.titleMedium,
                color = getScoreColor(balanceScore)
            )
        }
    }
}

@Composable
fun CircularBalanceIndicator(score: Float) {
    val animatedScore by animateFloatAsState(
        targetValue = score / 100f,
        animationSpec = tween(1500, easing = FastOutSlowInEasing),
        label = "score"
    )

    Canvas(modifier = Modifier.size(120.dp)) {
        val strokeWidth = 12.dp.toPx()
        val size = Size(size.width - strokeWidth, size.height - strokeWidth)
        val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)

        // Background arc
        drawArc(
            color = Color.LightGray.copy(alpha = 0.3f),
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = topLeft,
            size = size,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // Foreground arc
        drawArc(
            brush = Brush.sweepGradient(
                colors = listOf(
                    Color.Red,
                    Color.Yellow,
                    ElectricBlue,
                    GoldenAccent
                )
            ),
            startAngle = -90f,
            sweepAngle = 360f * animatedScore,
            useCenter = false,
            topLeft = topLeft,
            size = size,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun RecommendationCard(recommendation: String) {
    OlympusCard(
        modifier = Modifier.fillMaxWidth(),
        glowColor = GoldenAccent
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "💡",
                style = MaterialTheme.typography.displaySmall
            )
            Column {
                Text(
                    "Recommendation",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    recommendation,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun EmptyStateCard() {
    OlympusCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                "☁️",
                style = MaterialTheme.typography.displayLarge
            )
            Text(
                "No Data Yet",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                "Start logging activities to see your statistics",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ActivityListItem(
    name: String,
    category: ActivityCategory,
    duration: Int,
    date: String
) {
    OlympusCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(getCategoryColor(category).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    getCategoryIcon(category),
                    style = MaterialTheme.typography.titleLarge
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "${duration}m",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
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

fun getCategoryName(category: ActivityCategory): String {
    return when (category) {
        ActivityCategory.WORK -> "Work"
        ActivityCategory.REST -> "Rest"
        ActivityCategory.PERSONAL -> "Personal"
    }
}

fun getCategoryIcon(category: ActivityCategory): String {
    return when (category) {
        ActivityCategory.WORK -> "⚡"
        ActivityCategory.REST -> "☁️"
        ActivityCategory.PERSONAL -> "💎"
    }
}

fun getScoreColor(score: Float): Color {
    return when {
        score >= 80 -> GoldenAccent
        score >= 60 -> ElectricBlue
        score >= 40 -> Color.Yellow
        else -> Color.Red
    }
}

