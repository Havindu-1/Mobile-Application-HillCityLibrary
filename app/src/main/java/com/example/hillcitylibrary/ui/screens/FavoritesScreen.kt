package com.example.hillcitylibrary.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.hillcitylibrary.ui.BookViewModel
import com.example.hillcitylibrary.ui.components.GreetingHeader
import com.example.hillcitylibrary.ui.components.LibraryBookItem
import com.example.hillcitylibrary.ui.navigation.Screen
import com.example.hillcitylibrary.ui.theme.AccentGold
import com.example.hillcitylibrary.ui.theme.GradientEnd
import com.example.hillcitylibrary.ui.theme.GradientStart


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    navController: NavController,
    viewModel: BookViewModel = viewModel()
) {
    val favoriteBooks by viewModel.favoriteBooks.collectAsState()
    val collections by viewModel.collections.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Favorites", "My Shelves")

    // State for Dialogs
    var showCreateCollectionDialog by remember { mutableStateOf(false) }
    var bookIdAddToCollection by remember { mutableStateOf<String?>(null) }
    
    // State for Shelf Details
    var selectedCollection by remember { mutableStateOf<com.example.hillcitylibrary.model.BookCollection?>(null) }

    // Handle Back Press if inside a shelf
    BackHandler(enabled = selectedCollection != null) {
        selectedCollection = null
    }

    // Animation States
    var headerVisible by remember { mutableStateOf(false) }
    var contentVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        headerVisible = true
        delay(100)
        contentVisible = true
    }

    Scaffold(
        topBar = {
            AnimatedVisibility(
                visible = headerVisible,
                enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { -50 }
            ) {
                Box(
                    modifier = Modifier.background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                GradientStart,
                                GradientEnd
                            )
                        )
                    )
                ) {
                    Column {
                        GreetingHeader(
                            modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                            textColor = Color.White
                        )
                        TopAppBar(
                            title = { 
                                Text(
                                    text = if (selectedCollection != null) selectedCollection!!.name else "Favorites",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color.Transparent,
                                titleContentColor = Color.White
                            ),
                            navigationIcon = {
                                IconButton(
                                    onClick = {
                                        if (selectedCollection != null) {
                                            selectedCollection = null
                                        } else {
                                            navController.popBackStack()
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = "Back",
                                        tint = Color.White
                                    )
                                }
                            }
                        )
                        
                        if (selectedCollection == null) {
                            TabRow(
                                selectedTabIndex = selectedTab,
                                containerColor = Color.Transparent,
                                contentColor = Color.White,
                                indicator = { tabPositions ->
                                    TabRowDefaults.SecondaryIndicator(
                                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                        color = AccentGold
                                    )
                                }
                            ) {
                                tabs.forEachIndexed { index, title ->
                                    Tab(
                                        selected = selectedTab == index,
                                        onClick = { selectedTab = index },
                                        text = { 
                                            Text(
                                                title, 
                                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                                color = if (selectedTab == index) Color.White else Color.White.copy(alpha = 0.7f)
                                            ) 
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            if (selectedTab == 1 && selectedCollection == null) {
                FloatingActionButton(
                    onClick = { showCreateCollectionDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Filled.Add, "Create Shelf")
                }
            }
        }
    ) { paddingValues ->
        AnimatedVisibility(
            visible = contentVisible,
            enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { 50 }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (selectedCollection != null) {
                    // Shelf Details View
                    val collectionBooks = viewModel.books.collectAsState().value.filter { selectedCollection!!.bookIds.contains(it.id) }
                    
                    if (collectionBooks.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("This shelf is empty.", style = MaterialTheme.typography.bodyLarge)
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(collectionBooks) { book ->
                                LibraryBookItem(
                                    book = book,
                                    onBookClick = { bookId -> navController.navigate(Screen.BookDetails.createRoute(bookId)) },
                                    onReserveClick = viewModel::reserveBook,
                                    onFavoriteClick = viewModel::toggleFavorite,
                                    onAddToCollectionClick = { /* Already in collection, maybe remove? */ }
                                )
                            }
                        }
                    }
                } else {
                    when (selectedTab) {
                        0 -> { // Favorites Tab
                            if (favoriteBooks.isEmpty()) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "No favorites yet.\nGo to the library to add some!",
                                        style = MaterialTheme.typography.bodyLarge,
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            } else {
                                LazyColumn(
                                    contentPadding = PaddingValues(bottom = 80.dp, top = 16.dp, start = 16.dp, end = 16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(favoriteBooks) { book ->
                                        LibraryBookItem(
                                            book = book,
                                            onBookClick = { bookId -> navController.navigate(Screen.BookDetails.createRoute(bookId)) },
                                            onReserveClick = viewModel::reserveBook,
                                            onFavoriteClick = viewModel::toggleFavorite,
                                            onAddToCollectionClick = { bookId -> bookIdAddToCollection = bookId }
                                        )
                                    }
                                }
                            }
                        }
                        1 -> { // Shelves Tab
                            if (collections.isEmpty()) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "No shelves yet.\nCreate one to organize your books!",
                                        style = MaterialTheme.typography.bodyLarge,
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            } else {
                                LazyVerticalGrid(
                                    columns = GridCells.Adaptive(150.dp),
                                    contentPadding = PaddingValues(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    items(collections.size) { index ->
                                        val collection = collections[index]
                                        Card(
                                            modifier = Modifier
                                                .height(180.dp)
                                                .clickable { selectedCollection = collection },
                                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(16.dp),
                                                verticalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Menu, // Use a generic icon or custom
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(40.dp)
                                                )
                                                Column {
                                                    Text(
                                                        text = collection.name,
                                                        style = MaterialTheme.typography.titleMedium,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    Text(
                                                        text = "${collection.bookIds.size} books",
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialogs
    if (showCreateCollectionDialog) {
        var newCollectionName by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showCreateCollectionDialog = false },
            title = { Text("Create New Shelf") },
            text = {
                OutlinedTextField(
                    value = newCollectionName,
                    onValueChange = { newCollectionName = it },
                    label = { Text("Shelf Name") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newCollectionName.isNotBlank()) {
                            viewModel.createCollection(newCollectionName)
                            showCreateCollectionDialog = false
                        }
                    }
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateCollectionDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (bookIdAddToCollection != null) {
        AlertDialog(
            onDismissRequest = { bookIdAddToCollection = null },
            title = { Text("Add to Shelf") },
            text = {
                if (collections.isEmpty()) {
                    Text("No shelves available. Create one first!")
                } else {
                    LazyColumn {
                        items(collections.size) { index ->
                            val collection = collections[index]
                            ListItem(
                                headlineContent = { Text(collection.name) },
                                trailingContent = {
                                    if (collection.bookIds.contains(bookIdAddToCollection)) {
                                        Icon(Icons.Filled.Check, contentDescription = "Added", tint = Color.Green)
                                    } else {
                                        Button(
                                            onClick = {
                                                viewModel.addBookToCollection(collection.id, bookIdAddToCollection!!)
                                                bookIdAddToCollection = null
                                            }
                                        ) {
                                            Text("Add")
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { bookIdAddToCollection = null }) {
                    Text("Close")
                }
            }
        )
    }
}
