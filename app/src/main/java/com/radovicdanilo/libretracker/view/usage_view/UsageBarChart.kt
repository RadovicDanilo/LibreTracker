package com.radovicdanilo.libretracker.view.usage_view

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp

@Composable
fun UsageBarChart(usageTime: Array<Int>?) {
    if (usageTime.isNullOrEmpty())
        return

    val usageOrdered = mutableListOf<Int>()
    usageOrdered.add(usageTime[23])
    usageOrdered.addAll(usageTime)
    usageOrdered.remove(24)

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
                Paint().apply {
                    color = axisColor.toArgb()
                    textSize = 24f
                }
            )
        }

        for (i in usageOrdered.indices) {
            val barHeight = (usageOrdered[i] / maxValue) * graphHeight
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

        for (i in 0 until hours step 3) {
            val xLabelValue = i.toString()
            val xLabelOffset = padding + i * barWidth + barWidth / 2
            drawContext.canvas.nativeCanvas.drawText(
                xLabelValue,
                xLabelOffset,
                canvasHeight - padding + 20f,
                Paint().apply {
                    color = axisColor.toArgb()
                    textSize = 24f
                    textAlign = Paint.Align.CENTER
                }
            )
        }
    }
}