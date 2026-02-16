package com.example.hillcitylibrary.data

import com.example.hillcitylibrary.R
import com.example.hillcitylibrary.model.Book
import com.example.hillcitylibrary.model.BookGenre

object MockData {
    val sampleBooks = listOf(
        Book(
            title = "The Great Gatsby",
            author = "F. Scott Fitzgerald",
            description = "A novel set in the Jazz Age that tells the story of Jay Gatsby's unrequited love for Daisy Buchanan.",
            coverImg = R.drawable.book4,
            genre = BookGenre.FICTION,
            pageCount = 180,
            rating = 4.5
        ),
        Book(
            title = "Clean Code",
            author = "Robert C. Martin",
            description = "A Handbook of Agile Software Craftsmanship.",
            coverImg = R.drawable.book2,
            genre = BookGenre.TECHNOLOGY,
            pageCount = 464,
            rating = 4.8
        ),
        Book(
            title = "Sapiens",
            author = "Yuval Noah Harari",
            description = "A Brief History of Humankind.",
            coverImg = R.drawable.book3,
            genre = BookGenre.NON_FICTION,
            pageCount = 443,
            rating = 4.7
        ),
        Book(
            title = "To Kill a Mockingbird",
            author = "Harper Lee",
            description = "A novel by Harper Lee published in 1960. It was immediately successful, winning the Pulitzer Prize, and has become a classic of modern American literature.",
            coverImg = R.drawable.book1,
            genre = BookGenre.NOVEL,
            pageCount = 281,
            rating = 4.9
        ),
        Book(
            title = "Introduction to Algorithms",
            author = "Thomas H. Cormen",
            description = "Typically known as CLRS, it is the standard textbook for algorithms.",
            coverImg = R.drawable.book5,
            genre = BookGenre.EDUCATION,
            pageCount = 1312,
            rating = 4.6
        ),
        Book(
            title = "1984",
            author = "George Orwell",
            description = "A dystopian social science fiction novel and cautionary tale.",
            coverImg = R.drawable.book6,
            genre = BookGenre.FICTION,
            pageCount = 328,
            rating = 4.8
        ),
        Book(
            title = "Atomic Habits",
            author = "James Clear",
            description = "Your journey to mastery.",
            coverImg = R.drawable.book8,
            genre = BookGenre.TECHNOLOGY,
            pageCount = 352,
            rating = 4.9
        ),
        Book(
            title = "Beast Quest Chapter 1",
            author = "James Clear",
            description = "Your journey to mastery.",
            coverImg = R.drawable.book9,
            genre = BookGenre.FICTION,
            pageCount = 352,
            rating = 4.9
        ),
        Book(
            title = "Meditation",
            author = "Marcus Aurelius",
            description = "Your journey to mastery.",
            coverImg = R.drawable.book10,
            genre = BookGenre.TECHNOLOGY,
            pageCount = 352,
            rating = 4.9
        ),
        Book(
            title = "Harry Potter and the Sorcerer's Stone",
            author = "J.K. Rowling",
            description = "Your journey to mastery.",
            coverImg = R.drawable.book7,
            genre = BookGenre.TECHNOLOGY,
            pageCount = 352,
            rating = 4.9
        )

    )
}
