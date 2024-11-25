package com.danilor.libretracker.managers

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import com.danilor.libretracker.model.AppUsageInfo
import com.danilor.libretracker.model.UsageInfoDaily
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

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

    //TODO enhance this
    fun getDailyUsageTimeInMinutes(context: Context, date: LocalDateTime): UsageInfoDaily {
        val usageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val startDate =
            date.toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endDate =
            date.toLocalDate().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
                .toEpochMilli()
        val excludedPackages = ExcludedPackagesManager.getAllExcludedPackages()

        val stateMap = HashMap<String, AppStateModel>()
        val eventList = usageStatsManager.queryEvents(startDate, endDate)

        val usageByHour = Array(24) { 0 }

        while (eventList.hasNextEvent()) {
            val event = UsageEvents.Event()
            eventList.getNextEvent(event)

            if (excludedPackages.contains(event.packageName)) continue
            if (event.className == null) continue

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
                        val usageDuration = event.timeStamp - classState.startTime

                        packageState.totalTime += usageDuration

                        val hour = LocalDateTime.ofEpochSecond(
                            event.timeStamp / 1000,
                            0,
                            ZoneOffset.UTC
                        ).hour
                        usageByHour[hour] += (usageDuration / 60000).toInt()

                        classState.isResume = false
                    }
                }
            }
        }

        val appUsageDetails = stateMap.map { (packageName, appState) ->
            AppUsageInfo(
                appName = packageName,
                appIcon = packageName, //TODO get icon
                usageInMinutes = (appState.totalTime / 60000).toInt()
            )
        }

        val totalUsage = stateMap.values.sumOf { it.totalTime } / 60000

        return UsageInfoDaily(usageByHour, appUsageDetails, totalUsage)
    }
}
