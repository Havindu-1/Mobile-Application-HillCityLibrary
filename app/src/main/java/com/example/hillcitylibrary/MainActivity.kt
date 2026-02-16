package com.example.hillcitylibrary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hillcitylibrary.ui.BookViewModel
import com.example.hillcitylibrary.ui.HillCityLibraryApp
import com.example.hillcitylibrary.ui.theme.HillcitylibraryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: BookViewModel = viewModel()
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()

            HillcitylibraryTheme(darkTheme = isDarkTheme) {
                HillCityLibraryApp(viewModel = viewModel)
            }
        }
    }
}
