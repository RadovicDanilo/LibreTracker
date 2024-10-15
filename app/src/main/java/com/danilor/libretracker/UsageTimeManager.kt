package com.danilor.libretracker

import android.app.usage.UsageStatsManager
import android.content.Context
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId

object UsageTimeManager {
    fun getDailyUsageTimeInMinutes(context: Context, date: LocalDateTime): Long {
        val startDate =
            date.toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endDate =
            date.toLocalDate().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
                .toEpochMilli()

        val usageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val stats = usageStatsManager.queryAndAggregateUsageStats(startDate, endDate)

        val excludedPackages = ExcludedPackagesManager.getExcludedPackages()
        var totalMillis = 0L

        for ((packageName, usageStats) in stats) {
            if (!excludedPackages.contains(packageName)) {
                totalMillis += usageStats.totalTimeInForeground
            }
        }

        return Duration.ofMillis(totalMillis).toMinutes()
    }

}