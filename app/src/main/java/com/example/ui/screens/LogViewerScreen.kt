package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LogLevel
import com.example.model.LogcatEntry
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
import com.example.ui.theme.TerminalBg
import com.example.viewmodel.AzukuScreen
import com.example.viewmodel.AzukuViewModel

@Composable
fun LogViewerScreen(
    viewModel: AzukuViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val logs by viewModel.logs.collectAsState()
    val filterTag by viewModel.logFilterTag.collectAsState()
    val filterLevel by viewModel.logFilterLevel.collectAsState()
    val isPaused by viewModel.isLogsPaused.collectAsState()

    val filteredLogs = remember(logs, filterTag, filterLevel) {
        logs.filter { entry ->
            val matchesTag = filterTag.isEmpty() ||
                    entry.tag.contains(filterTag, ignoreCase = true) ||
                    entry.message.contains(filterTag, ignoreCase = true)
            val matchesLevel = filterLevel == null || entry.level == filterLevel
            matchesTag && matchesLevel
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("log_viewer_screen")
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
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
                    modifier = Modifier.testTag("logs_back_button")
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
                        text = "LOG VIEWER (LOGCAT)",
                        fontFamily = SpaceGroteskFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color.White
                    )
                    Text(
                        text = "Live kernel & system log buffer",
                        fontFamily = SpaceGroteskFontFamily,
                        fontSize = 11.sp,
                        color = NeonCyan.copy(alpha = 0.8f)
                    )
                }
            }

            Row {
                IconButton(
                    onClick = { viewModel.togglePauseLogs() },
                    modifier = Modifier.testTag("pause_logs_button")
                ) {
                    Icon(
                        imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = if (isPaused) "Resume" else "Pause",
                        tint = if (isPaused) NeonGreen else NeonAmber
                    )
                }

                IconButton(
                    onClick = {
                        val text = filteredLogs.joinToString("\n") { "${it.timestamp} ${it.level.name.first()}/${it.tag}(${it.pid}): ${it.message}" }
                        clipboardManager.setText(AnnotatedString(text))
                        Toast.makeText(context, "Copied ${filteredLogs.size} logs to clipboard!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.testTag("copy_logs_button")
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = NeonCyan)
                }

                IconButton(
                    onClick = { viewModel.refreshLogs() },
                    modifier = Modifier.testTag("refresh_logs_button")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = NeonCyan)
                }

                IconButton(
                    onClick = { viewModel.clearLogs() },
                    modifier = Modifier.testTag("clear_logs_button")
                ) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = MaterialTheme.colorScheme.error)
                }
            }
        }

        // Search & Tag Filter
        OutlinedTextField(
            value = filterTag,
            onValueChange = { viewModel.setLogFilterTag(it) },
            placeholder = { Text("Filter by Tag or Message...", color = Color.Gray, fontSize = 12.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = NeonCyan) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("log_filter_field"),
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(fontFamily = SpaceGroteskFontFamily, color = Color.White),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = NeonCyan.copy(alpha = 0.3f),
                focusedContainerColor = DarkNavyGlass,
                unfocusedContainerColor = DarkNavyGlass
            )
        )

        // Level Filter Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            LevelFilterChip(label = "ALL", isSelected = filterLevel == null, color = NeonCyan) {
                viewModel.setLogFilterLevel(null)
            }
            LevelFilterChip(label = "V", isSelected = filterLevel == LogLevel.VERBOSE, color = Color.LightGray) {
                viewModel.setLogFilterLevel(LogLevel.VERBOSE)
            }
            LevelFilterChip(label = "D", isSelected = filterLevel == LogLevel.DEBUG, color = NeonCyan) {
                viewModel.setLogFilterLevel(LogLevel.DEBUG)
            }
            LevelFilterChip(label = "I", isSelected = filterLevel == LogLevel.INFO, color = NeonGreen) {
                viewModel.setLogFilterLevel(LogLevel.INFO)
            }
            LevelFilterChip(label = "W", isSelected = filterLevel == LogLevel.WARN, color = NeonAmber) {
                viewModel.setLogFilterLevel(LogLevel.WARN)
            }
            LevelFilterChip(label = "E", isSelected = filterLevel == LogLevel.ERROR, color = NeonRed) {
                viewModel.setLogFilterLevel(LogLevel.ERROR)
            }
        }

        // Logs Display List
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, NeonCyan.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                .background(TerminalBg)
                .padding(8.dp)
        ) {
            if (filteredLogs.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No logs matching current filter.",
                        fontFamily = SpaceGroteskFontFamily,
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filteredLogs, key = { it.id }) { log ->
                        LogcatEntryRow(log)
                    }
                }
            }
        }
    }
}

@Composable
fun LevelFilterChip(
    label: String,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) color.copy(alpha = 0.25f) else DarkNavyGlass)
            .border(1.dp, if (isSelected) color else Color.White.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            fontFamily = RobotoMonoFontFamily,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) color else Color.Gray
        )
    }
}

@Composable
fun LogcatEntryRow(entry: LogcatEntry) {
    val levelColor = when (entry.level) {
        LogLevel.VERBOSE -> Color.Gray
        LogLevel.DEBUG -> NeonCyan
        LogLevel.INFO -> NeonGreen
        LogLevel.WARN -> NeonAmber
        LogLevel.ERROR -> NeonRed
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(DarkNavyGlass.copy(alpha = 0.45f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = entry.level.name.first().toString(),
                    fontFamily = RobotoMonoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = levelColor
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${entry.tag} (${entry.pid})",
                    fontFamily = RobotoMonoFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                    color = Color.White
                )
            }
            Text(
                text = entry.timestamp,
                fontFamily = RobotoMonoFontFamily,
                fontSize = 9.sp,
                color = Color.Gray
            )
        }

        Text(
            text = entry.message,
            fontFamily = RobotoMonoFontFamily,
            fontSize = 11.sp,
            lineHeight = 15.sp,
            color = Color.LightGray
        )
    }
}
