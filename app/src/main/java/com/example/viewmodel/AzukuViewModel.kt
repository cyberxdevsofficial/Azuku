package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.data.local.AzukuDatabase
import com.example.data.local.CommandHistoryEntity
import com.example.data.local.SavedScriptEntity
import com.example.model.AppPackageInfo
import com.example.model.ConnectionMode
import com.example.model.DeviceSpecs
import com.example.model.LogLevel
import com.example.model.LogcatEntry
import com.example.model.PermissionDetail
import com.example.model.ProcessItem
import com.example.model.ServiceState
import com.example.model.ServiceStatus
import com.example.model.SettingNamespace
import com.example.model.SystemSettingEntry
import com.example.service.AzukuCoreEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

enum class AzukuScreen(val title: String, val route: String) {
    DASHBOARD("Dashboard", "dashboard"),
    TERMINAL("Terminal", "terminal"),
    ADB_BUILDER("ADB Builder", "adb_builder"),
    PACKAGE_MANAGER("Packages", "packages"),
    PERMISSION_MANAGER("Permissions", "permissions"),
    LOG_VIEWER("Log Viewer", "logs"),
    SETTINGS_EDITOR("Settings", "settings"),
    PROCESS_VIEWER("Processes", "processes"),
    AUTOMATION("Automation", "automation"),
    DEVICE_INFO("Device Specs", "device_specs")
}

enum class PackageFilter {
    ALL, USER, SYSTEM, FROZEN
}

class AzukuViewModel(application: Application) : AndroidViewModel(application) {

    private val engine = AzukuCoreEngine(application.applicationContext)

    private val database: AzukuDatabase by lazy {
        Room.databaseBuilder(
            application.applicationContext,
            AzukuDatabase::class.java,
            "azuku_toolkit.db"
        ).fallbackToDestructiveMigration().build()
    }

    // UI Theme state
    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    // Navigation state
    private val _currentScreen = MutableStateFlow(AzukuScreen.DASHBOARD)
    val currentScreen: StateFlow<AzukuScreen> = _currentScreen.asStateFlow()

    // Service & System state
    val serviceStatus: StateFlow<ServiceStatus> = engine.serviceStatus

    private val _deviceSpecs = MutableStateFlow<DeviceSpecs?>(null)
    val deviceSpecs: StateFlow<DeviceSpecs?> = _deviceSpecs.asStateFlow()

    // Terminal state
    private val _commandInput = MutableStateFlow("")
    val commandInput: StateFlow<String> = _commandInput.asStateFlow()

    private val _terminalOutput = MutableStateFlow("Azuku Shell Environment v4.8\nType 'help' for built-in tools or run standard Linux/Android shell commands.\nConnected via Wireless ADB Bridge [127.0.0.1:5555]\n")
    val terminalOutput: StateFlow<String> = _terminalOutput.asStateFlow()

    private val _isExecuting = MutableStateFlow(false)
    val isExecuting: StateFlow<Boolean> = _isExecuting.asStateFlow()

    private val _commandHistory = MutableStateFlow<List<CommandHistoryEntity>>(emptyList())
    val commandHistory: StateFlow<List<CommandHistoryEntity>> = _commandHistory.asStateFlow()

    // Package Manager state
    private val _packageList = MutableStateFlow<List<AppPackageInfo>>(emptyList())
    val packageList: StateFlow<List<AppPackageInfo>> = _packageList.asStateFlow()

    private val _packageSearch = MutableStateFlow("")
    val packageSearch: StateFlow<String> = _packageSearch.asStateFlow()

    private val _packageFilter = MutableStateFlow(PackageFilter.ALL)
    val packageFilter: StateFlow<PackageFilter> = _packageFilter.asStateFlow()

    private val _selectedPackage = MutableStateFlow<AppPackageInfo?>(null)
    val selectedPackage: StateFlow<AppPackageInfo?> = _selectedPackage.asStateFlow()

    private val _isPackagesLoading = MutableStateFlow(false)
    val isPackagesLoading: StateFlow<Boolean> = _isPackagesLoading.asStateFlow()

    // Permission state
    private val _permissions = MutableStateFlow<List<PermissionDetail>>(emptyList())
    val permissions: StateFlow<List<PermissionDetail>> = _permissions.asStateFlow()

    // Logcat state
    private val _logs = MutableStateFlow<List<LogcatEntry>>(emptyList())
    val logs: StateFlow<List<LogcatEntry>> = _logs.asStateFlow()

    private val _logFilterTag = MutableStateFlow("")
    val logFilterTag: StateFlow<String> = _logFilterTag.asStateFlow()

    private val _logFilterLevel = MutableStateFlow<LogLevel?>(null)
    val logFilterLevel: StateFlow<LogLevel?> = _logFilterLevel.asStateFlow()

    private val _isLogsPaused = MutableStateFlow(false)
    val isLogsPaused: StateFlow<Boolean> = _isLogsPaused.asStateFlow()

    // System Settings state
    private val _settingsList = MutableStateFlow<List<SystemSettingEntry>>(emptyList())
    val settingsList: StateFlow<List<SystemSettingEntry>> = _settingsList.asStateFlow()

    private val _settingsSearch = MutableStateFlow("")
    val settingsSearch: StateFlow<String> = _settingsSearch.asStateFlow()

    private val _settingsNamespace = MutableStateFlow<SettingNamespace?>(null)
    val settingsNamespace: StateFlow<SettingNamespace?> = _settingsNamespace.asStateFlow()

