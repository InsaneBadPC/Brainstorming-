package com.example.ui.dialogs

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.MindNode
import com.example.data.model.VibeCodingArtifact
import com.example.data.model.VibeTargetStack
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCoral
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardElevated
import com.example.ui.theme.TechBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun VibeCodingDialog(
    isOpen: Boolean,
    artifact: VibeCodingArtifact?,
    isLoading: Boolean,
    selectedStack: VibeTargetStack,
    selectedNode: MindNode?,
    projectTitle: String,
    onDismiss: () -> Unit,
    onSelectStack: (VibeTargetStack) -> Unit,
    onRegenerate: (VibeTargetStack) -> Unit
) {
    if (!isOpen) return

    val context = LocalContext.current
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    fun copyToClipboard(label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$label zkopírován do schránky!", Toast.LENGTH_SHORT).show()
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = SlateCard,
            border = BorderStroke(1.2.dp, NeonEmerald.copy(alpha = 0.6f)),
            shadowElevation = 24.dp,
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.94f)
                .testTag("vibe_coding_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(44.dp)
                                .background(NeonEmerald.copy(alpha = 0.16f), CircleShape)
                                .border(1.5.dp, NeonEmerald.copy(alpha = 0.6f), CircleShape)
                        ) {
                            Text(text = "💻", fontSize = 22.sp)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Vibe Coding & Scaffolder",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = NeonEmerald.copy(alpha = 0.18f),
                                    border = BorderStroke(0.8.dp, NeonEmerald.copy(alpha = 0.5f))
                                ) {
                                    Text(
                                        text = "⚡ NEO AI",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NeonEmerald,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = if (selectedNode != null) "Komponenta: ${selectedNode.title}" else "Celý projekt: $projectTitle",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                maxLines = 1
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .background(SlateCardElevated, CircleShape)
                            .testTag("btn_close_vibe_dialog")
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

                // Target Stack Selector Ribbon
                Text(
                    text = "Vyberte cílový technologický stack:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(VibeTargetStack.entries) { stack ->
                        val isSelected = selectedStack == stack
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) NeonEmerald.copy(alpha = 0.22f) else SlateCardElevated,
                            border = BorderStroke(
                                1.2.dp,
                                if (isSelected) NeonEmerald else TechBorder
                            ),
                            modifier = Modifier
                                .clickable {
                                    onSelectStack(stack)
                                    onRegenerate(stack)
                                }
                                .testTag("vibe_stack_${stack.name}")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(text = stack.iconEmoji, fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = stack.displayName,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) NeonEmerald else TextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tab Switcher (Kód, Cursor, Databáze, Terminál)
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = SlateCardElevated,
                    contentColor = NeonEmerald,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = NeonEmerald
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        text = { Text("💻 Kód", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        text = { Text("⚡ Cursor & Prompty", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTabIndex == 2,
                        onClick = { selectedTabIndex = 2 },
                        text = { Text("🗄️ DB & API", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTabIndex == 3,
                        onClick = { selectedTabIndex = 3 },
                        text = { Text("🚀 Terminál", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Content Area or Loading State
                if (isLoading) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                color = NeonEmerald,
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Neo generuje vibe kód a prompt balíček...",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = NeonEmerald
                            )
                            Text(
                                text = "Příprava ${selectedStack.displayName} scaffoldu",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }
                } else if (artifact != null) {
                    Box(modifier = Modifier.weight(1f)) {
                        when (selectedTabIndex) {
                            0 -> CodeSnippetTab(artifact = artifact, onCopy = { copyToClipboard("Zdrojový kód", artifact.codeSnippet) })
                            1 -> CursorPromptTab(
                                artifact = artifact,
                                onCopyCursor = { copyToClipboard("Cursor Prompt", artifact.cursorPrompt) },
                                onCopyCopilot = { copyToClipboard("Copilot Instrukce", artifact.copilotInstructions) }
                            )
                            2 -> DatabaseApiTab(
                                artifact = artifact,
                                onCopyDb = { copyToClipboard("Databázové schéma", artifact.dbSchema) },
                                onCopyApi = { copyToClipboard("API Specifikace", artifact.apiSpec) }
                            )
                            3 -> TerminalTab(
                                artifact = artifact,
                                onCopyCommands = {
                                    val all = artifact.terminalSetupCommands.joinToString("\n")
                                    copyToClipboard("Terminálové příkazy", all)
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom Action Bar: Regenerate & Neo's Advice Box
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    artifact?.explanation?.let { explanation ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 12.dp)
                        ) {
                            Text(text = "💡", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = explanation,
                                fontSize = 10.sp,
                                color = TextSecondary,
                                maxLines = 2
                            )
                        }
                    }

                    Button(
                        onClick = { onRegenerate(selectedStack) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonEmerald,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .height(40.dp)
                            .testTag("btn_regenerate_vibe")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Znovu s AI",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CodeSnippetTab(
    artifact: VibeCodingArtifact,
    onCopy: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // File header bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .background(SlateCardElevated, RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                .border(1.dp, TechBorder, RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Code,
                    contentDescription = null,
                    tint = NeonEmerald,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = artifact.filename,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonEmerald
                )
            }

            OutlinedButton(
                onClick = onCopy,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, TechBorder),
                modifier = Modifier
                    .height(30.dp)
                    .testTag("btn_copy_code")
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = null,
                    tint = TextPrimary,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Kopírovat kód",
                    fontSize = 10.sp,
                    color = TextPrimary
                )
            }
        }

        // Monospace code block
        Surface(
            color = ObsidianBg,
            border = BorderStroke(1.dp, TechBorder),
            shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            SelectionContainer {
                Text(
                    text = artifact.codeSnippet,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = Color(0xFFE2E8F0),
                    lineHeight = 16.sp,
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .horizontalScroll(rememberScrollState())
                        .padding(14.dp)
                )
            }
        }
    }
}

@Composable
private fun CursorPromptTab(
    artifact: VibeCodingArtifact,
    onCopyCursor: () -> Unit,
    onCopyCopilot: () -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            // Cursor / Claude Composer prompt card
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = SlateCardElevated,
                border = BorderStroke(1.dp, TechBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🎯", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Cursor Composer & .cursorrules",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonCyan
                            )
                        }

                        Button(
                            onClick = onCopyCursor,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonCyan.copy(alpha = 0.2f),
                                contentColor = NeonCyan
                            ),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, null, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Kopírovat pro Cursor", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    SelectionContainer {
                        Text(
                            text = artifact.cursorPrompt,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = TextPrimary,
                            lineHeight = 16.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(ObsidianBg, RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        )
                    }
                }
            }
        }

        item {
            // GitHub Copilot instructions card
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = SlateCardElevated,
                border = BorderStroke(1.dp, TechBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🤖", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "GitHub Copilot (.github/copilot-instructions.md)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonPurple
                            )
                        }

                        Button(
                            onClick = onCopyCopilot,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonPurple.copy(alpha = 0.2f),
                                contentColor = NeonPurple
                            ),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, null, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Kopírovat", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    SelectionContainer {
                        Text(
                            text = artifact.copilotInstructions,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = TextPrimary,
                            lineHeight = 16.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(ObsidianBg, RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DatabaseApiTab(
    artifact: VibeCodingArtifact,
    onCopyDb: () -> Unit,
    onCopyApi: () -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            // Database Schema Card
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = SlateCardElevated,
                border = BorderStroke(1.dp, TechBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🗄️", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Databázové schéma (Prisma & SQL)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonAmber
                            )
                        }

                        Button(
                            onClick = onCopyDb,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonAmber.copy(alpha = 0.2f),
                                contentColor = NeonAmber
                            ),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, null, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Kopírovat DB", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    SelectionContainer {
                        Text(
                            text = artifact.dbSchema,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = TextPrimary,
                            lineHeight = 16.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(ObsidianBg, RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        )
                    }
                }
            }
        }

        item {
            // REST API Contract Card
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = SlateCardElevated,
                border = BorderStroke(1.dp, TechBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "⚡", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "REST API Endpointy & Curl",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonEmerald
                            )
                        }

                        Button(
                            onClick = onCopyApi,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonEmerald.copy(alpha = 0.2f),
                                contentColor = NeonEmerald
                            ),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, null, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Kopírovat API", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    SelectionContainer {
                        Text(
                            text = artifact.apiSpec,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = TextPrimary,
                            lineHeight = 16.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(ObsidianBg, RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TerminalTab(
    artifact: VibeCodingArtifact,
    onCopyCommands: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Terminal,
                    contentDescription = null,
                    tint = NeonEmerald,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Okamžité terminálové příkazy:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Button(
                onClick = onCopyCommands,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonEmerald,
                    contentColor = Color.Black
                ),
                modifier = Modifier.height(30.dp)
            ) {
                Icon(Icons.Default.ContentCopy, null, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Zkopírovat vše", fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(artifact.terminalSetupCommands) { cmd ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = ObsidianBg,
                    border = BorderStroke(1.dp, TechBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "$ ",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonEmerald
                        )
                        SelectionContainer(modifier = Modifier.weight(1f)) {
                            Text(
                                text = cmd,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}
