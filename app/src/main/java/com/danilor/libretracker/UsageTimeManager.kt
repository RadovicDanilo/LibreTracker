package com.danilor.libretracker

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import java.time.LocalDateTime
import java.time.ZoneId

object UsageTimeManager {
    data class AppStateModel(
        val packageName: String,
        var totalTime: Long = 0,
        var classMap: HashMap<String, BoolObj> = HashMap()
    )

    data class BoolObj(
        var startTime: Long,
        var isResume: Boolean
    )

    fun getDailyUsageTimeInMinutes(context: Context, date: LocalDateTime): Long {
        val usageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val startDate =
            date.toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endDate =
            date.toLocalDate().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
                .toEpochMilli()
        val excludedPackages = ExcludedPackagesManager.getExcludedPackages()

        val stateMap = HashMap<String, AppStateModel>()
        val eventList = usageStatsManager.queryEvents(startDate, endDate)

        while (eventList.hasNextEvent()) {
            val event = UsageEvents.Event()
            eventList.getNextEvent(event)

            if (excludedPackages.contains(event.packageName)) continue

            val packageState =
                stateMap.getOrPut(event.packageName) { AppStateModel(event.packageName) }
            val classState = packageState.classMap.getOrPut(event.className) { BoolObj(0, false) }

            when (event.eventType) {
                UsageEvents.Event.ACTIVITY_RESUMED -> {
                    classState.startTime = event.timeStamp
                    classState.isResume = true
                }

                UsageEvents.Event.ACTIVITY_STOPPED -> {
                    if (classState.isResume) {
                        packageState.totalTime += event.timeStamp - classState.startTime
                        classState.isResume = false
                    }
                }
            }
        }

        return stateMap.values.sumOf { it.totalTime } / 60000
    }
}
