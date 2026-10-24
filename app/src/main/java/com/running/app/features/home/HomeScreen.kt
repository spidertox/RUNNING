package com.running.app.features.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.running.app.core.ui.*
import com.running.app.features.run.RunViewModel

@Composable
fun HomeScreen(
    onStartRun: () -> Unit,
    viewModel: RunViewModel = hiltViewModel()
) {
    val runs by viewModel.runs.collectAsState()
    val totalKm = runs.sumOf { it.distanceMeters.toDouble() } / 1000.0

    Column(
        Modifier.fillMaxSize().background(AdidasBlack).padding(20.dp)
    ) {
        Text("GOOD MORNING 👋", color = AdidasGray, fontSize = 13.sp)
        Text("Ready to run?", color = AdidasWhite,
            fontSize = 28.sp, fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(24.dp))

        // Big stat cards — Adidas style
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard("THIS WEEK", "%.1f".format(totalKm) + " km", Modifier.weight(1f))
            StatCard("RUNS", "${'$'}{runs.size}", Modifier.weight(1f))
        }

        Spacer(Modifier.height(12.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            val avgPace = if (runs.isEmpty()) "--:--" else {
                val avg = runs.map { it.avgPaceSecPerKm }.average().toFloat()
                "%d:%02d".format((avg / 60).toInt(), (avg % 60).toInt()) + " /km"
            }
            StatCard("AVG PACE", avgPace, Modifier.weight(1f))
            StatCard("CALORIES", runs.sumOf { it.calories }.toString() + " kcal", Modifier.weight(1f))
        }

        Spacer(Modifier.weight(1f))

        // Giant START button — Adidas signature look
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Button(
                onClick = onStartRun,
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = AdidasAccent),
                modifier = Modifier.size(150.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("START", color = AdidasBlack,
                    fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = AdidasCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(label, color = AdidasGray, fontSize = 11.sp,
                fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(value, color = AdidasWhite, fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold)
        }
    }
}
