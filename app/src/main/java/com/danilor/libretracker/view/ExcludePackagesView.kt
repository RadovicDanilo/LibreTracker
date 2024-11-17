import android.content.Context
import android.content.pm.PackageInfo
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danilor.libretracker.managers.ExcludedPackagesManager
import com.danilor.libretracker.view.AppIconDisplay
import com.danilor.libretracker.view.getAppIcon
import com.danilor.libretracker.view.getAppName

@Composable
fun ExcludePackagesView(context: Context) {
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
