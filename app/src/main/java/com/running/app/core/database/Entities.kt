package com.running.app.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "runs")
data class Run(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startTime: Long,
    val endTime: Long,
    val distanceMeters: Float,
    val durationMs: Long,
    val avgPaceSecPerKm: Float,
    val avgSpeedKmh: Float,
    val calories: Int,
    val isSynced: Boolean = false
)

@Entity(tableName = "track_points")
data class TrackPoint(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val runId: Long,
    val lat: Double,
    val lon: Double,
    val altitude: Double,
    val time: Long,
    val speed: Float
)

@Entity(tableName = "offline_regions")
data class OfflineRegion(
    @PrimaryKey val id: String,
    val name: String,
    val centerLat: Double,
    val centerLon: Double,
    val minZoom: Int,
    val maxZoom: Int,
    val filePath: String,
    val sizeBytes: Long
)
