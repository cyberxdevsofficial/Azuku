package com.example.ui.screens

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppPackageInfo
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
import com.example.viewmodel.PackageFilter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PackageManagerScreen(
    viewModel: AzukuViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val packages by viewModel.packageList.collectAsState()
    val search by viewModel.packageSearch.collectAsState()
    val filter by viewModel.packageFilter.collectAsState()
    val isLoading by viewModel.isPackagesLoading.collectAsState()
    val selectedApp by viewModel.selectedPackage.collectAsState()

    // APK File Picker for App Installer
    val apkPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/vnd.android.package-archive")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            try {
                context.startActivity(intent)
                Toast.makeText(context, "Launching Azuku Package Installer...", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Could not launch package installer", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val filteredList = remember(packages, filter) {
        when (filter) {
            PackageFilter.ALL -> packages
            PackageFilter.USER -> packages.filter { !it.isSystemApp }
            PackageFilter.SYSTEM -> packages.filter { it.isSystemApp }
            PackageFilter.FROZEN -> packages.filter { it.isFrozen }
        }
    }

    val frozenCount = remember(packages) { packages.count { it.isFrozen } }
    val userCount = remember(packages) { packages.count { !it.isSystemApp } }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("package_manager_screen")
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
                    modifier = Modifier.testTag("package_manager_back_button")
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
                        text = "PACKAGE MANAGER",
                        fontFamily = SpaceGroteskFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color.White
                    )
                    Text(
                        text = "App Freezer, APK Installer & Uninstaller",
                        fontFamily = SpaceGroteskFontFamily,
                        fontSize = 11.sp,
                        color = NeonCyan.copy(alpha = 0.8f)
                    )
                }
            }

            Row {
                IconButton(
                    onClick = { apkPickerLauncher.launch("application/vnd.android.package-archive") },
                    modifier = Modifier.testTag("install_apk_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.FileDownload,
                        contentDescription = "Install APK",
                        tint = NeonGreen
                    )
                }

                IconButton(
                    onClick = { viewModel.refreshPackages() },
                    modifier = Modifier.testTag("refresh_packages_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = NeonCyan
                    )
                }
            }
        }

        // Search Bar
        OutlinedTextField(
            value = search,
            onValueChange = { viewModel.setPackageSearch(it) },
            placeholder = { Text("Search installed packages or app name...", color = Color.Gray, fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = NeonCyan) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("package_search_field"),
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

        // Filter Chips & Stats
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(
                    PackageFilter.ALL to "All (${packages.size})",
                    PackageFilter.USER to "User ($userCount)",
                    PackageFilter.SYSTEM to "System",
                    PackageFilter.FROZEN to "Frozen ($frozenCount)"
                ).forEach { (f, label) ->
                    val isSelected = filter == f
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) NeonCyan.copy(alpha = 0.25f) else DarkNavyGlass)
                            .border(1.dp, if (isSelected) NeonCyan else Color.White.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            .clickable { viewModel.setPackageFilter(f) }
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = label,
                            fontFamily = SpaceGroteskFontFamily,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) NeonCyan else Color.LightGray
                        )
                    }
                }
            }
        }

        // Package List
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = NeonCyan)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredList, key = { it.packageName }) { app ->
                    AppPackageCard(
                        app = app,
                        onFreezeToggle = { viewModel.toggleAppFreeze(app.packageName, app.isFrozen) },
                        onDetailsClick = { viewModel.selectPackage(app) },
                        onUninstallClick = {
                            val uninstallIntent = Intent(Intent.ACTION_DELETE).apply {
                                data = Uri.parse("package:${app.packageName}")
                            }
                            context.startActivity(uninstallIntent)
                        }
                    )
                }
            }
        }
    }

    // App Details Dialog
    selectedApp?.let { app ->
        AlertDialog(
            onDismissRequest = { viewModel.selectPackage(null) },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkNavyGlass),
                        contentAlignment = Alignment.Center
                    ) {
                        if (app.iconDrawable != null) {
                            val bmp = drawableToBitmap(app.iconDrawable)
                            if (bmp != null) {
                                Image(
                                    bitmap = bmp.asImageBitmap(),
                                    contentDescription = app.appName,
                                    modifier = Modifier.size(32.dp)
                                )
                            } else {
                                Icon(Icons.Default.Android, contentDescription = null, tint = NeonCyan)
                            }
                        } else {
                            Icon(Icons.Default.Android, contentDescription = null, tint = NeonCyan)
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = app.appName,
                            fontFamily = SpaceGroteskFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 15.sp
                        )
                        Text(
                            text = app.packageName,
                            fontFamily = RobotoMonoFontFamily,
                            fontSize = 10.sp,
                            color = NeonCyan
                        )
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    DetailRow("Version", "${app.versionName} (${app.versionCode})")
                    DetailRow("Target SDK", "API ${app.targetSdkVersion}")
                    DetailRow("Min SDK", "API ${app.minSdkVersion}")
                    DetailRow("UID", app.uid.toString())
                    DetailRow("APK Size", "${app.apkSizeMb} MB")
                    DetailRow("Permissions", "${app.permissionsCount} declared")
                    DetailRow("Frozen State", if (app.isFrozen) "FROZEN (Disabled)" else "Active (Enabled)")
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.selectPackage(null) }) {
                    Text("Close", color = NeonCyan)
                }
            },
            containerColor = DarkNavyGlass,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun AppPackageCard(
    app: AppPackageInfo,
    onFreezeToggle: () -> Unit,
    onDetailsClick: () -> Unit,
    onUninstallClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = 12.dp,
        onClick = onDetailsClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // App Icon
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (app.isFrozen) Color.Gray.copy(alpha = 0.2f) else DarkNavyGlass),
                    contentAlignment = Alignment.Center
                ) {
                    if (app.iconDrawable != null) {
                        val bmp = drawableToBitmap(app.iconDrawable)
                        if (bmp != null) {
                            Image(
                                bitmap = bmp.asImageBitmap(),
                                contentDescription = app.appName,
                                modifier = Modifier.size(32.dp)
                            )
                        } else {
                            Icon(Icons.Default.Android, contentDescription = null, tint = NeonCyan)
                        }
                    } else {
                        Icon(Icons.Default.Android, contentDescription = null, tint = NeonCyan)
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = app.appName,
                            fontFamily = SpaceGroteskFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = if (app.isFrozen) Color.Gray else Color.White
                        )
                        if (app.isFrozen) {
                            Spacer(modifier = Modifier.width(6.dp))
                            CyberBadge(text = "FROZEN", color = NeonCyan, showDot = false)
                        }
                    }
                    Text(
                        text = app.packageName,
                        fontFamily = RobotoMonoFontFamily,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Quick Actions: Freeze / Unfreeze toggle & Uninstall
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onFreezeToggle,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (app.isFrozen) Icons.Default.WbSunny else Icons.Default.AcUnit,
                        contentDescription = if (app.isFrozen) "Defrost" else "Freeze",
                        tint = if (app.isFrozen) NeonAmber else NeonCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = onUninstallClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Uninstall",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontFamily = SpaceGroteskFontFamily, fontSize = 12.sp, color = Color.Gray)
        Text(text = value, fontFamily = RobotoMonoFontFamily, fontSize = 12.sp, color = Color.White)
    }
}

fun drawableToBitmap(drawable: Drawable): Bitmap? {
    if (drawable is BitmapDrawable && drawable.bitmap != null) {
        return drawable.bitmap
    }
    return try {
        val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 96
        val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 96
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        bitmap
    } catch (e: Exception) {
        null
    }
}
