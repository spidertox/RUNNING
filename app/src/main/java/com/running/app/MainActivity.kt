package com.running.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.running.app.core.ui.*
import com.running.app.features.home.HomeScreen
import com.running.app.features.progress.ProgressScreen
import com.running.app.features.profile.ProfileScreen
import com.running.app.features.history.HistoryScreen
import com.running.app.features.run.RunScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RunningTheme {
                val nav = rememberNavController()
                val backStack by nav.currentBackStackEntryAsState()
                val currentRoute = backStack?.destination?.route

                Scaffold(
                    containerColor = AdidasBlack,
                    bottomBar = {
                        NavigationBar(containerColor = AdidasSurface) {
                            TabItem("Home", "🏠", "home", currentRoute) { nav.navigate("home") }
                            TabItem("Progress", "📈", "progress", currentRoute) { nav.navigate("progress") }
                            TabItem("History", "📋", "history", currentRoute) { nav.navigate("history") }
                            TabItem("Profile", "👤", "profile", currentRoute) { nav.navigate("profile") }
                        }
                    }
                ) { padding ->
                    NavHost(nav, startDestination = "home", Modifier.padding(padding)) {
                        composable("home") { HomeScreen(onStartRun = { nav.navigate("run") }) }
                        composable("run") { RunScreen() }
                        composable("progress") { ProgressScreen() }
                        composable("history") { HistoryScreen(onRunClick = {}) }
                        composable("profile") { ProfileScreen() }
                    }
                }
            }
        }
    }
}

@Composable
fun RowScope.TabItem(
    label: String, icon: String, route: String, currentRoute: String?, onClick: () -> Unit
) {
    NavigationBarItem(
        selected = currentRoute == route,
        onClick = onClick,
        icon = { Text(icon, fontSize = androidx.compose.ui.unit.sp(20)) },
        label = { Text(label) }
    )
}

// tiny helper to avoid importing unit-sp everywhere
@Composable
fun Int.sp_value() = androidx.compose.ui.unit.sp(this * 1f)
