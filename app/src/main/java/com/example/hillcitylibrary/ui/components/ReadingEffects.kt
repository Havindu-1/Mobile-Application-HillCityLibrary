package com.example.hillcitylibrary.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun ParticleBackground(modifier: Modifier = Modifier, isNightMode: Boolean) {
    if (!isNightMode) return

    val infiniteTransition = rememberInfiniteTransition()
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val particles = remember {
        List(30) {
            ReadingParticle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                speed = Random.nextFloat() * 0.5f + 0.1f,
                size = Random.nextFloat() * 4f + 2f,
                offsetY = Random.nextFloat() * 1000f
            )
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        particles.forEach { particle ->
            val currentY = (particle.y * height - time * particle.speed + particle.offsetY) % height
            val adjustedY = if (currentY < 0) currentY + height else currentY
            
            // Subtle horizontal drift
            val currentX = (particle.x * width + sin(time / 10f * particle.speed) * 20f) % width
            
            drawCircle(
                color = Color(0xFFEAB308).copy(alpha = 0.3f),
                radius = particle.size,
                center = Offset(currentX, adjustedY)
            )
        }
    }
}

data class ReadingParticle(val x: Float, val y: Float, val speed: Float, val size: Float, val offsetY: Float)


@Composable
fun AchievementPopup(
    achievementTitle: String,
    achievementDesc: String,
    isVisible: Boolean,
    onDismiss: () -> Unit
) {
    LaunchedEffect(isVisible) {
        if (isVisible) {
            delay(4000)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, start = 16.dp, end = 16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2937)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFFEAB308).copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Star, contentDescription = "Achievement", tint = Color(0xFFEAB308))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Achievement Unlocked!", color = Color(0xFFEAB308), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(achievementTitle, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(achievementDesc, color = Color.LightGray, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
