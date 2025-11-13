package com.stromeese.appsofr.ui.theme

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.sin
import kotlin.random.Random

/**
 * Olympus Sky Gradient Background
 */
@Composable
fun OlympusSkyBackground(
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = false
) {
    val gradientColors = if (isDarkTheme) {
        listOf(
            NightOlympusBackground,
            NightSkyBlue,
            NightOlympusBackground
        )
    } else {
        listOf(
            SkyBlueLight,
            Color(0xFFB3DBFF),
            SkyBlueDark
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = gradientColors
                )
            )
    )
}

/**
 * Glowing Card with Olympus aesthetics
 */
@Composable
fun OlympusCard(
    modifier: Modifier = Modifier,
    glowColor: Color = GlowBlue,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = glowColor,
                spotColor = glowColor
            )
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        contentAlignment = Alignment.Center,
        content = content
    )
}

/**
 * Lightning Strike Effect
 */
@Composable
fun LightningStrike(
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    color: Color = LightningWhite
) {
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 200),
        label = "lightning_alpha"
    )

    if (alpha > 0f) {
        Canvas(
            modifier = modifier
                .fillMaxSize()
                .alpha(alpha)
        ) {
            val width = size.width
            val height = size.height
            val centerX = width / 2

            val path = Path().apply {
                moveTo(centerX, 0f)
                lineTo(centerX - 30f, height * 0.3f)
                lineTo(centerX + 10f, height * 0.3f)
                lineTo(centerX - 20f, height * 0.6f)
                lineTo(centerX + 15f, height * 0.6f)
                lineTo(centerX - 10f, height)
            }

            drawPath(
                path = path,
                color = color,
                style = Stroke(width = 4f, cap = StrokeCap.Round)
            )

            // Glow effect
            drawPath(
                path = path,
                color = color.copy(alpha = 0.3f),
                style = Stroke(width = 12f, cap = StrokeCap.Round)
            )
        }
    }
}

/**
 * Floating Clouds Animation
 */
@Composable
fun FloatingClouds(
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "clouds")
    
    val cloud1Offset by infiniteTransition.animateFloat(
        initialValue = -200f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(30000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "cloud1"
    )
    
    val cloud2Offset by infiniteTransition.animateFloat(
        initialValue = 500f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(
            animation = tween(25000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "cloud2"
    )

    val cloudColor = if (isDarkTheme) {
        Color.White.copy(alpha = 0.05f)
    } else {
        Color.White.copy(alpha = 0.3f)
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        // Cloud 1
        drawCircle(
            color = cloudColor,
            radius = 50f,
            center = Offset(cloud1Offset, 100f)
        )
        drawCircle(
            color = cloudColor,
            radius = 70f,
            center = Offset(cloud1Offset + 40f, 100f)
        )
        drawCircle(
            color = cloudColor,
            radius = 60f,
            center = Offset(cloud1Offset + 80f, 100f)
        )

        // Cloud 2
        drawCircle(
            color = cloudColor,
            radius = 60f,
            center = Offset(cloud2Offset, 250f)
        )
        drawCircle(
            color = cloudColor,
            radius = 80f,
            center = Offset(cloud2Offset + 50f, 250f)
        )
        drawCircle(
            color = cloudColor,
            radius = 55f,
            center = Offset(cloud2Offset + 100f, 250f)
        )
    }
}

/**
 * Golden Glow Effect for achievements
 */
@Composable
fun GoldenGlow(
    modifier: Modifier = Modifier,
    isGlowing: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    if (isGlowing) {
        Box(
            modifier = modifier
                .alpha(alpha)
                .blur(20.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            GoldenAccent,
                            GoldenAccent.copy(alpha = 0f)
                        )
                    )
                )
        )
    }
}

/**
 * Olympus Divider - Like a marble column
 */
@Composable
fun OlympusDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = 2.dp,
    color: Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(thickness)
    ) {
        drawLine(
            color = color,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            strokeWidth = thickness.toPx()
        )
    }
}

/**
 * Pulsating Divine Aura
 */
@Composable
fun DivineAura(
    modifier: Modifier = Modifier,
    isActive: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "aura")
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura_scale"
    )
    
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura_alpha"
    )

    if (isActive) {
        Canvas(
            modifier = modifier
                .fillMaxSize()
                .alpha(alpha)
        ) {
            drawCircle(
                color = ElectricBlue,
                radius = size.minDimension / 2 * scale,
                center = center
            )
        }
    }
}

/**
 * Starfield Effect for night theme
 */
@Composable
fun Starfield(
    modifier: Modifier = Modifier,
    starCount: Int = 50
) {
    val stars = remember {
        List(starCount) {
            Star(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 2f + 1f,
                alpha = Random.nextFloat() * 0.5f + 0.3f
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "stars")
    val twinkle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "twinkle"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        stars.forEach { star ->
            val twinkleAlpha = star.alpha * (0.7f + 0.3f * sin(twinkle * Math.PI.toFloat()))
            drawCircle(
                color = Color.White.copy(alpha = twinkleAlpha),
                radius = star.size,
                center = Offset(star.x * size.width, star.y * size.height)
            )
        }
    }
}

private data class Star(
    val x: Float,
    val y: Float,
    val size: Float,
    val alpha: Float
)

