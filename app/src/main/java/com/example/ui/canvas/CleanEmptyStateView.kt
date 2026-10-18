package com.example.ui.canvas

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChangeHistory
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricBlueBright
import com.example.ui.theme.ElectricBlueLight
import com.example.ui.theme.FramerBlue
import com.example.ui.theme.PureWhite

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CleanEmptyStateView(
    onCreateMapClick: () -> Unit,
    onCreateQuickMap: (title: String, useAi: Boolean) -> Unit,
    onVoiceClick: () -> Unit,
    onOpenFeaturesCatalog: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var quickInput by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf("Pages") }
    val scrollState = rememberScrollState()

    // Pure Royal Electric Blue Palette (Zero Turquoise)
    val royalBlueDeep = Color(0xFF0038CC)
    val royalBlueNeon = Color(0xFF0066FF)
    val royalBlueBright = Color(0xFF2563EB)
    val royalBlueElectric = Color(0xFF3B82F6)
    val royalBlueGlowLight = Color(0xFF93C5FD)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF000000))
            .testTag("clean_empty_state_view")
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // 1. Framer Hero Headline with Radiant Neon Glow (Zářící bílý text)
            Text(
                text = "Váš další nápad\nzačíná zde",
                style = TextStyle(
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    lineHeight = 36.sp,
                    letterSpacing = (-1.0).sp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFFFFFF),
                            Color(0xFFFFFFFF),
                            Color(0xFFF1F5F9),
                            Color(0xFFDBEAFE)
                        )
                    ),
                    shadow = Shadow(
                        color = royalBlueNeon.copy(alpha = 0.95f),
                        offset = Offset(0f, 0f),
                        blurRadius = 38f
                    )
                ),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .drawBehind {
                        // Ambient text back-glow
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    royalBlueNeon.copy(alpha = 0.28f),
                                    Color.Transparent
                                ),
                                center = Offset(size.width / 2f, size.height / 2f),
                                radius = size.width * 0.7f
                            )
                        )
                    }
            )

            Spacer(modifier = Modifier.height(18.dp))

            // 2. Framer Solid Pure White Pill Button: "Začněte zdarma"
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = PureWhite,
                shadowElevation = 14.dp,
                modifier = Modifier
                    .shadow(
                        elevation = 16.dp,
                        shape = RoundedCornerShape(999.dp),
                        spotColor = Color.White.copy(alpha = 0.6f)
                    )
                    .clickable {
                        if (quickInput.isNotBlank()) {
                            onCreateQuickMap(quickInput.trim(), true)
                        } else {
                            onCreateMapClick()
                        }
                    }
                    .testTag("btn_framer_hero_start")
            ) {
                Text(
                    text = "Začněte zdarma",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 26.dp, vertical = 11.dp)
                )
            }

            Spacer(modifier = Modifier.height(34.dp))

            // 3. Floating Canvas Window Container with Diffuse Horizontal Blue Aura (Záře modré)
            Box(
                contentAlignment = Alignment.TopCenter,
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 480.dp)
            ) {
                // REAL ATMOSPHERIC BLUE AURA & LENS FLARE (Žádné ohraničené kolo!)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                        .offset(y = (-80).dp)
                        .drawBehind {
                            val w = size.width
                            val h = size.height

                            // A. Wide horizontal atmospheric wash (stretches beyond screen edges so no circle boundary exists)
                            drawOval(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        royalBlueNeon.copy(alpha = 0.90f),
                                        royalBlueDeep.copy(alpha = 0.70f),
                                        royalBlueBright.copy(alpha = 0.35f),
                                        Color(0xFF1D4ED8).copy(alpha = 0.15f),
                                        Color.Transparent
                                    ),
                                    center = Offset(w / 2f, h * 0.78f),
                                    radius = w * 0.95f
                                ),
                                topLeft = Offset(-w * 0.35f, 0f),
                                size = Size(w * 1.7f, h * 1.25f)
                            )

                            // B. Intense horizontal light flare along top edge of card
                            drawOval(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        royalBlueGlowLight.copy(alpha = 0.95f),
                                        royalBlueElectric.copy(alpha = 0.85f),
                                        royalBlueNeon.copy(alpha = 0.40f),
                                        Color.Transparent
                                    ),
                                    center = Offset(w / 2f, h * 0.85f),
                                    radius = w * 0.55f
                                ),
                                topLeft = Offset(w * 0.05f, h * 0.65f),
                                size = Size(w * 0.9f, h * 0.45f)
                            )

                            // C. Luminous top-edge laser beam
                            drawRect(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        royalBlueNeon.copy(alpha = 0.3f),
                                        royalBlueElectric,
                                        royalBlueGlowLight,
                                        royalBlueElectric,
                                        royalBlueNeon.copy(alpha = 0.3f),
                                        Color.Transparent
                                    )
                                ),
                                topLeft = Offset(0f, h * 0.87f),
                                size = Size(w, 3.dp.toPx())
                            )
                        }
                )

                // The Floating Window Card
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = Color(0xFF090D16),
                    shadowElevation = 30.dp,
                    border = BorderStroke(
                        width = 1.8.dp,
                        brush = Brush.horizontalGradient(
                            listOf(
                                royalBlueDeep.copy(alpha = 0.3f),
                                royalBlueElectric,
                                royalBlueGlowLight,
                                royalBlueElectric,
                                royalBlueDeep.copy(alpha = 0.3f)
                            )
                        )
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 28.dp,
                            shape = RoundedCornerShape(22.dp),
                            spotColor = royalBlueNeon,
                            ambientColor = royalBlueNeon
                        )
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        
                        // Toolbar: [◱ Canvas ▾] + Tools (+ ⛶ T △)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF0C101B))
                                .padding(horizontal = 14.dp, vertical = 9.dp)
                        ) {
                            // Canvas Pill Dropdown
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0x22FFFFFF),
                                border = BorderStroke(1.dp, Color(0x33FFFFFF)),
                                modifier = Modifier.clickable { onCreateMapClick() }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Apps,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Canvas",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = null,
                                        tint = Color(0xFF94A3B8),
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }

                            // Tool Icons in Royal Electric Blue (+ ⛶ T △)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                IconButton(
                                    onClick = { onCreateMapClick() },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Přidat uzel",
                                        tint = royalBlueElectric,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { onCreateMapClick() },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CropFree,
                                        contentDescription = "Rámec / Layout",
                                        tint = royalBlueElectric,
                                        modifier = Modifier.size(17.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { onOpenFeaturesCatalog() },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.TextFields,
                                        contentDescription = "Text & Kód",
                                        tint = royalBlueElectric,
                                        modifier = Modifier.size(17.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { onOpenFeaturesCatalog() },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ChangeHistory,
                                        contentDescription = "Kulatý stůl 3 AI",
                                        tint = royalBlueElectric,
                                        modifier = Modifier.size(17.dp)
                                    )
                                }
                            }
                        }

                        // Segmented Tab Bar: Pages | Layers | Assets
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF070A12))
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            listOf("Pages", "Layers", "Assets").forEach { tab ->
                                val isSelected = selectedTab == tab
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) Color(0x28FFFFFF) else Color.Transparent,
                                    modifier = Modifier
                                        .clickable { selectedTab = tab }
                                        .padding(end = 6.dp)
                                ) {
                                    Text(
                                        text = tab,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else Color(0xFF94A3B8),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        // Search Bar inside Canvas window
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF131826))
                                .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Search...",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                        }

                        // Interactive Workspace & Quick Creation Body
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            // Direct Quick Start Input Field
                            OutlinedTextField(
                                value = quickInput,
                                onValueChange = { quickInput = it },
                                placeholder = {
                                    Text(
                                        text = "Zadejte název mapy nebo nápad...",
                                        fontSize = 13.sp,
                                        color = Color(0xFF64748B)
                                    )
                                },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = royalBlueElectric,
                                    unfocusedBorderColor = Color(0x33FFFFFF),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedContainerColor = Color(0xFF101524),
                                    unfocusedContainerColor = Color(0xFF101524)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_quick_start_map")
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // If text entered, show instant actions
                            if (quickInput.isNotBlank()) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(999.dp),
                                        color = PureWhite,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { onCreateQuickMap(quickInput.trim(), true) }
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center,
                                            modifier = Modifier.padding(vertical = 10.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.AutoAwesome,
                                                contentDescription = null,
                                                tint = Color.Black,
                                                modifier = Modifier.size(15.dp)
                                            )
                                            Spacer(modifier = Modifier.width(5.dp))
                                            Text(
                                                text = "Vytvořit s AI",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color.Black
                                            )
                                        }
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(999.dp),
                                        color = Color(0x22FFFFFF),
                                        border = BorderStroke(1.dp, Color(0x44FFFFFF)),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { onCreateQuickMap(quickInput.trim(), false) }
                                    ) {
                                        Text(
                                            text = "Prázdná mapa",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(vertical = 10.dp)
                                        )
                                    }
                                }
                            } else {
                                // Default White CTA button
                                Surface(
                                    shape = RoundedCornerShape(999.dp),
                                    color = PureWhite,
                                    shadowElevation = 8.dp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onCreateMapClick() }
                                        .testTag("btn_empty_create_map")
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                        modifier = Modifier.padding(vertical = 11.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = null,
                                            tint = Color.Black,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Vytvořit novou mapu",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.Black
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Voice input button
                                Surface(
                                    shape = RoundedCornerShape(999.dp),
                                    color = Color(0x18FFFFFF),
                                    border = BorderStroke(1.dp, Color(0x28FFFFFF)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onVoiceClick() }
                                        .testTag("btn_empty_voice_start")
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                        modifier = Modifier.padding(vertical = 9.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Mic,
                                            contentDescription = null,
                                            tint = royalBlueElectric,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Popsat nápad hlasem",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFFE2E8F0)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "Rychlá témata pro začátek:",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // Flow row of template chips
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                listOf(
                                    "📱 Mobilní aplikace",
                                    "🚀 SaaS Startup",
                                    "🛍️ E-shop expanze",
                                    "📈 Marketingový plán"
                                ).forEach { idea ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFF131826),
                                        border = BorderStroke(1.dp, Color(0x22FFFFFF)),
                                        modifier = Modifier.clickable {
                                            onCreateQuickMap(idea.substringAfter(" "), true)
                                        }
                                    ) {
                                        Text(
                                            text = idea,
                                            fontSize = 11.sp,
                                            color = Color(0xFFE2E8F0),
                                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Features & Guide Banner
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF0F1424),
                                border = BorderStroke(1.2.dp, royalBlueNeon.copy(alpha = 0.6f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onOpenFeaturesCatalog() }
                                    .testTag("btn_empty_open_features_catalog")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Apps,
                                            contentDescription = null,
                                            tint = royalBlueElectric,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = "⚡ Centrum funkcí & Průvodce",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                            Text(
                                                text = "Přehled 22+ funkcí včetně 3 AI Kulatého stolu",
                                                fontSize = 10.sp,
                                                color = royalBlueElectric
                                            )
                                        }
                                    }
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = royalBlueElectric,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
