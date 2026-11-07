package com.example.model

enum class ServiceState {
    RUNNING,
    STANDBY,
    CONNECTING,
    ERROR
}

enum class ConnectionMode {
    WIRELESS_ADB,
    SHIZUKU_BINDER,
    ROOT_SU,
    STANDALONE_SHELL
}

data class ServiceStatus(
    val state: ServiceState = ServiceState.RUNNING,
    val mode: ConnectionMode = ConnectionMode.WIRELESS_ADB,
    val port: Int = 5555,
    val pid: Int = 1337,
    val version: String = "v13.5.2 (Native ART 64-bit)",
    val apiVersion: Int = 34,
    val isPrivileged: Boolean = true,
    val uptimeSeconds: Long = 3482,
    val binderToken: String = "azuku-ipc-binder-0x7F4A"
)

data class DeviceSpecs(
    val model: String,
    val manufacturer: String,
    val androidVersion: String,
    val sdkInt: Int,
    val securityPatch: String,
    val kernelVersion: String,
    val selinuxStatus: String,
    val architecture: String,
    val totalRamMb: Long,
    val availableRamMb: Long,
    val batteryLevel: Int,
    val batteryTemp: Float,
    val displayMetrics: String,
    val refreshRateHz: Int,
    val isEmulator: Boolean
)
