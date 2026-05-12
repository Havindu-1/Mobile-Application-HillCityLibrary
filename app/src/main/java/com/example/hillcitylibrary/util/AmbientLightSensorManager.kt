package com.example.hillcitylibrary.util

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AmbientLightSensorManager(context: Context) : SensorEventListener {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val lightSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

    private val _isDarkEnvironment = MutableStateFlow(false)
    val isDarkEnvironment: StateFlow<Boolean> = _isDarkEnvironment.asStateFlow()

    private val _ambientLux = MutableStateFlow(0f)
    val ambientLux: StateFlow<Float> = _ambientLux.asStateFlow()

    fun startListening() {
        lightSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun stopListening() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_LIGHT) {
            val lux = event.values[0]
            _ambientLux.value = lux
            
            // Typical threshold: < 10 lux is very dark (nighttime/dark room)
            // between 10-50 is dim
            if (lux < 1.0f && !_isDarkEnvironment.value) {
                _isDarkEnvironment.value = true
            } else if (lux >= 1.0f && _isDarkEnvironment.value) {
                _isDarkEnvironment.value = false
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No-op
    }
}
