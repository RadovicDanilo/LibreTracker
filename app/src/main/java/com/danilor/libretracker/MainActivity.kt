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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.danilor.libretracker.managers.ExcludedPackagesManager
import com.danilor.libretracker.nav.AppNavigation
import com.danilor.libretracker.ui.theme.LibreTrackerTheme
import com.danilor.libretracker.view.RequestPermissionView
import com.danilor.libretracker.view.widgets.WidgetUpdateWorker
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LibreTrackerTheme {
                scheduleWidgetUpdate(applicationContext)
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    ExcludedPackagesManager.initialize(applicationContext)

                    var hasPermission by remember { mutableStateOf(checkUsageStatsPermission()) }
                    LaunchedEffect(Unit) {
                        while (!hasPermission) {
                            if (checkUsageStatsPermission()) {
                                hasPermission = true
                            }
                            delay(1000)
                        }
                    }

                    if (hasPermission) {
                        AppNavigation()
                    } else {
                        RequestPermissionView(onRequestPermission = {
                            openUsageAccessSettings()
                        })
                    }
                }
            }
        }
    }

    private fun scheduleWidgetUpdate(context: Context) {
        val updateRequest =
            PeriodicWorkRequestBuilder<WidgetUpdateWorker>(1, TimeUnit.MINUTES).build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "WidgetUpdateWork", ExistingPeriodicWorkPolicy.UPDATE, updateRequest
        )
    }

    private fun checkUsageStatsPermission(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun openUsageAccessSettings() {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        startActivity(intent)
    }
}
