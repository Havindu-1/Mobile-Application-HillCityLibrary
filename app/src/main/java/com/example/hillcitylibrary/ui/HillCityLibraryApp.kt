package com.example.hillcitylibrary.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalConfiguration
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

    val configuration = LocalConfiguration.current
    val isWideScreen = configuration.screenWidthDp > 600

    Row(modifier = Modifier.fillMaxSize()) {
        // Navigation Rail for Wide Screens
        if (isWideScreen && 
            currentRoute != Screen.Login.route && 
            currentRoute != Screen.SignUp.route) {
            
            NavigationRail(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                header = {
                     Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = "Logo",
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                Spacer(modifier = Modifier.weight(1f))
                val currentDestination = navBackStackEntry?.destination
                bottomNavItems.forEach { screen ->
                    val isSelected = currentDestination?.hierarchy?.any { 
                        it.route == screen.route 
                    } == true
                    
                    NavigationRailItem(
                        icon = { 
                            Icon(
                                screen.icon!!,
                                contentDescription = screen.title
                            )
                        },
                        label = { Text(screen.title) },
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
                        colors = NavigationRailItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }

        Scaffold(
            bottomBar = {
                // Show bottom bar only on main screens AND NOT wide screen
                if (!isWideScreen &&
                    currentRoute != Screen.Login.route && 
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
                    // If wide screen, we might need to adjust padding or let scaffold handle it? 
                    // Actually, innerPadding from Scaffold handles bottom bar.
                    // For side rail, it's outside scaffold.
                    .fillMaxSize(),
                enterTransition = {
                    slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(500)) + fadeIn(animationSpec = tween(500))
                },
                exitTransition = {
                    slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween(500)) + fadeOut(animationSpec = tween(500))
                },
                popEnterTransition = {
                    slideInHorizontally(initialOffsetX = { -1000 }, animationSpec = tween(500)) + fadeIn(animationSpec = tween(500))
                },
                popExitTransition = {
                    slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(500)) + fadeOut(animationSpec = tween(500))
                }
            ) {
                // AUTH Screens - Fade Transitions
                composable(
                    route = Screen.Login.route,
                    enterTransition = { fadeIn(tween(500)) },
                    exitTransition = { fadeOut(tween(500)) }
                ) { LoginScreen(navController, viewModel) }
                
                composable(
                    route = Screen.SignUp.route,
                    enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn() },
                    exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) + fadeOut() },
                    popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }) + fadeIn() },
                    popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) + fadeOut() }
                ) { SignUpScreen(navController, viewModel) }

                // MAIN TABS - Cross-fade (No sliding between top-level destinations)
                val mainTabs = listOf(
                    Screen.Home.route, Screen.Library.route, Screen.Shop.route, 
                    Screen.Favorites.route, Screen.Progress.route, Screen.Profile.route
                )
                
                composable(
                    route = Screen.Home.route,
                    enterTransition = { fadeIn(tween(500)) },
                    exitTransition = { fadeOut(tween(500)) }
                ) { HomeScreen(navController, viewModel) }
                
                composable(
                    route = Screen.Library.route,
                    enterTransition = { fadeIn(tween(500)) },
                    exitTransition = { fadeOut(tween(500)) }
                ) { LibraryScreen(navController, viewModel) }
                
                composable(
                    route = Screen.Shop.route,
                    enterTransition = { fadeIn(tween(500)) },
                    exitTransition = { fadeOut(tween(500)) }
                ) { ShopScreen(navController, shopViewModel) }
                
                composable(
                    route = Screen.Favorites.route,
                    enterTransition = { fadeIn(tween(500)) },
                    exitTransition = { fadeOut(tween(500)) }
                ) { FavoritesScreen(navController, viewModel) }
                
                composable(
                    route = Screen.Progress.route,
                    enterTransition = { fadeIn(tween(500)) },
                    exitTransition = { fadeOut(tween(500)) }
                ) { ProgressScreen(navController, viewModel) }
                
                composable(
                    route = Screen.Profile.route,
                    enterTransition = { fadeIn(tween(500)) },
                    exitTransition = { fadeOut(tween(500)) }
                ) { ProfileScreen(navController, viewModel) }

                // DETAIL SCREENS - Slide Transitions (Default set in NavHost, but explicit here for clarity if needed)
                composable(Screen.Cart.route) { CartScreen(navController, shopViewModel) }
                
                composable(Screen.Settings.route) { SettingsScreen(navController, viewModel) }
                
                composable(Screen.BookDetails.route) { backStackEntry ->
                    val bookId = backStackEntry.arguments?.getString("bookId")
                    BookDetailsScreen(navController, bookId, viewModel)
                }
            }
        }
    }
}
