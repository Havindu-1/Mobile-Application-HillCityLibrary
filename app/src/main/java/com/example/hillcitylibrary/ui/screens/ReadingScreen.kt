package com.example.hillcitylibrary.ui.screens

import android.app.Application
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.hillcitylibrary.model.AchievementsList
import com.example.hillcitylibrary.ui.ReadingViewModel
import com.example.hillcitylibrary.ui.components.AchievementPopup
import com.example.hillcitylibrary.ui.components.ParticleBackground

class ReadingViewModelFactory(private val application: Application) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReadingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReadingViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingScreen(
    navController: NavController,
    bookId: String?,
    viewModel: ReadingViewModel = viewModel(
        factory = ReadingViewModelFactory(LocalContext.current.applicationContext as Application)
    )
) {
    val isDarkEnvironment by viewModel.isDarkEnvironment.collectAsState()
    val sessionMinutes by viewModel.readingSessionMinutes.collectAsState()
    val activeCombo by viewModel.activeCombo.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val newlyUnlocked by viewModel.newlyUnlockedAchievement.collectAsState()

    val isDeepFocus by viewModel.isDeepFocus.collectAsState()
    val stableMinutes by viewModel.stableMinutes.collectAsState()
    val stabilityScore by viewModel.stabilityScore.collectAsState()
    val focusBrokenMessage by viewModel.focusBrokenMessage.collectAsState()

    var showUi by remember { mutableStateOf(true) }

    // Start timer on entry
    LaunchedEffect(Unit) {
        viewModel.startReadingSession()
    }

    // Auto-hide UI (Deep Focus Mode)
    LaunchedEffect(isDeepFocus) {
        if (isDeepFocus && showUi) {
            showUi = false
        }
    }

    val backgroundColor by animateColorAsState(
        targetValue = if (isDarkEnvironment) Color(0xFF0F172A) else Color(0xFFF8FAFC),
        animationSpec = tween(2000)
    )
    val textColor by animateColorAsState(
        targetValue = if (isDarkEnvironment) Color(0xFFE2E8F0) else Color(0xFF1E293B),
        animationSpec = tween(2000)
    )
    val hudBackgroundColor = if (isDarkEnvironment || isDeepFocus) Color(0xFF1E293B).copy(alpha = 0.6f) else Color.White.copy(alpha = 0.6f)
    
    val baseAuraColor = if (isDarkEnvironment || isDeepFocus) Color(0xFF38BDF8) else Color(0xFF8B5CF6)
    val auraColor = when {
        isDeepFocus -> baseAuraColor.copy(alpha = 0.3f)
        stabilityScore > 80f -> baseAuraColor.copy(alpha = 0.15f)
        stabilityScore > 40f -> baseAuraColor.copy(alpha = 0.05f)
        else -> Color.Transparent
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                showUi = !showUi
            }
    ) {
        // Particle Background for Night Mode or Deep Focus Mode
        ParticleBackground(isNightMode = isDarkEnvironment || isDeepFocus)

        // Reading Content Area (Simulated Ebook)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(100.dp))
            
            Text(
                text = "Chapter 1: The Awakening",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                fontFamily = FontFamily.Serif
            )
            Spacer(modifier = Modifier.height(24.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(auraColor, RoundedCornerShape(16.dp))
                    .padding(8.dp)
            ) {
                Text(
                    text = """
                        The library was a place of secrets. Dust motes danced in the sparse shafts of sunlight that pierced the tall, arched windows. Every shelf held a world waiting to be discovered, but some worlds were better left undisturbed.
                        
                        As Elara walked down the aisle of forgotten histories, she felt a strange pull. A faint, almost imperceptible hum resonated from a leather-bound tome at the very end of the row. It wasn't the kind of sound you heard with your ears, but one you felt in your bones.
                        
                        She reached out, her fingers brushing the spine. The moment she touched it, the ambient light seemed to dim, casting long, wavering shadows across the stone floor. The air grew cold, carrying the scent of ozone and ancient parchment.
                        
                        "Who are you?" a voice whispered, echoing not in the room, but directly in her mind.
                        
                        Elara pulled her hand back, her heart racing. The book sat innocently on the shelf, the hum now silent. But the shadows remained, watching, waiting.
                        
                        (This is a simulated premium reading experience. Stay here to earn XP, level up, and unlock achievements. Try covering your device's light sensor to enter Night Reading Mode!)
                        
                        The journey begins...
                        
                        (More text to allow scrolling...)
                        The library was a place of secrets. Dust motes danced in the sparse shafts of sunlight that pierced the tall, arched windows. Every shelf held a world waiting to be discovered, but some worlds were better left undisturbed.
                        
                        As Elara walked down the aisle of forgotten histories, she felt a strange pull. A faint, almost imperceptible hum resonated from a leather-bound tome at the very end of the row. It wasn't the kind of sound you heard with your ears, but one you felt in your bones.
                    """.trimIndent(),
                    fontSize = 18.sp,
                    lineHeight = 32.sp,
                    color = textColor.copy(alpha = 0.9f),
                    fontFamily = FontFamily.Serif,
                    textAlign = TextAlign.Justify
                )
            }
            Spacer(modifier = Modifier.height(100.dp))
        }

        // HUD and Controls
        AnimatedVisibility(
            visible = showUi,
            enter = fadeIn(tween(500)),
            exit = fadeOut(tween(500)),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(backgroundColor.copy(alpha = 0.95f), Color.Transparent)
                        )
                    )
            ) {
                // Top App Bar Area
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = textColor)
                    }
                    
                    // Gamification HUD (Glassmorphism style)
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .background(hudBackgroundColor)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Title/Level
                        Column(horizontalAlignment = Alignment.End) {
                            Text(userProfile.title, fontSize = 12.sp, color = textColor, fontWeight = FontWeight.Bold)
                            Text("Level ${userProfile.level} • ${userProfile.xp} XP", fontSize = 10.sp, color = textColor.copy(alpha = 0.7f))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        // Progress ring placeholder
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(Color(0xFF6366F1), Color(0xFFEC4899)))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("L${userProfile.level}", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Focus Stats
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Session Timer
                    Column {
                        Text("Session Focus", fontSize = 12.sp, color = textColor.copy(alpha = 0.6f))
                        Text("${stableMinutes}m", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textColor)
                    }
                    
                    // Stability Score
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Stability", fontSize = 12.sp, color = textColor.copy(alpha = 0.6f))
                        Text("${stabilityScore.toInt()}%", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textColor)
                    }
                    
                    // Combo Multiplier
                    if (activeCombo > 1) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Combo Active", fontSize = 12.sp, color = Color(0xFFF59E0B))
                            Text("x$activeCombo XP", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFF59E0B))
                        }
                    } else {
                        Spacer(modifier = Modifier.width(40.dp))
                    }
                }
            }
        }

        // Night Mode Indicator (Bottom)
        AnimatedVisibility(
            visible = showUi,
            enter = fadeIn(tween(500)),
            exit = fadeOut(tween(500)),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(hudBackgroundColor)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isDarkEnvironment) Icons.Default.Bedtime else Icons.Default.WbSunny,
                    contentDescription = null,
                    tint = if (isDarkEnvironment) Color(0xFF60A5FA) else Color(0xFFF59E0B),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isDarkEnvironment) "Night Reading Mode Active" else "Light Mode Active",
                    fontSize = 14.sp,
                    color = textColor,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Focus Broken Message Overlay
        AnimatedVisibility(
            visible = focusBrokenMessage != null,
            enter = fadeIn(tween(500)),
            exit = fadeOut(tween(1000)),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black.copy(alpha = 0.8f))
                    .padding(24.dp)
            ) {
                Text(
                    text = focusBrokenMessage ?: "",
                    color = Color(0xFFEF4444), // Red-ish
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily.Serif
                )
            }
        }

        // Achievement Popup Overlay
        newlyUnlocked?.let { achievementId ->
            val achievement = AchievementsList.All.find { it.id == achievementId }
            if (achievement != null) {
                AchievementPopup(
                    achievementTitle = achievement.title,
                    achievementDesc = achievement.description,
                    isVisible = true,
                    onDismiss = { viewModel.clearAchievement() }
                )
            }
        }
    }
}
