package com.example.hillcitylibrary.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.hillcitylibrary.data.LibraryRepository
import com.example.hillcitylibrary.data.SettingsManager
import com.example.hillcitylibrary.di.DependencyProvider
import com.example.hillcitylibrary.model.Book
import com.example.hillcitylibrary.model.BookGenre
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BookViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = DependencyProvider.provideRepository(application)
    val gamificationManager = DependencyProvider.provideGamificationManager(application)
    private val settingsManager = DependencyProvider.provideSettingsManager(application)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedGenre = MutableStateFlow<BookGenre?>(null)
    val selectedGenre: StateFlow<BookGenre?> = _selectedGenre.asStateFlow()

    private val _visibleBooksLimit = MutableStateFlow(10)
    val visibleBooksLimit: StateFlow<Int> = _visibleBooksLimit.asStateFlow()

    // Combined stream of local library books and search results
    val localBooks: StateFlow<List<Book>> = repository.libraryBooks
    val searchResults: StateFlow<List<Book>> = repository.searchResults
    val networkError = repository.networkError

    // Sorting
    enum class SortOption {
        TITLE, AUTHOR, RATING
    }

    private val _sortOption = MutableStateFlow(SortOption.TITLE)
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()

    fun onSortOptionSelected(option: SortOption) {
        _sortOption.value = option
        _visibleBooksLimit.value = 10
    }

    // View Mode
    private val _isGridView = MutableStateFlow(false)
    val isGridView: StateFlow<Boolean> = _isGridView.asStateFlow()

    fun toggleViewMode() {
        _isGridView.value = !_isGridView.value
    }

    val profilePictureUri: StateFlow<String?> = settingsManager.profilePictureUri

    fun setProfilePictureUri(uri: String?) {
        settingsManager.setProfilePictureUri(uri)
    }

    val combinedBooks: StateFlow<List<Book>> = combine(localBooks, searchResults) { local, search ->
        val map = mutableMapOf<String, Book>()
        search.forEach { map[it.id] = it }
        local.forEach { map[it.id] = it } // local overrides search, preserving progress/favorites
        val result = map.values.toList()
        if (result.isEmpty()) {
            com.example.hillcitylibrary.data.MockData.sampleBooks
        } else {
            result
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), com.example.hillcitylibrary.data.MockData.sampleBooks)

    // Derived state for filtered books
    val filteredBooks: StateFlow<List<Book>> = combine(
        combinedBooks,
        _searchQuery,
        _selectedGenre,
        _sortOption
    ) { allBooks, query, genre, sort ->
        val filtered = allBooks.filter { book ->
            val matchesQuery = if (query.isBlank()) {
                true
            } else {
                val terms = query.trim().split(Regex("\\s+"))
                terms.all { term ->
                    book.title.contains(term, ignoreCase = true) ||
                    book.author.contains(term, ignoreCase = true) ||
                    book.description.contains(term, ignoreCase = true)
                }
            }
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
        initialValue = com.example.hillcitylibrary.data.MockData.sampleBooks
    )

    init {
        // Trigger a default search so the library isn't empty initially
        viewModelScope.launch {
            repository.searchBooksOnline("fiction")
        }
    }

    val favoriteBooks: StateFlow<List<Book>> = localBooks
        .map { it.filter { book -> book.isFavorite } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private var searchJob: Job? = null

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        _visibleBooksLimit.value = 10
        searchJob?.cancel()
        if (query.isBlank()) {
            val currentGenre = _selectedGenre.value
            if (currentGenre != null && currentGenre != BookGenre.ALL) {
                // If query is cleared but a genre is selected, search by genre
                searchJob = viewModelScope.launch {
                    delay(500)
                    repository.searchBooksOnline("subject:${currentGenre.displayName.lowercase()}", currentGenre)
                }
            } else {
                repository.clearSearchResults()
            }
        } else {
            searchJob = viewModelScope.launch {
                delay(500) // debounce
                val currentGenre = _selectedGenre.value ?: BookGenre.ALL
                repository.searchBooksOnline(query, currentGenre)
            }
        }
    }

    fun onGenreSelected(genre: BookGenre?) {
        _selectedGenre.value = genre
        _visibleBooksLimit.value = 10
        
        searchJob?.cancel()
        val currentQuery = _searchQuery.value
        if (currentQuery.isNotBlank()) {
            searchJob = viewModelScope.launch {
                repository.searchBooksOnline(currentQuery, genre ?: BookGenre.ALL)
            }
        } else if (genre != null && genre != BookGenre.ALL) {
            searchJob = viewModelScope.launch {
                repository.searchBooksOnline("subject:${genre.displayName.lowercase()}", genre)
            }
        } else {
            repository.clearSearchResults()
        }
    }

    fun loadMoreBooks() {
        _visibleBooksLimit.value += 10
    }

    fun toggleFavorite(bookId: String) {
        val currentBook = combinedBooks.value.find { it.id == bookId }
        repository.toggleFavorite(bookId, currentBook)
    }

    fun reserveBook(bookId: String) {
        val currentBook = combinedBooks.value.find { it.id == bookId }
        repository.reserveBook(bookId, currentBook)
    }

    val readingStats = localBooks.map { currentBooks ->
        val completed = currentBooks.count { it.progress?.isCompleted == true }
        val totalPages = currentBooks.sumOf { it.progress?.currentPage ?: 0 }
        val totalTime = currentBooks.sumOf { it.progress?.totalTimeSpentMinutes ?: 0 }
        Triple(completed, totalPages, totalTime)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Triple(0, 0, 0L))

    fun updateProgress(bookId: String, pagesRead: Int, timeSpentMinutes: Long) {
        val currentBook = combinedBooks.value.find { it.id == bookId }
        repository.updateProgress(bookId, pagesRead, timeSpentMinutes, currentBook)
        viewModelScope.launch {
            gamificationManager.addReadingProgress(pagesRead, timeSpentMinutes)
        }
    }

    val isDarkTheme = settingsManager.isDarkTheme
    val isLightSensorEnabled = settingsManager.isLightSensorEnabled
    val isMotionSensorEnabled = settingsManager.isMotionSensorEnabled
    val isNoiseSensorEnabled = settingsManager.isNoiseSensorEnabled
    val isNotificationsEnabled = settingsManager.isNotificationsEnabled
 
    fun toggleTheme(isDark: Boolean) {
        settingsManager.setDarkTheme(isDark)
    }
 
    fun toggleLightSensor(enabled: Boolean) {
        settingsManager.setLightSensorEnabled(enabled)
    }
 
    fun toggleMotionSensor(enabled: Boolean) {
        settingsManager.setMotionSensorEnabled(enabled)
    }
 
    fun toggleNoiseSensor(enabled: Boolean) {
        settingsManager.setNoiseSensorEnabled(enabled)
    }
 
    fun toggleNotifications(enabled: Boolean) {
        settingsManager.setNotificationsEnabled(enabled)
    }

    // Collections
    val collections = repository.collections

    fun createCollection(name: String) = repository.createCollection(name)
    fun deleteCollection(collectionId: String) = repository.deleteCollection(collectionId)
    fun addBookToCollection(collectionId: String, bookId: String) = repository.addBookToCollection(collectionId, bookId)
    fun removeBookFromCollection(collectionId: String, bookId: String) = repository.removeBookFromCollection(collectionId, bookId)

    // Book selected for Details screen - persists across recompositions
    private val _selectedBook = MutableStateFlow<Book?>(null)
    val selectedBook: StateFlow<Book?> = _selectedBook.asStateFlow()

    fun selectBook(bookId: String) {
        _selectedBook.value = combinedBooks.value.find { it.id == bookId }
    }

    fun getBook(bookId: String) = combinedBooks.map { allBooks ->
        allBooks.find { it.id == bookId }
    }
}
