package com.example.hillcitylibrary.model

import java.util.UUID

data class Book(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val author: String,
    val description: String,
    val coverImg: Int, // In a real app this might be a URL, here likely a wrapper or resource ID logic
    val genre: BookGenre,
    val pageCount: Int,
    val rating: Double = 0.0,
    val isFavorite: Boolean = false,
    val isReserved: Boolean = false,
    val progress: ReadingProgress? = null
)

enum class BookGenre(val displayName: String) {
    FICTION("Fiction"),
    NON_FICTION("Non-Fiction"),
    EDUCATION("Education"),
    TECHNOLOGY("Technology"),
    NOVEL("Novels"),
    ALL("All")
}

data class ReadingProgress(
    val currentPage: Int = 0,
    val totalTimeSpentMinutes: Long = 0,
    val isCompleted: Boolean = false
)
