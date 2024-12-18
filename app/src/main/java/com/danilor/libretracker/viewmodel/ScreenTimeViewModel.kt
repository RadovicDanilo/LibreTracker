package com.danilor.libretracker.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danilor.libretracker.managers.UsageTimeManager
import com.danilor.libretracker.model.UsageInfoDaily
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class ScreenTimeViewModel : ViewModel() {
    var usageInfo by mutableStateOf<UsageInfoDaily?>(null)
        private set

    fun fetchUsageInfo(context: Context, dateTime: LocalDateTime) {
        viewModelScope.launch {
            val usage = withContext(Dispatchers.IO) {
                UsageTimeManager.getDailyUsageTimeInMinutes(context, dateTime)
            }
            usageInfo = usage
        }
    }
}
