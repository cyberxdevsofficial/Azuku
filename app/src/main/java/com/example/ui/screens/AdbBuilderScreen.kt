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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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

enum class AdbCategory(val label: String) {
    PERMISSIONS("Permissions"),
    APPOPS("AppOps"),
    DISPLAY("Display / WM"),
    BATTERY("Battery & Doze"),
    SETTINGS("Settings"),
    PACKAGE("Package Dexopt")
}

@Composable
fun AdbBuilderScreen(
    viewModel: AzukuViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var selectedCategory by remember { mutableStateOf(AdbCategory.PERMISSIONS) }
    var targetPackage by remember { mutableStateOf("com.example.app") }
    var selectedPermission by remember { mutableStateOf("android.permission.WRITE_SECURE_SETTINGS") }
    var selectedAppOp by remember { mutableStateOf("SYSTEM_ALERT_WINDOW") }
    var appOpMode by remember { mutableStateOf("allow") }
    var displayParam by remember { mutableStateOf("1080x2400") }
    var densityParam by remember { mutableStateOf("420") }
    var batteryLevel by remember { mutableStateOf("100") }
    var settingNamespace by remember { mutableStateOf("global") }
    var settingKey by remember { mutableStateOf("animator_duration_scale") }
    var settingValue by remember { mutableStateOf("0.5") }
    var dexoptFilter by remember { mutableStateOf("speed-profile") }
    var showPairingGuide by remember { mutableStateOf(false) }

    val generatedCommand = remember(
        selectedCategory,
        targetPackage,
        selectedPermission,
        selectedAppOp,
        appOpMode,
        displayParam,
        densityParam,
        batteryLevel,
        settingNamespace,
        settingKey,
        settingValue,
        dexoptFilter
    ) {
        when (selectedCategory) {
            AdbCategory.PERMISSIONS -> "pm grant $targetPackage $selectedPermission"
            AdbCategory.APPOPS -> "appops set $targetPackage $selectedAppOp $appOpMode"
            AdbCategory.DISPLAY -> if (displayParam == "reset") "wm size reset" else "wm size $displayParam"
            AdbCategory.BATTERY -> "dumpsys battery set level $batteryLevel && dumpsys battery unplug"
            AdbCategory.SETTINGS -> "settings put $settingNamespace $settingKey $settingValue"
            AdbCategory.PACKAGE -> "cmd package compile -m $dexoptFilter -f $targetPackage"
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("adb_builder_screen")
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
                    modifier = Modifier.testTag("adb_builder_back_button")
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
                        text = "ADB COMMAND BUILDER",
                        fontFamily = SpaceGroteskFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color.White
                    )
                    Text(
                        text = "Visual constructor for high-privilege Android APIs",
                        fontFamily = SpaceGroteskFontFamily,
                        fontSize = 11.sp,
                        color = NeonCyan.copy(alpha = 0.8f)
                    )
                }
            }

            IconButton(
                onClick = { showPairingGuide = true },
                modifier = Modifier.testTag("pairing_guide_button")
            ) {
                Icon(
                    imageVector = Icons.Default.HelpOutline,
                    contentDescription = "Pairing Guide",
                    tint = NeonViolet
                )
            }
        }

        // Category Horizontal Selector
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(AdbCategory.entries.toTypedArray()) { cat ->
                val isSelected = cat == selectedCategory
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) NeonViolet.copy(alpha = 0.25f) else DarkNavyGlass)
                        .border(
                            1.dp,
                            if (isSelected) NeonViolet else NeonCyan.copy(alpha = 0.2f),
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { selectedCategory = cat }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = cat.label,
                        fontFamily = SpaceGroteskFontFamily,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 12.sp,
                        color = if (isSelected) NeonViolet else Color.White
                    )
                }
            }
        }

        // Interactive Parameter Configuration Card
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "COMMAND PARAMETERS",
                    fontFamily = SpaceGroteskFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = NeonCyan
                )

                when (selectedCategory) {
                    AdbCategory.PERMISSIONS -> {
                        OutlinedTextField(
                            value = targetPackage,
                            onValueChange = { targetPackage = it },
                            label = { Text("Target Package Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(fontFamily = RobotoMonoFontFamily, color = Color.White),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonCyan)
                        )

                        Text("Select Permission:", fontFamily = SpaceGroteskFontFamily, fontSize = 12.sp, color = Color.Gray)
                        val perms = listOf(
                            "android.permission.WRITE_SECURE_SETTINGS",
                            "android.permission.PACKAGE_USAGE_STATS",
                            "android.permission.READ_LOGS",
                            "android.permission.DUMP",
                            "moe.shizuku.manager.permission.API_V23"
                        )
                        perms.forEach { perm ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (selectedPermission == perm) NeonCyan.copy(alpha = 0.15f) else Color.Transparent)
                                    .clickable { selectedPermission = perm }
                                    .padding(vertical = 4.dp, horizontal = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = perm,
                                    fontFamily = RobotoMonoFontFamily,
                                    fontSize = 11.sp,
                                    color = if (selectedPermission == perm) NeonCyan else Color.White
                                )
                            }
                        }
                    }

                    AdbCategory.APPOPS -> {
                        OutlinedTextField(
                            value = targetPackage,
                            onValueChange = { targetPackage = it },
                            label = { Text("Target Package Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(fontFamily = RobotoMonoFontFamily, color = Color.White)
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("SYSTEM_ALERT_WINDOW", "GET_USAGE_STATS", "MANAGE_EXTERNAL_STORAGE").forEach { op ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (selectedAppOp == op) NeonAmber.copy(alpha = 0.2f) else DarkNavyGlass)
                                        .border(1.dp, if (selectedAppOp == op) NeonAmber else Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                        .clickable { selectedAppOp = op }
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Text(op, fontFamily = RobotoMonoFontFamily, fontSize = 9.sp, color = if (selectedAppOp == op) NeonAmber else Color.White)
                                }
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("allow", "ignore", "default").forEach { mode ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (appOpMode == mode) NeonGreen.copy(alpha = 0.2f) else DarkNavyGlass)
                                        .clickable { appOpMode = mode }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(mode, fontFamily = RobotoMonoFontFamily, fontSize = 11.sp, color = if (appOpMode == mode) NeonGreen else Color.White)
                                }
                            }
                        }
                    }

                    AdbCategory.DISPLAY -> {
                        Text("Display Resolution (wm size):", fontFamily = SpaceGroteskFontFamily, fontSize = 12.sp, color = Color.Gray)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("1080x2400", "1440x3120", "720x1600", "reset").forEach { res ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (displayParam == res) NeonCyan.copy(alpha = 0.2f) else DarkNavyGlass)
                                        .border(1.dp, if (displayParam == res) NeonCyan else Color.Gray.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                        .clickable { displayParam = res }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(res, fontFamily = RobotoMonoFontFamily, fontSize = 11.sp, color = if (displayParam == res) NeonCyan else Color.White)
                                }
                            }
                        }
                    }

                    AdbCategory.BATTERY -> {
                        Text("Simulate Battery Capacity (%):", fontFamily = SpaceGroteskFontFamily, fontSize = 12.sp, color = Color.Gray)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("100", "75", "50", "20", "5").forEach { lvl ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (batteryLevel == lvl) NeonGreen.copy(alpha = 0.2f) else DarkNavyGlass)
                                        .clickable { batteryLevel = lvl }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text("$lvl%", fontFamily = RobotoMonoFontFamily, fontSize = 11.sp, color = if (batteryLevel == lvl) NeonGreen else Color.White)
                                }
                            }
                        }
                    }

                    AdbCategory.SETTINGS -> {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("global", "secure", "system").forEach { ns ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (settingNamespace == ns) NeonViolet.copy(alpha = 0.2f) else DarkNavyGlass)
                                        .clickable { settingNamespace = ns }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(ns, fontFamily = RobotoMonoFontFamily, fontSize = 11.sp, color = if (settingNamespace == ns) NeonViolet else Color.White)
                                }
                            }
                        }
                        OutlinedTextField(
                            value = settingKey,
                            onValueChange = { settingKey = it },
                            label = { Text("Setting Key") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(fontFamily = RobotoMonoFontFamily, color = Color.White)
                        )
                        OutlinedTextField(
                            value = settingValue,
                            onValueChange = { settingValue = it },
                            label = { Text("Value") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(fontFamily = RobotoMonoFontFamily, color = Color.White)
                        )
                    }

                    AdbCategory.PACKAGE -> {
                        OutlinedTextField(
                            value = targetPackage,
                            onValueChange = { targetPackage = it },
                            label = { Text("Target Package Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(fontFamily = RobotoMonoFontFamily, color = Color.White)
                        )
                        Text("Compilation Mode (-m):", fontFamily = SpaceGroteskFontFamily, fontSize = 12.sp, color = Color.Gray)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("speed", "speed-profile", "verify", "quicken").forEach { m ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (dexoptFilter == m) NeonCyan.copy(alpha = 0.2f) else DarkNavyGlass)
                                        .clickable { dexoptFilter = m }
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Text(m, fontFamily = RobotoMonoFontFamily, fontSize = 10.sp, color = if (dexoptFilter == m) NeonCyan else Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Live Generated ADB Command Preview
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(TerminalBg)
                .border(1.dp, NeonCyan.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "GENERATED ADB COMMAND",
                    fontFamily = SpaceGroteskFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = NeonCyan
                )
                CyberBadge(text = "READY", color = NeonGreen)
            }

            Text(
                text = "adb shell $generatedCommand",
                fontFamily = RobotoMonoFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                color = TerminalGreen
            )
        }

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = {
                    clipboardManager.setText(AnnotatedString("adb shell $generatedCommand"))
                    Toast.makeText(context, "Copied ADB command to clipboard!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("copy_adb_command_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkNavyGlass)
            ) {
                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, tint = NeonCyan)
                Spacer(modifier = Modifier.width(6.dp))
                Text("COPY ADB", fontFamily = SpaceGroteskFontFamily, fontWeight = FontWeight.Bold, color = NeonCyan)
            }

            Button(
                onClick = {
                    viewModel.executeTerminalCommand(generatedCommand)
                    viewModel.navigateTo(AzukuScreen.TERMINAL)
                    Toast.makeText(context, "Executing in Azuku Terminal...", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("run_in_azuku_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
            ) {
                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(6.dp))
                Text("RUN IN AZUKU", fontFamily = SpaceGroteskFontFamily, fontWeight = FontWeight.Bold, color = Color.Black)
            }
        }
    }

    // Wireless ADB Pairing Guide Dialog
    if (showPairingGuide) {
        AlertDialog(
            onDismissRequest = { showPairingGuide = false },
            title = {
                Text(
                    text = "Wireless ADB Pairing Guide",
                    fontFamily = SpaceGroteskFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "To start Azuku without a PC on Android 11–16:",
                        fontFamily = SpaceGroteskFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        color = NeonCyan,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "1. Enable 'Developer Options' by tapping Build Number 7 times.\n" +
                                "2. Open Developer Options > Enable 'Wireless debugging'.\n" +
                                "3. Tap 'Pair device with pairing code' to view Wi-Fi IP, Port, and 6-digit Code.\n" +
                                "4. In Azuku or terminal, connect using the Wireless ADB port (typically 5555 or 30000+).\n" +
                                "5. Once paired, Azuku's service daemon runs continuously with elevated shell privileges.",
                        fontFamily = SpaceGroteskFontFamily,
                        fontSize = 12.sp,
                        color = Color.LightGray,
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showPairingGuide = false }) {
                    Text("Got it", color = NeonCyan)
                }
            },
            containerColor = DarkNavyGlass,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
