package com.example.hillcitylibrary.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.hillcitylibrary.ui.navigation.Screen
import com.example.hillcitylibrary.ui.navigation.bottomNavItems
import com.example.hillcitylibrary.ui.screens.*
import com.example.hillcitylibrary.ui.theme.AccentGold
import com.example.hillcitylibrary.ui.theme.GradientEnd
import com.example.hillcitylibrary.ui.theme.GradientStart

@Composable
fun HillCityLibraryApp(
    viewModel: BookViewModel = viewModel(),
    shopViewModel: ShopViewModel = viewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            // Show bottom bar only on main screens, not on login/signup/cart
            if (currentRoute != Screen.Login.route && 
                currentRoute != Screen.SignUp.route &&
                currentRoute != Screen.Cart.route) {
                
                // Enhanced Navigation Bar with gradient background
                Box(
                    modifier = Modifier
                        .shadow(
                            elevation = 16.dp,
                            spotColor = GradientStart.copy(alpha = 0.3f)
                        )
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    GradientStart.copy(alpha = 0.98f),
                                    GradientEnd.copy(alpha = 0.98f)
                                )
                            )
                        )
                ) {
                    NavigationBar(
                        modifier = Modifier.height(80.dp),
                        containerColor = Color.Transparent,
                        tonalElevation = 0.dp
                    ) {
                        val currentDestination = navBackStackEntry?.destination
                        bottomNavItems.forEach { screen ->
                            val isSelected = currentDestination?.hierarchy?.any { 
                                it.route == screen.route 
                            } == true
                            
                            // Animated scale for selected items
                            val scale by animateFloatAsState(
                                targetValue = if (isSelected) 1.2f else 1f,
                                animationSpec = tween(durationMillis = 200),
                                label = "nav_scale"
                            )
                            
                            // Animated color for icons
                            val iconColor by animateColorAsState(
                                targetValue = if (isSelected) AccentGold else Color.White.copy(alpha = 0.6f),
                                animationSpec = tween(durationMillis = 200),
                                label = "icon_color"
                            )
                            
                            NavigationBarItem(
                                icon = { 
                                    Icon(
                                        screen.icon!!,
                                        contentDescription = screen.title,
                                        modifier = Modifier
                                            .scale(scale)
                                            .size(24.dp), // Consistent size
                                        tint = iconColor
                                    )
                                },
                                label = { 
                                    if(isSelected) {
                                        Text(
                                            screen.title,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AccentGold
                                        )
                                    }
                                },
                                selected = isSelected,
                                onClick = {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = AccentGold,
                                    unselectedIconColor = Color.White.copy(alpha = 0.6f),
                                    selectedTextColor = AccentGold, // Match icon
                                    unselectedTextColor = Color.Transparent, // Hide unselected label for cleaner look
                                    indicatorColor = Color.White.copy(alpha = 0.1f) // Subtle indicator
                                )
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            composable(Screen.Login.route) { LoginScreen(navController, viewModel) }
            composable(Screen.SignUp.route) { SignUpScreen(navController, viewModel) }
            composable(Screen.Home.route) { HomeScreen(navController, viewModel) }
            composable(Screen.Library.route) { LibraryScreen(navController, viewModel) }
            composable(Screen.Shop.route) { ShopScreen(navController, shopViewModel) }
            composable(Screen.Cart.route) { CartScreen(navController, shopViewModel) }
            composable(Screen.Favorites.route) { FavoritesScreen(navController, viewModel) }
            composable(Screen.Progress.route) { ProgressScreen(navController, viewModel) }
            composable(Screen.Profile.route) { ProfileScreen(navController, viewModel) }
            composable(Screen.Settings.route) { SettingsScreen(navController, viewModel) }
            composable(Screen.BookDetails.route) { backStackEntry ->
                val bookId = backStackEntry.arguments?.getString("bookId")
                BookDetailsScreen(navController, bookId, viewModel)
            }
        }
    }
}
