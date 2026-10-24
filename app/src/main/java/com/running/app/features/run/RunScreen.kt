package com.running.app.features.run

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.running.app.core.ui.*
import java.util.Locale

@Composable
fun RunScreen(viewModel: RunViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val state by viewModel.trackingState.collectAsState()

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED
        )
    }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grants -> hasPermission = grants[Manifest.permission.ACCESS_FINE_LOCATION] == true }

    LaunchedEffect(Unit) {
        if (!hasPermission) launcher.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.POST_NOTIFICATIONS
        ))
    }

    Column(
        Modifier.fillMaxSize().background(AdidasBlack).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Giant timer — hero element (Adidas style)
        Text(formatTime(state.durationMs), color = AdidasWhite,
            fontSize = 64.sp, fontWeight = FontWeight.ExtraBold)
        Text("TIME", color = AdidasGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(32.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            BigStat("%.2f".format(state.distanceMeters / 1000.0), "KM")
            BigStat(formatPace(state.paceSecPerKm), "PACE")
            BigStat("%d".format(state.calories), "KCAL")
        }

        Spacer(Modifier.weight(1f))

        if (!state.isTracking) {
            Button(
                onClick = { viewModel.startRun(context) },
                enabled = hasPermission,
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = AdidasAccent),
                modifier = Modifier.size(120.dp),
                contentPadding = PaddingValues(0.dp)
            ) { Text("GO", color = AdidasBlack, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold) }
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(28.dp),
                verticalAlignment = Alignment.CenterVertically) {
                FilledIconButton(
                    onClick = { viewModel.pauseRun(context) },
                    modifier = Modifier.size(64.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = AdidasCard)
                ) { Text("⏸", fontSize = 24.sp) }
                Button(
                    onClick = { viewModel.stopRun(context) },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.size(88.dp),
                    contentPadding = PaddingValues(0.dp)
                ) { Text("■", fontSize = 26.sp) }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun BigStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = AdidasAccent, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
        Text(label, color = AdidasGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

fun formatTime(ms: Long): String {
    val t = ms / 1000
    return String.format(Locale.US, "%02d:%02d:%02d", t / 3600, (t % 3600) / 60, t % 60)
}

fun formatPace(sec: Float): String =
    if (sec <= 0) "--:--"
    else String.format(Locale.US, "%d:%02d", (sec / 60).toInt(), (sec % 60).toInt())
