package com.example.hillcitylibrary.data

import com.example.hillcitylibrary.model.Book
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object LibraryRepository {
    private val _books = MutableStateFlow(MockData.sampleBooks)
    val books: StateFlow<List<Book>> = _books.asStateFlow()

    fun toggleFavorite(bookId: String) {
        _books.update { currentBooks ->
            currentBooks.map { book ->
                if (book.id == bookId) {
                    book.copy(isFavorite = !book.isFavorite)
                } else {
                    book
                }
            }
        }
    }

    fun reserveBook(bookId: String) {
        _books.update { currentBooks ->
            currentBooks.map { book ->
                if (book.id == bookId) {
                    book.copy(isReserved = !book.isReserved)
                } else {
                    book
                }
            }
        }
    }
    
    fun getBook(bookId: String): Book? {
        return _books.value.find { it.id == bookId }
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

    fun updateProgress(bookId: String, pagesRead: Int, timeSpentMinutes: Long) {
        _books.update { currentBooks ->
            currentBooks.map { book ->
                if (book.id == bookId) {
                    val currentProgress = book.progress ?: com.example.hillcitylibrary.model.ReadingProgress()
                    val newCurrentPage = (currentProgress.currentPage + pagesRead).coerceAtMost(book.pageCount)
                    val newTotalTime = currentProgress.totalTimeSpentMinutes + timeSpentMinutes
                    val isCompleted = newCurrentPage >= book.pageCount
                    
                    book.copy(progress = currentProgress.copy(
                        currentPage = newCurrentPage,
                        totalTimeSpentMinutes = newTotalTime,
                        isCompleted = isCompleted
                    ))
                } else {
                    book
                }
            }
        }
    }
}
