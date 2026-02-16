package com.example.hillcitylibrary.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import kotlinx.coroutines.delay
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.hillcitylibrary.model.Book
import com.example.hillcitylibrary.model.BookGenre
import com.example.hillcitylibrary.ui.BookViewModel
import com.example.hillcitylibrary.ui.components.BookCard
import com.example.hillcitylibrary.ui.components.GreetingHeader
import com.example.hillcitylibrary.ui.navigation.Screen
import com.example.hillcitylibrary.ui.theme.AccentGold
import com.example.hillcitylibrary.ui.theme.GradientEnd
import com.example.hillcitylibrary.ui.theme.GradientStart

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: BookViewModel = viewModel()
) {
    val books by viewModel.books.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    // Logic for "Continue Reading"
    val currentBook = remember(books) {
        books.firstOrNull { it.progress != null && !it.progress.isCompleted }
    }

    // Logic for "Featured" (e.g., highest rated)
    val featuredBook = remember(books) {
        books.sortedByDescending { it.rating }.firstOrNull()
    }

    // Animation States
    var headerVisible by remember { mutableStateOf(false) }
    var continueReadingVisible by remember { mutableStateOf(false) }
    var categoriesVisible by remember { mutableStateOf(false) }
    var featuredVisible by remember { mutableStateOf(false) }
    var popularVisible by remember { mutableStateOf(false) }
    var newArrivalsVisible by remember { mutableStateOf(false) }

    // Staggered Effect
    LaunchedEffect(Unit) {
        headerVisible = true
        delay(100)
        continueReadingVisible = true
        delay(100)
        categoriesVisible = true
        delay(100)
        featuredVisible = true
        delay(100)
        popularVisible = true
        delay(100)
        newArrivalsVisible = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // --- Header Section ---
        AnimatedVisibility(
            visible = headerVisible,
            enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { -it / 2 }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(GradientStart, GradientEnd)
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .padding(top = 40.dp, bottom = 40.dp, start = 20.dp, end = 20.dp)
                ) {
                    GreetingHeader(
                        modifier = Modifier.padding(bottom = 8.dp),
                        textColor = Color.White
                    )
                    
                    Text(
                        text = "What would you like to read today?",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        lineHeight = 32.sp,
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = viewModel::onSearchQueryChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(8.dp, RoundedCornerShape(16.dp), spotColor = Color.Black.copy(alpha = 0.2f))
                            .background(Color.White, RoundedCornerShape(16.dp)),
                        placeholder = { 
                            Text("Search books, authors...", color = Color.Gray) 
                        },
                        leadingIcon = { 
                            Icon(Icons.Default.Search, "Search", tint = Color.Gray) 
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        singleLine = true
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .offset(y = (-20).dp) // Overlap the header slightly
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(MaterialTheme.colorScheme.background)
                .padding(top = 24.dp)
        ) {
            
            // --- Continue Reading Section ---
            AnimatedVisibility(
                visible = continueReadingVisible,
                enter = fadeIn(tween(500)) + slideInHorizontally(tween(500)) { 100 }
            ) {
                Column {
                    if (currentBook != null) {
                        PaddingSectionHeader("Continue Reading")
                        ContinueReadingCard(
                            book = currentBook,
                            onClick = { navController.navigate(Screen.BookDetails.createRoute(currentBook.id)) }
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }

            // --- Categories ---
            AnimatedVisibility(
                visible = categoriesVisible,
                enter = fadeIn(tween(500)) + slideInHorizontally(tween(500)) { 100 }
            ) {
                Column {
                    PaddingSectionHeader("Explore Categories")
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(BookGenre.values()) { genre ->
                            if (genre != BookGenre.ALL) {
                                CategoryPill(
                                    genre = genre,
                                    onClick = {
                                        viewModel.onGenreSelected(genre)
                                        navController.navigate(Screen.Library.route)
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }

            // --- Featured Book ---
            AnimatedVisibility(
                visible = featuredVisible,
                enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { 100 }
            ) {
                Column {
                    if (featuredBook != null) {
                        PaddingSectionHeader("Daily Pick")
                        FeaturedBookCard(
                            book = featuredBook,
                            onClick = { navController.navigate(Screen.BookDetails.createRoute(featuredBook.id)) }
                        )
                          Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }

          
            // --- Popular Books ---
            AnimatedVisibility(
                visible = popularVisible,
                enter = fadeIn(tween(500)) + slideInHorizontally(tween(500)) { 100 }
            ) {
                Column {
                    PaddingSectionHeader("Popular Books", "See All") { 
                         navController.navigate(Screen.Library.route) 
                    }
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(books.sortedByDescending { it.rating }.take(5)) { book ->
                            BookCard(
                                book = book,
                                onBookClick = { bookId -> navController.navigate(Screen.BookDetails.createRoute(bookId)) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
            
            // --- Recently Added ---
            AnimatedVisibility(
                visible = newArrivalsVisible,
                enter = fadeIn(tween(500)) + slideInHorizontally(tween(500)) { 100 }
            ) {
                Column {
                     PaddingSectionHeader("New Arrivals", "See All") { 
                         navController.navigate(Screen.Library.route) 
                    }
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(books.takeLast(5).reversed()) { book ->
                            BookCard(
                                book = book,
                                onBookClick = { bookId -> navController.navigate(Screen.BookDetails.createRoute(bookId)) }
                            )
                        }
                    }
        
                    Spacer(modifier = Modifier.height(100.dp)) // Bottom padding
                }
            }
        }
    }
}

@Composable
fun PaddingSectionHeader(title: String, action: String? = null, onActionClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        if (action != null) {
            Text(
                text = action,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { onActionClick() }
            )
        }
    }
}

@Composable
fun ContinueReadingCard(book: Book, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(140.dp)
            .clickable { onClick() }
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = book.coverImg),
                contentDescription = book.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(100.dp)
                    .fillMaxHeight()
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = book.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = book.author,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Column {
                   val progress = book.progress ?: com.example.hillcitylibrary.model.ReadingProgress()
                   val progressPercent = if(book.pageCount > 0) progress.currentPage.toFloat() / book.pageCount else 0f
                   
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Page ${progress.currentPage} of ${book.pageCount}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "${(progressPercent * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = progressPercent,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.primaryContainer
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryPill(genre: BookGenre, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
    ) {
        Text(
            text = genre.displayName,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun FeaturedBookCard(book: Book, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(200.dp) 
            .clickable { onClick() }
            .shadow(8.dp, RoundedCornerShape(20.dp)),
         shape = RoundedCornerShape(20.dp),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = book.coverImg),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color.Black.copy(alpha = 0.8f), Color.Transparent),
                            startX = 0f,
                            endX = 600f
                        )
                    )
            )
            
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(24.dp)
                    .width(200.dp) // Limit text width
            ) {
                Surface(
                    color = AccentGold,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "FEATURED",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = book.author,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
                 Spacer(modifier = Modifier.height(16.dp))
                 Button(
                     onClick = onClick,
                     colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                     contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                     modifier = Modifier.height(36.dp)
                 ) {
                     Text("Read Now", fontSize = 12.sp)
                     Spacer(modifier = Modifier.width(4.dp))
                     Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                 }
            }
        }
    }
}
