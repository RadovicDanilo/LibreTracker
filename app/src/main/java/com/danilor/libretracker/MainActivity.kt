package com.danilor.libretracker

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.danilor.libretracker.managers.ExcludedPackagesManager
import com.danilor.libretracker.nav.AppNavigation
import com.danilor.libretracker.ui.theme.LibreTrackerTheme
import com.danilor.libretracker.view.RequestPermissionView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LibreTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ExcludedPackagesManager.initialize(applicationContext)

                    var hasPermission by remember { mutableStateOf(checkUsageStatsPermission()) }

                    if (hasPermission) {
                        AppNavigation()
                    } else {
                        RequestPermissionView(onRequestPermission = {
                            openUsageAccessSettings()
                            hasPermission = checkUsageStatsPermission()
                        })
                    }
                }
            }
        }
    }

    private fun checkUsageStatsPermission(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun openUsageAccessSettings() {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        startActivity(intent)
    }
}
