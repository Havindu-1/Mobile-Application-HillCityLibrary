package com.example.hillcitylibrary.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.IconButton
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.hillcitylibrary.model.Book
import com.example.hillcitylibrary.ui.BookViewModel
import com.example.hillcitylibrary.ui.components.GreetingHeader
import com.example.hillcitylibrary.ui.theme.GradientEnd
import com.example.hillcitylibrary.ui.theme.GradientStart
import com.example.hillcitylibrary.ui.theme.SuccessGreen
import com.example.hillcitylibrary.util.GamificationManager
import androidx.compose.ui.platform.LocalContext

@Composable
fun ProgressScreen(
    navController: NavController,
    viewModel: BookViewModel = viewModel()
) {
    val books by viewModel.books.collectAsState()
    val stats by viewModel.readingStats.collectAsState() // Triple(completed, totalPages, totalTime)
    val inProgressBooks = books.filter { it.isReserved }

    val context = LocalContext.current
    val gamificationManager = remember { GamificationManager(context) }
    val userProfile by gamificationManager.userProfile.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var selectedBookId by remember { mutableStateOf<String?>(null) }
    var showCelebration by remember { mutableStateOf(false) }

    // Timer State
    var isTimerRunning by remember { mutableStateOf(false) }
    var elapsedTimeSeconds by remember { mutableStateOf(0L) }

    LaunchedEffect(isTimerRunning) {
        if (isTimerRunning) {
            val startTime = System.currentTimeMillis() - (elapsedTimeSeconds * 1000)
            while (true) {
                val currentTime = System.currentTimeMillis()
                elapsedTimeSeconds = (currentTime - startTime) / 1000
                kotlinx.coroutines.delay(1000)
            }
        }
    }

    if (showDialog && selectedBookId != null) {
        val selectedBook = books.find { it.id == selectedBookId }
        val timerMinutes = if (!isTimerRunning && elapsedTimeSeconds > 0) elapsedTimeSeconds / 60 else 0
        
        LogReadingDialog(
            initialTimeMinutes = timerMinutes,
            onDismiss = { showDialog = false },
            onConfirm = { pages, time ->
                if (selectedBook != null) {
                    val currentPages = selectedBook.progress?.currentPage ?: 0
                    val totalPages = selectedBook.pageCount
                    val newPages = currentPages + pages
                    
                    viewModel.updateProgress(selectedBookId!!, pages, time)
                    showDialog = false
                    
                    if (time == timerMinutes && timerMinutes > 0) {
                        elapsedTimeSeconds = 0 // Reset timer if used
                    }
                    
                    if (newPages >= totalPages && currentPages < totalPages) {
                        showCelebration = true
                    }
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 24.dp)
        ) {
            // Item 1: Header
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    GradientStart,
                                    GradientEnd
                                )
                            )
                        )
                        .padding(top = 48.dp, bottom = 24.dp, start = 20.dp, end = 20.dp)
                ) {
                    Column {
                        GreetingHeader(
                            modifier = Modifier.padding(bottom = 8.dp),
                            textColor = Color.White
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { navController.popBackStack() },
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White
                                )
                            }
                            Text(
                                text = "Your Reading Progress",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // Item 2: Timer and Stats Container
            item {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Active Reading Timer Section
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Current Session",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            // Time Display
                            val hours = elapsedTimeSeconds / 3600
                            val minutes = (elapsedTimeSeconds % 3600) / 60
                            val seconds = elapsedTimeSeconds % 60
                            Text(
                                text = String.format("%02d:%02d:%02d", hours, minutes, seconds),
                                fontSize = 40.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Button(
                                    onClick = { isTimerRunning = !isTimerRunning },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isTimerRunning) Color(0xFFEF4444) else SuccessGreen
                                    ),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        if (isTimerRunning) Icons.Default.Timer else Icons.Default.Timer, // Could swap icon
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(if (isTimerRunning) "Stop" else "Start")
                                }
                                
                                if (!isTimerRunning && elapsedTimeSeconds > 0) {
                                    Button(
                                        onClick = {
                                            // Placeholder action
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.secondary
                                        ),
                                        modifier = Modifier.weight(1f),
                                        enabled = false 
                                    ) {
                                        Text("Select Book below")
                                    }
                                }
                            }
                            if (!isTimerRunning && elapsedTimeSeconds > 0) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Select a book below to log this time",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Stats Row with gradient cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ProgressStatCard(
                            icon = Icons.Default.Book,
                            label = "Books",
                            value = stats.first.toString(),
                            modifier = Modifier.weight(1f),
                            gradientColors = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
                        )
                        ProgressStatCard(
                            icon = Icons.Default.Book,
                            label = "Pages",
                            value = stats.second.toString(),
                            modifier = Modifier.weight(1f),
                            gradientColors = listOf(Color(0xFF06B6D4), Color(0xFF0891B2))
                        )
                        val h = stats.third / 60
                        val m = stats.third % 60
                        ProgressStatCard(
                            icon = Icons.Default.Timer,
                            label = "Time",
                            value = "${h}h ${m}m",
                            modifier = Modifier.weight(1f),
                            gradientColors = listOf(Color(0xFFEC4899), Color(0xFFDB2777))
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Focus Dashboard",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ProgressStatCard(
                            icon = Icons.Default.Timer,
                            label = "Focus Mins",
                            value = "${userProfile.totalFocusReadingMinutes}",
                            modifier = Modifier.weight(1f),
                            gradientColors = listOf(Color(0xFF10B981), Color(0xFF047857))
                        )
                        ProgressStatCard(
                            icon = Icons.Default.Book,
                            label = "Stable Sessions",
                            value = "${userProfile.totalStableSessions}",
                            modifier = Modifier.weight(1f),
                            gradientColors = listOf(Color(0xFFF59E0B), Color(0xFFD97706))
                        )
                        ProgressStatCard(
                            icon = Icons.Default.Timer,
                            label = "Deep Streak",
                            value = "${userProfile.deepFocusConsecutiveDays}",
                            modifier = Modifier.weight(1f),
                            gradientColors = listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9))
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = "Currently Reading",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Item 3: Book List
            items(inProgressBooks) { book ->
                Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                    ProgressBookItem(
                        book = book,
                        onLogClick = {
                            selectedBookId = book.id
                            showDialog = true
                        },
                        onReadClick = {
                            navController.navigate(com.example.hillcitylibrary.ui.navigation.Screen.Reading.createRoute(book.id))
                        }
                    )
                }
            }
        }
        
        if (showCelebration) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f))
                    .clickable { showCelebration = false },
                contentAlignment = Alignment.Center
            ) {
                com.example.hillcitylibrary.ui.components.FireworksAnimation()
                
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.padding(32.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Congratulations!",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = GradientEnd
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "You've completed another book!",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { showCelebration = false },
                            colors = ButtonDefaults.buttonColors(containerColor = GradientEnd),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Awesome!", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProgressStatCard(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    gradientColors: List<Color> = listOf(GradientStart, GradientEnd)
) {
    Card(
        modifier = modifier
            .height(120.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(gradientColors)
                )
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.8f)
                )
                Column {
                    Text(
                        text = value,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }
    }
}

