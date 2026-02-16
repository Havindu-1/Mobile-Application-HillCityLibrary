package com.example.hillcitylibrary.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.hillcitylibrary.model.BookGenre
import com.example.hillcitylibrary.ui.BookViewModel
import com.example.hillcitylibrary.ui.components.GreetingHeader
import com.example.hillcitylibrary.ui.components.LibraryBookGridItem
import com.example.hillcitylibrary.ui.components.LibraryBookItem
import com.example.hillcitylibrary.ui.navigation.Screen
import com.example.hillcitylibrary.ui.theme.GradientEnd
import com.example.hillcitylibrary.ui.theme.GradientStart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    navController: NavController,
    viewModel: BookViewModel = viewModel()
) {
    val filteredBooks by viewModel.filteredBooks.collectAsState()
    val selectedGenre by viewModel.selectedGenre.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isGridView by viewModel.isGridView.collectAsState()
    val sortOption by viewModel.sortOption.collectAsState()
    
    var showSortMenu by remember { mutableStateOf(false) }

    // Animation States
    var controlsVisible by remember { mutableStateOf(false) }
    var listVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        controlsVisible = true
        delay(100)
        listVisible = true
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier.background(
                    brush = Brush.verticalGradient(
                        colors = listOf(GradientStart, GradientEnd)
                    )
                )
            ) {
                Column {
                    TopAppBar(
                        title = {
                            Text(
                                text = "Library",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White
                                )
                            }
                        },
                        actions = {
                            // Search Toggle or Icon could go here, but we'll use a persistent bar below
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = Color.White,
                            navigationIconContentColor = Color.White
                        )
                    )
                    
                    // Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = viewModel::onSearchQueryChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .background(Color.White, RoundedCornerShape(12.dp)),
                        placeholder = { Text("Search title, author...", color = Color.Gray) },
                        leadingIcon = { Icon(Icons.Default.Search, "Search", tint = Color.Gray) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                    Icon(Icons.Default.Close, "Clear", tint = Color.Gray)
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Controls Row (Filter, Sort, Toggle)
            AnimatedVisibility(
                visible = controlsVisible,
                enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { -50 }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Genre Filter List
                    LazyRow(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(BookGenre.values()) { genre ->
                            val isSelected = genre == selectedGenre || (selectedGenre == null && genre == BookGenre.ALL)
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.onGenreSelected(if (genre == BookGenre.ALL) null else genre) },
                                label = { Text(genre.displayName) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Sort Button
                    Box {
                        IconButton(onClick = { showSortMenu = true }) {
                            Icon(Icons.Default.Sort, "Sort")
                        }
                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            BookViewModel.SortOption.values().forEach { option ->
                                DropdownMenuItem(
                                    text = { 
                                        Text(
                                            option.name.lowercase().replaceFirstChar { 
                                                if (it.isLowerCase()) it.titlecase() else it.toString() 
                                            }
                                        ) 
                                    },
                                    onClick = {
                                        viewModel.onSortOptionSelected(option)
                                        showSortMenu = false
                                    },
                                    leadingIcon = {
                                        if (option == sortOption) {
                                            Icon(Icons.Default.FilterList, contentDescription = null, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                )
                            }
                        }
                    }

                    // View Toggle Button
                    IconButton(onClick = viewModel::toggleViewMode) {
                        Icon(
                            imageVector = if (isGridView) Icons.Default.ViewList else Icons.Default.GridView,
                            contentDescription = "Toggle View"
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = listVisible,
                enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { 100 }
            ) {
                Column {
                    // Results Count
                    Text(
                        text = "${filteredBooks.size} books found",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )

                    if (filteredBooks.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.Search, 
                                    contentDescription = null, 
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "No books found",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        if (isGridView) {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(filteredBooks) { book ->
                                    LibraryBookGridItem(
                                        book = book,
                                        onBookClick = { bookId -> navController.navigate(Screen.BookDetails.createRoute(bookId)) }
                                    )
                                }
                                item { Spacer(modifier = Modifier.height(80.dp)) }
                            }
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(filteredBooks) { book ->
                                    LibraryBookItem(
                                        book = book,
                                        onBookClick = { bookId -> navController.navigate(Screen.BookDetails.createRoute(bookId)) },
                                        onReserveClick = viewModel::reserveBook,
                                        onFavoriteClick = viewModel::toggleFavorite
                                    )
                                }
                                item { Spacer(modifier = Modifier.height(80.dp)) }
                            }
                        }
                    }
                }
            }
        }
    }
}

