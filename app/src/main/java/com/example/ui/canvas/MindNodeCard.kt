package com.example.ui.canvas

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AiPersona
import com.example.data.model.MindNode
import com.example.data.model.NodeCategory
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MindNodeCard(
    node: MindNode,
    isSelected: Boolean,
    hasChildren: Boolean,
    onSelect: () -> Unit,
    onDragDelta: (Float, Float) -> Unit,
    onAddChild: () -> Unit,
    onAiDeepen: () -> Unit,
    onGenerateDiagram: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleExpand: () -> Unit,
    onToggleCompleted: () -> Unit,
    onGenerateWireframe: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val nodeColor = node.category.defaultColor
    val isRoot = node.parentId == null
    val persona = AiPersona.fromId(node.authorPersona)
    val flowSteps = remember(node.diagramData) { node.getFlowSteps() }

    val borderColor = if (isSelected) {
        persona?.color ?: nodeColor
    } else {
        (persona?.color ?: nodeColor).copy(alpha = 0.5f)
    }

    val glowBrush = Brush.verticalGradient(
        colors = listOf(
            (persona?.color ?: nodeColor).copy(alpha = if (isSelected) 0.28f else 0.09f),
            SlateCard
        )
    )

    Surface(
        shape = RoundedCornerShape(if (isRoot) 20.dp else 16.dp),
        color = SlateCard,
        shadowElevation = if (isSelected) 12.dp else 4.dp,
        modifier = modifier
            .widthIn(min = if (isRoot) 210.dp else 180.dp, max = if (flowSteps.isNotEmpty()) 320.dp else 260.dp)
            .shadow(
                elevation = if (isSelected) 10.dp else 2.dp,
                shape = RoundedCornerShape(if (isRoot) 20.dp else 16.dp),
                spotColor = persona?.color ?: nodeColor
            )
            .border(
                width = if (isSelected) 2.5.dp else 1.2.dp,
                color = borderColor,
                shape = RoundedCornerShape(if (isRoot) 20.dp else 16.dp)
            )
            .clip(RoundedCornerShape(if (isRoot) 20.dp else 16.dp))
            .background(glowBrush)
            .clickable { onSelect() }
            .pointerInput(node.id) {
                detectDragGestures(
                    onDragStart = { onSelect() },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        onDragDelta(dragAmount.x, dragAmount.y)
                    }
                )
            }
            .testTag("node_${node.id}")
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Header Row: Category Pill & Persona Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Category Chip
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(nodeColor.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(nodeColor, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = node.category.displayName,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = nodeColor
                    )
                }

                // Persona Author Badge (Nova, Vortex, Atlas)
                if (persona != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(persona.color.copy(alpha = 0.22f), RoundedCornerShape(6.dp))
                            .border(0.8.dp, persona.color.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = persona.emoji,
                            fontSize = 10.sp
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = persona.displayName,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = persona.color
                        )
                    }
                } else if (node.isAiGenerated) {
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "AI",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Title
            Text(
                text = node.title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontSize = if (isRoot) 15.sp else 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                ),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            // Details
            if (node.details.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = node.details,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 15.sp
                    ),
                    maxLines = if (isSelected) 6 else 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Visual Flowchart / Process Diagram (if available)
            if (flowSteps.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SlateCardElevated.copy(alpha = 0.9f))
                        .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccountTree,
                            contentDescription = null,
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "Vizuální procesní tok:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF38BDF8)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))

                    // Flow steps cards
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        flowSteps.forEachIndexed { index, step ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .background(SlateCard, RoundedCornerShape(6.dp))
                                    .border(0.8.dp, Color(0xFF38BDF8).copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "${index + 1}. $step",
                                    fontSize = 10.sp,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            if (index < flowSteps.size - 1) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "další",
                                    tint = Color(0xFF38BDF8).copy(alpha = 0.7f),
                                    modifier = Modifier
                                        .size(11.dp)
                                        .align(Alignment.CenterVertically)
                                )
                            }
                        }
                    }
                }
            }

            // Cluster Tag (if assigned)
            if (!node.clusterTag.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(NeonCyan.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "#${node.clusterTag}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan
                    )
                }
            }

            // Interactive UI Wireframe Preview (if available)
            if (!node.wireframeMockup.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0D1520))
                        .border(1.dp, NeonPurple.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Devices,
                            contentDescription = null,
                            tint = NeonPurple,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "UI Wireframe Návrh:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonPurple
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF161F2E),
                        border = androidx.compose.foundation.BorderStroke(0.8.dp, Color(0xFF2E3E56)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = node.wireframeMockup,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            fontSize = 9.sp,
                            color = TextPrimary,
                            lineHeight = 13.sp,
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                }
            }

            // Checklist Items (if available)
            val checklist = node.getChecklist()
            if (checklist.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SlateCardElevated.copy(alpha = 0.9f))
                        .border(1.dp, NeonEmerald.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = NeonEmerald,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "Akční kroky:",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonEmerald
                            )
                        }
                        val doneCount = checklist.count { it.isDone }
                        Text(
                            text = "$doneCount/${checklist.size}",
                            fontSize = 9.sp,
                            color = TextSecondary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    checklist.take(if (isSelected) 5 else 2).forEach { item ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 1.dp)
                        ) {
                            Text(
                                text = if (item.isDone) "☑" else "☐",
                                fontSize = 11.sp,
                                color = if (item.isDone) NeonEmerald else TextSecondary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = item.text,
                                fontSize = 10.sp,
                                color = if (item.isDone) TextSecondary else TextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
            if (!node.searchSources.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color(0xFF4285F4).copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Google Search",
                        tint = Color(0xFF4285F4),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Ověřeno: ${node.searchSources.take(35)}",
                        fontSize = 9.sp,
                        color = Color(0xFF93BDFC),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Quick Actions Bar when selected
            AnimatedVisibility(visible = isSelected) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .height(1.dp)
                            .fillMaxWidth()
                            .background(nodeColor.copy(alpha = 0.3f))
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // AI Deepen Action
                        IconButton(
                            onClick = onAiDeepen,
                            modifier = Modifier
                                .size(32.dp)
                                .background(nodeColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                .testTag("btn_ai_deepen_${node.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Prohloubit s AI",
                                tint = nodeColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        // Generate Diagram Action
                        IconButton(
                            onClick = onGenerateDiagram,
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color(0xFF38BDF8).copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                .testTag("btn_diagram_${node.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountTree,
                                contentDescription = "Procesní diagram",
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        // Generate UI Wireframe Mockup
                        IconButton(
                            onClick = onGenerateWireframe,
                            modifier = Modifier
                                .size(32.dp)
                                .background(NeonPurple.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                .testTag("btn_wireframe_${node.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Devices,
                                contentDescription = "UI Wireframe návrh",
                                tint = NeonPurple,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        // Add Child
                        IconButton(
                            onClick = onAddChild,
                            modifier = Modifier
                                .size(32.dp)
                                .background(SlateCardElevated, RoundedCornerShape(8.dp))
                                .testTag("btn_add_child_${node.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Přidat větev",
                                tint = TextPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        // Edit
                        IconButton(
                            onClick = onEdit,
                            modifier = Modifier
                                .size(32.dp)
                                .background(SlateCardElevated, RoundedCornerShape(8.dp))
                                .testTag("btn_edit_node_${node.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Upravit uzel",
                                tint = TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        // Delete (only non-root)
                        if (!isRoot) {
                            IconButton(
                                onClick = onDelete,
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(SlateCardElevated, RoundedCornerShape(8.dp))
                                    .testTag("btn_delete_node_${node.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Smazat",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
