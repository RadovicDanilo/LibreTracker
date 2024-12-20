package com.radovicdanilo.libretracker.view.usage_view

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.radovicdanilo.libretracker.managers.ExcludedPackagesManager
import com.radovicdanilo.libretracker.model.AppUsageInfo

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
            if (!ExcludedPackagesManager.getAllExcludedPackages().contains(appUsage.appName)) {
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
        }
    }
}

@Composable
fun AppIconDisplay(drawable: Drawable?) {
    if (drawable is BitmapDrawable) {
        val bitmap = drawable.bitmap
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier.size(40.dp)
        )
    } else if (drawable != null) {
        val painter = rememberDrawablePainter(drawable)
        Image(
            painter = painter,
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
        val applicationInfo = context.packageManager.getApplicationInfo(packageName, 0)
        applicationInfo.loadLabel(context.packageManager).toString().takeIf { it.isNotEmpty() }
            ?: applicationInfo.packageName
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        packageName
    }
}

fun getAppIcon(context: Context, packageName: String): Drawable? = runCatching {
    context.packageManager.getApplicationIcon(packageName)
}.getOrElse {
    null
}