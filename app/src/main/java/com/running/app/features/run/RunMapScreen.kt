package com.running.app.features.run

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.maplibre.android.annotations.PolylineOptions
import com.maplibre.android.camera.CameraUpdateFactory
import com.maplibre.android.geometry.LatLng
import com.maplibre.android.maps.MapView
import com.maplibre.android.maps.Style

@Composable
fun RunMapScreen(viewModel: RunViewModel = hiltViewModel()) {
    val state by viewModel.trackingState.collectAsState()
    var mapView: MapView? by remember { mutableStateOf(null) }

    AndroidView(
        factory = { ctx ->
            MapView(ctx).apply {
                getMapAsync { map ->
                    map.setStyle(Style.Builder().fromUri("https://demotiles.maplibre.org/style.json")) { style ->
                        // Live route redraw:
                        map.addPolyline(
                            PolylineOptions()
                                .addAll(state.points.map { LatLng(it.lat, it.lon) })
                                .color(android.graphics.Color.parseColor("#3B82F6"))
                                .width(6f)
                        )
                    }
                }
                mapView = this
            }
        },
        update = { view ->
            view.getMapAsync { map ->
                if (state.points.isNotEmpty()) {
                    val latLngs = state.points.map { LatLng(it.lat, it.lon) }
                    map.clear()
                    map.addPolyline(
                        PolylineOptions().addAll(latLngs)
                            .color(android.graphics.Color.parseColor("#3B82F6"))
                            .width(6f)
                    )
                    if (state.points.size == 1) {
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngs.first(), 16.0))
                    }
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}
