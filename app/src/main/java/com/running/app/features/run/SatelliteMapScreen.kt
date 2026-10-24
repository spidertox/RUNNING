package com.running.app.features.run

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.launch
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import com.running.app.core.map.SatelliteOfflineManager
import com.running.app.core.ui.*

@Composable
fun SatelliteMapScreen(manager: SatelliteOfflineManager) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var downloadProgress by remember { mutableStateOf<Float?>(null) }
    var downloaded by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                MapView(ctx).apply {
                    getMapAsync { map ->
                        // Offline-safe: asset:// style + offline region cache
                        map.setStyle(Style.Builder()
                            .fromUri(SatelliteOfflineManager.STYLE_URL))
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Esri attribution (license requirement)
        Text(
            "Imagery © Esri, Maxar",
            color = AdidasWhite,
            modifier = Modifier.align(androidx.compose.ui.Alignment.BottomStart).padding(8.dp)
        )

        // Download button (needs internet ONCE, then offline forever)
        if (!downloaded) {
            FloatingActionButton(
                onClick = {
                    // TODO: user ki current location se center lo
                    val center = LatLng(28.6139, 77.2090) // default: New Delhi
                    downloadProgress = 0f
                    scope.launch {
                        manager.downloadRegion(center).collect { progress ->
                            downloadProgress = progress
                            if (progress >= 1f) downloaded = true
                        }
                    }
                },
                modifier = Modifier.align(androidx.compose.ui.Alignment.TopEnd).padding(16.dp)
            ) { Text("⬇") }
        }

        downloadProgress?.let { progress ->
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().align(androidx.compose.ui.Alignment.TopCenter),
                color = AdidasAccent
            )
        }
    }
}
