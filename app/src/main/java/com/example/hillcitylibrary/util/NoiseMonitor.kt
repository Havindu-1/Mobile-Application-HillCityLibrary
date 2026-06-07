package com.example.hillcitylibrary.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlin.math.log10

object NoiseMonitor {
    private const val CHANNEL_ID = "NOISE_MONITOR_CHANNEL"

    // Expose live decibel reading so UI can observe it
    private val _currentDecibels = MutableStateFlow(0.0)
    val currentDecibels: StateFlow<Double> = _currentDecibels.asStateFlow()

    private val _isNoisyEnvironment = MutableStateFlow(false)
    val isNoisyEnvironment: StateFlow<Boolean> = _isNoisyEnvironment.asStateFlow()

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Noise Alerts"
            val descriptionText = "Notifications for high noise levels"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showNoiseNotification(context: Context) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Environment too noisy")
            .setContentText("The noise level has exceeded 60dB for more than 5 seconds.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return
            }
            notify(1001, builder.build())
        }
    }

    suspend fun monitorNoiseLevel(
        context: Context,
        onNoiseExceeded: () -> Unit
    ) = withContext(Dispatchers.IO) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            Log.e("NoiseMonitor", "RECORD_AUDIO permission not granted")
            return@withContext
        }

        var mediaRecorder: MediaRecorder? = null
        try {
            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }

            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            // Use app-specific cache dir instead of /dev/null for broader compatibility
            val outputFile = java.io.File(context.cacheDir, "noise_discard.3gp")
            mediaRecorder.setOutputFile(outputFile.absolutePath)

            mediaRecorder.prepare()
            mediaRecorder.start()

            var elevatedNoiseDuration = 0L
            val thresholdDb = 60.0
            val checkIntervalMs = 500L
            val requiredDurationMs = 5000L

            while (isActive) {
                val maxAmplitude = mediaRecorder.maxAmplitude

                val db = if (maxAmplitude > 0) {
                    20 * log10(maxAmplitude.toDouble())
                } else {
                    0.0
                }

                // Publish live dB value for UI
                _currentDecibels.value = db
                _isNoisyEnvironment.value = db > thresholdDb

                if (db > thresholdDb) {
                    elevatedNoiseDuration += checkIntervalMs
                    if (elevatedNoiseDuration >= requiredDurationMs) {
                        withContext(Dispatchers.Main) {
                            onNoiseExceeded()
                        }
                        elevatedNoiseDuration = 0L
                    }
                } else {
                    elevatedNoiseDuration = 0L
                }

                delay(checkIntervalMs)
            }
        } catch (e: Exception) {
            Log.e("NoiseMonitor", "Error monitoring noise level", e)
        } finally {
            try {
                mediaRecorder?.stop()
            } catch (e: Exception) {
                Log.e("NoiseMonitor", "Error stopping recorder", e)
            }
            mediaRecorder?.release()
            _currentDecibels.value = 0.0
            _isNoisyEnvironment.value = false
        }
    }
}
