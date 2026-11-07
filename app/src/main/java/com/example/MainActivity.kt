package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.AdbBuilderScreen
import com.example.ui.screens.AutomationScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DeviceInfoScreen
import com.example.ui.screens.LogViewerScreen
import com.example.ui.screens.PackageManagerScreen
import com.example.ui.screens.PermissionManagerScreen
import com.example.ui.screens.ProcessViewerScreen
import com.example.ui.screens.SystemSettingsScreen
import com.example.ui.screens.TerminalScreen
import com.example.ui.theme.AzukuDarkBackground
import com.example.ui.theme.AzukuTheme
import com.example.ui.theme.DarkNavyGlass
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.SpaceGroteskFontFamily
import com.example.viewmodel.AzukuScreen
import com.example.viewmodel.AzukuViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: AzukuViewModel = viewModel()
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()

            AzukuTheme(darkTheme = isDarkTheme) {
                AzukuApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun AzukuApp(viewModel: AzukuViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()

    // Handle Hardware Back Button
    if (currentScreen != AzukuScreen.DASHBOARD) {
        BackHandler {
            viewModel.navigateTo(AzukuScreen.DASHBOARD)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        AzukuDarkBackground,
                        Color(0xFF04070D)
                    )
                )
            ),
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            // Glassmorphic Cyber Bottom Navigation Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(
                        1.dp,
                        NeonCyan.copy(alpha = 0.25f),
                        RoundedCornerShape(20.dp)
                    )
                    .background(DarkNavyGlass.copy(alpha = 0.88f))
            ) {
                NavigationBar(
                    containerColor = Color.Transparent,
                    tonalElevation = 0.dp,
                    modifier = Modifier.height(64.dp)
                ) {
                    val navItems = listOf(
                        Triple(AzukuScreen.DASHBOARD, "Dashboard", Icons.Default.Dashboard),
                        Triple(AzukuScreen.TERMINAL, "Terminal", Icons.Default.Terminal),
                        Triple(AzukuScreen.ADB_BUILDER, "ADB Builder", Icons.Default.Code),
                        Triple(AzukuScreen.PACKAGE_MANAGER, "Packages", Icons.Default.Apps),
                        Triple(AzukuScreen.PERMISSION_MANAGER, "Permissions", Icons.Default.Security)
                    )

                    navItems.forEach { (screen, label, icon) ->
                        val isSelected = currentScreen == screen
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.navigateTo(screen) },
                            icon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    tint = if (isSelected) NeonCyan else Color.White.copy(alpha = 0.55f),
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = label,
                                    fontFamily = SpaceGroteskFontFamily,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) NeonCyan else Color.White.copy(alpha = 0.55f)
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = NeonCyan.copy(alpha = 0.18f),
                                selectedIconColor = NeonCyan,
                                selectedTextColor = NeonCyan,
                                unselectedIconColor = Color.White.copy(alpha = 0.55f),
                                unselectedTextColor = Color.White.copy(alpha = 0.55f)
                            ),
                            modifier = Modifier.testTag("nav_item_${screen.route}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    (slideInHorizontally { width -> width / 3 } + fadeIn())
                        .togetherWith(slideOutHorizontally { width -> -width / 3 } + fadeOut())
                },
                label = "screen_transition"
            ) { targetScreen ->
                when (targetScreen) {
                    AzukuScreen.DASHBOARD -> DashboardScreen(viewModel = viewModel)
                    AzukuScreen.TERMINAL -> TerminalScreen(viewModel = viewModel)
                    AzukuScreen.ADB_BUILDER -> AdbBuilderScreen(viewModel = viewModel)
                    AzukuScreen.PACKAGE_MANAGER -> PackageManagerScreen(viewModel = viewModel)
                    AzukuScreen.PERMISSION_MANAGER -> PermissionManagerScreen(viewModel = viewModel)
                    AzukuScreen.LOG_VIEWER -> LogViewerScreen(viewModel = viewModel)
                    AzukuScreen.SETTINGS_EDITOR -> SystemSettingsScreen(viewModel = viewModel)
                    AzukuScreen.PROCESS_VIEWER -> ProcessViewerScreen(viewModel = viewModel)
                    AzukuScreen.AUTOMATION -> AutomationScreen(viewModel = viewModel)
                    AzukuScreen.DEVICE_INFO -> DeviceInfoScreen(viewModel = viewModel)
                }
            }
        }
    }
}
