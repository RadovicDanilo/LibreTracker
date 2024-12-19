package com.radovicdanilo.libretracker.view.widgets

import android.content.Context
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.radovicdanilo.libretracker.managers.UsageTimeManager
import com.radovicdanilo.libretracker.ui.theme.GlanceColors
import java.time.LocalDateTime

class SimpleUsageWidget : GlanceAppWidget() {
    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            Column(
                modifier = GlanceModifier.fillMaxSize().background(GlanceColors.background)
                    .padding(16.dp),
                horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
                verticalAlignment = Alignment.Vertical.CenterVertically
            ) {
                Text(
                    text = "Usage Time", style = TextStyle(
                        color = GlanceColors.secondary, fontSize = 16.sp
                    )
                )
                Spacer(modifier = GlanceModifier.height(Dp(5f)))
                val usageInfo =
                    UsageTimeManager.getDailyUsageTimeInMinutes(context, LocalDateTime.now())
                val usageText = String.format(
                    "%02d:%02d", usageInfo.totalUsage / 60, usageInfo.totalUsage % 60
                )
                Text(
                    text = usageText, style = TextStyle(
                        color = GlanceColors.primary, fontSize = 32.sp
                    )
                )
            }
        }
    }
}
