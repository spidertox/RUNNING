package com.running.app.features.run

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.running.app.core.database.Run
import com.running.app.core.database.TrackPoint
import com.running.app.core.location.Tracker
import com.running.app.core.location.TrackingUiState
import com.running.app.data.repository.RunRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RunViewModel @Inject constructor(
    private val repository: RunRepository
) : ViewModel() {

    val trackingState: StateFlow<TrackingUiState> = Tracker.state
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TrackingUiState())

    val runs: StateFlow<List<Run>> = repository.getAllRuns()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedRunPoints = MutableStateFlow<List<TrackPoint>>(emptyList())
    val selectedRunPoints: StateFlow<List<TrackPoint>> = _selectedRunPoints.asStateFlow()

    fun startRun(context: android.content.Context) {
        val intent = Intent(context, com.running.app.core.location.TrackingService::class.java)
            .setAction(com.running.app.core.location.TrackingService.ACTION_START)
        context.startForegroundService(intent)
    }

    fun pauseRun(context: android.content.Context) {
        context.startService(Intent(context, com.running.app.core.location.TrackingService::class.java)
            .setAction(com.running.app.core.location.TrackingService.ACTION_PAUSE))
    }

    fun resumeRun(context: android.content.Context) {
        context.startService(Intent(context, com.running.app.core.location.TrackingService::class.java)
            .setAction(com.running.app.core.location.TrackingService.ACTION_RESUME))
    }

    fun stopRun(context: android.content.Context) {
        context.startService(Intent(context, com.running.app.core.location.TrackingService::class.java)
            .setAction(com.running.app.core.location.TrackingService.ACTION_STOP))
    }

    fun loadRunDetail(runId: Long) {
        viewModelScope.launch {
            _selectedRunPoints.value = repository.getPoints(runId)
        }
    }
}
