package com.example.hillcitylibrary.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.hillcitylibrary.model.BookGenre
import com.example.hillcitylibrary.ui.BookViewModel
import com.example.hillcitylibrary.ui.components.GreetingHeader
import com.example.hillcitylibrary.ui.components.LibraryBookItem
import com.example.hillcitylibrary.ui.navigation.Screen
import com.example.hillcitylibrary.ui.theme.GradientStart
import com.example.hillcitylibrary.ui.theme.GradientEnd
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.TopAppBarDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    navController: NavController,
    viewModel: BookViewModel = viewModel()
) {
    val filteredBooks by viewModel.filteredBooks.collectAsState()
    val selectedGenre by viewModel.selectedGenre.collectAsState()



    Scaffold(
        topBar = {
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
                                text = "Library",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = Color.White
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Categories
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(BookGenre.values()) { genre ->
                    FilterChip(
                        selected = genre == selectedGenre || (selectedGenre == null && genre == BookGenre.ALL),
                        onClick = { viewModel.onGenreSelected(if (genre == BookGenre.ALL) null else genre) },
                        label = { Text(genre.displayName) },
                        modifier = Modifier
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Book List
            LazyColumn(
                contentPadding = PaddingValues(bottom = 80.dp) // Space for bottom nav
            ) {
                items(filteredBooks) { book ->
                    LibraryBookItem(
                        book = book,
                        onBookClick = { bookId -> navController.navigate(Screen.BookDetails.createRoute(bookId)) },
                        onReserveClick = viewModel::reserveBook,
                        onFavoriteClick = viewModel::toggleFavorite

                    )
                }
            }
        }
    }
}
