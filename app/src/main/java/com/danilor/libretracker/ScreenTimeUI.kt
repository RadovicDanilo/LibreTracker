package com.danilor.libretracker

//TODO show a list of most used apps
//TODO button to edit excluded packages

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mahmoud.composecharts.barchart.BarChart
import com.mahmoud.composecharts.barchart.BarChartEntity

@Composable
fun ScreenTimeUI(viewModel: ScreenTimeViewModel = viewModel()) {
    val context = LocalContext.current

    val usageInfo = viewModel.usageInfo

    LaunchedEffect(Unit) {
        viewModel.fetchUsageInfo(context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Total Screen Time Today",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = String.format(
                "%02d:%02d", (usageInfo?.totalUsage ?: 0) / 60, (usageInfo?.totalUsage ?: 0) % 60
            ),
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                viewModel.fetchUsageInfo(context)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Icon(
                imageVector = Icons.Filled.Refresh,
                contentDescription = "Refresh",
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

        UsageBarChart(usageByHour = usageInfo?.usageByHour)
        AppUsageCard(usageByApp = usageInfo?.usageByApp, context = context)
    }
}

//TODO fix height
@Composable
fun UsageBarChart(usageByHour: Array<Int>?) {
    if (usageByHour == null)
        return

    val barChartData = mutableListOf<BarChartEntity>()
    for (i in 0..23) {
        barChartData.add(
            BarChartEntity(
                usageByHour[i].toFloat(),
                MaterialTheme.colorScheme.primary,
                if (i % 4 == 0) "$i" else ""
            )
        )
    }

    val verticalAxisValues = listOf(0.0f, 30.0f, 60.0f)

    BarChart(
        barChartData = barChartData,
        verticalAxisValues = verticalAxisValues,
        axisColor = MaterialTheme.colorScheme.onBackground,
        verticalAxisLabelColor = MaterialTheme.colorScheme.onBackground,
        verticalAxisLabelFontSize = TextUnit(12f, TextUnitType.Sp),
        horizontalAxisLabelColor = MaterialTheme.colorScheme.onBackground,
        horizontalAxisLabelFontSize = TextUnit(12f, TextUnitType.Sp),
        paddingBetweenBars = 1.dp,
        isShowVerticalAxis = true,
        isShowHorizontalLines = true,
    )
}

@Composable
fun AppUsageCard(usageByApp: List<AppUsageInfo>?, context: Context) {
    if (usageByApp == null) return

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .height(200.dp)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(usageByApp.sorted()) { appUsage ->

            val packageName = appUsage.appName
            val appName = try {
                val applicationInfo =
                    context.applicationContext.packageManager.getApplicationInfo(packageName, 0)
                val label =
                    context.applicationContext.packageManager.getApplicationLabel(applicationInfo)
                        .toString()
                if (label.isEmpty()) packageName else label
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                packageName
            }

            val usageTime = appUsage.usageInMinutes

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val drawable = try {
                        context.applicationContext.packageManager.getApplicationIcon(appUsage.appName)
                    } catch (e: PackageManager.NameNotFoundException) {
                        e.printStackTrace()
                        null
                    }

                    val bitmap = (drawable as? BitmapDrawable)?.bitmap

                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier.size(40.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Android,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = appName,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "${usageTime}m",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }
    }
}