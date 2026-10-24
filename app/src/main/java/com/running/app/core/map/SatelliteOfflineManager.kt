package com.running.app.core.map

import android.content.Context
import com.maplibre.android.geometry.LatLng
import com.maplibre.android.geometry.LatLngBounds
import com.maplibre.android.offline.OfflineManager
import com.maplibre.android.offline.OfflineRegion
import com.maplibre.android.offline.OfflineRegionError
import com.maplibre.android.offline.OfflineRegionStatus
import com.maplibre.android.offline.OfflineTilePyramidRegionDefinition
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SatelliteOfflineManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val STYLE_URL = "asset://satellite_style.json"
        const val REGION_NAME = "running_satellite_region"
    }

    private val offlineManager = OfflineManager.getInstance(context)

    /** Ek baar internet se region download karo — uske baad 100% offline chalega. */
    fun downloadRegion(
        center: LatLng,
        radiusDeg: Double = 0.08,   // ~10-12 km around center
        minZoom: Double = 10.0,
        maxZoom: Double = 16.0
    ): Flow<Float> = callbackFlow {
        val bounds = LatLngBounds.Builder()
            .include(LatLng(center.latitude - radiusDeg, center.longitude - radiusDeg))
            .include(LatLng(center.latitude + radiusDeg, center.longitude + radiusDeg))
            .build()

        val definition = OfflineTilePyramidRegionDefinition(
            STYLE_URL, bounds, minZoom, maxZoom, context.resources.displayMetrics.density
        )

        val metadata = JSONObject().put("name", REGION_NAME).toString().toByteArray()

        offlineManager.createOfflineRegion(definition, metadata,
            object : OfflineManager.CreateOfflineRegionCallback {
                override fun onCreate(region: OfflineRegion) {
                    region.setDownloadState(OfflineRegion.STATE_ACTIVE)
                    region.setObserver(object : OfflineRegion.OfflineRegionObserver {
                        override fun onStatusChanged(status: OfflineRegionStatus) {
                            val progress = if (status.requiredResourceCount > 0)
                                status.completedResourceCount.toFloat() / status.requiredResourceCount
                            else 0f
                            trySend(progress)
                            if (status.isComplete) {
                                trySend(1f); close()
                            }
                        }
                        override fun onError(error: OfflineRegionError) { close() }
                        override fun mapboxTileCountLimitExceeded(limit: Long) { close() }
                    })
                }
                override fun onError(error: String) { close() }
            })
        awaitClose()
    }

    /** Kitna download ho chuka (offline check ke liye). */
    fun listRegions(callback: (Array<OfflineRegion>) -> Unit) {
        offlineManager.listOfflineRegions(object : OfflineManager.ListOfflineRegionsCallback {
            override fun onList(offlineRegions: Array<OfflineRegion>) = callback(offlineRegions)
            override fun onError(error: String) = callback(emptyArray())
        })
    }
}
