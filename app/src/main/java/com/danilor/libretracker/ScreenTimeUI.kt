package com.danilor.libretracker

//TODO button to edit excluded packages
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

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

@Composable
fun UsageBarChart(usageByHour: Array<Int>?) {
    val barColor = MaterialTheme.colorScheme.primary
    val axisColor = MaterialTheme.colorScheme.onSurface
    val backgroundColor = MaterialTheme.colorScheme.surface

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.3f)
            .padding(16.dp)
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val padding = 40f
        val graphWidth = canvasWidth - 2 * padding
        val graphHeight = canvasHeight - 2 * padding
        val hours = 24
        val maxValue = 60f
        val barWidth = graphWidth / hours

        drawRect(
            color = backgroundColor,
            topLeft = Offset.Zero,
            size = size
        )

        drawLine(
            start = Offset(padding, canvasHeight - padding),
            end = Offset(canvasWidth - padding, canvasHeight - padding),
            color = axisColor,
            strokeWidth = 4f,
            cap = StrokeCap.Round
        )
        drawLine(
            start = Offset(padding, canvasHeight - padding),
            end = Offset(padding, padding),
            color = axisColor,
            strokeWidth = 4f,
            cap = StrokeCap.Round
        )

        val yStep = maxValue / 2
        val yLabelCount = 3
        val yOffsetStep = graphHeight / (yLabelCount - 1)

        for (i in 0 until yLabelCount) {
            val yLabelValue = (yStep * i).toInt()
            val yLabelOffset = canvasHeight - padding - (i * yOffsetStep)
            drawContext.canvas.nativeCanvas.drawText(
                yLabelValue.toString(),
                padding - 30f,
                yLabelOffset,
                android.graphics.Paint().apply {
                    color = axisColor.toArgb()
                    textSize = 24f
                }
            )
        }

        if (usageByHour != null) {
            for (i in usageByHour.indices) {
                val barHeight = (usageByHour[i] / maxValue) * graphHeight
                val barLeft = padding + i * barWidth
                val barRight = barLeft + barWidth * 0.8f
                val barBottom = canvasHeight - padding
                val barTop = barBottom - barHeight

                drawRect(
                    color = barColor,
                    topLeft = Offset(barLeft, barTop),
                    size = Size(barRight - barLeft, barHeight)
                )
            }
        }

        for (i in 0 until hours step 3) {
            val xLabelValue = i.toString()
            val xLabelOffset = padding + i * barWidth + barWidth / 2
            drawContext.canvas.nativeCanvas.drawText(
                xLabelValue,
                xLabelOffset,
                canvasHeight - padding + 20f,
                android.graphics.Paint().apply {
                    color = axisColor.toArgb()
                    textSize = 24f
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )
        }
    }
}

@Composable
fun AppUsageCard(usageByApp: List<AppUsageInfo>?, context: Context) {
    if (usageByApp.isNullOrEmpty()) return

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .height(200.dp)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(usageByApp.sorted()) { appUsage ->
            if (!ExcludedPackagesManager.getExcludedPackages().contains(appUsage.appName)) {
                AppUsageItem(appUsage, context)
            }
        }
    }
}

@Composable
fun AppUsageItem(appUsage: AppUsageInfo, context: Context) {
    val packageName = appUsage.appName
    val appName = getAppName(context, packageName)
    val usageTime = appUsage.usageInMinutes
    val appIcon = getAppIcon(context, packageName)

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
            AppIconDisplay(appIcon)

            Spacer(modifier = Modifier.width(8.dp))

            AppInfoDisplay(appName, usageTime)

            IconButton(onClick = {
                ExcludedPackagesManager.removePackageFromExclude(packageName)
            }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Options"
                )
            }
        }
    }
}

@Composable
fun AppIconDisplay(drawable: Drawable?) {
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
}

@Composable
fun AppInfoDisplay(appName: String, usageTime: Int) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = appName,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = String.format("%02dh %02dm", usageTime / 60, usageTime % 60),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

fun getAppName(context: Context, packageName: String): String {
    return try {
        val applicationInfo =
            context.packageManager.getApplicationInfo(packageName, 0)
        val label = context.packageManager.getApplicationLabel(applicationInfo).toString()
        label.ifEmpty { packageName }
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        packageName
    }
}

fun getAppIcon(context: Context, packageName: String): Drawable? {
    return try {
        context.packageManager.getApplicationIcon(packageName)
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        null
    }
}