package com.danilor.libretracker.view

import android.content.Context
import android.content.pm.PackageInfo
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danilor.libretracker.managers.ExcludedPackagesManager
import com.danilor.libretracker.view.usage_view.AppIconDisplay
import com.danilor.libretracker.view.usage_view.getAppIcon
import com.danilor.libretracker.view.usage_view.getAppName

@Composable
fun ExcludePackagesView(context: Context, onNavigateToScreenTimeUi: () -> Unit) {
    val packageManager = context.packageManager
    val packages = remember { packageManager.getInstalledPackages(0) }.filterNot {
        ExcludedPackagesManager.getDefaultExcludedPackages().contains(it.packageName)
    }.sortedBy { getAppName(context, it.packageName) }

    Log.d("E_PP", "++++")
    for (pack in packages) {
        Log.d("E_PP", pack.packageName)
    }
    Log.d("E_PP", "----")

    val excludedPackages = remember {
        mutableStateMapOf<String, Boolean>().apply {
            packages.forEach {
                put(
                    it.packageName,
                    ExcludedPackagesManager.getAllExcludedPackages().contains(it.packageName)
                )
            }
        }
    }

    val allExcluded by remember { derivedStateOf { excludedPackages.values.all { it } } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Excluded Packages",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    val newState = !allExcluded
                    excludedPackages.keys.forEach { packageName ->
                        excludedPackages[packageName] = newState
                        if (newState) ExcludedPackagesManager.addPackageToExclude(packageName)
                        else ExcludedPackagesManager.removePackageFromExclude(packageName)
                    }
                }, colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ), modifier = Modifier.size(width = 120.dp, height = 48.dp)
            ) {
                Text(if (allExcluded) "Include All" else "Exclude All")
            }
        }

        val listState = rememberLazyListState()
        LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
            items(packages) { packageInfo ->
                val isExcluded = excludedPackages[packageInfo.packageName] ?: false
                PackageCard(context = context,
                    packageInfo = packageInfo,
                    isExcluded = isExcluded,
                    onCheckedChange = { checked ->
                        excludedPackages[packageInfo.packageName] = checked
                        if (checked) ExcludedPackagesManager.addPackageToExclude(packageInfo.packageName)
                        else ExcludedPackagesManager.removePackageFromExclude(packageInfo.packageName)
                    })
            }
        }
    }
}

@Composable
fun PackageCard(
    context: Context,
    packageInfo: PackageInfo,
    isExcluded: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val packageName = packageInfo.packageName
    val appName = getAppName(context, packageName)
    val appIcon = getAppIcon(context, packageName)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppIconDisplay(appIcon)
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = appName,
            modifier = Modifier.weight(1f),
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Checkbox(
            checked = isExcluded, onCheckedChange = onCheckedChange
        )
    }
}
