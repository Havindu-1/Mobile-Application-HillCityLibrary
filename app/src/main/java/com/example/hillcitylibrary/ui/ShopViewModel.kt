package com.example.hillcitylibrary.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hillcitylibrary.R
import com.example.hillcitylibrary.model.BookGenre
import com.example.hillcitylibrary.model.CartItem
import com.example.hillcitylibrary.model.Order
import com.example.hillcitylibrary.model.OrderStatus
import com.example.hillcitylibrary.model.ShopBook
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.util.UUID

class ShopViewModel : ViewModel() {

    // Sample shop books with pricing
    private val shopBooksData = listOf(
        ShopBook(
            id = "shop_1",
            title = "The Midnight Library",
            author = "Matt Haig",
            description = "A dazzling novel about all the choices that go into a life well lived.",
            coverImg = R.drawable.book9,
            genre = BookGenre.FICTION,
            pageCount = 304,
            rating = 4.5,
            price = 599.0,
            discountPercent = 20,
            isNew = true,
            isBestSeller = true
        ),
        ShopBook(
            id = "shop_2",
            title = "Atomic Habits",
            author = "James Clear",
            description = "An Easy & Proven Way to Build Good Habits & Break Bad Ones.",
            coverImg = R.drawable.book10,
            genre = BookGenre.NON_FICTION,
            pageCount = 320,
            rating = 4.8,
            price = 499.0,
            discountPercent = 15,
            isBestSeller = true
        ),
        ShopBook(
            id = "shop_3",
            title = "Deep Work",
            author = "Cal Newport",
            description = "Rules for Focused Success in a Distracted World.",
            coverImg = R.drawable.book2,
            genre = BookGenre.NON_FICTION,
            pageCount = 296,
            rating = 4.6,
            price = 449.0,
            discountPercent = 10
        ),
        ShopBook(
            id = "shop_4",
            title = "Clean Code",
            author = "Robert C. Martin",
            description = "A Handbook of Agile Software Craftsmanship.",
            coverImg = R.drawable.book4,
            genre = BookGenre.TECHNOLOGY,
            pageCount = 464,
            rating = 4.7,
            price = 799.0,
            discountPercent = 25,
            isBestSeller = true,
            isNew = true
        ),
        ShopBook(
            id = "shop_5",
            title = "The Psychology of Money",
            author = "Morgan Housel",
            description = "Timeless lessons on wealth, greed, and happiness.",
            coverImg = R.drawable.book3,
            genre = BookGenre.NON_FICTION,
            pageCount = 256,
            rating = 4.9,
            price = 399.0,
            discountPercent = 0,
            isNew = true
        ),
        ShopBook(
            id = "shop_6",
            title = "1984",
            author = "George Orwell",
            description = "A dystopian social science fiction novel and cautionary tale.",
            coverImg = R.drawable.book5,
            genre = BookGenre.FICTION,
            pageCount = 328,
            rating = 4.4,
            price = 299.0,
            discountPercent = 5
        ),
        ShopBook(
            id = "shop_7",
            title = "Sapiens",
            author = "Yuval Noah Harari",
            description = "A Brief History of Humankind.",
            coverImg = R.drawable.book6,
            genre = BookGenre.NON_FICTION,
            pageCount = 443,
            rating = 4.7,
            price = 699.0,
            discountPercent = 30,
            isBestSeller = true
        ),
        ShopBook(
            id = "shop_8",
            title = "The Alchemist",
            author = "Paulo Coelho",
            description = "A magical fable about following your dreams.",
            coverImg = R.drawable.book7,
            genre = BookGenre.NOVEL,
            pageCount = 208,
            rating = 4.5,
            price = 349.0,
            discountPercent = 20
        )
    )

    private val _shopBooks = MutableStateFlow(shopBooksData)
    val shopBooks: StateFlow<List<ShopBook>> = _shopBooks.asStateFlow()

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _selectedCategory = MutableStateFlow<BookGenre?>(null)
    val selectedCategory: StateFlow<BookGenre?> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    // Computed properties
    val cartTotal: StateFlow<Double> = cartItems
        .map { items -> items.sumOf { it.totalPrice } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val cartItemCount: StateFlow<Int> = cartItems
        .map { items -> items.sumOf { it.quantity } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Filter books based on search and category
    val filteredBooks: StateFlow<List<ShopBook>> = combine(
        shopBooks,
        selectedCategory,
        searchQuery
    ) { books, category, query ->
        books.filter { book ->
            val matchesCategory = category == null || category == BookGenre.ALL || book.genre == category
            val matchesSearch = query.isEmpty() ||
                book.title.contains(query, ignoreCase = true) ||
                book.author.contains(query, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), shopBooksData)

    fun addToCart(book: ShopBook, quantity: Int = 1) {
        _cartItems.update { currentCart ->
            val existingItem = currentCart.find { it.book.id == book.id }
            if (existingItem != null) {
                // Update quantity if item already exists
                currentCart.map { item ->
                    if (item.book.id == book.id) {
                        item.copy(quantity = item.quantity + quantity)
                    } else {
                        item
                    }
                }
            } else {
                // Add new item
                currentCart + CartItem(book, quantity)
            }
        }
    }

    fun removeFromCart(bookId: String) {
        _cartItems.update { currentCart ->
            currentCart.filter { it.book.id != bookId }
        }
    }

    fun updateCartItemQuantity(bookId: String, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeFromCart(bookId)
            return
        }

        _cartItems.update { currentCart ->
            currentCart.map { item ->
                if (item.book.id == bookId) {
                    item.copy(quantity = newQuantity)
                } else {
                    item
                }
            }
        }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    fun checkout(): Order? {
        val items = _cartItems.value
        if (items.isEmpty()) return null

        val order = Order(
            orderId = UUID.randomUUID().toString(),
            items = items,
            totalAmount = items.sumOf { it.totalPrice },
            orderDate = System.currentTimeMillis(),
            status = OrderStatus.CONFIRMED
        )

        _orders.update { it + order }
        clearCart()

        return order
    }

    fun setCategory(category: BookGenre?) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun getBook(bookId: String): ShopBook? {
        return _shopBooks.value.find { it.id == bookId }
    }
}
