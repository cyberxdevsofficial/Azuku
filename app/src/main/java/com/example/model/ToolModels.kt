package com.example.model

import android.graphics.drawable.Drawable

data class AppPackageInfo(
    val packageName: String,
    val appName: String,
    val versionName: String,
    val versionCode: Long,
    val isSystemApp: Boolean,
    val isFrozen: Boolean = false,
    val targetSdkVersion: Int,
    val minSdkVersion: Int,
    val installTime: Long,
    val uid: Int,
    val permissionsCount: Int,
    val apkSizeMb: Double = 0.0,
    @Transient val iconDrawable: Drawable? = null
)

data class ProcessItem(
    val pid: Int,
    val name: String,
    val user: String,
    val memUsageMb: Float,
    val threads: Int,
    val priority: Int,
    val isSystem: Boolean
)

data class LogcatEntry(
    val id: Long,
    val timestamp: String,
    val pid: String,
    val level: LogLevel,
    val tag: String,
    val message: String
)

enum class LogLevel {
    VERBOSE, DEBUG, INFO, WARN, ERROR
}

data class SystemSettingEntry(
    val namespace: SettingNamespace,
    val key: String,
    val value: String,
    val description: String = ""
)

enum class SettingNamespace {
    GLOBAL, SECURE, SYSTEM
}

data class PermissionDetail(
    val permission: String,
    val title: String,
    val category: String,
    val isGranted: Boolean,
    val isPrivileged: Boolean,
    val description: String,
    val adbCommand: String,
    val settingsAction: String? = null
)
