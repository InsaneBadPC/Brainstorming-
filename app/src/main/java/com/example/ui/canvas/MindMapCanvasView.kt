package com.example.ui.canvas

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MindEdge
import com.example.data.model.MindNode
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardElevated
import com.example.ui.theme.TechBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import kotlin.math.roundToInt

@Composable
fun MindMapCanvasView(
    nodes: List<MindNode>,
    edges: List<MindEdge>,
    selectedNodeId: String?,
    scale: Float,
    panOffset: Offset,
    onPanDelta: (Offset) -> Unit,
    onZoomChange: (zoomFactor: Float, centroid: Offset?) -> Unit,
    onResetZoom: () -> Unit,
    onAutoLayout: () -> Unit,
    onNodeSelect: (String) -> Unit,
    onCanvasTap: () -> Unit,
    onNodeDrag: (String, Float, Float) -> Unit,
    onAddChild: (String) -> Unit,
    onAiDeepen: (MindNode) -> Unit,
    onGenerateDiagram: (MindNode) -> Unit,
    onEditNode: (MindNode) -> Unit,
    onDeleteNode: (String) -> Unit,
    onGenerateWireframe: (MindNode) -> Unit = {},
    onOpenFeaturesGuide: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current

    ZoomablePanContainer(
        scale = scale,
        panOffset = panOffset,
        onZoomChange = onZoomChange,
        onPanDelta = onPanDelta,
        onCanvasTap = onCanvasTap,
        onDoubleTap = { onResetZoom() },
        minScale = 0.25f,
        maxScale = 3.5f,
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .testTag("mind_map_canvas")
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val canvasWidthPx = constraints.maxWidth.toFloat()
        val canvasHeightPx = constraints.maxHeight.toFloat()
        val centerX = canvasWidthPx / 2f
        val centerY = canvasHeightPx / 2f

        val nodeMap = remember(nodes) { nodes.associateBy { it.id } }

        // 1. Scalable & Pannable World Layer (GPU transformed via graphicsLayer)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    translationX = panOffset.x
                    translationY = panOffset.y
                    transformOrigin = TransformOrigin(0.5f, 0.5f)
                }
        ) {
            // A. Background Grid & Connection Bezier Lines
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                val gridStep = 44.dp.toPx()
                // Infinite dot grid centered on origin
                val startX = (centerX % gridStep) - gridStep * 40
                val endX = canvasWidthPx + gridStep * 40
                val startY = (centerY % gridStep) - gridStep * 40
                val endY = canvasHeightPx + gridStep * 40

                var gx = startX
                while (gx < endX) {
                    var gy = startY
                    while (gy < endY) {
                        drawCircle(
                            color = TechBorder.copy(alpha = 0.45f),
                            radius = 1.3.dp.toPx(),
                            center = Offset(gx, gy)
                        )
                        gy += gridStep
                    }
                    gx += gridStep
                }

                // Smooth curved Bezier branches
                nodes.forEach { node ->
                    val parent = node.parentId?.let { nodeMap[it] }
                    if (parent != null) {
                        val pX = centerX + with(density) { parent.xPos.dp.toPx() }
                        val pY = centerY + with(density) { parent.yPos.dp.toPx() }

                        val cX = centerX + with(density) { node.xPos.dp.toPx() }
                        val cY = centerY + with(density) { node.yPos.dp.toPx() }

                        val branchColor = node.category.defaultColor
                        val isBranchSelected = node.id == selectedNodeId || parent.id == selectedNodeId

                        val path = Path().apply {
                            moveTo(pX, pY)
                            val midX = (pX + cX) / 2f
                            cubicTo(
                                midX, pY,
                                midX, cY,
                                cX, cY
                            )
                        }

                        // Outer glowing halo
                        if (isBranchSelected) {
                            drawPath(
                                path = path,
                                color = branchColor.copy(alpha = 0.4f),
                                style = Stroke(
                                    width = 8.dp.toPx(),
                                    cap = StrokeCap.Round
                                )
                            )
                        }

                        // Main branch path
                        drawPath(
                            path = path,
                            color = branchColor.copy(alpha = if (isBranchSelected) 0.95f else 0.65f),
                            style = Stroke(
                                width = if (isBranchSelected) 3.5.dp.toPx() else 2.2.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        )

                        // Anchor circle at node destination
                        drawCircle(
                            color = branchColor,
                            radius = if (isBranchSelected) 4.5.dp.toPx() else 3.2.dp.toPx(),
                            center = Offset(cX, cY)
                        )
                    }
                }
            }

            // B. Node Cards placed in canvas coordinates
            nodes.forEach { node ->
                val cardWidthDp = if (node.parentId == null) 230.dp else 200.dp
                val halfWidthPx = with(density) { cardWidthDp.toPx() / 2f }
                val halfHeightPx = with(density) { 65.dp.toPx() }

                val nodeXPx = with(density) { node.xPos.dp.toPx() }
                val nodeYPx = with(density) { node.yPos.dp.toPx() }

                val cardPixelX = (centerX + nodeXPx - halfWidthPx).roundToInt()
                val cardPixelY = (centerY + nodeYPx - halfHeightPx).roundToInt()

                val isSelected = node.id == selectedNodeId
                val hasChildren = nodes.any { it.parentId == node.id }

                Box(
                    modifier = Modifier
                        .offset { IntOffset(cardPixelX, cardPixelY) }
                ) {
                    MindNodeCard(
                        node = node,
                        isSelected = isSelected,
                        hasChildren = hasChildren,
                        onSelect = { onNodeSelect(node.id) },
                        onDragDelta = { dx, dy ->
                            val dxDp = with(density) { dx.toDp().value }
                            val dyDp = with(density) { dy.toDp().value }
                            onNodeDrag(node.id, dxDp, dyDp)
                        },
                        onAddChild = { onAddChild(node.id) },
                        onAiDeepen = { onAiDeepen(node) },
                        onGenerateDiagram = { onGenerateDiagram(node) },
                        onEdit = { onEditNode(node) },
                        onDelete = { onDeleteNode(node.id) },
                        onToggleExpand = {},
                        onToggleCompleted = {},
                        onGenerateWireframe = { onGenerateWireframe(node) }
                    )
                }
            }
        }

        // 2. Floating On-Screen Canvas Controls (Zoom In/Out, 100%, Auto-Layout, Recenter)
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = SlateCard.copy(alpha = 0.94f),
            border = androidx.compose.foundation.BorderStroke(1.dp, TechBorder),
            shadowElevation = 10.dp,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 12.dp)
                .testTag("canvas_zoom_controls")
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(5.dp)
            ) {
                // Auto-Layout Button
                IconButton(
                    onClick = { onAutoLayout() },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Uspořádat mapu",
                        tint = NeonCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Recenter / Fit Button
                IconButton(
                    onClick = { onResetZoom() },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CenterFocusStrong,
                        contentDescription = "Vycentrovat plátno",
                        tint = TextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Zoom In Button (+)
                IconButton(
                    onClick = { onZoomChange(1.25f, null) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Přiblížit",
                        tint = TextPrimary,
                        modifier = Modifier.size(19.dp)
                    )
                }

                // Zoom Percentage Indicator Badge (tap to reset to 100%)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SlateCardElevated,
                    modifier = Modifier
                        .clickable { onResetZoom() }
                        .padding(vertical = 2.dp)
                ) {
                    Text(
                        text = "${(scale * 100).roundToInt()}%",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }

                // Zoom Out Button (-)
                IconButton(
                    onClick = { onZoomChange(0.8f, null) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Oddálit",
                        tint = TextPrimary,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }
        }


    }
}
}