@Composable
fun ProgressBookItem(book: Book, onLogClick: () -> Unit, onReadClick: () -> Unit) {
    val progress = book.progress?.currentPage ?: 0
    val total = book.pageCount
    val percentage = if (total > 0) progress.toFloat() / total else 0f
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = book.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = book.author,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "${(percentage * 100).toInt()}%",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            
            // Progress bar with gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(percentage)
                        .fillMaxSize()
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(GradientStart, GradientEnd)
                            )
                        )
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$progress / $total pages",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onLogClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("Manual Log")
                }
                
                Button(
                    onClick = onReadClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Read Now", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun LogReadingDialog(
    initialTimeMinutes: Long = 0,
    onDismiss: () -> Unit, 
    onConfirm: (Int, Long) -> Unit
) {
    var pages by remember { mutableStateOf("") }
    var time by remember { mutableStateOf(if (initialTimeMinutes > 0) initialTimeMinutes.toString() else "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Reading Session") },
        text = {
            Column {
                OutlinedTextField(
                    value = pages,
                    onValueChange = { pages = it },
                    label = { Text("Pages Read") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("Time (minutes)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { 
                val p = pages.toIntOrNull() ?: 0
                val t = time.toLongOrNull() ?: 0L
                onConfirm(p, t)
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
