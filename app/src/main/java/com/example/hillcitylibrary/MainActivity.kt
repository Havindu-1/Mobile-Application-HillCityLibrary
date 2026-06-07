package com.example.hillcitylibrary

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hillcitylibrary.di.DependencyProvider
import com.example.hillcitylibrary.ui.AuthViewModel
import com.example.hillcitylibrary.ui.BookViewModel
import com.example.hillcitylibrary.ui.HillCityLibraryApp
import com.example.hillcitylibrary.ui.theme.HillcitylibraryTheme
import com.example.hillcitylibrary.util.NoiseMonitor
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job

class MainActivity : ComponentActivity() {
    private val settingsManager by lazy { DependencyProvider.provideSettingsManager(applicationContext) }
    private var noiseMonitorJob: Job? = null

    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val audioGranted = permissions[Manifest.permission.RECORD_AUDIO] == true
        if (audioGranted && settingsManager.isNoiseSensorEnabled.value) {
            startNoiseMonitoring()
        }
    }

    private fun startNoiseMonitoring() {
        if (noiseMonitorJob?.isActive == true) return
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) return

        noiseMonitorJob = lifecycleScope.launch {
            NoiseMonitor.monitorNoiseLevel(this@MainActivity) {
                if (settingsManager.isNotificationsEnabled.value) {
                    NoiseMonitor.showNoiseNotification(this@MainActivity)
                }
            }
        }
    }

    private fun stopNoiseMonitoring() {
        noiseMonitorJob?.cancel()
        noiseMonitorJob = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        NoiseMonitor.createNotificationChannel(this)

        // Observe the noise sensor setting dynamically
        lifecycleScope.launch {
            settingsManager.isNoiseSensorEnabled.collect { enabled ->
                if (enabled) {
                    startNoiseMonitoring()
                } else {
                    stopNoiseMonitoring()
                }
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
             val permissionsToRequest = mutableListOf(Manifest.permission.RECORD_AUDIO)
             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                 if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                     permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
                 }
             }
             requestPermissionsLauncher.launch(permissionsToRequest.toTypedArray())
        }

        enableEdgeToEdge()
        setContent {
            val viewModel: BookViewModel = viewModel()
            val authViewModel: AuthViewModel = viewModel()
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()

            HillcitylibraryTheme(darkTheme = isDarkTheme) {
                HillCityLibraryApp(
                    viewModel = viewModel,
                    authViewModel = authViewModel
                )
            }
        }
    }
}
