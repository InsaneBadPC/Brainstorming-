package com.example.ui.dialogs

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AiPersona
import com.example.data.model.MindNode
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardElevated
import com.example.ui.theme.TechBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class BrainstormMessage(
    val sender: String,
    val text: String,
    val persona: AiPersona? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrainstormCoPilotSheet(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    messages: List<BrainstormMessage>,
    solutionBlueprint: String?,
    isProcessing: Boolean,
    selectedNode: MindNode?,
    selectedPersona: AiPersona?,
    isGoogleSearchEnabled: Boolean,
    onSelectPersona: (AiPersona?) -> Unit,
    onToggleGoogleSearch: () -> Unit,
    onSendMessage: (String) -> Unit,
    onQuickAction: (String) -> Unit,
    onSpeakMessage: (String, AiPersona?) -> Unit,
    onGenerateBlueprint: () -> Unit,
    onToggleVoiceMode: (() -> Unit)? = null,
    isListening: Boolean = false,
    onOpenRoundTableDebate: (() -> Unit)? = null
) {
    if (!isOpen) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    var inputText by remember { mutableStateOf("") }

    val quickPrompts = listOf(
        "🌟 Zahájit debatu 3 AI u Kulatého stolu",
        "✨ Nova: Navrhni 3 revoluční funkce",
        "🛡️ Vortex: Kde je v tomhle past a riziko?",
        "🏛️ Atlas: Vytvoř procesní diagram",
        "🌐 Ověř konkurenci na Google Search",
        "🚀 Sestavit prováděcí roadmapu"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SlateCard,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(2.dp))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(NeonCyan, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "3 AI Brainstorming Lab",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("btn_close_copilot")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Zavřít",
                        tint = TextSecondary
                    )
                }
            }

            // Tabs: Chat / Blueprint
            SecondaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = SlateCard,
                contentColor = NeonCyan
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Debata & Chat (3 AI)", color = if (selectedTab == 0) NeonCyan else TextSecondary) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Funkční Blueprint", color = if (selectedTab == 1) NeonEmerald else TextSecondary) }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            when (selectedTab) {
                0 -> {
                    // Persona Switcher Bar
                    Text(
                        text = "Kdo s vámi diskutuje:",
                        fontSize = 11.sp,
                        color = TextMuted,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(5.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Option 1: Roundtable
                        item {
                            val isSelected = selectedPersona == null
                            PersonaSelectChip(
                                label = "🌟 Všichni (Kulatý stůl)",
                                color = NeonCyan,
                                isSelected = isSelected,
                                onClick = { onSelectPersona(null) }
                            )
                        }

                        // Option 2,3,4: Nova, Vortex, Atlas
                        items(AiPersona.entries) { p ->
                            val isSelected = selectedPersona == p
                            PersonaSelectChip(
                                label = "${p.emoji} ${p.displayName} (${p.title.split("&").first().trim()})",
                                color = p.color,
                                isSelected = isSelected,
                                onClick = { onSelectPersona(p) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Quick Action Prompts
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(quickPrompts) { prompt ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(SlateCardElevated)
                                    .border(1.dp, NeonPurple.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                                    .clickable {
                                        if (prompt.contains("Kulatého stolu") && onOpenRoundTableDebate != null) {
                                            onOpenRoundTableDebate()
                                        } else {
                                            onQuickAction(prompt)
                                        }
                                    }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                                    .testTag("chip_prompt")
                            ) {
                                Text(
                                    text = prompt,
                                    fontSize = 11.sp,
                                    color = TextPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Context Tag if node selected
                    if (selectedNode != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(selectedNode.category.defaultColor.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
                                .border(1.dp, selectedNode.category.defaultColor.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "Aktivní uzel: ",
                                fontSize = 11.sp,
                                color = selectedNode.category.defaultColor,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = selectedNode.title,
                                fontSize = 11.sp,
                                color = TextPrimary,
                                maxLines = 1
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    // Messages List
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (messages.isEmpty()) {
                            item {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 24.dp)
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "✨ Nova (Inovátor) • 🛡️ Vortex (Kritik) • 🏛️ Atlas (Architekt)",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NeonCyan
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "Napište nápad a nechte 3 AI diskutovat u kulatého stolu.",
                                            fontSize = 12.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            }
                        }

                        items(messages) { msg ->
                            val isUser = msg.sender == "User"
                            val persona = msg.persona ?: AiPersona.fromId(msg.sender)
                            val bubbleColor = when {
                                isUser -> NeonCyan.copy(alpha = 0.18f)
                                persona != null -> persona.color.copy(alpha = 0.15f)
                                else -> SlateCardElevated
                            }
                            val accentColor = when {
                                isUser -> NeonCyan
                                persona != null -> persona.color
                                else -> NeonPurple
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.88f)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(bubbleColor)
                                        .border(1.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                                        .padding(10.dp)
                                ) {
                                    Column {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = if (isUser) "👤 Vy" else "${persona?.emoji ?: "🤖"} ${msg.sender}",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = accentColor
                                                )
                                                if (persona != null) {
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = "(${persona.title.split("&").first().trim()})",
                                                        fontSize = 9.sp,
                                                        color = TextMuted
                                                    )
                                                }
                                            }

                                            if (!isUser) {
                                                IconButton(
                                                    onClick = { onSpeakMessage(msg.text, persona) },
                                                    modifier = Modifier.size(24.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                                        contentDescription = "Přečíst nahlas",
                                                        tint = accentColor,
                                                        modifier = Modifier.size(15.dp)
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = msg.text,
                                            fontSize = 12.sp,
                                            color = TextPrimary,
                                            lineHeight = 17.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Google Search Grounding Toggle Bar
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = null,
                                tint = if (isGoogleSearchEnabled) Color(0xFF4285F4) else TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Google Search Data (Ověřování faktů & trhu)",
                                fontSize = 11.sp,
                                color = if (isGoogleSearchEnabled) TextPrimary else TextMuted
                            )
                        }

                        Switch(
                            checked = isGoogleSearchEnabled,
                            onCheckedChange = { onToggleGoogleSearch() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF4285F4),
                                checkedTrackColor = Color(0xFF4285F4).copy(alpha = 0.3f)
                            ),
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Text Input Bar
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            placeholder = {
                                Text(
                                    text = if (selectedPersona == null) "Zadejte téma pro debatu 3 AI..." else "Zeptejte se ${selectedPersona.displayName}...",
                                    color = TextMuted,
                                    fontSize = 12.sp
                                )
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = selectedPersona?.color ?: NeonCyan,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                cursorColor = selectedPersona?.color ?: NeonCyan
                            ),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_ai_message")
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Voice Mode Toggle Button (Text chat is default, click to switch to voice)
                        IconButton(
                            onClick = { onToggleVoiceMode?.invoke() },
                            modifier = Modifier
                                .size(44.dp)
                                .background(if (isListening) NeonCyan.copy(alpha = 0.25f) else SlateCardElevated, CircleShape)
                                .border(1.dp, if (isListening) NeonCyan else TechBorder, CircleShape)
                                .testTag("btn_copilot_voice_mode")
                        ) {
                            Icon(
                                imageVector = if (isListening) Icons.AutoMirrored.Filled.VolumeUp else Icons.Default.Mic,
                                contentDescription = if (isListening) "Poslouchám..." else "Přepnout do hlasového režimu",
                                tint = if (isListening) NeonCyan else TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        IconButton(
                            onClick = {
                                if (inputText.isNotBlank() && !isProcessing) {
                                    val txt = inputText
                                    inputText = ""
                                    onSendMessage(txt)
                                }
                            },
                            enabled = inputText.isNotBlank() && !isProcessing,
                            modifier = Modifier
                                .size(44.dp)
                                .background(if (inputText.isNotBlank()) (selectedPersona?.color ?: NeonCyan) else SlateCardElevated, CircleShape)
                                .testTag("btn_send_ai_message")
                        ) {
                            if (isProcessing) {
                                CircularProgressIndicator(
                                    color = NeonCyan,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(20.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Odeslat",
                                    tint = if (inputText.isNotBlank()) Color.Black else TextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                1 -> {
                    // Blueprint Tab
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Prováděcí plán konsenzu 3 AI:",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )

                            Row {
                                if (!solutionBlueprint.isNullOrBlank()) {
                                    IconButton(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            val clip = ClipData.newPlainText("Blueprint", solutionBlueprint)
                                            clipboard.setPrimaryClip(clip)
                                            Toast.makeText(context, "Blueprint zkopírován do schránky", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Kopírovat",
                                            tint = NeonCyan,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = onGenerateBlueprint,
                                    enabled = !isProcessing,
                                    modifier = Modifier
                                        .background(NeonEmerald.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            tint = NeonEmerald,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Aktualizovat",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NeonEmerald
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (isProcessing) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator(color = NeonEmerald)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "Nova, Vortex a Atlas syntetizují řešení...",
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        } else if (solutionBlueprint.isNullOrBlank()) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.Description,
                                        contentDescription = null,
                                        tint = TextMuted,
                                        modifier = Modifier.size(42.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Zatím nebyl vygenerován prováděcí plán.",
                                        fontSize = 13.sp,
                                        color = TextSecondary
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(18.dp))
                                            .background(NeonEmerald.copy(alpha = 0.2f))
                                            .border(1.dp, NeonEmerald, RoundedCornerShape(18.dp))
                                            .clickable { onGenerateBlueprint() }
                                            .padding(horizontal = 16.dp, vertical = 8.dp)
                                            .testTag("btn_trigger_blueprint")
                                    ) {
                                        Text(
                                            text = "⚡ Vytvořit Funkční Blueprint z mapy",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NeonEmerald
                                        )
                                    }
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SlateCardElevated)
                                    .padding(14.dp)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Text(
                                    text = solutionBlueprint,
                                    fontSize = 13.sp,
                                    color = TextPrimary,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PersonaSelectChip(
    label: String,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) color.copy(alpha = 0.22f) else SlateCardElevated)
            .border(
                width = if (isSelected) 1.5.dp else 0.8.dp,
                color = if (isSelected) color else color.copy(alpha = 0.35f),
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) color else TextSecondary
        )
    }
}
