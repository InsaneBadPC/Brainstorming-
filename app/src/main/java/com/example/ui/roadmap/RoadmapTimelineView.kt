package com.example.ui.roadmap

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MindNode
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardElevated
import com.example.ui.theme.TechBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun RoadmapTimelineView(
    nodes: List<MindNode>,
    onToggleNodeCompleted: (String, Boolean) -> Unit,
    onSelectNode: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalCount = nodes.size
    val completedCount = nodes.count { it.isCompleted }
    val progress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f

    // Group nodes into phases: either by roadmapPhase, or fallback into 3 logical phases
    val phases = listOf(
        "Fáze 1: Vize & MVP Základ" to nodes.filter { it.roadmapPhase?.contains("1") == true || it.category.name == "GOAL" || it.category.name == "FEATURE" },
        "Fáze 2: Architektura & Zabezpečení" to nodes.filter { it.roadmapPhase?.contains("2") == true || it.category.name == "ARCHITECTURE" || it.category.name == "DIAGRAM" || it.category.name == "RISK" },
        "Fáze 3: Řešení & Exekuce (Roadmapa)" to nodes.filter { it.roadmapPhase?.contains("3") == true || it.category.name == "SOLUTION" || it.category.name == "ACTION_ITEM" }
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .padding(16.dp)
            .testTag("roadmap_timeline_view")
    ) {
        // Overall Project Progress Banner
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = SlateCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, TechBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Timeline,
                            contentDescription = null,
                            tint = NeonEmerald,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Roadmapa realizace (Gantt)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Text(
                        text = "$completedCount / $totalCount splněno (${(progress * 100).toInt()}%)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonEmerald
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    color = NeonEmerald,
                    trackColor = SlateCardElevated,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Timeline Milestones
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            phases.forEachIndexed { pIndex, (phaseName, phaseNodes) ->
                item {
                    Column {
                        // Milestone Header
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(NeonCyan.copy(alpha = 0.2f), CircleShape)
                                    .border(1.5.dp, NeonCyan, CircleShape)
                            ) {
                                Text(
                                    text = "${pIndex + 1}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonCyan
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = phaseName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Nodes list in this phase
                        if (phaseNodes.isEmpty()) {
                            Text(
                                text = "Zatím žádné uzly v této fázi",
                                fontSize = 12.sp,
                                color = TextMuted,
                                modifier = Modifier.padding(start = 34.dp)
                            )
                        } else {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(start = 12.dp)
                            ) {
                                phaseNodes.forEach { node ->
                                    RoadmapNodeItem(
                                        node = node,
                                        onToggleCompleted = { onToggleNodeCompleted(node.id, !node.isCompleted) },
                                        onSelect = { onSelectNode(node.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RoadmapNodeItem(
    node: MindNode,
    onToggleCompleted: () -> Unit,
    onSelect: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = SlateCardElevated,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (node.isCompleted) NeonEmerald.copy(alpha = 0.5f) else TechBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(10.dp)
        ) {
            Checkbox(
                checked = node.isCompleted,
                onCheckedChange = { onToggleCompleted() },
                colors = CheckboxDefaults.colors(
                    checkedColor = NeonEmerald,
                    checkmarkColor = Color.Black,
                    uncheckedColor = TextMuted
                ),
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(node.category.defaultColor, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = node.category.displayName,
                        fontSize = 10.sp,
                        color = node.category.defaultColor,
                        fontWeight = FontWeight.Bold
                    )
                    node.authorPersona?.let {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "• @$it", fontSize = 10.sp, color = TextMuted)
                    }
                }

                Text(
                    text = node.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (node.isCompleted) TextMuted else TextPrimary
                )

                if (node.details.isNotBlank()) {
                    Text(
                        text = node.details,
                        fontSize = 11.sp,
                        color = TextMuted,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
