package com.example.ui.matrix

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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MindNode
import com.example.data.model.NodeCategory
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCoral
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

enum class MatrixQuadrant(
    val title: String,
    val subtitle: String,
    val color: Color,
    val icon: ImageVector,
    val recommendation: String
) {
    QUICK_WINS(
        "Rychlé výhry (Quick Wins)",
        "Vysoký dopad • Nízké úsilí",
        NeonEmerald,
        Icons.Default.Bolt,
        "Implementujte okamžitě pro rychlou validaci a hybnou sílu."
    ),
    STRATEGIC_BETS(
        "Strategické pilíře (Strategic Bets)",
        "Vysoký dopad • Vysoké úsilí",
        NeonPurple,
        Icons.Default.RocketLaunch,
        "Jádro architektury a unikátní hodnota. Naplánujte do fází."
    ),
    FILL_INS(
        "Drobné doplňky (Fill-ins)",
        "Nízký dopad • Nízké úsilí",
        NeonCyan,
        Icons.Default.HourglassEmpty,
        "Udělejte, když zbyde čas, nebo automatizujte."
    ),
    TIME_WASTERS(
        "Rizika a pasti (Time Wasters)",
        "Nízký dopad • Vysoké úsilí",
        NeonCoral,
        Icons.Default.Warning,
        "Pozor! Tyto prvky odložte, zjednodušte nebo vynechte."
    );
}

@Composable
fun PrioritizationMatrixView(
    nodes: List<MindNode>,
    onToggleNodeCompleted: (String, Boolean) -> Unit,
    onSelectNode: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedQuadrant by remember { mutableStateOf(MatrixQuadrant.QUICK_WINS) }

    fun classifyNode(node: MindNode): MatrixQuadrant {
        return when (node.category) {
            NodeCategory.GOAL, NodeCategory.SOLUTION -> MatrixQuadrant.QUICK_WINS
            NodeCategory.ARCHITECTURE, NodeCategory.FEATURE -> {
                if (node.title.length > 25 || node.details.length > 50) MatrixQuadrant.STRATEGIC_BETS
                else MatrixQuadrant.QUICK_WINS
            }
            NodeCategory.ACTION_ITEM, NodeCategory.DIAGRAM -> MatrixQuadrant.FILL_INS
            NodeCategory.RISK -> MatrixQuadrant.TIME_WASTERS
        }
    }

    val classifiedNodes = remember(nodes) {
        nodes.groupBy { classifyNode(it) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .padding(horizontal = 14.dp)
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 4.dp)
        ) {
            Text(
                text = "Prioritní matice 2x2 (Dopad vs Úsilí)",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }
        Text(
            text = "Rozdělení nápadů z myšlenkové mapy podle poměru přidané hodnoty a náročnosti.",
            fontSize = 11.sp,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // 2x2 Overview Dashboard Grid
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            QuadrantMetricCard(
                quadrant = MatrixQuadrant.QUICK_WINS,
                count = classifiedNodes[MatrixQuadrant.QUICK_WINS]?.size ?: 0,
                isSelected = selectedQuadrant == MatrixQuadrant.QUICK_WINS,
                onClick = { selectedQuadrant = MatrixQuadrant.QUICK_WINS },
                modifier = Modifier.weight(1f)
            )
            QuadrantMetricCard(
                quadrant = MatrixQuadrant.STRATEGIC_BETS,
                count = classifiedNodes[MatrixQuadrant.STRATEGIC_BETS]?.size ?: 0,
                isSelected = selectedQuadrant == MatrixQuadrant.STRATEGIC_BETS,
                onClick = { selectedQuadrant = MatrixQuadrant.STRATEGIC_BETS },
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            QuadrantMetricCard(
                quadrant = MatrixQuadrant.FILL_INS,
                count = classifiedNodes[MatrixQuadrant.FILL_INS]?.size ?: 0,
                isSelected = selectedQuadrant == MatrixQuadrant.FILL_INS,
                onClick = { selectedQuadrant = MatrixQuadrant.FILL_INS },
                modifier = Modifier.weight(1f)
            )
            QuadrantMetricCard(
                quadrant = MatrixQuadrant.TIME_WASTERS,
                count = classifiedNodes[MatrixQuadrant.TIME_WASTERS]?.size ?: 0,
                isSelected = selectedQuadrant == MatrixQuadrant.TIME_WASTERS,
                onClick = { selectedQuadrant = MatrixQuadrant.TIME_WASTERS },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Selected Quadrant Details & Recommendation
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = selectedQuadrant.color.copy(alpha = 0.12f),
            border = androidx.compose.foundation.BorderStroke(1.dp, selectedQuadrant.color.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(10.dp)
            ) {
                Icon(
                    imageVector = selectedQuadrant.icon,
                    contentDescription = null,
                    tint = selectedQuadrant.color,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = selectedQuadrant.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = selectedQuadrant.color
                    )
                    Text(
                        text = selectedQuadrant.recommendation,
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Nodes in the selected quadrant
        val quadrantNodes = classifiedNodes[selectedQuadrant] ?: emptyList()

        if (quadrantNodes.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Text(
                    text = "V tomto kvadrantu zatím nejsou žádné uzly.",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(quadrantNodes, key = { it.id }) { node ->
                    MatrixNodeCard(
                        node = node,
                        quadrantColor = selectedQuadrant.color,
                        onToggleCompleted = { done -> onToggleNodeCompleted(node.id, done) },
                        onSelect = { onSelectNode(node.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun QuadrantMetricCard(
    quadrant: MatrixQuadrant,
    count: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) SlateCardElevated else SlateCard
        ),
        border = androidx.compose.foundation.BorderStroke(
            if (isSelected) 1.5.dp else 1.dp,
            if (isSelected) quadrant.color else quadrant.color.copy(alpha = 0.25f)
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = quadrant.icon,
                    contentDescription = null,
                    tint = quadrant.color,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "$count",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = quadrant.color
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = quadrant.title.substringBefore(" ("),
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = quadrant.subtitle,
                fontSize = 9.sp,
                color = TextSecondary,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun MatrixNodeCard(
    node: MindNode,
    quadrantColor: Color,
    onToggleCompleted: (Boolean) -> Unit,
    onSelect: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SlateCardElevated),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, quadrantColor.copy(alpha = 0.3f)),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onSelect() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(10.dp)
        ) {
            IconButton(
                onClick = { onToggleCompleted(!node.isCompleted) },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = if (node.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = "Splněno",
                    tint = if (node.isCompleted) NeonEmerald else TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = node.category.displayName,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = node.category.defaultColor
                    )
                    if (node.clusterTag != null) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "#${node.clusterTag}",
                            fontSize = 9.sp,
                            color = NeonCyan
                        )
                    }
                }
                Text(
                    text = node.title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (node.isCompleted) TextSecondary else TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (node.details.isNotBlank()) {
                    Text(
                        text = node.details,
                        fontSize = 10.sp,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Zobrazit ➔",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = quadrantColor
            )
        }
    }
}
