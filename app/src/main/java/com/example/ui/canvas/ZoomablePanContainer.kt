package com.example.ui.canvas

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag

/**
 * Custom Compose component that handles multi-touch zoom (pinch-to-zoom),
 * pan, tap, and double-tap gestures using [Modifier.pointerInput].
 *
 * Designed specifically for infinite or scalable mind map canvases to prevent
 * gesture conflicts between canvas navigation and child item interactions.
 */
@Composable
fun ZoomablePanContainer(
    scale: Float,
    panOffset: Offset,
    onZoomChange: (zoomFactor: Float, centroid: Offset) -> Unit,
    onPanDelta: (Offset) -> Unit,
    onCanvasTap: () -> Unit,
    onDoubleTap: (Offset) -> Unit = {},
    minScale: Float = 0.2f,
    maxScale: Float = 4.0f,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    // Tracks double-tap timing and position
    var lastTapTime by remember { mutableLongStateOf(0L) }
    var lastTapOffset by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(scale, minScale, maxScale) {
                detectZoomAndPanGestures(
                    onZoomChange = { zoomFactor, centroid ->
                        val targetScale = (scale * zoomFactor).coerceIn(minScale, maxScale)
                        val effectiveFactor = targetScale / scale
                        if (effectiveFactor != 1.0f) {
                            onZoomChange(effectiveFactor, centroid)
                        }
                    },
                    onPanDelta = onPanDelta,
                    onTap = { tapOffset ->
                        val now = System.currentTimeMillis()
                        val diffTime = now - lastTapTime
                        val diffDistance = (tapOffset - lastTapOffset).getDistance()

                        if (diffTime < 320 && diffDistance < 40f) {
                            // Double tap triggered
                            onDoubleTap(tapOffset)
                            lastTapTime = 0L
                        } else {
                            // Single tap
                            lastTapTime = now
                            lastTapOffset = tapOffset
                            onCanvasTap()
                        }
                    }
                )
            }
            .testTag("zoomable_pan_container")
    ) {
        content()
    }
}

/**
 * Dedicated pointer input processor using [awaitEachGesture] to cleanly decouple
 * multi-touch transformation (pinch zoom & pan) from single-touch panning and taps.
 */
private suspend fun PointerInputScope.detectZoomAndPanGestures(
    onZoomChange: (zoomFactor: Float, centroid: Offset) -> Unit,
    onPanDelta: (Offset) -> Unit,
    onTap: (Offset) -> Unit
) {
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)
        val downPosition = down.position
        val downTime = System.currentTimeMillis()

        var totalPan = Offset.Zero
        var hasMultiTouch = false
        var hasTransformed = false

        do {
            val event = awaitPointerEvent()
            val pointerCount = event.changes.size

            if (pointerCount >= 2) {
                hasMultiTouch = true
                val zoomChange = event.calculateZoom()
                val panChange = event.calculatePan()
                val centroid = event.calculateCentroid()

                if (zoomChange != 1.0f || panChange != Offset.Zero) {
                    hasTransformed = true
                    onPanDelta(panChange)
                    if (zoomChange != 1.0f) {
                        onZoomChange(zoomChange, centroid)
                    }
                    event.changes.forEach { it.consume() }
                }
            } else if (pointerCount == 1 && !hasMultiTouch) {
                val panChange = event.calculatePan()
                totalPan += panChange

                // Small slop threshold before committing to canvas pan
                if (totalPan.getDistance() > 6f) {
                    hasTransformed = true
                    onPanDelta(panChange)
                    event.changes.forEach { it.consume() }
                }
            }
        } while (event.changes.any { it.pressed })

        val duration = System.currentTimeMillis() - downTime
        if (!hasTransformed && totalPan.getDistance() < 10f && duration < 350) {
            onTap(downPosition)
        }
    }
}
