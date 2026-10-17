package com.example.ui.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AppFeatureInfo
import com.example.data.model.FeatureCatalog
import com.example.data.model.FeatureCategory
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardElevated
import com.example.ui.theme.TechBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun FeatureCatalogDialog(
    onDismiss: () -> Unit,
    onSelectFeatureToRun: (AppFeatureInfo) -> Unit
) {
    var selectedCategoryFilter by remember { mutableStateOf<FeatureCategory?>(null) }
    var expandedFeatureId by remember { mutableStateOf<String?>(null) }

    val filteredFeatures = remember(selectedCategoryFilter) {
        if (selectedCategoryFilter == null) {
            FeatureCatalog.allFeatures
        } else {
            FeatureCatalog.allFeatures.filter { it.category == selectedCategoryFilter }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = SlateCard,
            border = BorderStroke(1.dp, TechBorder),
            shadowElevation = 24.dp,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.88f)
                .widthIn(max = 560.dp)
                .testTag("dialog_feature_catalog")
        ) {
            Column(
                modifier = Modifier.padding(18.dp)
            ) {
                // Header Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(38.dp)
                                .background(NeonCyan.copy(alpha = 0.15f), CircleShape)
                                .border(1.dp, NeonCyan.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Průvodce všemi funkcemi",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Klepnutím na ikonku ⓘ rozbalíte podrobnosti",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Zavřít",
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Category Filter Pills
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (selectedCategoryFilter == null) NeonCyan.copy(alpha = 0.25f) else SlateCardElevated,
                            border = BorderStroke(1.dp, if (selectedCategoryFilter == null) NeonCyan else TechBorder),
                            modifier = Modifier.clickable { selectedCategoryFilter = null }
                        ) {
                            Text(
                                text = "Vše (${FeatureCatalog.allFeatures.size})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedCategoryFilter == null) NeonCyan else TextSecondary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }

                    items(FeatureCategory.entries) { cat ->
                        val isSelected = selectedCategoryFilter == cat
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) cat.color.copy(alpha = 0.25f) else SlateCardElevated,
                            border = BorderStroke(1.dp, if (isSelected) cat.color else TechBorder),
                            modifier = Modifier.clickable { selectedCategoryFilter = cat }
                        ) {
                            Text(
                                text = cat.displayName,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) cat.color else TextSecondary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Features List with Expandable Detail Panels
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredFeatures, key = { it.id }) { feat ->
                        val isExpanded = expandedFeatureId == feat.id

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = SlateCardElevated,
                            border = BorderStroke(
                                width = if (isExpanded) 1.5.dp else 1.dp,
                                color = if (isExpanded) feat.color else TechBorder
                            ),
                            shadowElevation = if (isExpanded) 8.dp else 2.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                // Title row: Icon + Name + Category + Info Button + Expand Arrow
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            expandedFeatureId = if (isExpanded) null else feat.id
                                        }
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(feat.color.copy(alpha = 0.16f), CircleShape)
                                            .border(1.dp, feat.color.copy(alpha = 0.4f), CircleShape)
                                    ) {
                                        Icon(
                                            imageVector = feat.icon,
                                            contentDescription = null,
                                            tint = feat.color,
                                            modifier = Modifier.size(19.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = feat.title,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = feat.subtitle,
                                            fontSize = 10.sp,
                                            color = TextSecondary,
                                            maxLines = if (isExpanded) 3 else 1
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    // Info Button (ⓘ) that triggers expansion
                                    IconButton(
                                        onClick = {
                                            expandedFeatureId = if (isExpanded) null else feat.id
                                        },
                                        modifier = Modifier
                                            .size(32.dp)
                                            .background(
                                                if (isExpanded) feat.color.copy(alpha = 0.2f) else TechBorder.copy(alpha = 0.4f),
                                                CircleShape
                                            )
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = "Podrobné info o ${feat.title}",
                                            tint = if (isExpanded) feat.color else TextSecondary,
                                            modifier = Modifier.size(17.dp)
                                        )
                                    }

                                    Icon(
                                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                        contentDescription = null,
                                        tint = TextMuted,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                // Expanded Detailed Information Panel
                                AnimatedVisibility(
                                    visible = isExpanded,
                                    enter = expandVertically(),
                                    exit = shrinkVertically()
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 12.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(1.dp)
                                                .background(TechBorder)
                                        )

                                        Spacer(modifier = Modifier.height(10.dp))

                                        // How it works
                                        Text(
                                            text = "Jak to funguje:",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextSecondary
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = feat.description,
                                            fontSize = 12.sp,
                                            color = TextPrimary,
                                            lineHeight = 17.sp
                                        )

                                        Spacer(modifier = Modifier.height(10.dp))

                                        // Practical example card
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = ObsidianBg,
                                            border = BorderStroke(0.8.dp, feat.color.copy(alpha = 0.35f)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(modifier = Modifier.padding(10.dp)) {
                                                Text(
                                                    text = "💡 Příklad z praxe:",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = feat.color
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = feat.practicalExample,
                                                    fontSize = 11.sp,
                                                    color = TextPrimary.copy(alpha = 0.9f),
                                                    lineHeight = 16.sp
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        // Key benefits
                                        Text(
                                            text = "Klíčové přínosy:",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextSecondary
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        feat.keyBenefits.forEach { benefit ->
                                            Row(
                                                verticalAlignment = Alignment.Top,
                                                modifier = Modifier.padding(vertical = 2.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = feat.color,
                                                    modifier = Modifier
                                                        .size(13.dp)
                                                        .padding(top = 2.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = benefit,
                                                    fontSize = 11.sp,
                                                    color = TextPrimary,
                                                    lineHeight = 15.sp
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(14.dp))

                                        // Action Button
                                        Button(
                                            onClick = {
                                                onDismiss()
                                                onSelectFeatureToRun(feat)
                                            },
                                            shape = RoundedCornerShape(10.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = feat.color,
                                                contentColor = Color.Black
                                            ),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(40.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.PlayArrow,
                                                contentDescription = null,
                                                tint = Color.Black,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = feat.actionLabel,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
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
    }
}
