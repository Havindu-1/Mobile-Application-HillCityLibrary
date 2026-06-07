package com.example.hillcitylibrary.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import coil.compose.AsyncImage
import androidx.compose.foundation.background
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.hillcitylibrary.ui.BookViewModel
import com.example.hillcitylibrary.ui.components.BookCoverImage
import com.example.hillcitylibrary.ui.navigation.Screen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailsScreen(
    navController: NavController,
    bookId: String?,
    viewModel: BookViewModel = viewModel()
) {
    if (bookId == null) return

    // Observe the book that was selected before navigation
    val selectedBook by viewModel.selectedBook.collectAsState()
    // Also watch live updates (e.g., favorite/reserve toggle)
    val allBooks by viewModel.combinedBooks.collectAsState()
    val decodedId = remember(bookId) { Screen.BookDetails.decodeId(bookId) }
    val liveBook = allBooks.find { it.id == decodedId }

    // Prefer live update; fall back to what was selected at click time
    val currentBook = liveBook ?: selectedBook

    if (currentBook == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            androidx.compose.material3.CircularProgressIndicator()
        }
        return
    }

    currentBook.let { currentBook ->
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Shop Icon
                            IconButton(onClick = { navController.navigate(Screen.Shop.route) }) {
                                Icon(
                                    imageVector = Icons.Filled.ShoppingCart,
                                    contentDescription = "Shop",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            // Calendar Icon (Progress)
                            IconButton(onClick = { navController.navigate(Screen.Progress.route) }) {
                                Icon(
                                    imageVector = Icons.Filled.DateRange,
                                    contentDescription = "Progress",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            // Favorite Icon
                            IconButton(onClick = { viewModel.toggleFavorite(currentBook.id) }) {
                                Icon(
                                    imageVector = if (currentBook.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                    contentDescription = "Favorite",
                                    tint = if (currentBook.isFavorite) Color.Red else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Animation States
                var coverVisible by remember { mutableStateOf(false) }
                var infoVisible by remember { mutableStateOf(false) }
                var detailsVisible by remember { mutableStateOf(false) }
                var descriptionVisible by remember { mutableStateOf(false) }
                var actionsVisible by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    coverVisible = true
                    delay(100)
                    infoVisible = true
                    delay(100)
                    detailsVisible = true
                    delay(100)
                    descriptionVisible = true
                    delay(100)
                    actionsVisible = true
                }

                // Cover Image with Shadow
                AnimatedVisibility(
                    visible = coverVisible,
                    enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { -50 }
                ) {
                    Card(
                        modifier = Modifier
                            .height(300.dp)
                            .width(200.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 16.dp),
                        colors = CardDefaults.elevatedCardColors()
                    ) {
                        BookCoverImage(
                            coverUrl = currentBook.coverUrl,
                            coverImg = currentBook.coverImg,
                            title = currentBook.title,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))

                // Title and Author
                AnimatedVisibility(
                    visible = infoVisible,
                    enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { 50 }
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = currentBook.title,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "by ${currentBook.author}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                // Metadata Row (Rating, Pages, Genre)
                AnimatedVisibility(
                    visible = detailsVisible,
                    enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { 50 }
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Rating
                            MetadataItem(
                                icon = Icons.Default.Star,
                                text = String.format("%.1f", currentBook.rating),
                                iconTint = MaterialTheme.colorScheme.secondary,
                                contentDescription = "Rating"
                            )

                            Spacer(modifier = Modifier.width(24.dp))
                            
                            // Divider
                            Box(
                                modifier = Modifier
                                    .height(24.dp)
                                    .width(1.dp)
                                    .background(MaterialTheme.colorScheme.outlineVariant)
                            )
                            
                            Spacer(modifier = Modifier.width(24.dp))

                            // Page Count
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${currentBook.pageCount}",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Pages",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.width(24.dp))
                            
                            // Divider
                            Box(
                                modifier = Modifier
                                    .height(24.dp)
                                    .width(1.dp)
                                    .background(MaterialTheme.colorScheme.outlineVariant)
                            )

                            Spacer(modifier = Modifier.width(24.dp))

                            // Language / Genre
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Eng",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Lang",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Genre Pill
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = CircleShape,
                        ) {
                            Text(
                                text = currentBook.genre.displayName,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Description
                AnimatedVisibility(
                    visible = descriptionVisible,
                    enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { 50 }
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Description",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = currentBook.description,
                            style = MaterialTheme.typography.bodyLarge,
                            lineHeight = 26.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.9f),
                            textAlign = TextAlign.Start
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(48.dp))

                // CTA Button
                AnimatedVisibility(
                    visible = actionsVisible,
                    enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { 50 }
                ) {
                    Button(
                        onClick = { 
                            if (currentBook.isReserved) {
                                navController.navigate(Screen.Reading.createRoute(currentBook.id))
                            } else {
                                viewModel.reserveBook(currentBook.id) 
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = if (currentBook.isReserved) "Read Book" else "Book / Reserve",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun MetadataItem(
    icon: ImageVector,
    text: String,
    iconTint: Color = MaterialTheme.colorScheme.onSurface,
    contentDescription: String? = null
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = iconTint,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
