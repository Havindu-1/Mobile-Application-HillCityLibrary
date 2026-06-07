package com.example.hillcitylibrary.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.hillcitylibrary.di.DependencyProvider
import com.example.hillcitylibrary.util.AmbientLightSensorManager
import com.example.hillcitylibrary.util.GamificationManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.hillcitylibrary.util.MotionSensorManager
import com.example.hillcitylibrary.util.MotionState
import com.example.hillcitylibrary.util.NoiseMonitor

class ReadingViewModel(application: Application) : AndroidViewModel(application) {
    private val gamificationManager = GamificationManager(application)
    private val sensorManager = AmbientLightSensorManager(application)
    private val motionSensorManager = MotionSensorManager(application)
    private val settingsManager = DependencyProvider.provideSettingsManager(application)

    val userProfile = gamificationManager.userProfile
    val newlyUnlockedAchievement = gamificationManager.newlyUnlockedAchievement
    val isDarkEnvironment = sensorManager.isDarkEnvironment
    val ambientLux = sensorManager.ambientLux

    val motionState = motionSensorManager.motionState
    val stabilityScore = motionSensorManager.stabilityScore

    // Noise monitor data (published by NoiseMonitor singleton)
    val currentDecibels = NoiseMonitor.currentDecibels
    val isNoisyEnvironment = NoiseMonitor.isNoisyEnvironment

    private val _readingSessionMinutes = MutableStateFlow(0)
    val readingSessionMinutes: StateFlow<Int> = _readingSessionMinutes.asStateFlow()

    private val _stableMinutes = MutableStateFlow(0)
    val stableMinutes: StateFlow<Int> = _stableMinutes.asStateFlow()

    private val _activeCombo = MutableStateFlow(1)
    val activeCombo: StateFlow<Int> = _activeCombo.asStateFlow()

    private val _focusBrokenMessage = MutableStateFlow<String?>(null)
    val focusBrokenMessage: StateFlow<String?> = _focusBrokenMessage.asStateFlow()

    private val _isDeepFocus = MutableStateFlow(false)
    val isDeepFocus: StateFlow<Boolean> = _isDeepFocus.asStateFlow()

    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            settingsManager.isLightSensorEnabled.collect { enabled ->
                if (enabled) {
                    sensorManager.startListening()
                } else {
                    sensorManager.stopListening()
                }
            }
        }
 
        viewModelScope.launch {
            settingsManager.isMotionSensorEnabled.collect { enabled ->
                if (enabled) {
                    motionSensorManager.startListening()
                } else {
                    motionSensorManager.stopListening()
                }
            }
        }
 
        viewModelScope.launch {
            motionState.collect { state ->
                if (state == MotionState.SHAKING || state == MotionState.MOVING) {
                    handleMotionDistraction()
                }
            }
        }
    }

    fun startReadingSession() {
        if (timerJob?.isActive == true) return

        timerJob = viewModelScope.launch {
            while (true) {
                delay(60000) // 1 minute
                _readingSessionMinutes.value += 1

                if (motionState.value == MotionState.STABLE || stabilityScore.value > 80f) {
                    _stableMinutes.value += 1
                }
                
                if (_stableMinutes.value >= 15) {
                    _isDeepFocus.value = true
                }

                updateCombo(_stableMinutes.value)
                
                // Log XP every minute
                gamificationManager.logReadingSession(
                    minutes = 1,
                    stableMinutes = if (motionState.value == MotionState.STABLE || stabilityScore.value > 80f) 1 else 0,
                    isNightMode = isDarkEnvironment.value,
                    isDeepFocus = _isDeepFocus.value
                )
            }
        }
    }

    fun pauseReadingSession() {
        timerJob?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        sensorManager.stopListening()
        motionSensorManager.stopListening()
        pauseReadingSession()
    }

    private fun updateCombo(stableMins: Int) {
        _activeCombo.value = when {
            stableMins >= 90 -> 10
            stableMins >= 45 -> 5
            stableMins >= 20 -> 2
            stableMins >= 10 -> 1
            else -> 1
        }
    }

    private fun handleMotionDistraction() {
        // Prevent spamming the message
        if (_focusBrokenMessage.value != null) return

        _focusBrokenMessage.value = listOf(
            "Focus broken...",
            "The ancient pages lose their glow.",
            "Steady your mind to continue the quest."
        ).random()
        
        // Reset streak metrics but not total session minutes
        _stableMinutes.value = 0
        _isDeepFocus.value = false
        updateCombo(0)

        // Clear message after 3 seconds
        viewModelScope.launch {
            delay(3000)
            _focusBrokenMessage.value = null
        }
    }

    fun clearAchievement() {
        gamificationManager.clearAchievementNotification()
    }
}
