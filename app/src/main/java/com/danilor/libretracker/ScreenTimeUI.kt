package com.danilor.libretracker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.time.LocalDateTime

@Composable
fun ScreenTimeUI() {
    val context = LocalContext.current
    var totalTimeSpent by remember { mutableStateOf(0L) }

    LaunchedEffect(key1 = Unit) {
        totalTimeSpent = UsageTimeManager.getDailyUsageTimeInMinutes(
            context,
            LocalDateTime.now().toLocalDate().atStartOfDay()
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Total screen time today:")
        Spacer(modifier = Modifier.height(16.dp))

        Text("${totalTimeSpent / 60}:${totalTimeSpent % 60}")

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            totalTimeSpent = UsageTimeManager.getDailyUsageTimeInMinutes(
                context,
                LocalDateTime.now().toLocalDate().atStartOfDay()
            )
        }) {
            Text("Refresh")
        }
    }
}
