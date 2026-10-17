package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.MindNode
import com.example.data.model.NodeCategory
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCoral
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

enum class SwotPillar(
    val title: String,
    val subtitle: String,
    val color: Color,
    val icon: ImageVector,
    val category: NodeCategory
) {
    STRENGTHS("Silné stránky (Strengths)", "Interní výhody & inovace", NeonEmerald, Icons.Default.Shield, NodeCategory.SOLUTION),
    WEAKNESSES("Slabé stránky (Weaknesses)", "Limity, mezery & dluh", NeonCoral, Icons.Default.Warning, NodeCategory.RISK),
    OPPORTUNITIES("Příležitosti (Opportunities)", "Tržní šance & trendy", NeonCyan, Icons.Default.Lightbulb, NodeCategory.GOAL),
    THREATS("Hrozby (Threats)", "Konkurence & rizika", NeonAmber, Icons.Default.Security, NodeCategory.RISK);
}

data class SwotItem(
    val pillar: SwotPillar,
    val title: String,
    val details: String
)

@Composable
fun SwotAnalysisDialog(
    nodes: List<MindNode>,
    onDismiss: () -> Unit,
    onAddSwotItemToMap: (title: String, details: String, category: NodeCategory) -> Unit,
    onAiGenerateSwot: () -> Unit
) {
    var selectedPillar by remember { mutableStateOf(SwotPillar.STRENGTHS) }

    // Derive SWOT items from the existing mind map nodes
    val swotItems = remember(nodes) {
        val list = mutableListOf<SwotItem>()
        for (node in nodes) {
            when (node.category) {
                NodeCategory.SOLUTION, NodeCategory.ARCHITECTURE -> {
                    list.add(SwotItem(SwotPillar.STRENGTHS, node.title, node.details.ifBlank { "Klíčová technologická a produktová přednost" }))
                }
                NodeCategory.RISK -> {
                    if (node.title.contains("trh", ignoreCase = true) || node.title.contains("konkurenc", ignoreCase = true)) {
                        list.add(SwotItem(SwotPillar.THREATS, node.title, node.details.ifBlank { "Vnější hrozba z trhu" }))
                    } else {
                        list.add(SwotItem(SwotPillar.WEAKNESSES, node.title, node.details.ifBlank { "Vnitřní výzva a technické omezení" }))
                    }
                }
                NodeCategory.GOAL, NodeCategory.FEATURE -> {
                    list.add(SwotItem(SwotPillar.OPPORTUNITIES, node.title, node.details.ifBlank { "Tržní příležitost pro škálování a růst" }))
                }
                else -> {}
            }
        }
        // If empty, add template baseline
        if (list.isEmpty()) {
            list.add(SwotItem(SwotPillar.STRENGTHS, "Inovativní koncept", "Rychlé řešení problému uživatelů."))
            list.add(SwotItem(SwotPillar.WEAKNESSES, "Zatím raná fáze", "Potřeba dořešit architekturu a marketing."))
            list.add(SwotItem(SwotPillar.OPPORTUNITIES, "Rostoucí poptávka", "Moderní AI technologie otevírají nové trhy."))
            list.add(SwotItem(SwotPillar.THREATS, "Existující konkurenti", "Možnost rychlého okopírování nápadu."))
        }
        list
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = SlateCard,
            border = androidx.compose.foundation.BorderStroke(1.2.dp, NeonPurple.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(NeonPurple.copy(alpha = 0.2f))
                                .border(1.dp, NeonPurple, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = NeonPurple,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "SWOT Strategická Analýza",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "Syntéza silných stránek, slabin, příležitostí a hrozeb",
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Zavřít",
                            tint = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Pillar Selector Tabs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SwotPillar.entries.forEach { pillar ->
                        val isSelected = selectedPillar == pillar
                        val count = swotItems.count { it.pillar == pillar }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) pillar.color.copy(alpha = 0.25f) else SlateCardElevated)
                                .border(1.dp, if (isSelected) pillar.color else Color.Transparent, RoundedCornerShape(10.dp))
                                .clickable { selectedPillar = pillar }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = pillar.icon,
                                    contentDescription = null,
                                    tint = pillar.color,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = pillar.name.first().toString(),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 12.sp,
                                    color = if (isSelected) pillar.color else TextSecondary
                                )
                                Text(
                                    text = "($count)",
                                    fontSize = 9.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Current Pillar Banner
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = selectedPillar.color.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, selectedPillar.color.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(10.dp)
                    ) {
                        Icon(
                            imageVector = selectedPillar.icon,
                            contentDescription = null,
                            tint = selectedPillar.color,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = selectedPillar.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = selectedPillar.color
                            )
                            Text(
                                text = selectedPillar.subtitle,
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Items list
                val currentItems = swotItems.filter { it.pillar == selectedPillar }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(currentItems) { item ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SlateCardElevated),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, selectedPillar.color.copy(alpha = 0.25f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = item.details,
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        onAddSwotItemToMap(item.title, item.details, selectedPillar.category)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = selectedPillar.color),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Přidat do mapy",
                                        tint = Color.Black,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Do mapy",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // AI Deepen SWOT action
                Button(
                    onClick = {
                        onDismiss()
                        onAiGenerateSwot()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AI Hloubková SWOT Analýza přes 3 AI",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}
