package com.example.hillcitylibrary.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class Particle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var alpha: Float,
    var color: Color,
    var size: Float
)

@Composable
fun FireworksAnimation(
    modifier: Modifier = Modifier
) {
    val particles = remember { mutableStateListOf<Particle>() }
    val time = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        val startTime = System.nanoTime()
        while (true) {
            withFrameNanos { frameTime ->
                val currentTime = (frameTime - startTime) / 1_000_000L
                
                // Spawn new fireworks randomly
                if (Random.nextFloat() < 0.05f) { // 5% chance per frame
                    spawnFirework(particles)
                }

                // Update particles
                val iterator = particles.iterator()
                while (iterator.hasNext()) {
                    val p = iterator.next()
                    p.x += p.vx
                    p.y += p.vy
                    p.vy += 0.1f // Gravity
                    p.alpha -= 0.01f // Fade out
                    
                    if (p.alpha <= 0f) {
                        iterator.remove()
                    }
                }
            }
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        particles.forEach { p ->
            drawCircle(
                color = p.color.copy(alpha = p.alpha.coerceIn(0f, 1f)),
                radius = p.size,
                center = Offset(p.x, p.y)
            )
        }
    }
}

fun spawnFirework(particles: MutableList<Particle>) {
    val startX = Random.nextFloat() * 1080f // Approximate screen width, will be better with actual size but acceptable for effect
    val startY = Random.nextFloat() * 800f // Upper half of screen
    val color = listOf(
        Color(0xFFFF0000), Color(0xFF00FF00), Color(0xFF0000FF), 
        Color(0xFFFFFF00), Color(0xFF00FFFF), Color(0xFFFF00FF)
    ).random()

    for (i in 0..30) {
        val angle = Random.nextFloat() * 2 * Math.PI
        val speed = Random.nextFloat() * 5f + 2f
        particles.add(
            Particle(
                x = startX,
                y = startY,
                vx = (cos(angle) * speed).toFloat(),
                vy = (sin(angle) * speed).toFloat(),
                alpha = 1f,
                color = color,
                size = Random.nextFloat() * 4f + 2f
            )
        )
    }
}
