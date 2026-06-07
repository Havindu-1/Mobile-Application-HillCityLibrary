package com.example.hillcitylibrary.model

import java.util.UUID

data class Book(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val author: String,
    val description: String,
    val coverImg: Int? = null,
    val coverUrl: String? = null,
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
    MYSTERY("Mystery & Thriller"),
    SCI_FI("Science Fiction"),
    FANTASY("Fantasy"),
    BIOGRAPHY("Biography"),
    HISTORY("History"),
    ROMANCE("Romance"),
    ALL("All")
}

data class ReadingProgress(
    val currentPage: Int = 0,
    val totalTimeSpentMinutes: Long = 0,
    val isCompleted: Boolean = false
)