    // Process Viewer state
    private val _processList = MutableStateFlow<List<ProcessItem>>(emptyList())
    val processList: StateFlow<List<ProcessItem>> = _processList.asStateFlow()

    private val _isConnectingAnimation = MutableStateFlow(false)
    val isConnectingAnimation: StateFlow<Boolean> = _isConnectingAnimation.asStateFlow()

    init {
        loadInitialData()
        observeCommandHistory()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _deviceSpecs.value = engine.getDeviceSpecs()
            refreshPermissions()
            refreshPackages()
            refreshLogs()
            refreshSystemSettings()
            refreshProcesses()
        }
    }

    private fun observeCommandHistory() {
        viewModelScope.launch {
            database.azukuDao().getCommandHistory().collectLatest {
                _commandHistory.value = it
            }
        }
    }

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    fun navigateTo(screen: AzukuScreen) {
        _currentScreen.value = screen
    }

    fun oneTapStartup() {
        viewModelScope.launch {
            _isConnectingAnimation.value = true
            delay(800)
            engine.toggleService()
            _isConnectingAnimation.value = false
        }
    }

    fun setConnectionMode(mode: ConnectionMode) {
        engine.setConnectionMode(mode)
    }

    fun updateCommandInput(text: String) {
        _commandInput.value = text
    }

    fun executeTerminalCommand(cmd: String = _commandInput.value) {
        val commandToRun = cmd.trim()
        if (commandToRun.isEmpty()) return

        viewModelScope.launch(Dispatchers.IO) {
            _isExecuting.value = true

            if (commandToRun.equals("clear", ignoreCase = true)) {
                _terminalOutput.value = "Azuku Shell [Terminal Cleared]\n"
                _commandInput.value = ""
                _isExecuting.value = false
                return@launch
            }

            if (commandToRun.equals("help", ignoreCase = true)) {
                val helpText = """
                    Azuku Developer Terminal Help:
                    ----------------------------------------
                    Standard Shell: ls, pwd, id, uname, df, getprop, ps, top
                    Package Manager: pm list packages, pm path <pkg>, pm disable <pkg>, pm enable <pkg>
                    AppOps: appops get <pkg>, appops set <pkg> <op> allow
                    Window Manager: wm size, wm density, wm overscan
                    Settings: settings get global <key>, settings put global <key> <val>
                    Device Idle: dumpsys deviceidle force-idle
                    System Logs: logcat -d -t 50
                    Clear: clear
                """.trimIndent()
                _terminalOutput.value += "\n$ $commandToRun\n$helpText\n"
                _commandInput.value = ""
                _isExecuting.value = false
                return@launch
            }

            val result = engine.executeCommand(commandToRun)

            val display = "\n$ $commandToRun\n${result.output} [exit ${result.exitCode} in ${result.durationMs}ms]\n"
            _terminalOutput.value += display

            database.azukuDao().insertCommand(
                CommandHistoryEntity(
                    command = commandToRun,
                    exitCode = result.exitCode,
                    outputSnippet = result.output.take(120),
                    executionTimeMs = result.durationMs
                )
            )

            _commandInput.value = ""
            _isExecuting.value = false
        }
    }

    fun clearTerminal() {
        _terminalOutput.value = "Azuku Shell [Cleared]\n"
    }

    fun clearHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            database.azukuDao().clearHistory()
        }
    }

    fun refreshPackages() {
        viewModelScope.launch {
            _isPackagesLoading.value = true
            _packageList.value = engine.getInstalledPackages(_packageSearch.value)
            _isPackagesLoading.value = false
        }
    }

    fun setPackageSearch(query: String) {
        _packageSearch.value = query
        refreshPackages()
    }

    fun setPackageFilter(filter: PackageFilter) {
        _packageFilter.value = filter
    }

    fun selectPackage(pkg: AppPackageInfo?) {
        _selectedPackage.value = pkg
    }

    fun toggleAppFreeze(packageName: String, currentFrozen: Boolean) {
        viewModelScope.launch {
            if (currentFrozen) {
                engine.unfreezePackage(packageName)
                executeTerminalCommand("pm enable $packageName")
            } else {
                engine.freezePackage(packageName)
                executeTerminalCommand("pm disable $packageName")
            }
            refreshPackages()
        }
    }

    fun refreshPermissions() {
        viewModelScope.launch {
            _permissions.value = engine.getPermissionsList()
        }
    }

    fun refreshLogs() {
        if (_isLogsPaused.value) return
        viewModelScope.launch {
            _logs.value = engine.getLogcatEntries(100)
        }
    }

    fun togglePauseLogs() {
        _isLogsPaused.value = !_isLogsPaused.value
    }

    fun setLogFilterTag(tag: String) {
        _logFilterTag.value = tag
    }

    fun setLogFilterLevel(level: LogLevel?) {
        _logFilterLevel.value = level
    }

    fun clearLogs() {
        _logs.value = emptyList()
    }

    fun refreshSystemSettings() {
        viewModelScope.launch {
            _settingsList.value = engine.getSystemSettings()
        }
    }

    fun setSettingsSearch(query: String) {
        _settingsSearch.value = query
    }

    fun setSettingsNamespace(ns: SettingNamespace?) {
        _settingsNamespace.value = ns
    }

    fun refreshProcesses() {
        viewModelScope.launch {
            _processList.value = engine.getRunningProcesses()
        }
    }

    fun killProcess(pid: Int) {
        viewModelScope.launch {
            executeTerminalCommand("kill -9 $pid")
            delay(400)
            refreshProcesses()
        }
    }
}
