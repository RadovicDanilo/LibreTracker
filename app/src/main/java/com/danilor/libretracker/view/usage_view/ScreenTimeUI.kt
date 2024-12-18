package com.danilor.libretracker.view.usage_view

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.danilor.libretracker.viewmodel.ScreenTimeViewModel
import java.time.LocalDateTime

@Composable
fun ScreenTimeUI(
    viewModel: ScreenTimeViewModel = viewModel(), onNavigateToExcludedPackages: () -> Unit
) {
    val context = LocalContext.current

    val usageInfo = viewModel.usageInfo

    LaunchedEffect(Unit) {
        viewModel.fetchUsageInfo(context, LocalDateTime.now())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
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
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        DayOfTheWeekSelector(
            initialDateTime = LocalDateTime.now(), context = context, viewModel = viewModel
        )

        UsageBarChart(usageInfo?.usageByHour)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                color = MaterialTheme.colorScheme.onSurface, text = "Usage per app"
            )
            Button(
                onClick = onNavigateToExcludedPackages, colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.FilterList,
                    contentDescription = "Excluded Packages",
                    modifier = Modifier.size(22.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        AppUsageCard(usageByApp = usageInfo?.usageByApp, context = context)
    }
}
