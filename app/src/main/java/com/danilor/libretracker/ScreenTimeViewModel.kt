package com.danilor.libretracker


import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class ScreenTimeViewModel {
    var usageInfo by mutableStateOf<UsageInfoDaily?>(null)

    fun fetchUsageInfo(context: Context) {
        GlobalScope.launch {
            usageInfo = withContext(Dispatchers.IO) {
                UsageTimeManager.getDailyUsageTimeInMinutes(
                    context,
                    LocalDateTime.now().toLocalDate().atStartOfDay()
                )
            }
        }
    }
}
