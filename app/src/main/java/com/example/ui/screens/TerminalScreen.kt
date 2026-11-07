package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CyberBadge
import com.example.ui.components.GlassCard
import com.example.ui.theme.DarkNavyGlass
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.RobotoMonoFontFamily
import com.example.ui.theme.SpaceGroteskFontFamily
import com.example.ui.theme.TerminalBg
import com.example.ui.theme.TerminalGreen
import com.example.viewmodel.AzukuScreen
import com.example.viewmodel.AzukuViewModel

@Composable
fun TerminalScreen(
    viewModel: AzukuViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val output by viewModel.terminalOutput.collectAsState()
    val input by viewModel.commandInput.collectAsState()
    val isExecuting by viewModel.isExecuting.collectAsState()
    val commandHistory by viewModel.commandHistory.collectAsState()

    var showHistoryDialog by remember { mutableStateOf(false) }
    val terminalScrollState = rememberScrollState()

    // Auto-scroll to bottom on new output
    LaunchedEffect(output) {
        terminalScrollState.animateScrollTo(terminalScrollState.maxValue)
    }

    val quickCommands = listOf(
        "id",
        "whoami",
        "uname -a",
        "getprop ro.build.version.release",
        "pm list packages -3",
        "df -h /data",
        "dumpsys battery",
        "settings get global adb_enabled",
        "top -n 1 -b",
        "clear",
        "help"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("terminal_screen")
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
                    modifier = Modifier.testTag("terminal_back_button")
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
                        text = "SHELL TERMINAL",
                        fontFamily = SpaceGroteskFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                    Text(
                        text = "azuku@android:/ (Wireless ADB / Binder)",
                        fontFamily = RobotoMonoFontFamily,
                        fontSize = 10.sp,
                        color = NeonCyan.copy(alpha = 0.8f)
                    )
                }
            }

            Row {
                IconButton(
                    onClick = { showHistoryDialog = true },
                    modifier = Modifier.testTag("terminal_history_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "History",
                        tint = NeonViolet
                    )
                }

                IconButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(output))
                        Toast.makeText(context, "Terminal output copied!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.testTag("terminal_copy_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        tint = NeonCyan
                    )
                }

                IconButton(
                    onClick = { viewModel.clearTerminal() },
                    modifier = Modifier.testTag("terminal_clear_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        // Quick Suggestion Chips
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(quickCommands) { cmd ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkNavyGlass.copy(alpha = 0.7f))
                        .border(1.dp, NeonCyan.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .clickable { viewModel.executeTerminalCommand(cmd) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = cmd,
                        fontFamily = RobotoMonoFontFamily,
                        fontSize = 11.sp,
                        color = NeonCyan
                    )
                }
            }
        }

        // Terminal Console Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, NeonCyan.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                .background(TerminalBg)
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(terminalScrollState)
            ) {
                Text(
                    text = output,
                    fontFamily = RobotoMonoFontFamily,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = TerminalGreen
                )
            }

            if (isExecuting) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(18.dp),
                    color = NeonCyan,
                    strokeWidth = 2.dp
                )
            }
        }

        // Command Input Field
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = { viewModel.updateCommandInput(it) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("terminal_input_field"),
                placeholder = {
                    Text(
                        text = "Enter shell command...",
                        fontFamily = RobotoMonoFontFamily,
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                },
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontFamily = RobotoMonoFontFamily,
                    fontSize = 13.sp,
                    color = Color.White
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { viewModel.executeTerminalCommand() }),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = NeonCyan.copy(alpha = 0.3f),
                    focusedContainerColor = DarkNavyGlass,
                    unfocusedContainerColor = DarkNavyGlass
                )
            )

            IconButton(
                onClick = { viewModel.executeTerminalCommand() },
                enabled = input.isNotBlank() && !isExecuting,
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (input.isNotBlank()) NeonCyan else DarkNavyGlass)
                    .testTag("terminal_send_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Run",
                    tint = if (input.isNotBlank()) Color.Black else Color.Gray
                )
            }
        }
    }

    // Command History Dialog
    if (showHistoryDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showHistoryDialog = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Command History",
                        fontFamily = SpaceGroteskFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    CyberBadge(text = "${commandHistory.size} Items", color = NeonViolet)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (commandHistory.isEmpty()) {
                        Text(
                            text = "No saved commands yet. Run commands in terminal to record history.",
                            fontFamily = SpaceGroteskFontFamily,
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                    } else {
                        commandHistory.forEach { item ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DarkNavyGlass)
                                    .border(1.dp, NeonCyan.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                    .clickable {
                                        viewModel.updateCommandInput(item.command)
                                        showHistoryDialog = false
                                    }
                                    .padding(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.command,
                                            fontFamily = RobotoMonoFontFamily,
                                            fontSize = 12.sp,
                                            color = NeonCyan
                                        )
                                        Text(
                                            text = "Exit: ${item.exitCode} • ${item.executionTimeMs}ms",
                                            fontFamily = RobotoMonoFontFamily,
                                            fontSize = 10.sp,
                                            color = if (item.exitCode == 0) NeonGreen else MaterialTheme.colorScheme.error
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Run",
                                        tint = NeonCyan,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = { showHistoryDialog = false }) {
                    Text("Close", color = NeonCyan)
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { viewModel.clearHistory() }) {
                    Text("Clear All", color = MaterialTheme.colorScheme.error)
                }
            },
            containerColor = DarkNavyGlass,
            shape = RoundedCornerShape(18.dp)
        )
    }
}
