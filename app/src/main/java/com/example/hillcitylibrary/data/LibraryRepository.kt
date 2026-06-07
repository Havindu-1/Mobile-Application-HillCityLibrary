package com.example.hillcitylibrary.data

import android.util.Log
import com.example.hillcitylibrary.data.api.OpenLibraryApiService
import com.example.hillcitylibrary.data.local.BookDao
import com.example.hillcitylibrary.data.local.BookEntity
import com.example.hillcitylibrary.model.Book
import com.example.hillcitylibrary.model.BookGenre
import com.example.hillcitylibrary.model.ReadingProgress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LibraryRepository(
    private val bookDao: BookDao,
    private val apiService: OpenLibraryApiService
) {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    // Online search results
    val searchResults: StateFlow<List<Book>> = _searchResults.asStateFlow()

    private val _networkError = MutableStateFlow(false)
    val networkError: StateFlow<Boolean> = _networkError.asStateFlow()

    // Local library books (favorites, reserved, in progress)
    val libraryBooks: StateFlow<List<Book>> = bookDao.getAllBooks().map { entities ->
        entities.map { it.toDomainModel() }
    }.let { flow ->
        val stateFlow = MutableStateFlow<List<Book>>(emptyList())
        coroutineScope.launch {
            flow.collect { stateFlow.value = it }
        }
        stateFlow
    }

    suspend fun searchBooksOnline(query: String, genre: BookGenre = BookGenre.ALL) {
        try {
            val response = apiService.searchBooks(query = query)
            _networkError.value = false
            val mappedBooks = response.docs?.mapNotNull { doc ->
                Book(
                    id = doc.key,
                    title = doc.title,
                    author = doc.authorName?.joinToString(", ") ?: "Unknown Author",
                    description = doc.firstSentence?.joinToString(" ") ?: "No description available.",
                    coverImg = null,
                    coverUrl = doc.coverI?.let { "https://covers.openlibrary.org/b/id/$it-L.jpg" },
                    genre = genre,
                    pageCount = doc.numberOfPagesMedian ?: 300,
                    rating = doc.ratingsAverage ?: 0.0,
                    isFavorite = false,
                    isReserved = false,
                    progress = null
                )
            } ?: emptyList()
            if (mappedBooks.isEmpty()) {
                _searchResults.value = getMockDataFallback(query)
            } else {
                _searchResults.value = mappedBooks
            }
        } catch (e: Exception) {
            Log.e("LibraryRepository", "Error searching books", e)
            _networkError.value = true
            _searchResults.value = getMockDataFallback(query)
        }
    }

    fun clearSearchResults() {
        _searchResults.value = emptyList()
    }

    private fun getMockDataFallback(query: String): List<Book> {
        return com.example.hillcitylibrary.data.MockData.sampleBooks.filter {
            it.title.contains(query, ignoreCase = true) ||
            it.author.contains(query, ignoreCase = true) ||
            query.contains("subject")
        }
    }

    fun toggleFavorite(bookId: String, currentBook: Book? = null) {
        coroutineScope.launch {
            val localBook = bookDao.getBookById(bookId)
            if (localBook != null) {
                bookDao.updateFavoriteStatus(bookId, !localBook.isFavorite)
            } else if (currentBook != null) {
                val newBook = currentBook.copy(isFavorite = true)
                bookDao.insertBook(newBook.toEntity())
            }
        }
    }

    fun reserveBook(bookId: String, currentBook: Book? = null) {
        coroutineScope.launch {
            val localBook = bookDao.getBookById(bookId)
            if (localBook != null) {
                bookDao.updateReservedStatus(bookId, !localBook.isReserved)
            } else if (currentBook != null) {
                val newBook = currentBook.copy(isReserved = true)
                bookDao.insertBook(newBook.toEntity())
            }
        }
    }
    
    fun getBook(bookId: String): Book? {
        // Try local first, then search results
        val local = libraryBooks.value.find { it.id == bookId }
        if (local != null) return local
        return _searchResults.value.find { it.id == bookId }
    }

    private val _collections = MutableStateFlow<List<com.example.hillcitylibrary.model.BookCollection>>(emptyList())
    val collections: StateFlow<List<com.example.hillcitylibrary.model.BookCollection>> = _collections.asStateFlow()

    fun createCollection(name: String) {
        val newCollection = com.example.hillcitylibrary.model.BookCollection(
            id = java.util.UUID.randomUUID().toString(),
            name = name
        )
        _collections.update { it + newCollection }
    }

    fun deleteCollection(collectionId: String) {
        _collections.update { it.filter { collection -> collection.id != collectionId } }
    }

    fun addBookToCollection(collectionId: String, bookId: String) {
        _collections.update { currentCollections ->
            currentCollections.map { collection ->
                if (collection.id == collectionId && !collection.bookIds.contains(bookId)) {
                    collection.copy(bookIds = collection.bookIds + bookId)
                } else {
                    collection
                }
            }
        }
    }

    fun removeBookFromCollection(collectionId: String, bookId: String) {
        _collections.update { currentCollections ->
            currentCollections.map { collection ->
                if (collection.id == collectionId) {
                    collection.copy(bookIds = collection.bookIds - bookId)
                } else {
                    collection
                }
            }
        }
    }

    fun updateProgress(bookId: String, pagesRead: Int, timeSpentMinutes: Long, currentBook: Book? = null) {
        coroutineScope.launch {
            val localBook = bookDao.getBookById(bookId)
            val maxPages = (localBook?.pageCount ?: currentBook?.pageCount ?: 1).takeIf { it > 0 } ?: Int.MAX_VALUE
            val newCurrentPage = ((localBook?.currentPage ?: 0) + pagesRead).coerceAtMost(maxPages)
            val newTotalTime = (localBook?.totalTimeSpentMinutes ?: 0L) + timeSpentMinutes
            val isCompleted = newCurrentPage >= maxPages
            
            if (localBook != null) {
                bookDao.updateProgress(bookId, newCurrentPage, newTotalTime, isCompleted)
            } else if (currentBook != null) {
                val progress = ReadingProgress(newCurrentPage, newTotalTime, isCompleted)
                val newBook = currentBook.copy(progress = progress)
                bookDao.insertBook(newBook.toEntity())
            }
        }
    }

    private fun BookEntity.toDomainModel() = Book(
        id = id,
        title = title,
        author = author,
        description = description,
        coverImg = coverImgRes,
        coverUrl = coverUrl,
        genre = BookGenre.values().find { it.name == genreName } ?: BookGenre.ALL,
        pageCount = pageCount,
        rating = rating,
        isFavorite = isFavorite,
        isReserved = isReserved,
        progress = ReadingProgress(
            currentPage = currentPage,
            totalTimeSpentMinutes = totalTimeSpentMinutes,
            isCompleted = isCompleted
        )
    )

    private fun Book.toEntity() = BookEntity(
        id = id,
        title = title,
        author = author,
        description = description,
        coverImgRes = coverImg,
        coverUrl = coverUrl,
        genreName = genre.name,
        pageCount = pageCount,
        rating = rating,
        isFavorite = isFavorite,
        isReserved = isReserved,
        currentPage = progress?.currentPage ?: 0,
        totalTimeSpentMinutes = progress?.totalTimeSpentMinutes ?: 0L,
        isCompleted = progress?.isCompleted ?: false
    )
}
