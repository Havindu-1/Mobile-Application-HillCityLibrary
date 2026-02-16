package com.example.hillcitylibrary.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hillcitylibrary.data.LibraryRepository
import com.example.hillcitylibrary.model.Book
import com.example.hillcitylibrary.model.BookGenre
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class BookViewModel : ViewModel() {
    private val repository = LibraryRepository

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedGenre = MutableStateFlow<BookGenre?>(null)
    val selectedGenre: StateFlow<BookGenre?> = _selectedGenre.asStateFlow()

    val books: StateFlow<List<Book>> = repository.books

    // Sorting
    enum class SortOption {
        TITLE, AUTHOR, RATING
    }

    private val _sortOption = MutableStateFlow(SortOption.TITLE)
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()

    fun onSortOptionSelected(option: SortOption) {
        _sortOption.value = option
    }

    // View Mode
    private val _isGridView = MutableStateFlow(false)
    val isGridView: StateFlow<Boolean> = _isGridView.asStateFlow()

    fun toggleViewMode() {
        _isGridView.value = !_isGridView.value
    }

    // Derived state for filtered books
    val filteredBooks: StateFlow<List<Book>> = combine(
        books,
        _searchQuery,
        _selectedGenre,
        _sortOption
    ) { currentBooks, query, genre, sort ->
        val filtered = currentBooks.filter { book ->
            val matchesQuery = book.title.contains(query, ignoreCase = true) ||
                    book.author.contains(query, ignoreCase = true)
            val matchesGenre = genre == null || genre == BookGenre.ALL || book.genre == genre
            matchesQuery && matchesGenre
        }
        
        when (sort) {
            SortOption.TITLE -> filtered.sortedBy { it.title }
            SortOption.AUTHOR -> filtered.sortedBy { it.author }
            SortOption.RATING -> filtered.sortedByDescending { it.rating }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val favoriteBooks: StateFlow<List<Book>> = books
        .map { it.filter { book -> book.isFavorite } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onGenreSelected(genre: BookGenre?) {
        _selectedGenre.value = genre
    }

    fun toggleFavorite(bookId: String) {
        repository.toggleFavorite(bookId)
    }

    fun reserveBook(bookId: String) {
        repository.reserveBook(bookId)
    }

    val readingStats = books.map { currentBooks ->
        val completed = currentBooks.count { it.progress?.isCompleted == true }
        val totalPages = currentBooks.sumOf { it.progress?.currentPage ?: 0 }
        val totalTime = currentBooks.sumOf { it.progress?.totalTimeSpentMinutes ?: 0 }
        Triple(completed, totalPages, totalTime)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Triple(0, 0, 0L))

    fun updateProgress(bookId: String, pagesRead: Int, timeSpentMinutes: Long) {
       repository.updateProgress(bookId, pagesRead, timeSpentMinutes)
    }

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    fun toggleTheme(isDark: Boolean) {
        _isDarkTheme.value = isDark
    }

    // Collections
    val collections = repository.collections

    fun createCollection(name: String) = repository.createCollection(name)
    fun deleteCollection(collectionId: String) = repository.deleteCollection(collectionId)
    fun addBookToCollection(collectionId: String, bookId: String) = repository.addBookToCollection(collectionId, bookId)
    fun removeBookFromCollection(collectionId: String, bookId: String) = repository.removeBookFromCollection(collectionId, bookId)

    fun getBook(bookId: String) = books.map { it.find { book -> book.id == bookId } }
}
