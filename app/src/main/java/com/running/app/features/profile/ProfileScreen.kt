package com.running.app.features.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.running.app.core.ui.*

@Composable
fun ProfileScreen() {
    Column(Modifier.fillMaxSize().background(AdidasBlack).padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {

        Spacer(Modifier.height(32.dp))
        Box(Modifier.size(96.dp).background(AdidasAccent, CircleShape),
            contentAlignment = Alignment.Center) {
            Text("R", color = AdidasBlack, fontSize = 40.sp, fontWeight = FontWeight.ExtraBold)
        }
        Spacer(Modifier.height(16.dp))
        Text("Runner", color = AdidasWhite, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("Offline mode 🔒", color = AdidasGray, fontSize = 14.sp)

        Spacer(Modifier.height(32.dp))
        Card(colors = CardDefaults.cardColors(containerColor = AdidasCard),
            shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp)) {
                ProfileRow("👤", "Name", "Runner")
                ProfileRow("⚖️", "Weight", "70 kg")
                ProfileRow("🎯", "Weekly Goal", "25 km")
                ProfileRow("📍", "GPS", "High accuracy")
            }
        }
    }
}

@Composable
fun ProfileRow(icon: String, label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween) {
        Text("$icon  $label", color = AdidasGray, fontSize = 15.sp)
        Text(value, color = AdidasWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
    }
}
