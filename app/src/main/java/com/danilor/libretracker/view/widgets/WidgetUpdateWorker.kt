package com.danilor.libretracker.view.widgets

import SimpleUsageWidget
import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class WidgetUpdateWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val glanceManager = GlanceAppWidgetManager(applicationContext)
        val glanceIds = glanceManager.getGlanceIds(provider = SimpleUsageWidget::class.java)
        glanceIds.forEach { glanceId ->
            SimpleUsageWidget().update(applicationContext, glanceId)
        }
        return Result.success()
    }
}