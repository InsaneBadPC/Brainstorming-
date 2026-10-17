package com.example.ui.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AiPersona
import com.example.data.model.AuditDebateReport
import com.example.data.model.AuditRoundSpeech
import com.example.data.model.OutdatedToolFinding
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCoral
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardElevated
import com.example.ui.theme.TechBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.roundToInt

@Composable
fun AuditDebateDialog(
    projectTitle: String,
    nodeCount: Int,
    isRunning: Boolean,
    currentRound: Int,
    totalRounds: Int,
    speeches: List<AuditRoundSpeech>,
    outdatedTools: List<OutdatedToolFinding>,
    report: AuditDebateReport?,
    currentSpeaker: AiPersona?,
    onDismiss: () -> Unit,
    onStartDebate: (rounds: Int, enableVoice: Boolean) -> Unit,
    onStopDebate: () -> Unit,
    onClearReport: () -> Unit
) {
    var configuredRounds by remember { mutableIntStateOf(3) }
    var enableVoice by remember { mutableStateOf(true) }
    var selectedReportTab by remember { mutableIntStateOf(0) }
    var showInstructionsDetail by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    // Auto-scroll to latest speech during debate
    LaunchedEffect(speeches.size) {
        if (speeches.isNotEmpty()) {
            listState.animateScrollToItem(speeches.size - 1)
        }
    }

    Dialog(
        onDismissRequest = {
            if (!isRunning) onDismiss()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = SlateCard,
            border = androidx.compose.foundation.BorderStroke(1.2.dp, TechBorder),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Header Bar
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
                                .background(NeonCoral.copy(alpha = 0.2f))
                                .border(1.dp, NeonCoral, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = NeonCoral,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Architektonický & Tech Audit",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFF4285F4).copy(alpha = 0.2f))
                                        .border(0.8.dp, Color(0xFF4285F4), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Language,
                                            contentDescription = null,
                                            tint = Color(0xFF4285F4),
                                            modifier = Modifier.size(10.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "Web 2026",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF4285F4)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "Prověrka chyb, zastaralých nástrojů a multi-round AI debata",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            if (isRunning) onStopDebate()
                            onDismiss()
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Zavřít", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Content View according to state
                if (isRunning) {
                    // STATE 1: DEBATE RUNNING
                    DebateRunningView(
                        currentRound = currentRound,
                        totalRounds = totalRounds,
                        currentSpeaker = currentSpeaker,
                        speeches = speeches,
                        outdatedCount = outdatedTools.size,
                        listState = listState,
                        onStopDebate = onStopDebate,
                        modifier = Modifier.weight(1f)
                    )
                } else if (report != null) {
                    // STATE 2: REPORT COMPLETED
                    DebateReportView(
                        report = report,
                        selectedTab = selectedReportTab,
                        onSelectTab = { selectedReportTab = it },
                        onRunNewDebate = { onClearReport() },
                        onDismiss = onDismiss,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    // STATE 3: PRE-LAUNCH CONFIGURATION
                    DebateConfigView(
                        projectTitle = projectTitle,
                        nodeCount = nodeCount,
                        configuredRounds = configuredRounds,
                        onRoundsChange = { configuredRounds = it },
                        enableVoice = enableVoice,
                        onEnableVoiceChange = { enableVoice = it },
                        showInstructions = showInstructionsDetail,
                        onToggleInstructions = { showInstructionsDetail = !showInstructionsDetail },
                        onStart = { onStartDebate(configuredRounds, enableVoice) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun DebateConfigView(
    projectTitle: String,
    nodeCount: Int,
    configuredRounds: Int,
    onRoundsChange: (Int) -> Unit,
    enableVoice: Boolean,
    onEnableVoiceChange: (Boolean) -> Unit,
    showInstructions: Boolean,
    onToggleInstructions: () -> Unit,
    onStart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Project Summary Banner
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SlateCardElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, TechBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Prověřovaný projekt", fontSize = 11.sp, color = TextMuted)
                            Text(text = projectTitle, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                            Text(text = "$nodeCount uzlů v myšlenkové mapě", fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                }
            }

            // Rounds Configuration Slider
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateCardElevated),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TechBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Počet kol AI debaty",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(NeonPurple.copy(alpha = 0.2f))
                                    .border(1.dp, NeonPurple, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "$configuredRounds ${if (configuredRounds == 1) "kolo" else if (configuredRounds in 2..4) "kola" else "kol"}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonPurple
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Slider(
                            value = configuredRounds.toFloat(),
                            onValueChange = { onRoundsChange(it.roundToInt().coerceIn(1, 5)) },
                            valueRange = 1f..5f,
                            steps = 3,
                            colors = SliderDefaults.colors(
                                thumbColor = NeonPurple,
                                activeTrackColor = NeonPurple,
                                inactiveTrackColor = SlateCard
                            ),
                            modifier = Modifier.testTag("slider_audit_rounds")
                        )

                        Text(
                            text = when (configuredRounds) {
                                1 -> "1 kolo: Rychlá detekce kritických chyb a zastaralých závislostí."
                                2 -> "2 kola: Hloubková kritika + průzkum moderních webových alternativ."
                                3 -> "3 kola (Doporučeno): Kompletní audit chyb, eliminace single-points-of-failure a procesní diagram."
                                else -> "$configuredRounds kol: Extrémní stress-test komplexního ekosystému s produkční roadmapou."
                            },
                            fontSize = 11.sp,
                            color = NeonCyan,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            // Realtime Web & Voice Settings
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateCardElevated),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TechBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Language, contentDescription = null, tint = Color(0xFF4285F4), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(text = "Realtime přístup k webu (Google)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Text(text = "Ověřování aktuálních verzí knihoven, CVE zranitelností a trendů 2026", fontSize = 10.sp, color = TextSecondary)
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(NeonEmerald.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text(text = "Aktivní", fontSize = 10.sp, color = NeonEmerald, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.VolumeUp, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(text = "Hlasová debata (TTS)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Text(text = "Každá AI mluví s vlastním tónem hlasu a intonací", fontSize = 10.sp, color = TextSecondary)
                                }
                            }
                            Switch(
                                checked = enableVoice,
                                onCheckedChange = onEnableVoiceChange,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = NeonCyan,
                                    checkedTrackColor = NeonCyan.copy(alpha = 0.4f)
                                )
                            )
                        }
                    }
                }
            }

            // Thorough Instructions for the 3 AI Agents (Accordion)
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateCardElevated),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TechBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onToggleInstructions() }
                        ) {
                            Text(
                                text = "📋 Důkladné instrukce pro 3 AI (vč. Minecraft & tech úkolů)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = if (showInstructions) "Skrýt ▲" else "Zobrazit ▼",
                                fontSize = 11.sp,
                                color = NeonCyan
                            )
                        }

                        AnimatedVisibility(visible = showInstructions) {
                            Column(modifier = Modifier.padding(top = 10.dp)) {
                                PersonaInstructionCard(
                                    persona = AiPersona.VORTEX,
                                    instruction = "Nemilosrdný kritik: V každém kole hledá architektonické chyby, zranitelnosti, slabiny a ověřuje přes web zda navržené nástroje nejsou zastaralé nebo nebezpečné."
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                PersonaInstructionCard(
                                    persona = AiPersona.NOVA,
                                    instruction = "Inovátorka & realtime web: Na základě výtek Vortexe vyhledává na webu moderní frameworky pro rok 2026, 10x lepší open-source knihovny a škálovatelné alternativy."
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                PersonaInstructionCard(
                                    persona = AiPersona.ATLAS,
                                    instruction = "Hlavní systémový architekt: Syntetizuje obě strany do reálné architektury, sestavuje migrační diagramy a generuje DŮKLADNÉ KROKOVÉ INSTRUKCE ke všem úkolům (včetně instrukcí pro AI boty v Minecraftu / MindCraftu, parametrů, syntaxe a kontrolních podmínek)."
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Start Button
        Button(
            onClick = onStart,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("btn_start_audit_debate")
        ) {
            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Spustit debatu ($configuredRounds ${if (configuredRounds == 1) "kolo" else if (configuredRounds in 2..4) "kola" else "kol"})",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun PersonaInstructionCard(persona: AiPersona, instruction: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = persona.color.copy(alpha = 0.08f),
        border = androidx.compose.foundation.BorderStroke(0.8.dp, persona.color.copy(alpha = 0.35f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.padding(8.dp)
        ) {
            Text(text = persona.emoji, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "${persona.displayName} (${persona.title})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = persona.color
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = instruction,
                    fontSize = 10.sp,
                    color = TextPrimary,
                    lineHeight = 14.sp
                )
            }
        }
    }
}

@Composable
private fun DebateRunningView(
    currentRound: Int,
    totalRounds: Int,
    currentSpeaker: AiPersona?,
    speeches: List<AuditRoundSpeech>,
    outdatedCount: Int,
    listState: androidx.compose.foundation.lazy.LazyListState,
    onStopDebate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        // Progress Banner
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = SlateCardElevated,
            border = androidx.compose.foundation.BorderStroke(1.dp, TechBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            color = NeonPurple,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Probíhá debata: Kolo $currentRound z $totalRounds",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan
                        )
                    }

                    if (outdatedCount > 0) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(NeonCoral.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Odhaleno: $outdatedCount rizik",
                                fontSize = 10.sp,
                                color = NeonCoral,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { if (totalRounds > 0) currentRound.toFloat() / totalRounds.toFloat() else 0f },
                    color = NeonPurple,
                    trackColor = SlateCard,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                )

                // Current Speaker Banner
                if (currentSpeaker != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Právě mluví:", fontSize = 10.sp, color = TextMuted)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${currentSpeaker.emoji} ${currentSpeaker.displayName}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = currentSpeaker.color
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = when (currentSpeaker) {
                                AiPersona.VORTEX -> "• Hledá zranitelnosti a zastaralé nástroje"
                                AiPersona.NOVA -> "• Vyhledává moderní 2026 alternativy na webu"
                                AiPersona.ATLAS -> "• Tvoří procesní tok a opravy mapy"
                                AiPersona.NEO -> "• Generuje Vibe Coding prompty a produkční kód"
                            },
                            fontSize = 10.sp,
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Speeches Stream
        LazyColumn(
            state = listState,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(speeches) { speech ->
                SpeechBubbleCard(speech = speech)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Stop Button
        OutlinedButton(
            onClick = onStopDebate,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonCoral),
            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCoral),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(imageVector = Icons.Default.Stop, contentDescription = null, tint = NeonCoral)
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "Zastavit debatu a zobrazit dosavadní výsledky", fontSize = 12.sp)
        }
    }
}

@Composable
private fun SpeechBubbleCard(speech: AuditRoundSpeech) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SlateCardElevated),
        border = androidx.compose.foundation.BorderStroke(1.dp, speech.persona.color.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = speech.persona.emoji, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = speech.persona.displayName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = speech.persona.color
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "(${speech.persona.title})",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(SlateCard)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = "Kolo ${speech.round}", fontSize = 9.sp, color = NeonCyan)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = speech.spokenText,
                fontSize = 12.sp,
                color = TextPrimary,
                lineHeight = 16.sp
            )

            if (!speech.searchSources.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Language, contentDescription = null, tint = Color(0xFF4285F4), modifier = Modifier.size(11.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Web ověřeno: ${speech.searchSources}",
                        fontSize = 9.sp,
                        color = Color(0xFF4285F4),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun DebateReportView(
    report: AuditDebateReport,
    selectedTab: Int,
    onSelectTab: (Int) -> Unit,
    onRunNewDebate: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        // Health Score Summary Card
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = SlateCardElevated,
            border = androidx.compose.foundation.BorderStroke(1.2.dp, NeonEmerald.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(NeonEmerald.copy(alpha = 0.15f))
                        .border(2.dp, NeonEmerald, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${report.healthScore}%",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonEmerald
                        )
                        Text(text = "Zdraví", fontSize = 8.sp, color = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Audit úspěšně dokončen (${report.totalRounds} kol)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = report.summary,
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Tab Selector: 0 = Zastaralé nástroje, 1 = Záznam debaty, 2 = Přidané uzly
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = SlateCardElevated,
            contentColor = NeonCyan,
            edgePadding = 0.dp,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { onSelectTab(0) },
                text = { Text("⚠️ Zastaralé nástroje (${report.outdatedTools.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { onSelectTab(1) },
                text = { Text("💬 Debata (${report.speeches.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { onSelectTab(2) },
                text = { Text("🗺️ Přidané uzly (${report.addedNodes.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Tab Content
        Box(modifier = Modifier.weight(1f)) {
            when (selectedTab) {
                0 -> {
                    // Outdated Tools & Replacements
                    if (report.outdatedTools.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = NeonEmerald, modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = "Nebyly nalezeny žádné zastaralé nástroje!", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text(text = "Všechny komponenty odpovídají moderním standardům 2026.", fontSize = 11.sp, color = TextSecondary)
                            }
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(report.outdatedTools) { tool ->
                                OutdatedToolCard(tool = tool)
                            }
                        }
                    }
                }
                1 -> {
                    // Speeches transcript
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(report.speeches) { speech ->
                            SpeechBubbleCard(speech = speech)
                        }
                    }
                }
                2 -> {
                    // Added nodes list
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(report.addedNodes) { nodeDto ->
                            val persona = AiPersona.fromId(nodeDto.authorPersona)
                            val isInstructionTask = nodeDto.details.contains("INSTRUKCE", ignoreCase = true) ||
                                    nodeDto.category.equals("ACTION_ITEM", ignoreCase = true)

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = SlateCardElevated,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isInstructionTask) NeonEmerald.copy(alpha = 0.5f) else TechBorder
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = nodeDto.title,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isInstructionTask) NeonEmerald else NeonCyan
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(
                                                        if (isInstructionTask) NeonEmerald.copy(alpha = 0.2f)
                                                        else NeonPurple.copy(alpha = 0.2f)
                                                    )
                                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = if (isInstructionTask) "ÚKOL PRO AI" else nodeDto.category,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isInstructionTask) NeonEmerald else NeonPurple
                                                )
                                            }
                                        }

                                        if (persona != null) {
                                            Text(
                                                text = "${persona.emoji} ${persona.displayName}",
                                                fontSize = 10.sp,
                                                color = persona.color,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }

                                    if (nodeDto.details.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        if (isInstructionTask) {
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = SlateCard,
                                                border = androidx.compose.foundation.BorderStroke(0.8.dp, NeonEmerald.copy(alpha = 0.3f)),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Column(modifier = Modifier.padding(8.dp)) {
                                                    Text(
                                                        text = "📋 DŮKLADNÉ INSTRUKCE PRO REALIZACI:",
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = NeonEmerald
                                                    )
                                                    Spacer(modifier = Modifier.height(3.dp))
                                                    Text(
                                                        text = nodeDto.details,
                                                        fontSize = 11.sp,
                                                        color = TextPrimary,
                                                        lineHeight = 15.sp
                                                    )
                                                }
                                            }
                                        } else {
                                            Text(text = nodeDto.details, fontSize = 11.sp, color = TextPrimary)
                                        }
                                    }

                                    if (!nodeDto.diagramData.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Procesní tok: ${nodeDto.diagramData}",
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 9.sp,
                                            color = Color(0xFF38BDF8)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Action Buttons Row
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(
                onClick = onRunNewDebate,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Nová debata", fontSize = 12.sp)
            }

            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald),
                modifier = Modifier.weight(1.3f)
            ) {
                Text(text = "Zobrazit v mapě", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
        }
    }
}

@Composable
private fun OutdatedToolCard(tool: OutdatedToolFinding) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SlateCardElevated),
        border = androidx.compose.foundation.BorderStroke(1.dp, NeonCoral.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = NeonCoral, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = tool.toolName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCoral
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(SlateCard)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = tool.category, fontSize = 9.sp, color = TextMuted)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Problém / Riziko: ${tool.issueOrDeprecatedReason}",
                fontSize = 11.sp,
                color = TextPrimary,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = NeonEmerald.copy(alpha = 0.1f),
                border = androidx.compose.foundation.BorderStroke(0.8.dp, NeonEmerald.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = NeonEmerald, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(text = "Doporučená moderní alternativa (rok 2026):", fontSize = 9.sp, color = NeonEmerald, fontWeight = FontWeight.Bold)
                        Text(text = tool.recommendedModernAlternative, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                    }
                }
            }
        }
    }
}
