package com.example.hillcitylibrary.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.hillcitylibrary.model.BookGenre
import com.example.hillcitylibrary.model.ShopBook
import com.example.hillcitylibrary.ui.ShopViewModel
import com.example.hillcitylibrary.ui.components.GreetingHeader
import com.example.hillcitylibrary.ui.navigation.Screen
import com.example.hillcitylibrary.ui.theme.AccentGold
import com.example.hillcitylibrary.ui.theme.GradientEnd
import com.example.hillcitylibrary.ui.theme.GradientStart
import com.example.hillcitylibrary.ui.theme.SuccessGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopScreen(
    navController: NavController,
    viewModel: ShopViewModel = viewModel()
) {
    val shopBooks by viewModel.filteredBooks.collectAsState(initial = emptyList())
    val cartItemCount by viewModel.cartItemCount.collectAsState(initial = 0)
    val selectedCategory by viewModel.selectedCategory.collectAsState(initial = null)
    val searchQuery by viewModel.searchQuery.collectAsState(initial = "")

    // Animation States
    var headerVisible by remember { mutableStateOf(false) }
    var categoriesVisible by remember { mutableStateOf(false) }
    var contentVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        headerVisible = true
        delay(200)
        categoriesVisible = true
        delay(200)
        contentVisible = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header with gradient
        AnimatedVisibility(
            visible = headerVisible,
            enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { -50 }
        ) {
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
                    .padding(top = 40.dp, bottom = 16.dp, start = 20.dp, end = 20.dp)
            ) {
                Column {
                    GreetingHeader(
                        modifier = Modifier.padding(bottom = 8.dp),
                        textColor = Color.White
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                             IconButton(
                                onClick = { navController.popBackStack() },
                                modifier = Modifier
                                    .size(36.dp)
                                    .padding(end = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White
                                )
                            }
                            Column {
                                Text(
                                    text = "Book Store",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Find your next favorite book",
                                    fontSize = 14.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }

                        // Cart Button with Badge
                        BadgedBox(
                            badge = {
                                if (cartItemCount > 0) {
                                    Badge(
                                        containerColor = AccentGold,
                                        contentColor = Color.White
                                    ) {
                                        Text("$cartItemCount")
                                    }
                                }
                            }
                        ) {
                            IconButton(
                                onClick = { navController.navigate(Screen.Cart.route) },
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f))
                            ) {
                                Icon(
                                    Icons.Default.ShoppingCart,
                                    contentDescription = "Cart",
                                    tint = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White),
                        placeholder = {
                            Text(
                                "Search books...",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
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

        // Category Chips - Horizontally scrollable
        AnimatedVisibility(
            visible = categoriesVisible,
            enter = fadeIn(tween(500)) + slideInHorizontally(tween(500)) { 50 }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CategoryChip(
                    label = "All",
                    selected = selectedCategory == null,
                    onClick = { viewModel.setCategory(null) }
                )
                BookGenre.values().filter { it != BookGenre.ALL }.forEach { genre ->
                    CategoryChip(
                        label = genre.displayName,
                        selected = selectedCategory == genre,
                        onClick = { viewModel.setCategory(genre) }
                    )
                }
            }
        }

        // Books Grid - Single scrollable list
        AnimatedVisibility(
            visible = contentVisible,
            enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { 100 }
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Show message if no books found
                if (shopBooks.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "No books found",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    items(shopBooks) { book ->
                        ShopBookCard(
                            book = book,
                            onAddToCart = { viewModel.addToCart(book) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium) },
        leadingIcon = if (selected) {
            { Icon(Icons.Default.FilterList, contentDescription = null, modifier = Modifier.size(16.dp)) }
        } else null,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = GradientStart,
            selectedLabelColor = Color.White,
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(20.dp),
        border = null
    )
}

@Composable
fun ShopBookCard(
    book: ShopBook,
    onAddToCart: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(420.dp) // Increased height for better spacing
            .shadow(
                elevation = 4.dp, // Reduced elevation for cleaner look
                shape = RoundedCornerShape(16.dp),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxSize()
        ) {
            // Cover Image Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Image(
                    painter = painterResource(id = book.coverImg),
                    contentDescription = book.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Badges (New/Best)
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (book.isNew) {
                        Badge(
                            containerColor = SuccessGreen,
                            contentColor = Color.White,
                            modifier = Modifier.padding(0.dp)
                        ) {
                            Text("NEW", fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                        }
                    }
                    if (book.isBestSeller) {
                        Badge(
                            containerColor = AccentGold,
                            contentColor = Color.White,
                            modifier = Modifier.padding(0.dp)
                        ) {
                            Text("BEST", fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                        }
                    }
                }

                // Discount badge (Subtler)
                if (book.discountPercent > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                            .background(
                                color = Color(0xFFEF4444).copy(alpha = 0.9f),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            "-${book.discountPercent}%",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Book Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = book.author,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 12.sp
                )
                
                Spacer(modifier = Modifier.height(6.dp))

                // Rating
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = AccentGold,
                        modifier = Modifier.size(12.dp) // Smaller star
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        String.format("%.1f", book.rating),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Price & Add to Cart
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                 Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "₹${book.discountedPrice.toInt()}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    if (book.discountPercent > 0) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "₹${book.price.toInt()}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha=0.7f),
                            textDecoration = TextDecoration.LineThrough,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(10.dp))

                // Full width Add to Cart Button
                 androidx.compose.material3.Button(
                    onClick = onAddToCart,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Add",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}


