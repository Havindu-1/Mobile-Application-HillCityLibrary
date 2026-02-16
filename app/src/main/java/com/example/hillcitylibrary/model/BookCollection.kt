package com.example.hillcitylibrary.model

data class BookCollection(
    val id: String,
    val name: String,
    val bookIds: List<String> = emptyList()
)
