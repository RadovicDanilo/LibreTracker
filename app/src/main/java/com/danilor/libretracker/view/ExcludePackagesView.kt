package com.danilor.libretracker.view

import android.content.Context
import android.content.pm.PackageInfo
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
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danilor.libretracker.managers.ExcludedPackagesManager

@Composable
fun ExcludePackagesView(context: Context, onNavigateToScreenTimeUi: () -> Unit) {
    val packageManager = context.applicationContext.packageManager
    val packages = packageManager.getInstalledPackages(0)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Excluded Packages",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(packages) { packageInfo ->
                val isExcluded: MutableState<Boolean> =
                    remember {
                        mutableStateOf(
                            ExcludedPackagesManager.getExcludedPackages()
                                .contains(packageInfo.packageName)
                        )
                    }
                PackageCard(
                    context = context,
                    packageInfo = packageInfo,
                    isExcluded = isExcluded.value,
                    onCheckedChange = { checked ->
                        if (checked) {
                            ExcludedPackagesManager.addPackageToExclude(packageInfo.packageName)
                        } else {
                            ExcludedPackagesManager.removePackageFromExclude(packageInfo.packageName)
                        }
                        isExcluded.value = checked
                    }
                )
            }
        }
        Button(
            onClick = onNavigateToScreenTimeUi,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colors.secondary,
                contentColor = MaterialTheme.colors.onSecondary
            ),
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.FilterList,
                contentDescription = "Excluded Packages",
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colors.onSecondary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Excluded Packages")
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
        Text(
            text = appName,
            modifier = Modifier.weight(1f),
            fontSize = 16.sp
        )
        Checkbox(
            checked = isExcluded,
            onCheckedChange = onCheckedChange
        )
    }
}
