package com.danilor.libretracker

data class AppUsageInfo(
    val appName: String,
    val appIcon: String,
    var usageInMinutes: Int
) : Comparable<AppUsageInfo> {
    override fun compareTo(other: AppUsageInfo): Int {
        return other.usageInMinutes.compareTo(this.usageInMinutes)
    }
}
