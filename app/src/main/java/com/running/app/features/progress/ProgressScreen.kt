package com.running.app.features.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.running.app.core.ui.*
import com.running.app.features.run.RunViewModel

@Composable
fun ProgressScreen(viewModel: RunViewModel = hiltViewModel()) {
    val runs by viewModel.runs.collectAsState()
    val totalKm = runs.sumOf { it.distanceMeters.toDouble() } / 1000.0
    val totalTime = runs.sumOf { it.durationMs }

    Column(Modifier.fillMaxSize().background(AdidasBlack).padding(20.dp)) {
        Text("PROGRESS", color = AdidasWhite, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(20.dp))

        Card(colors = CardDefaults.cardColors(containerColor = AdidasCard),
            shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(24.dp)) {
                Text("TOTAL DISTANCE", color = AdidasGray, fontSize = 12.sp)
                Text("%.1f km".format(totalKm), color = AdidasAccent,
                    fontSize = 42.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(Modifier.height(16.dp))
                Text("TOTAL TIME", color = AdidasGray, fontSize = 12.sp)
                Text("%d hrs".format(totalTime / 3600000), color = AdidasWhite,
                    fontSize = 28.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
