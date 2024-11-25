import android.content.Context
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.size
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.danilor.libretracker.managers.UsageTimeManager
import com.danilor.libretracker.ui.theme.GlanceColors
import java.time.LocalDateTime

class SimpleUsageWidget : GlanceAppWidget() {
    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            Column(
                modifier = GlanceModifier.fillMaxSize().background(GlanceColors.background),
                horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
                verticalAlignment = Alignment.Vertical.CenterVertically
            ) {
                Text(
                    text = "Usage Time",
                    style = TextStyle(color = GlanceColors.secondary),
                    modifier = GlanceModifier.size(32.dp).fillMaxWidth(),
                )
                val usageInfo =
                    UsageTimeManager.getDailyUsageTimeInMinutes(context, LocalDateTime.now())
                val usageText = String.format(
                    "%02d:%02d", usageInfo.totalUsage / 60, usageInfo.totalUsage % 60
                )
                Text(
                    text = usageText,
                    style = TextStyle(color = GlanceColors.primary),
                    modifier = GlanceModifier.size(42.dp).fillMaxWidth()
                )
            }
        }
    }
}
