package com.running.app.core.location

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import com.running.app.MainActivity
import com.running.app.R
import com.running.app.core.database.AppDatabase
import com.running.app.core.database.Run
import com.running.app.core.database.TrackPoint
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class TrackingService : Service() {

    @Inject lateinit var db: AppDatabase

    private lateinit var fused: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var startTime = 0L
    private var timerJob: Job? = null
    private val points = mutableListOf<GeoPoint>()
    private var isPaused = false

    override fun onCreate() {
        super.onCreate()
        fused = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> start()
            ACTION_PAUSE -> isPaused = true
            ACTION_RESUME -> isPaused = false
            ACTION_STOP -> stopAndSave()
        }
        return START_STICKY
    }

    private fun start() {
        startForeground(1, buildNotification("Tracking your run..."))
        startTime = System.currentTimeMillis()
        points.clear()

        Tracker.update { it.copy(isTracking = true, points = emptyList()) }

        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 1000L
        ).setMinUpdateDistanceMeters(2f).build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                if (isPaused) return
                result.lastLocation?.let { loc ->
                    points.add(GeoPoint(loc.latitude, loc.longitude, loc.time, loc.speed))
                    updateState()
                }
            }
        }

        try {
            fused.requestLocationUpdates(request, locationCallback, mainLooper)
        } catch (e: SecurityException) {
            stopSelf()
        }

        timerJob = serviceScope.launch {
            while (true) {
                delay(1000)
                if (!isPaused) updateState()
            }
        }
    }

    private fun updateState() {
        val duration = System.currentTimeMillis() - startTime
        val dist = GeoUtils.totalDistance(points)
        val speed = if (duration > 0) (dist / 1000.0) / (duration / 3600000.0) else 0.0
        Tracker.update {
            it.copy(
                points = points.toList(),
                distanceMeters = dist,
                durationMs = duration,
                avgSpeedKmh = speed.toFloat(),
                paceSecPerKm = GeoUtils.paceSecPerKm(duration, dist),
                calories = GeoUtils.estimateCalories(dist)
            )
        }
    }

    private fun stopAndSave() {
        timerJob?.cancel()
        fused.removeLocationUpdates(locationCallback)

        val state = Tracker.state.value
        serviceScope.launch {
            val runId = db.runDao().insertRun(
                Run(
                    startTime = startTime,
                    endTime = System.currentTimeMillis(),
                    distanceMeters = state.distanceMeters.toFloat(),
                    durationMs = state.durationMs,
                    avgPaceSecPerKm = state.paceSecPerKm,
                    avgSpeedKmh = state.avgSpeedKmh,
                    calories = state.calories
                )
            )
            db.runDao().insertPoints(
                points.map { p -> TrackPoint(runId = runId, lat = p.lat, lon = p.lon, altitude = 0.0, time = p.time, speed = p.speed) }
            )
            Tracker.update { TrackingUiState() }
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
    }

    private fun buildNotification(text: String): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pi = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        return NotificationCompat.Builder(this, "tracking_channel")
            .setContentTitle("Running")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setContentIntent(pi)
            .setOngoing(true)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_RESUME = "ACTION_RESUME"
        const val ACTION_STOP = "ACTION_STOP"
    }
}
