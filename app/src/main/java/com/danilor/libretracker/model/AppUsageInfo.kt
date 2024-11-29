package com.danilor.libretracker.model

data class AppUsageInfo(
    val appName: String,
    var usageInMinutes: Int
) : Comparable<AppUsageInfo> {
    override fun compareTo(other: AppUsageInfo): Int {
        return other.usageInMinutes.compareTo(this.usageInMinutes)
    }
}
