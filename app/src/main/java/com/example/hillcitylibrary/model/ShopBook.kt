package com.example.hillcitylibrary.model

data class ShopBook(
    val id: String,
    val title: String,
    val author: String,
    val description: String,
    val coverImg: Int,
    val genre: BookGenre,
    val pageCount: Int,
    val rating: Double = 0.0,
    val price: Double,
    val discountPercent: Int = 0,
    val inStock: Boolean = true,
    val isNew: Boolean = false,
    val isBestSeller: Boolean = false
) {
    val discountedPrice: Double
        get() = if (discountPercent > 0) {
            price * (1 - discountPercent / 100.0)
        } else {
            price
        }
    
    val savedAmount: Double
        get() = price - discountedPrice
}

data class CartItem(
    val book: ShopBook,
    val quantity: Int = 1
) {
    val totalPrice: Double
        get() = book.discountedPrice * quantity
}

data class Order(
    val orderId: String,
    val items: List<CartItem>,
    val totalAmount: Double,
    val orderDate: Long = System.currentTimeMillis(),
    val status: OrderStatus = OrderStatus.PENDING
)

enum class OrderStatus {
    PENDING,
    CONFIRMED,
    SHIPPED,
    DELIVERED,
    CANCELLED
}
