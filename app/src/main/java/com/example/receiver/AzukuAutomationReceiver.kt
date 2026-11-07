package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.service.AzukuCoreEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AzukuAutomationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        Log.d("AzukuAutomation", "Received broadcast action: $action")

        val engine = AzukuCoreEngine(context.applicationContext)

        when (action) {
            "com.aistudio.azuku.EXEC_COMMAND" -> {
                val cmd = intent.getStringExtra("cmd") ?: intent.getStringExtra("command")
                if (!cmd.isNullOrBlank()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val result = engine.executeCommand(cmd)
                        Log.i("AzukuAutomation", "Command executed: $cmd -> exit ${result.exitCode}")
                    }
                    Toast.makeText(context, "Azuku: Executing automation task...", Toast.LENGTH_SHORT).show()
                }
            }
            "com.aistudio.azuku.TOGGLE_SERVICE" -> {
                engine.toggleService()
                Toast.makeText(context, "Azuku: Privileged Service state updated", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
