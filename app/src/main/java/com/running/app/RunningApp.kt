package com.running.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import dagger.hilt.android.HiltAndroidApp
import org.maplibre.android.MapLibre

@HiltAndroidApp
class RunningApp : Application() {
    override fun onCreate() {
        super.onCreate()
        MapLibre.getInstance(this)
        val channel = NotificationChannel(
            "tracking_channel",
            "Run Tracking",
            NotificationManager.IMPORTANCE_LOW
        )
        getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)
    }
}
