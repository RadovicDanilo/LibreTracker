package com.danilor.libretracker.view.usage_view

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danilor.libretracker.viewmodel.ScreenTimeViewModel
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun DayOfTheWeekSelector(
    initialDateTime: LocalDateTime,
    context: Context,
    viewModel: ScreenTimeViewModel,
    onDateSelected: (LocalDateTime) -> Unit
) {
    val currentWeekStart = remember { mutableStateOf(getStartOfWeek(initialDateTime)) }
    val selectedDate = remember { mutableStateOf(initialDateTime) }

    LaunchedEffect(selectedDate.value) {
        viewModel.fetchUsageInfo(context = context, selectedDate.value)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = selectedDate.value.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.size(12.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            NavigationButton(icon = Icons.AutoMirrored.Filled.ArrowBackIos,
                contentDescription = "Previous Week",
                onClick = {
                    currentWeekStart.value = currentWeekStart.value.minus(1, ChronoUnit.WEEKS)
                    selectedDate.value = currentWeekStart.value
                    onDateSelected(currentWeekStart.value)
                })


            for (i in 0..6) {
                val dayDate = currentWeekStart.value.plusDays(i.toLong())
                DayButton(
                    dateTime = dayDate,
                    isSelected = dayDate.toLocalDate() == selectedDate.value.toLocalDate(),
                    onClick = {
                        if (LocalDateTime.now().toLocalDate() >= dayDate.toLocalDate()) {
                            selectedDate.value = dayDate
                            onDateSelected(dayDate)
                        }
                    },
                )
            }

            NavigationButton(icon = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "Next Week",
                onClick = {
                    if (LocalDateTime.now().toLocalDate() >= currentWeekStart.value.plus(
                            1, ChronoUnit.WEEKS
                        ).toLocalDate()
                    ) {
                        currentWeekStart.value = currentWeekStart.value.plus(1, ChronoUnit.WEEKS)
                        selectedDate.value = currentWeekStart.value
                        onDateSelected(currentWeekStart.value)
                    }
                })
        }
    }
}

@Composable
fun DayButton(
    dateTime: LocalDateTime, isSelected: Boolean, onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .size(36.dp)
            .padding(2.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
        ),
        shape = MaterialTheme.shapes.small,
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = dateTime.dayOfWeek.name.substring(0, 1),
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
            maxLines = 1
        )
    }
}

@Composable
fun NavigationButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent, contentColor = MaterialTheme.colorScheme.primary
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

fun getStartOfWeek(dateTime: LocalDateTime): LocalDateTime {
    var date = dateTime
    while (date.dayOfWeek != DayOfWeek.MONDAY) {
        date = date.minusDays(1)
    }
    return date
}
