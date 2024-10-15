package com.danilor.libretracker

import android.app.usage.UsageStatsManager
import android.content.Context
import android.util.Log
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId

object UsageTimeManager {
    fun getDailyUsageTimeInMinutes(context: Context, date: LocalDateTime): Long {
        val startDate =
            date.toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant()
                .toEpochMilli()
        val endDate =
            date.toLocalDate().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
                .toEpochMilli()

        val usageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val stats = usageStatsManager.queryAndAggregateUsageStats(startDate, endDate)

        val excludedPackages = ExcludedPackagesManager.getExcludedPackages()
        var totalMillis = 0L

        Log.d("USAGE INFO DEBUG", "----------BEGIN")
        Log.d("USAGE INFO DEBUG", "start time: $startDate")
        Log.d("USAGE INFO DEBUG", "end time: $endDate")

        for ((packageName, usageStats) in stats) {
            if (!excludedPackages.contains(packageName)) {
                Log.d("USAGE INFO DEBUG", "$packageName ${usageStats.totalTimeInForeground}")
                totalMillis += usageStats.totalTimeInForeground
            }
        }

        Log.d("USAGE INFO DEBUG", "----------END")

        return Duration.ofMillis(totalMillis).toMinutes()
    }

}