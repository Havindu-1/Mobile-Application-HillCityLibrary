package com.example.hillcitylibrary.ui.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ShoppingCart

import androidx.compose.material.icons.filled.MenuBook

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Home : Screen("home", "Home", Icons.Filled.Home)
    object Library : Screen("library", "Library", Icons.Filled.MenuBook)
    object Shop : Screen("shop", "Shop", Icons.Filled.ShoppingCart)
    object Favorites : Screen("favorites", "Favorites", Icons.Filled.Favorite)
    object Progress : Screen("progress", "Progress", Icons.Filled.DateRange)
    object Profile : Screen("profile", "Profile", Icons.Filled.Person)
    object Settings : Screen("settings", "Settings")
    object BookDetails : Screen("book_details/{bookId}", "Book Details") {
        fun createRoute(bookId: String) = "book_details/$bookId"
    }
    object Cart : Screen("cart", "Cart")

    object Login : Screen("login", "Login")
    object SignUp : Screen("signup", "Sign Up")
}


val bottomNavItems = listOf(
    Screen.Home,
    Screen.Library,
    Screen.Favorites,
    Screen.Shop,
    Screen.Profile
)
