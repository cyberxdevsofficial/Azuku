package com.example.ui.screens

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CyberBadge
import com.example.ui.components.GlassCard
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.RobotoMonoFontFamily
import com.example.ui.theme.SpaceGroteskFontFamily
import com.example.viewmodel.AzukuScreen
import com.example.viewmodel.AzukuViewModel

@Composable
fun DeviceInfoScreen(
    viewModel: AzukuViewModel,
    modifier: Modifier = Modifier
) {
    val deviceSpecs by viewModel.deviceSpecs.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("device_info_screen")
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { viewModel.navigateTo(AzukuScreen.DASHBOARD) },
                    modifier = Modifier.testTag("device_info_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = NeonCyan
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = "DEVICE SPECIFICATIONS",
                        fontFamily = SpaceGroteskFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color.White
                    )
                    Text(
                        text = "Hardware, Kernel, Security & Compatibility",
                        fontFamily = SpaceGroteskFontFamily,
                        fontSize = 11.sp,
                        color = NeonCyan.copy(alpha = 0.8f)
                    )
                }
            }
        }

        deviceSpecs?.let { specs ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // OS & Platform Specs Card
                item {
                    SpecsCard(
                        title = "Operating System & Architecture",
                        icon = Icons.Default.Android,
                        color = NeonGreen
                    ) {
                        SpecLine("Android Release", specs.androidVersion)
                        SpecLine("API Level", "SDK ${specs.sdkInt}")
                        SpecLine("Security Patch", specs.securityPatch)
                        SpecLine("Instruction Architecture", specs.architecture)
                        SpecLine("Build Fingerprint", Build.FINGERPRINT.take(48) + "...")
                        SpecLine("Emulator Status", if (specs.isEmulator) "Virtual Container / Emulator" else "Physical Hardware")
                    }
                }

                // Security & Kernel Card
                item {
                    SpecsCard(
                        title = "Security & Kernel State",
                        icon = Icons.Default.Security,
                        color = NeonCyan
                    ) {
                        SpecLine("SELinux Mode", specs.selinuxStatus)
                        SpecLine("Kernel", specs.kernelVersion)
                        SpecLine("Root Privileges", "Sui / Superuser Binder Supported")
                        SpecLine("Wireless ADB Port", "5555 (Standard ADB Daemon)")
                    }
                }

                // Hardware & Display Card
                item {
                    SpecsCard(
                        title = "Display & Memory Telemetry",
                        icon = Icons.Default.Smartphone,
                        color = NeonViolet
                    ) {
                        SpecLine("Manufacturer & Model", "${specs.manufacturer} ${specs.model}")
                        SpecLine("Display Resolution", specs.displayMetrics)
                        SpecLine("Native Refresh Rate", "${specs.refreshRateHz} Hz High-Smoothness")
                        SpecLine("Total Physical RAM", "${specs.totalRamMb} MB")
                        SpecLine("Available Free RAM", "${specs.availableRamMb} MB")
                    }
                }

                // Battery Diagnostics Card
                item {
                    SpecsCard(
                        title = "Power & Battery Subsystem",
                        icon = Icons.Default.BatteryFull,
                        color = NeonAmber
                    ) {
                        SpecLine("Battery Charge Level", "${specs.batteryLevel} %")
                        SpecLine("Battery Temperature", "${specs.batteryTemp} °C [Optimal]")
                        SpecLine("Thermal Throttling", "Normal (No throttling active)")
                    }
                }

                // Android 11–16 Compatibility Status Card
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "ANDROID 11–16 COMPATIBILITY MATRIX",
                                fontFamily = SpaceGroteskFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = NeonCyan
                            )
                            Text(
                                text = "Azuku conforms to Android 11, 12, 13, 14, 15, and 16 developer API constraints:",
                                fontFamily = SpaceGroteskFontFamily,
                                fontSize = 12.sp,
                                color = Color.LightGray
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                CyberBadge(text = "A11 Wireless Debug", color = NeonGreen)
                                CyberBadge(text = "A12 Dynamic Color", color = NeonGreen)
                                CyberBadge(text = "A13 Notification Permission", color = NeonGreen)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                CyberBadge(text = "A14 Foreground Service", color = NeonGreen)
                                CyberBadge(text = "A15 Edge-to-Edge", color = NeonGreen)
                                CyberBadge(text = "A16 16KB Page Compatible", color = NeonGreen)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SpecsCard(
    title: String,
    icon: ImageVector,
    color: Color,
    content: @Composable () -> Unit
) {
    GlassCard(modifier = Modifier.fillMaxWidth(), borderColor = color.copy(alpha = 0.35f)) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontFamily = SpaceGroteskFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.White
                )
            }
            content()
        }
    }
}

@Composable
fun SpecLine(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontFamily = SpaceGroteskFontFamily, fontSize = 12.sp, color = Color.Gray)
        Text(
            text = value,
            fontFamily = RobotoMonoFontFamily,
            fontSize = 11.sp,
            color = Color.White,
            fontWeight = FontWeight.Medium
        )
    }
}
