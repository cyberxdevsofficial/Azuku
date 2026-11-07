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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SettingNamespace
import com.example.model.SystemSettingEntry
import com.example.ui.components.CyberBadge
import com.example.ui.components.GlassCard
import com.example.ui.theme.DarkNavyGlass
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.RobotoMonoFontFamily
import com.example.ui.theme.SpaceGroteskFontFamily
import com.example.viewmodel.AzukuScreen
import com.example.viewmodel.AzukuViewModel

@Composable
fun SystemSettingsScreen(
    viewModel: AzukuViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val settings by viewModel.settingsList.collectAsState()
    val search by viewModel.settingsSearch.collectAsState()
    val selectedNs by viewModel.settingsNamespace.collectAsState()

    var editingSetting by remember { mutableStateOf<SystemSettingEntry?>(null) }
    var editValue by remember { mutableStateOf("") }

    val filteredSettings = remember(settings, search, selectedNs) {
        settings.filter { item ->
            val matchNs = selectedNs == null || item.namespace == selectedNs
            val matchQuery = search.isEmpty() ||
                    item.key.contains(search, ignoreCase = true) ||
                    item.description.contains(search, ignoreCase = true)
            matchNs && matchQuery
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("system_settings_screen")
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
                    modifier = Modifier.testTag("settings_back_button")
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
                        text = "SYSTEM SETTINGS EDITOR",
                        fontFamily = SpaceGroteskFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color.White
                    )
                    Text(
                        text = "Global, Secure & System setting tables",
                        fontFamily = SpaceGroteskFontFamily,
                        fontSize = 11.sp,
                        color = NeonCyan.copy(alpha = 0.8f)
                    )
                }
            }

            IconButton(
                onClick = { viewModel.refreshSystemSettings() },
                modifier = Modifier.testTag("refresh_settings_button")
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = NeonCyan)
            }
        }

        // Search Bar
        OutlinedTextField(
            value = search,
            onValueChange = { viewModel.setSettingsSearch(it) },
            placeholder = { Text("Search system setting key...", color = Color.Gray, fontSize = 12.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = NeonCyan) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("settings_search_field"),
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

        // Namespace Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                null to "ALL",
                SettingNamespace.GLOBAL to "GLOBAL",
                SettingNamespace.SECURE to "SECURE",
                SettingNamespace.SYSTEM to "SYSTEM"
            ).forEach { (ns, label) ->
                val isSelected = selectedNs == ns
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) NeonViolet.copy(alpha = 0.25f) else DarkNavyGlass)
                        .border(1.dp, if (isSelected) NeonViolet else Color.White.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                        .clickable { viewModel.setSettingsNamespace(ns) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = label,
                        fontFamily = RobotoMonoFontFamily,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) NeonViolet else Color.LightGray
                    )
                }
            }
        }

        // Settings List
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredSettings, key = { "${it.namespace}_${it.key}" }) { item ->
                SettingItemCard(
                    setting = item,
                    onEdit = {
                        editingSetting = item
                        editValue = item.value
                    },
                    onCopyCommand = {
                        val cmd = "settings put ${item.namespace.name.lowercase()} ${item.key} ${item.value}"
                        clipboardManager.setText(AnnotatedString("adb shell $cmd"))
                        Toast.makeText(context, "Copied settings command!", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }

    // Edit Setting Dialog
    editingSetting?.let { item ->
        AlertDialog(
            onDismissRequest = { editingSetting = null },
            title = {
                Text(
                    text = "Edit Setting: ${item.key}",
                    fontFamily = SpaceGroteskFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 15.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = item.description,
                        fontFamily = SpaceGroteskFontFamily,
                        fontSize = 12.sp,
                        color = Color.LightGray
                    )
                    CyberBadge(text = item.namespace.name, color = NeonViolet)
                    OutlinedTextField(
                        value = editValue,
                        onValueChange = { editValue = it },
                        label = { Text("Value") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(fontFamily = RobotoMonoFontFamily, color = Color.White)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val cmd = "settings put ${item.namespace.name.lowercase()} ${item.key} $editValue"
                        viewModel.executeTerminalCommand(cmd)
                        editingSetting = null
                        Toast.makeText(context, "Updated ${item.key} to $editValue via Azuku!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                ) {
                    Text("Apply", color = Color.Black, fontFamily = SpaceGroteskFontFamily, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingSetting = null }) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            containerColor = DarkNavyGlass,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun SettingItemCard(
    setting: SystemSettingEntry,
    onEdit: () -> Unit,
    onCopyCommand: () -> Unit
) {
    val nsColor = when (setting.namespace) {
        SettingNamespace.GLOBAL -> NeonCyan
        SettingNamespace.SECURE -> NeonViolet
        SettingNamespace.SYSTEM -> NeonGreen
    }

    GlassCard(modifier = Modifier.fillMaxWidth(), contentPadding = 12.dp) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CyberBadge(text = setting.namespace.name, color = nsColor, showDot = false)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = setting.key,
                        fontFamily = RobotoMonoFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Current Value: ${setting.value}",
                    fontFamily = RobotoMonoFontFamily,
                    fontSize = 11.sp,
                    color = NeonAmber
                )
                if (setting.description.isNotEmpty()) {
                    Text(
                        text = setting.description,
                        fontFamily = SpaceGroteskFontFamily,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row {
                IconButton(onClick = onCopyCommand, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = NeonCyan, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = NeonViolet, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
