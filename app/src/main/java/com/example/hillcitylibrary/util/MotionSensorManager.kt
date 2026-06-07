package com.example.hillcitylibrary.util

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.sqrt

enum class MotionState {
    STABLE, MOVING, SHAKING
}

class MotionSensorManager(context: Context) : SensorEventListener {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val gyroscope: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

    private val _motionState = MutableStateFlow(MotionState.STABLE)
    val motionState: StateFlow<MotionState> = _motionState.asStateFlow()

    private val _stabilityScore = MutableStateFlow(100f)
    val stabilityScore: StateFlow<Float> = _stabilityScore.asStateFlow()

    // Thresholds: gravity at rest = ~9.8 m/s²
    // Stable zone: 8.5 – 10.8 (at rest in any orientation)
    // Moving: outside stable zone but not violently shaking
    // Shaking: > 15 m/s²
    private val SHAKE_THRESHOLD = 15f
    private val STABLE_MIN = 8.5f
    private val STABLE_MAX = 10.8f

    // Rolling variables for stability score
    private var lastUpdateMillis = 0L

    fun startListening() {
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        gyroscope?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stopListening() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        val currentTime = System.currentTimeMillis()
        if ((currentTime - lastUpdateMillis) > 100) {
            val dt = currentTime - lastUpdateMillis
            lastUpdateMillis = currentTime

            if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                val gForce = sqrt((x * x + y * y + z * z).toDouble()).toFloat()

                // Determine Motion State
                val currentState = when {
                    gForce > SHAKE_THRESHOLD -> MotionState.SHAKING
                    gForce < STABLE_MIN || gForce > STABLE_MAX -> MotionState.MOVING
                    else -> MotionState.STABLE
                }

                if (_motionState.value != currentState) {
                    _motionState.value = currentState
                }

                // Adjust stability score based on state
                var currentScore = _stabilityScore.value
                when (currentState) {
                    MotionState.STABLE -> {
                        // Slowly recover stability score
                        currentScore = (currentScore + 0.5f).coerceAtMost(100f)
                    }
                    MotionState.MOVING -> {
                        currentScore = (currentScore - 2f).coerceAtLeast(0f)
                    }
                    MotionState.SHAKING -> {
                        currentScore = (currentScore - 10f).coerceAtLeast(0f)
                    }
                }
                _stabilityScore.value = currentScore
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not implemented
    }
}
