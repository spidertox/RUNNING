package com.running.app.features.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.running.app.features.run.RunViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(
    onRunClick: (Long) -> Unit,
    viewModel: RunViewModel = hiltViewModel()
) {
    val runs by viewModel.runs.collectAsState()
    val fmt = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }

    if (runs.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            Text("Abhi tak koi run nahi. Pehli run shuru karo! 🏃")
        }
    } else {
        LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
            items(runs) { run ->
                Card(Modifier.fillMaxWidth().padding(vertical = 6.dp)
                    .clickable { onRunClick(run.id) }) {
                    Column(Modifier.padding(16.dp)) {
                        Text(fmt.format(Date(run.startTime)), style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(4.dp))
                        Text("%.2f km  •  %.0f kcal".format(run.distanceMeters / 1000f, run.calories.toFloat()))
                    }
                }
            }
        }
    }
}
