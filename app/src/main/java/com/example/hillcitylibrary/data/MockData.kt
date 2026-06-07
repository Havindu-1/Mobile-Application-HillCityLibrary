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
            genre = BookGenre.HISTORY,
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
            genre = BookGenre.SCI_FI,
            pageCount = 328,
            rating = 4.8
        ),
        Book(
            title = "Atomic Habits",
            author = "James Clear",
            description = "Your journey to mastery.",
            coverImg = R.drawable.book8,
            genre = BookGenre.NON_FICTION,
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
            genre = BookGenre.NON_FICTION,
            pageCount = 352,
            rating = 4.9
        ),
        Book(
            title = "Harry Potter and the Sorcerer's Stone",
            author = "J.K. Rowling",
            description = "Your journey to mastery.",
            coverImg = R.drawable.book7,
            genre = BookGenre.FANTASY,
            pageCount = 352,
            rating = 4.9
        ),
        Book(
            title = "The Hobbit",
            author = "J.R.R. Tolkien",
            description = "A fantasy novel and children's book by English author J. R. R. Tolkien. It was published in 1937 to wide acclaim.",
            coverImg = R.drawable.book1,
            genre = BookGenre.FANTASY,
            pageCount = 310,
            rating = 4.8
        ),
        Book(
            title = "Dune",
            author = "Frank Herbert",
            description = "A science fiction novel by American author Frank Herbert, originally published as two separate serials in Analog magazine.",
            coverImg = R.drawable.book6,
            genre = BookGenre.SCI_FI,
            pageCount = 612,
            rating = 4.7
        ),
            Book(
            title = "The Silent Patient",
            author = "Alex Michaelides",
            description = "A psychological thriller novel by British-Cypriot author Alex Michaelides.",
            coverImg = R.drawable.book4,
            genre = BookGenre.MYSTERY,
            pageCount = 336,
            rating = 4.5
        ),
        Book(
            title = "Steve Jobs",
            author = "Walter Isaacson",
            description = "The authorized self-titled biography of American business magnate Steve Jobs.",
            coverImg = R.drawable.book3,
            genre = BookGenre.BIOGRAPHY,
            pageCount = 656,
            rating = 4.8
        ),
        Book(
            title = "Pride and Prejudice",
            author = "Jane Austen",
            description = "A romantic novel of manners written by Jane Austen in 1813.",
            coverImg = R.drawable.book2,
            genre = BookGenre.ROMANCE,
            pageCount = 279,
            rating = 4.9
        ),
        Book(
            title = "Brave New World",
            author = "Aldous Huxley",
            description = "A dystopian social science fiction novel by English author Aldous Huxley, written in 1931 and published in 1932.",
            coverImg = R.drawable.book8,
            genre = BookGenre.SCI_FI,
            pageCount = 268,
            rating = 4.6
        ),
        Book(
            title = "The Da Vinci Code",
            author = "Dan Brown",
            description = "A 2003 mystery thriller novel by Dan Brown.",
            coverImg = R.drawable.book9,
            genre = BookGenre.MYSTERY,
            pageCount = 489,
            rating = 4.3
        ),
        Book(
            title = "Educated",
            author = "Tara Westover",
            description = "A memoir by American author Tara Westover. Westover recounts overcoming her survivalist, Mormon family to go to college.",
            coverImg = R.drawable.book3,
            genre = BookGenre.BIOGRAPHY,
            pageCount = 352,
            rating = 4.8
        ),
        Book(
            title = "The Guns of August",
            author = "Barbara W. Tuchman",
            description = "A volume of history by Barbara W. Tuchman. It centered on the first month of World War I.",
            coverImg = R.drawable.book10,
            genre = BookGenre.HISTORY,
            pageCount = 511,
            rating = 4.7
        ),
        Book(
            title = "Jane Eyre",
            author = "Charlotte Brontë",
            description = "A novel by English writer Charlotte Brontë, published under the pen name 'Currer Bell' in 1847.",
            coverImg = R.drawable.book2,
            genre = BookGenre.ROMANCE,
            pageCount = 500,
            rating = 4.7
        ),
        Book(
            title = "The Fellowship of the Ring",
            author = "J.R.R. Tolkien",
            description = "The first of three volumes of the epic novel The Lord of the Rings by English author J. R. R. Tolkien.",
            coverImg = R.drawable.book1,
            genre = BookGenre.FANTASY,
            pageCount = 423,
            rating = 4.9
        )
    )
}
