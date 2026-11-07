package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeviceHub
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ConnectionMode
import com.example.model.ServiceState
import com.example.ui.components.AnimatedGlassCard
import com.example.ui.components.AnimatedStatusRing
import com.example.ui.components.CyberBadge
import com.example.ui.components.GlassCard
import com.example.ui.theme.DarkNavyGlass
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonRed
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.RobotoMonoFontFamily
import com.example.ui.theme.SpaceGroteskFontFamily
import com.example.viewmodel.AzukuScreen
import com.example.viewmodel.AzukuViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(
    viewModel: AzukuViewModel,
    modifier: Modifier = Modifier
) {
    val serviceStatus by viewModel.serviceStatus.collectAsState()
    val deviceSpecs by viewModel.deviceSpecs.collectAsState()
    val isConnecting by viewModel.isConnectingAnimation.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen")
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "AZUKU",
                            fontFamily = SpaceGroteskFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,
                            letterSpacing = 1.sp,
                            color = NeonCyan
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        CyberBadge(text = "PRO CORE", color = NeonViolet)
                    }
                    Text(
                        text = "Android 11–16 Privileged Service Framework",
                        fontFamily = SpaceGroteskFontFamily,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.toggleTheme() },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(DarkNavyGlass.copy(alpha = 0.6f))
                            .border(1.dp, NeonCyan.copy(alpha = 0.25f), CircleShape)
                            .testTag("theme_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Theme",
                            tint = NeonCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Live 3D Service Status Card
        item {
            AnimatedGlassCard(
                modifier = Modifier.fillMaxWidth(),
                glowColor = when (serviceStatus.state) {
                    ServiceState.RUNNING -> NeonGreen
                    ServiceState.STANDBY -> NeonCyan
                    ServiceState.CONNECTING -> NeonAmber
                    ServiceState.ERROR -> NeonRed
                }
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CyberBadge(
                            text = if (serviceStatus.state == ServiceState.RUNNING) "SERVICE ONLINE" else "STANDBY MODE",
                            color = if (serviceStatus.state == ServiceState.RUNNING) NeonGreen else NeonCyan
                        )

                        Text(
                            text = "PID: ${serviceStatus.pid} • PORT: ${serviceStatus.port}",
                            fontFamily = RobotoMonoFontFamily,
                            fontSize = 11.sp,
                            color = NeonCyan.copy(alpha = 0.8f)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // 3D Animated Status Ring
                    AnimatedStatusRing(
                        state = serviceStatus.state,
                        size = 148.dp
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = if (serviceStatus.state == ServiceState.RUNNING) Icons.Default.Bolt else Icons.Default.Security,
                                contentDescription = "Azuku Status",
                                tint = if (serviceStatus.state == ServiceState.RUNNING) NeonGreen else NeonCyan,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (serviceStatus.state == ServiceState.RUNNING) "ACTIVE" else "READY",
                                fontFamily = SpaceGroteskFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                            Text(
                                text = "${serviceStatus.mode.name.replace("_", " ")}",
                                fontFamily = RobotoMonoFontFamily,
                                fontSize = 9.sp,
                                color = NeonCyan.copy(alpha = 0.7f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = if (serviceStatus.state == ServiceState.RUNNING)
                            "Privileged Binder IPC active • Full ADB shell privileges available"
                        else
                            "Azuku daemon is on standby. Tap below for one-tap startup.",
                        fontFamily = SpaceGroteskFontFamily,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // One-Tap Startup Button
                    Button(
                        onClick = { viewModel.oneTapStartup() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("one_tap_startup_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (serviceStatus.state == ServiceState.RUNNING)
                                NeonViolet
                            else
                                NeonCyan
                        )
                    ) {
                        if (isConnecting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Pairing with Port 5555...",
                                fontFamily = SpaceGroteskFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        } else {
                            Icon(
                                imageVector = if (serviceStatus.state == ServiceState.RUNNING) Icons.Default.Refresh else Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = if (serviceStatus.state == ServiceState.RUNNING) Color.White else Color.Black
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (serviceStatus.state == ServiceState.RUNNING) "RESTART AZUKU DAEMON" else "ONE-TAP STARTUP (WIRELESS ADB)",
                                fontFamily = SpaceGroteskFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = if (serviceStatus.state == ServiceState.RUNNING) Color.White else Color.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Mode Selection Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ModeChip(
                            title = "Wireless ADB",
                            isSelected = serviceStatus.mode == ConnectionMode.WIRELESS_ADB,
                            onClick = { viewModel.setConnectionMode(ConnectionMode.WIRELESS_ADB) }
                        )
                        ModeChip(
                            title = "Shizuku Binder",
                            isSelected = serviceStatus.mode == ConnectionMode.SHIZUKU_BINDER,
                            onClick = { viewModel.setConnectionMode(ConnectionMode.SHIZUKU_BINDER) }
                        )
                        ModeChip(
                            title = "Root (su)",
                            isSelected = serviceStatus.mode == ConnectionMode.ROOT_SU,
                            onClick = { viewModel.setConnectionMode(ConnectionMode.ROOT_SU) }
                        )
                    }
                }
            }
        }

        // Live Telemetry / System Specs Glance
        item {
            deviceSpecs?.let { specs ->
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "SYSTEM METRICS",
                                fontFamily = SpaceGroteskFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = NeonCyan
                            )
                            CyberBadge(text = specs.androidVersion, color = NeonGreen)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            MetricPill(label = "DEVICE", value = "${specs.manufacturer} ${specs.model}")
                            MetricPill(label = "ARCH", value = specs.architecture)
                            MetricPill(label = "SELINUX", value = specs.selinuxStatus)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            MetricPill(label = "RAM", value = "${specs.availableRamMb}M / ${specs.totalRamMb}M")
                            MetricPill(label = "BATTERY", value = "${specs.batteryLevel}% (${specs.batteryTemp}°C)")
                            MetricPill(label = "DISPLAY", value = "${specs.refreshRateHz}Hz Native")
                        }
                    }
                }
            }
        }

        // Developer Toolkit Section Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DEVELOPER TOOLKIT",
                    fontFamily = SpaceGroteskFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White
                )
                Text(
                    text = "Android 11–16 Suite",
                    fontFamily = RobotoMonoFontFamily,
                    fontSize = 11.sp,
                    color = NeonCyan
                )
            }
        }

        // Tools Grid (2-column quick launch)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ToolkitCard(
                        title = "Shell Terminal",
                        subtitle = "Interactive sh console",
                        icon = Icons.Default.Terminal,
                        color = NeonCyan,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(AzukuScreen.TERMINAL) }
                    )
                    ToolkitCard(
                        title = "ADB Builder",
                        subtitle = "Visual command tool",
                        icon = Icons.Default.Code,
                        color = NeonViolet,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(AzukuScreen.ADB_BUILDER) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ToolkitCard(
                        title = "Package Manager",
                        subtitle = "App freezer & APKs",
                        icon = Icons.Default.Apps,
                        color = NeonGreen,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(AzukuScreen.PACKAGE_MANAGER) }
                    )
                    ToolkitCard(
                        title = "Permission Center",
                        subtitle = "All Android permissions",
                        icon = Icons.Default.Security,
                        color = NeonAmber,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(AzukuScreen.PERMISSION_MANAGER) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ToolkitCard(
                        title = "Log Viewer",
                        subtitle = "Real-time Logcat stream",
                        icon = Icons.Default.ViewList,
                        color = NeonCyan,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(AzukuScreen.LOG_VIEWER) }
                    )
                    ToolkitCard(
                        title = "Settings Editor",
                        subtitle = "Global / Secure table",
                        icon = Icons.Default.Settings,
                        color = NeonViolet,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(AzukuScreen.SETTINGS_EDITOR) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ToolkitCard(
                        title = "Process Viewer",
                        subtitle = "Active tasks & memory",
                        icon = Icons.Default.Memory,
                        color = NeonAmber,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(AzukuScreen.PROCESS_VIEWER) }
                    )
                    ToolkitCard(
                        title = "Automation",
                        subtitle = "Tasker & Termux IPC",
                        icon = Icons.Default.AutoAwesome,
                        color = NeonGreen,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(AzukuScreen.AUTOMATION) }
                    )
                }
            }
        }

        // Native Engine Badge
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = DarkNavyGlass.copy(alpha = 0.5f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(28.dp)
                    )
                    Column {
                        Text(
                            text = "Native 120Hz Hardware-Accelerated Engine",
                            fontFamily = SpaceGroteskFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Zero-Jank ART Bytecode execution with instant IPC binding.",
                            fontFamily = SpaceGroteskFontFamily,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ModeChip(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .border(
                1.dp,
                if (isSelected) NeonCyan else Color.White.copy(alpha = 0.15f),
                RoundedCornerShape(10.dp)
            )
            .background(if (isSelected) NeonCyan.copy(alpha = 0.18f) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = title,
            fontFamily = RobotoMonoFontFamily,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) NeonCyan else Color.White.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun MetricPill(label: String, value: String) {
    Column {
        Text(
            text = label,
            fontFamily = RobotoMonoFontFamily,
            fontSize = 9.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontFamily = SpaceGroteskFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            color = Color.White
        )
    }
}

@Composable
fun ToolkitCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = modifier.height(106.dp),
        borderColor = color.copy(alpha = 0.35f),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = color,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = color.copy(alpha = 0.6f),
                    modifier = Modifier.size(16.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    fontFamily = SpaceGroteskFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = Color.White
                )
                Text(
                    text = subtitle,
                    fontFamily = SpaceGroteskFontFamily,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
