package com.example.ui.screens

import android.content.Intent
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CyberBadge
import com.example.ui.components.GlassCard
import com.example.ui.theme.DarkNavyGlass
import com.example.ui.theme.NeonAmber
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
fun AutomationScreen(
    viewModel: AzukuViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var testCommand by remember { mutableStateOf("dumpsys battery") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("automation_screen")
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
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
                    modifier = Modifier.testTag("automation_back_button")
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
                        text = "AUTOMATION INTEGRATION",
                        fontFamily = SpaceGroteskFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color.White
                    )
                    Text(
                        text = "Tasker, MacroDroid, Termux & ADB broadcasts",
                        fontFamily = SpaceGroteskFontFamily,
                        fontSize = 11.sp,
                        color = NeonCyan.copy(alpha = 0.8f)
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Live Test Trigger Card
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "BROADCAST TEST BENCH",
                                fontFamily = SpaceGroteskFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = NeonGreen
                            )
                            CyberBadge(text = "LIVE IPC", color = NeonGreen)
                        }

                        Text(
                            text = "Fire an automated intent broadcast through Azuku's registered automation receiver:",
                            fontFamily = SpaceGroteskFontFamily,
                            fontSize = 12.sp,
                            color = Color.LightGray
                        )

                        OutlinedTextField(
                            value = testCommand,
                            onValueChange = { testCommand = it },
                            label = { Text("Command payload (extra: cmd)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(fontFamily = RobotoMonoFontFamily, color = Color.White),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonGreen,
                                unfocusedBorderColor = NeonGreen.copy(alpha = 0.3f)
                            )
                        )

                        Button(
                            onClick = {
                                val intent = Intent("com.aistudio.azuku.EXEC_COMMAND").apply {
                                    setPackage(context.packageName)
                                    putExtra("cmd", testCommand)
                                }
                                context.sendBroadcast(intent)
                                Toast.makeText(context, "Sent Broadcast: com.aistudio.azuku.EXEC_COMMAND", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonGreen)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("TRIGGER BROADCAST NOW", fontFamily = SpaceGroteskFontFamily, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                }
            }

            // Integration Snippet: ADB Shell
            item {
                AutomationSnippetCard(
                    title = "ADB Shell Broadcast",
                    description = "Execute elevated commands from an external script or terminal:",
                    code = "am broadcast -a com.aistudio.azuku.EXEC_COMMAND -p ${context.packageName} --es cmd \"$testCommand\"",
                    onCopy = {
                        val cmd = "am broadcast -a com.aistudio.azuku.EXEC_COMMAND -p ${context.packageName} --es cmd \"$testCommand\""
                        clipboardManager.setText(AnnotatedString(cmd))
                        Toast.makeText(context, "Copied ADB snippet!", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            // Integration Snippet: Tasker
            item {
                AutomationSnippetCard(
                    title = "Tasker Action: Send Intent",
                    description = "Configure in Tasker under 'System' > 'Send Intent':",
                    code = """Action: com.aistudio.azuku.EXEC_COMMAND
Package: ${context.packageName}
Target: Broadcast Receiver
Extra: cmd: $testCommand""",
                    onCopy = {
                        val text = "Action: com.aistudio.azuku.EXEC_COMMAND\nPackage: ${context.packageName}\nExtra: cmd: $testCommand"
                        clipboardManager.setText(AnnotatedString(text))
                        Toast.makeText(context, "Copied Tasker setup snippet!", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            // Integration Snippet: MacroDroid
            item {
                AutomationSnippetCard(
                    title = "MacroDroid Action: Send Intent",
                    description = "In MacroDroid, add 'Action' > 'Connectivity' > 'Send Intent':",
                    code = """Action: com.aistudio.azuku.EXEC_COMMAND
Package: ${context.packageName}
Receiver: .receiver.AzukuAutomationReceiver
Extra parameter: cmd = $testCommand""",
                    onCopy = {
                        val text = "com.aistudio.azuku.EXEC_COMMAND\ncmd = $testCommand"
                        clipboardManager.setText(AnnotatedString(text))
                        Toast.makeText(context, "Copied MacroDroid snippet!", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            // Integration Snippet: Termux
            item {
                AutomationSnippetCard(
                    title = "Termux Bash Script",
                    description = "Run privileged commands seamlessly from your Termux shell:",
                    code = """termux-azuku() {
  am broadcast -a com.aistudio.azuku.EXEC_COMMAND \
    -p ${context.packageName} --es cmd "${'$'}*"
}
# Usage: termux-azuku pm list packages -3""",
                    onCopy = {
                        val script = "termux-azuku() {\n  am broadcast -a com.aistudio.azuku.EXEC_COMMAND -p ${context.packageName} --es cmd \"\$*\"\n}"
                        clipboardManager.setText(AnnotatedString(script))
                        Toast.makeText(context, "Copied Termux bash function!", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

@Composable
fun AutomationSnippetCard(
    title: String,
    description: String,
    code: String,
    onCopy: () -> Unit
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontFamily = SpaceGroteskFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = NeonCyan
                )
                IconButton(onClick = onCopy, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = NeonCyan, modifier = Modifier.size(16.dp))
                }
            }

            Text(
                text = description,
                fontFamily = SpaceGroteskFontFamily,
                fontSize = 11.sp,
                color = Color.LightGray
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(TerminalBg)
                    .border(1.dp, NeonCyan.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Text(
                    text = code,
                    fontFamily = RobotoMonoFontFamily,
                    fontSize = 11.sp,
                    color = TerminalGreen,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
