package com.danilor.libretracker

import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId

object UsageTimeManager {

    @RequiresApi(Build.VERSION_CODES.O)
    fun GetDailyUsageTimeInMinutes(context: Context, date: LocalDateTime): Long {
        val start =
            date.toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val end = date.toLocalDate().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
            .toEpochMilli()

        val usageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val stats = usageStatsManager.queryAndAggregateUsageStats(start, end)

        val total = Duration.ofMillis(stats.values.map { it.totalTimeInForeground }.sum())
        Log.d("XDDD", "YOU SPENT ${total.toMinutes()} mins.")
        return total.toMinutes()
    }

}