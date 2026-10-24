package com.running.app.core.location

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class TrackingUiState(
    val isTracking: Boolean = false,
    val points: List<GeoPoint> = emptyList(),
    val distanceMeters: Double = 0.0,
    val durationMs: Long = 0L,
    val avgSpeedKmh: Float = 0f,
    val paceSecPerKm: Float = 0f,
    val calories: Int = 0
)

object Tracker {
    private val _state = MutableStateFlow(TrackingUiState())
    val state: StateFlow<TrackingUiState> = _state

    fun update(transform: (TrackingUiState) -> TrackingUiState) {
        _state.value = transform(_state.value)
    }
}
