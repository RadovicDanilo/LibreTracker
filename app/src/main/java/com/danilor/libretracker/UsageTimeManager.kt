package com.danilor.libretracker

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import java.time.LocalDateTime
import java.time.ZoneId

object UsageTimeManager {
    data class AppStateModel(
        val packageName: String,
        var startTime: Long = 0,
        var endTime: Long = 0,
        var totalTime: Long = 0,
        var totalCapacity: Double = 0.0,
        var currentCapacity: Double = 0.0,
        var isAlreadyResume: Boolean = false,
        var className: String = "",
        var classMap: HashMap<String, BoolObj> = HashMap()
    )

    data class BoolObj(
        var startTime: Long,
        var isResume: Boolean
    )

    fun getDailyUsageTimeInMinutes(context: Context, date: LocalDateTime): Long {
        val stateMap = HashMap<String, AppStateModel>()
        val usageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        val startDate =
            date.toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endDate =
            date.toLocalDate().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
                .toEpochMilli()

        val excludedPackages = ExcludedPackagesManager.getExcludedPackages()

        val eventList = usageStatsManager.queryEvents(startDate, endDate)
        while (eventList.hasNextEvent()) {
            val event = UsageEvents.Event()
            eventList.getNextEvent(event)

            if (!excludedPackages.contains(event.packageName)) {
                if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                    val packageCheck = stateMap[event.packageName]
                    if (packageCheck != null) {
                        if (stateMap[event.packageName]!!.classMap[event.className] != null) {
                            stateMap[event.packageName]!!.className = event.className
                            stateMap[event.packageName]!!.startTime = event.timeStamp
                            stateMap[event.packageName]!!.classMap[event.className]!!.startTime =
                                event.timeStamp
                            stateMap[event.packageName]!!.classMap[event.className]!!.isResume =
                                true
                        } else {
                            stateMap[event.packageName]!!.className = event.className
                            stateMap[event.packageName]!!.startTime = event.timeStamp
                            stateMap[event.packageName]!!.classMap[event.className] =
                                BoolObj(event.timeStamp, true)
                        }
                    } else {
                        val appStates = AppStateModel(
                            packageName = event.packageName,
                            className = event.className,
                            startTime = event.timeStamp
                        )
                        appStates.classMap[event.className] = BoolObj(event.timeStamp, true)
                        stateMap[event.packageName] = appStates
                    }
                } else if (event.eventType == UsageEvents.Event.ACTIVITY_STOPPED) {
                    val packageCheck = stateMap[event.packageName]
                    if (packageCheck != null) {
                        if (stateMap[event.packageName]!!.classMap[event.className] != null) {
                            stateMap[event.packageName]!!.totalTime += event.timeStamp - stateMap[event.packageName]!!.classMap[event.className]!!.startTime
                            stateMap[event.packageName]!!.classMap[event.className]!!.isResume =
                                false
                        }
                    }
                }
            }
        }

        return stateMap.values.sumOf { it.totalTime } / 60000
    }
}
