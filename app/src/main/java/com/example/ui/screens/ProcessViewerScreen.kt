package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ProcessItem
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

@Composable
fun ProcessViewerScreen(
    viewModel: AzukuViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val processes by viewModel.processList.collectAsState()
    val deviceSpecs by viewModel.deviceSpecs.collectAsState()

    var processToKill by remember { mutableStateOf<ProcessItem?>(null) }

    val totalRam = deviceSpecs?.totalRamMb ?: 8192
    val availRam = deviceSpecs?.availableRamMb ?: 4320
    val usedRam = totalRam - availRam
    val ramPercent = (usedRam.toFloat() / totalRam.toFloat()).coerceIn(0f, 1f)

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("process_viewer_screen")
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
                    modifier = Modifier.testTag("processes_back_button")
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
                        text = "PROCESS VIEWER",
                        fontFamily = SpaceGroteskFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color.White
                    )
                    Text(
                        text = "Active tasks, threads & memory usage",
                        fontFamily = SpaceGroteskFontFamily,
                        fontSize = 11.sp,
                        color = NeonCyan.copy(alpha = 0.8f)
                    )
                }
            }

            IconButton(
                onClick = { viewModel.refreshProcesses() },
                modifier = Modifier.testTag("refresh_processes_button")
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = NeonCyan)
            }
        }

        // Live Memory Usage Bar
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SYSTEM RAM ALLOCATION",
                        fontFamily = SpaceGroteskFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = NeonCyan
                    )
                    Text(
                        text = "$usedRam MB / $totalRam MB (${(ramPercent * 100).toInt()}%)",
                        fontFamily = RobotoMonoFontFamily,
                        fontSize = 11.sp,
                        color = if (ramPercent > 0.85f) NeonRed else NeonGreen
                    )
                }

                LinearProgressIndicator(
                    progress = { ramPercent },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (ramPercent > 0.85f) NeonRed else NeonCyan,
                    trackColor = DarkNavyGlass
                )
            }
        }

        // Process List Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ACTIVE PROCESSES (${processes.size})",
                fontFamily = SpaceGroteskFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = Color.White
            )
            CyberBadge(text = "SORT: MEMORY", color = NeonViolet)
        }

        // Process List
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(processes, key = { it.pid }) { proc ->
                ProcessItemRow(
                    process = proc,
                    onKill = { processToKill = proc }
                )
            }
        }
    }

    // Kill Confirmation Dialog
    processToKill?.let { proc ->
        AlertDialog(
            onDismissRequest = { processToKill = null },
            title = {
                Text(
                    text = "Terminate Process?",
                    fontFamily = SpaceGroteskFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to send SIGKILL (-9) to ${proc.name} (PID: ${proc.pid}) via Azuku daemon?",
                    fontFamily = SpaceGroteskFontFamily,
                    fontSize = 13.sp,
                    color = Color.LightGray
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.killProcess(proc.pid)
                        Toast.makeText(context, "Terminated PID: ${proc.pid}", Toast.LENGTH_SHORT).show()
                        processToKill = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Kill", color = Color.White, fontFamily = SpaceGroteskFontFamily, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { processToKill = null }) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            containerColor = DarkNavyGlass,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun ProcessItemRow(
    process: ProcessItem,
    onKill: () -> Unit
) {
    GlassCard(modifier = Modifier.fillMaxWidth(), contentPadding = 12.dp) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (process.isSystem) NeonViolet.copy(alpha = 0.2f) else NeonCyan.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Memory,
                        contentDescription = null,
                        tint = if (process.isSystem) NeonViolet else NeonCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = process.name,
                            fontFamily = RobotoMonoFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "PID: ${process.pid}",
                            fontFamily = RobotoMonoFontFamily,
                            fontSize = 10.sp,
                            color = NeonCyan
                        )
                    }

                    Text(
                        text = "${process.user} • ${process.threads} threads • ${String.format("%.1f", process.memUsageMb)} MB",
                        fontFamily = RobotoMonoFontFamily,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(
                onClick = onKill,
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.error.copy(alpha = 0.15f))
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Kill",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
