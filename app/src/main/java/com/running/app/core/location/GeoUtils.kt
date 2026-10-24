package com.running.app.core.location

import kotlin.math.*

data class GeoPoint(val lat: Double, val lon: Double, val time: Long, val speed: Float)

object GeoUtils {

    fun distanceMeters(p1: GeoPoint, p2: GeoPoint): Double {
        val r = 6371000.0
        val dLat = Math.toRadians(p2.lat - p1.lat)
        val dLon = Math.toRadians(p2.lon - p1.lon)
        val a = sin(dLat / 2).pow(2.0) +
                cos(Math.toRadians(p1.lat)) * cos(Math.toRadians(p2.lat)) *
                sin(dLon / 2).pow(2.0)
        return 2 * r * asin(sqrt(a))
    }

    fun totalDistance(points: List<GeoPoint>): Double {
        var sum = 0.0
        for (i in 1 until points.size) {
            sum += distanceMeters(points[i - 1], points[i])
        }
        return sum
    }

    fun paceSecPerKm(durationMs: Long, distanceMeters: Double): Float =
        if (distanceMeters < 10) 0f
        else (durationMs / 1000f) / (distanceMeters / 1000f)

    fun estimateCalories(distanceMeters: Double, weightKg: Float = 70f): Int =
        (distanceMeters / 1000.0 * weightKg * 1.0).toInt()
}
