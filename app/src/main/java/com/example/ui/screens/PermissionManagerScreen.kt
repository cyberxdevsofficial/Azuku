package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.model.PermissionDetail
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
fun PermissionManagerScreen(
    viewModel: AzukuViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val permissions by viewModel.permissions.collectAsState()

    // Runtime Permission Launcher (POST_NOTIFICATIONS)
    val notifPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.refreshPermissions()
        val msg = if (isGranted) "Notification permission granted!" else "Permission declined"
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    val grantedCount = permissions.count { it.isGranted }
    val totalCount = permissions.size

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("permission_manager_screen")
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
                    modifier = Modifier.testTag("permissions_back_button")
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
                        text = "PERMISSION CENTER",
                        fontFamily = SpaceGroteskFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color.White
                    )
                    Text(
                        text = "Android System & Privileged Access Control",
                        fontFamily = SpaceGroteskFontFamily,
                        fontSize = 11.sp,
                        color = NeonCyan.copy(alpha = 0.8f)
                    )
                }
            }

            IconButton(
                onClick = { viewModel.refreshPermissions() },
                modifier = Modifier.testTag("refresh_permissions_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    tint = NeonCyan
                )
            }
        }

        // Summary Card
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "PRIVILEGED STATUS",
                        fontFamily = SpaceGroteskFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = NeonCyan
                    )
                    Text(
                        text = "$grantedCount of $totalCount permissions configured",
                        fontFamily = SpaceGroteskFontFamily,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }

                Button(
                    onClick = {
                        // Request notification runtime permission if needed
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            notifPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            viewModel.refreshPermissions()
                            Toast.makeText(context, "Permissions refreshed!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                ) {
                    Icon(Icons.Default.Bolt, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("REQUEST ALL", fontFamily = SpaceGroteskFontFamily, fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }
        }

        // Permissions List
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(permissions, key = { it.permission }) { perm ->
                PermissionCard(
                    perm = perm,
                    onOpenSettings = {
                        if (perm.settingsAction != null) {
                            val intent = Intent(perm.settingsAction)
                            if (perm.settingsAction == Settings.ACTION_APP_NOTIFICATION_SETTINGS) {
                                intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                            } else {
                                intent.data = Uri.parse("package:${context.packageName}")
                            }
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                val genericIntent = Intent(Settings.ACTION_SETTINGS)
                                context.startActivity(genericIntent)
                            }
                        } else {
                            // Copy ADB Command
                            clipboardManager.setText(AnnotatedString("adb shell ${perm.adbCommand}"))
                            Toast.makeText(context, "Copied ADB command to grant!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onCopyAdb = {
                        clipboardManager.setText(AnnotatedString("adb shell ${perm.adbCommand}"))
                        Toast.makeText(context, "Copied: adb shell ${perm.adbCommand}", Toast.LENGTH_SHORT).show()
                    },
                    onGrantInAzuku = {
                        viewModel.executeTerminalCommand(perm.adbCommand)
                        Toast.makeText(context, "Executed grant in Azuku Privileged Core!", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

@Composable
fun PermissionCard(
    perm: PermissionDetail,
    onOpenSettings: () -> Unit,
    onCopyAdb: () -> Unit,
    onGrantInAzuku: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = if (perm.isGranted) NeonGreen.copy(alpha = 0.35f) else NeonAmber.copy(alpha = 0.35f)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (perm.isGranted) Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (perm.isGranted) NeonGreen else NeonAmber,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = perm.title,
                            fontFamily = SpaceGroteskFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                        Text(
                            text = perm.permission,
                            fontFamily = RobotoMonoFontFamily,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                CyberBadge(
                    text = if (perm.isGranted) "GRANTED" else "REQUIRED",
                    color = if (perm.isGranted) NeonGreen else NeonAmber
                )
            }

            Text(
                text = perm.description,
                fontFamily = SpaceGroteskFontFamily,
                fontSize = 12.sp,
                color = Color.LightGray
            )

            // ADB Command box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkNavyGlass)
                    .border(1.dp, NeonCyan.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "adb shell ${perm.adbCommand}",
                        fontFamily = RobotoMonoFontFamily,
                        fontSize = 10.sp,
                        color = NeonCyan,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = onCopyAdb,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = NeonCyan, modifier = Modifier.size(16.dp))
                    }
                }
            }

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (perm.settingsAction != null) {
                    Button(
                        onClick = onOpenSettings,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkNavyGlass),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(Icons.Default.OpenInNew, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("SYSTEM SETTINGS", fontFamily = SpaceGroteskFontFamily, fontSize = 11.sp, color = NeonCyan)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Button(
                    onClick = onGrantInAzuku,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (perm.isGranted) NeonGreen.copy(alpha = 0.2f) else NeonViolet),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(Icons.Default.Bolt, contentDescription = null, tint = if (perm.isGranted) NeonGreen else Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (perm.isGranted) "RE-APPLY" else "GRANT IN AZUKU", fontFamily = SpaceGroteskFontFamily, fontSize = 11.sp, color = if (perm.isGranted) NeonGreen else Color.White)
                }
            }
        }
    }
}
