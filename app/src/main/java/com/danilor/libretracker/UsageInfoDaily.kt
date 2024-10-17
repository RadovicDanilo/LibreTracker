package com.danilor.libretracker

class UsageInfoDaily(
    var usageByHour: Array<Int>,
    var usageByApp: List<AppUsageInfo>,
    var totalUsage: Long
) {
    override fun toString(): String {
        return "UsageInfoDaily(usageByHour=${usageByHour.contentToString()} \ntotalUsage=$totalUsage)"
    }
}