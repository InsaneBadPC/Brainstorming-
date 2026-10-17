package com.example.ui

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Slideshow
import androidx.compose.material.icons.filled.ViewTimeline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SystemUpdate
import com.example.BuildConfig
import com.example.data.model.AiPersona
import com.example.data.model.CustomPersona
import com.example.data.model.MindNode
import com.example.ui.canvas.CleanEmptyStateView
import com.example.ui.canvas.MindMapCanvasView
import com.example.ui.dialogs.AppUpdateDialog
import com.example.ui.dialogs.AuditDebateDialog
import com.example.ui.dialogs.BrainstormCoPilotSheet
import com.example.ui.dialogs.CloudSyncDialog
import com.example.ui.dialogs.CreateNodeDialog
import com.example.ui.dialogs.CreatePersonaDialog
import com.example.ui.dialogs.EditNodeDialog
import com.example.ui.dialogs.ExportShareDialog
import com.example.ui.dialogs.FeaturesCatalogDialog
import com.example.ui.dialogs.GoogleModelSelectorDialog
import com.example.ui.dialogs.ImportNotesDialog
import com.example.ui.dialogs.VibeCodingDialog
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Terminal
import com.example.ui.dialogs.NewMapDialog
import com.example.ui.dialogs.SwotAnalysisDialog
import com.example.ui.dialogs.VersionHistoryDialog
import com.example.ui.dialogs.WhatIfSimulationDialog
import com.example.ui.matrix.PrioritizationMatrixView
import com.example.ui.pitch.PitchPresentationOverlay
import com.example.ui.roadmap.RoadmapTimelineView
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
import com.example.ui.voice.VoiceWaveformBar
import com.example.voice.VoiceState

enum class CanvasViewMode {
    CANVAS,
    ROADMAP,
    MATRIX
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MindMapScreen(
    viewModel: MindMapViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val maps by viewModel.allMaps.collectAsState()
    val currentMapId by viewModel.currentMapId.collectAsState()
    val nodes by viewModel.nodes.collectAsState()
    val edges by viewModel.edges.collectAsState()
    val selectedNodeId by viewModel.selectedNodeId.collectAsState()
    val selectedPersona by viewModel.selectedPersona.collectAsState()
    val customPersonas by viewModel.customPersonas.collectAsState()
    val selectedCustomPersona by viewModel.selectedCustomPersona.collectAsState()
    val isGoogleSearchEnabled by viewModel.isGoogleSearchEnabled.collectAsState()
    val panOffset by viewModel.panOffset.collectAsState()
    val scale by viewModel.zoomScale.collectAsState()
    val isAiProcessing by viewModel.isAiProcessing.collectAsState()
    val brainstormMessages by viewModel.brainstormMessages.collectAsState()
    val solutionBlueprint by viewModel.solutionBlueprint.collectAsState()

    val currentUser by viewModel.currentUser.collectAsState()
    val isCloudSyncing by viewModel.isCloudSyncing.collectAsState()
    val lastCloudSyncTimestamp by viewModel.lastCloudSyncTimestamp.collectAsState()
    val cloudSyncMessage by viewModel.cloudSyncMessage.collectAsState()
    val snapshots by viewModel.snapshots.collectAsState()

    val isAuditRunning by viewModel.isAuditRunning.collectAsState()
    val auditCurrentRound by viewModel.auditCurrentRound.collectAsState()
    val auditTotalRounds by viewModel.auditTotalRounds.collectAsState()
    val auditSpeeches by viewModel.auditSpeeches.collectAsState()
    val auditOutdatedTools by viewModel.auditOutdatedTools.collectAsState()
    val auditReport by viewModel.auditReport.collectAsState()
    val currentAuditingSpeaker by viewModel.currentAuditingSpeaker.collectAsState()

    val voiceState by viewModel.voiceManager.voiceState.collectAsState()
    val transcription by viewModel.voiceManager.transcription.collectAsState()
    val audioRms by viewModel.voiceManager.audioRms.collectAsState()

    val appUpdateInfo by viewModel.appUpdateInfo.collectAsState()
    val isCheckingUpdate by viewModel.isCheckingUpdate.collectAsState()
    val updateCheckMessage by viewModel.updateCheckMessage.collectAsState()
    val showUpdateDialog by viewModel.showUpdateDialog.collectAsState()
    val selectedGoogleModel by viewModel.selectedGoogleModel.collectAsState()
    val vibeArtifact by viewModel.vibeArtifact.collectAsState()
    val isVibeLoading by viewModel.isVibeLoading.collectAsState()
    val selectedVibeStack by viewModel.selectedVibeStack.collectAsState()

    LaunchedEffect(updateCheckMessage) {
        updateCheckMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearUpdateMessage()
        }
    }

    var showMapSelectorMenu by remember { mutableStateOf(false) }
    var showMoreToolsMenu by remember { mutableStateOf(false) }
    var showNewMapDialog by remember { mutableStateOf(false) }
    var showAddNodeDialog by remember { mutableStateOf(false) }
    var nodeToEdit by remember { mutableStateOf<MindNode?>(null) }
    var showCoPilotSheet by remember { mutableStateOf(false) }
    var connectingSourceNodeId by remember { mutableStateOf<String?>(null) }
    var canvasViewMode by remember { mutableStateOf(CanvasViewMode.CANVAS) }
    var showPitchPresentation by remember { mutableStateOf(false) }
    var showWhatIfDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var showCreatePersonaDialog by remember { mutableStateOf(false) }
    var showCloudSyncDialog by remember { mutableStateOf(false) }
    var showSwotDialog by remember { mutableStateOf(false) }
    var showVersionHistoryDialog by remember { mutableStateOf(false) }
    var showAuditDebateDialog by remember { mutableStateOf(false) }
    var showModelSelectorDialog by remember { mutableStateOf(false) }
    var showFeaturesCatalogDialog by remember { mutableStateOf(false) }
    var showVibeCodingDialog by remember { mutableStateOf(false) }

    val currentMap = maps.firstOrNull { it.id == currentMapId }
    val selectedNode = nodes.firstOrNull { it.id == selectedNodeId }

    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.voiceManager.startListening { spokenText ->
                viewModel.aiProcessVoiceOrChatInput(spokenText)
            }
        } else {
            Toast.makeText(context, "Pro hlasový režim je potřeba povolit mikrofon", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = ObsidianBg,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 1. Interactive 2D Mind Map Canvas OR Roadmap Timeline View OR Prioritization Matrix
            when (canvasViewMode) {
                CanvasViewMode.ROADMAP -> {
                    RoadmapTimelineView(
                        nodes = nodes,
                        onToggleNodeCompleted = { id, done -> viewModel.toggleNodeCompletion(id, done) },
                        onSelectNode = { id ->
                            viewModel.selectNode(id)
                            canvasViewMode = CanvasViewMode.CANVAS
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 115.dp, bottom = 80.dp)
                    )
                }
                CanvasViewMode.MATRIX -> {
                    PrioritizationMatrixView(
                        nodes = nodes,
                        onToggleNodeCompleted = { id, done -> viewModel.toggleNodeCompletion(id, done) },
                        onSelectNode = { id ->
                            viewModel.selectNode(id)
                            canvasViewMode = CanvasViewMode.CANVAS
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 115.dp, bottom = 80.dp)
                    )
                }
                CanvasViewMode.CANVAS -> {
                    if (currentMap == null || maps.isEmpty()) {
                        CleanEmptyStateView(
                            onCreateMapClick = { showNewMapDialog = true },
                            onCreateQuickMap = { title, useAi ->
                                viewModel.createNewMap(
                                    title = title,
                                    prompt = if (useAi) title else ""
                                )
                            },
                            onVoiceClick = {
                                val hasPermission = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.RECORD_AUDIO
                                ) == PackageManager.PERMISSION_GRANTED

                                if (hasPermission) {
                                    viewModel.voiceManager.startListening { spoken ->
                                        viewModel.aiProcessVoiceOrChatInput(spoken)
                                    }
                                } else {
                                    audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                }
                            },
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 70.dp, bottom = 20.dp)
                        )
                    } else if (nodes.isEmpty()) {
                        CleanEmptyStateView(
                            onCreateMapClick = { showAddNodeDialog = true },
                            onCreateQuickMap = { title, useAi ->
                                if (useAi) {
                                    viewModel.aiProcessVoiceOrChatInput(title)
                                } else {
                                    viewModel.createCustomNode(
                                        title = title,
                                        details = "",
                                        category = com.example.data.model.NodeCategory.GOAL,
                                        parentId = null
                                    )
                                }
                            },
                            onVoiceClick = {
                                val hasPermission = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.RECORD_AUDIO
                                ) == PackageManager.PERMISSION_GRANTED

                                if (hasPermission) {
                                    viewModel.voiceManager.startListening { spoken ->
                                        viewModel.aiProcessVoiceOrChatInput(spoken)
                                    }
                                } else {
                                    audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                }
                            },
                            onOpenFeaturesCatalog = { showFeaturesCatalogDialog = true },
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 110.dp, bottom = 80.dp)
                        )
                    } else {
                        MindMapCanvasView(
                            nodes = nodes,
                            edges = edges,
                            selectedNodeId = selectedNodeId,
                            scale = scale,
                            panOffset = panOffset,
                            onPanDelta = { delta -> viewModel.onPanDelta(delta) },
                            onZoomChange = { zoomDelta, centroid -> viewModel.onZoomChange(zoomDelta, centroid) },
                            onResetZoom = { viewModel.resetView() },
                            onAutoLayout = { viewModel.autoLayout() },
                            onNodeSelect = { id ->
                                if (connectingSourceNodeId != null) {
                                    if (connectingSourceNodeId != id) {
                                        viewModel.connectNodes(connectingSourceNodeId!!, id)
                                        Toast.makeText(context, "Spojnice mezi uzly vytvořena", Toast.LENGTH_SHORT).show()
                                    }
                                    connectingSourceNodeId = null
                                } else {
                                    viewModel.selectNode(id)
                                }
                            },
                            onCanvasTap = {
                                if (connectingSourceNodeId != null) {
                                    connectingSourceNodeId = null
                                } else {
                                    viewModel.selectNode(null)
                                }
                            },
                            onNodeDrag = { id, dx, dy -> viewModel.onNodeDrag(id, dx, dy) },
                            onAddChild = { parentId -> viewModel.addManualChildNode(parentId) },
                            onAiDeepen = { node -> viewModel.aiDeepenNode(node) },
                            onGenerateDiagram = { node -> viewModel.generateProcessDiagram(node) },
                            onGenerateWireframe = { node -> viewModel.generateWireframeForNode(node) },
                            onEditNode = { node -> nodeToEdit = node },
                            onDeleteNode = { id -> viewModel.deleteNode(id) },
                            onOpenFeaturesGuide = { showFeaturesCatalogDialog = true },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            // 2. Connecting Mode Top Banner (when user is creating a manual edge)
            AnimatedVisibility(
                visible = connectingSourceNodeId != null,
                enter = fadeIn() + slideInVertically { -it },
                exit = fadeOut() + slideOutVertically { -it },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(top = 100.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = NeonCyan.copy(alpha = 0.95f),
                    shadowElevation = 10.dp
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Klepněte na druhý uzel pro vytvoření spojnice",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        IconButton(
                            onClick = { connectingSourceNodeId = null },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Zrušit",
                                tint = Color.Black
                            )
                        }
                    }
                }
            }

            // 3. Top App Bar & Controls Overlay
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
            ) {
                Surface(
                    color = SlateCard.copy(alpha = 0.94f),
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .statusBarsPadding()
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Left Section: Project Switcher & View Mode Toggle (when map active) or Clean Brand Header
                            if (currentMap != null) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(SlateCardElevated)
                                                .border(1.dp, TechBorder, RoundedCornerShape(12.dp))
                                                .clickable { showMapSelectorMenu = true }
                                                .padding(horizontal = 9.dp, vertical = 6.dp)
                                                .testTag("btn_switch_map")
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .background(NeonCyan, CircleShape)
                                            )
                                            Spacer(modifier = Modifier.width(5.dp))
                                            Text(
                                                text = currentMap.title,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.widthIn(max = 110.dp)
                                            )
                                            Icon(
                                                imageVector = Icons.Default.KeyboardArrowDown,
                                                contentDescription = null,
                                                tint = TextSecondary,
                                                modifier = Modifier.size(15.dp)
                                            )
                                        }

                                        DropdownMenu(
                                            expanded = showMapSelectorMenu,
                                            onDismissRequest = { showMapSelectorMenu = false },
                                            containerColor = SlateCard
                                        ) {
                                            DropdownMenuItem(
                                                text = {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(
                                                            imageVector = Icons.Default.Add,
                                                            contentDescription = null,
                                                            tint = NeonCyan,
                                                            modifier = Modifier.size(18.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Text("+ Nový brainstorming", color = NeonCyan, fontWeight = FontWeight.Bold)
                                                    }
                                                },
                                                onClick = {
                                                    showMapSelectorMenu = false
                                                    showNewMapDialog = true
                                                }
                                            )

                                            maps.forEach { map ->
                                                DropdownMenuItem(
                                                    text = {
                                                        Text(
                                                            text = map.title,
                                                            color = if (map.id == currentMapId) NeonCyan else TextPrimary
                                                        )
                                                    },
                                                    onClick = {
                                                        showMapSelectorMenu = false
                                                        viewModel.selectMap(map.id)
                                                    }
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    // View Mode Pill Switcher: Mapa vs. Fáze vs. Matice 2x2
                                    Row(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(SlateCardElevated)
                                            .border(1.dp, TechBorder, RoundedCornerShape(10.dp))
                                            .padding(2.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (canvasViewMode == CanvasViewMode.CANVAS) NeonCyan.copy(alpha = 0.25f) else Color.Transparent)
                                                .clickable { canvasViewMode = CanvasViewMode.CANVAS }
                                                .padding(horizontal = 7.dp, vertical = 4.dp)
                                                .testTag("btn_mode_canvas")
                                        ) {
                                            Text(
                                                text = "🗺️ Mapa",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (canvasViewMode == CanvasViewMode.CANVAS) NeonCyan else TextMuted
                                            )
                                        }
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (canvasViewMode == CanvasViewMode.ROADMAP) NeonEmerald.copy(alpha = 0.25f) else Color.Transparent)
                                                .clickable { canvasViewMode = CanvasViewMode.ROADMAP }
                                                .padding(horizontal = 7.dp, vertical = 4.dp)
                                                .testTag("btn_mode_roadmap")
                                        ) {
                                            Text(
                                                text = "📊 Fáze",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (canvasViewMode == CanvasViewMode.ROADMAP) NeonEmerald else TextMuted
                                            )
                                        }
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (canvasViewMode == CanvasViewMode.MATRIX) NeonPurple.copy(alpha = 0.25f) else Color.Transparent)
                                                .clickable { canvasViewMode = CanvasViewMode.MATRIX }
                                                .padding(horizontal = 7.dp, vertical = 4.dp)
                                                .testTag("btn_mode_matrix")
                                        ) {
                                            Text(
                                                text = "🎯 Matice",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (canvasViewMode == CanvasViewMode.MATRIX) NeonPurple else TextMuted
                                            )
                                        }
                                    }
                                }
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(32.dp)
                                            .background(NeonCyan.copy(alpha = 0.15f), CircleShape)
                                            .border(1.dp, NeonCyan.copy(alpha = 0.4f), CircleShape)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Hub,
                                            contentDescription = null,
                                            tint = NeonCyan,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "MindCraft AI",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                }
                            }

                            // Top Action Icons
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (isAiProcessing || isCloudSyncing) {
                                    CircularProgressIndicator(
                                        color = if (isCloudSyncing) NeonCyan else NeonPurple,
                                        strokeWidth = 2.dp,
                                        modifier = Modifier
                                            .size(18.dp)
                                            .padding(end = 4.dp)
                                    )
                                }

                                // Google Free AI Model Chip
                                Surface(
                                    shape = RoundedCornerShape(9.dp),
                                    color = SlateCardElevated,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, TechBorder),
                                    modifier = Modifier
                                        .clickable { showModelSelectorDialog = true }
                                        .testTag("chip_google_ai_model")
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            tint = NeonCyan,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = selectedGoogleModel.badge,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NeonCyan
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(6.dp))

                                // Quick New Map button
                                IconButton(
                                    onClick = { showNewMapDialog = true },
                                    modifier = Modifier
                                        .size(34.dp)
                                        .background(NeonCyan.copy(alpha = 0.15f), RoundedCornerShape(9.dp))
                                        .border(1.dp, NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(9.dp))
                                        .testTag("btn_quick_new_map")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Nová mapa",
                                        tint = NeonCyan,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(5.dp))

                                // Features Center & Guide Button (Prominent top bar access)
                                Surface(
                                    shape = RoundedCornerShape(9.dp),
                                    color = SlateCardElevated,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)),
                                    modifier = Modifier
                                        .clickable { showFeaturesCatalogDialog = true }
                                        .testTag("btn_top_features_catalog")
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Apps,
                                            contentDescription = "Centrum funkcí & Nápověda",
                                            tint = NeonCyan,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "⚡ Funkce",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NeonCyan
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(4.dp))

                                // Vibe Coding & Code Scaffolder Button
                                Surface(
                                    shape = RoundedCornerShape(9.dp),
                                    color = SlateCardElevated,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonEmerald.copy(alpha = 0.5f)),
                                    modifier = Modifier
                                        .clickable {
                                            if (vibeArtifact == null) {
                                                viewModel.generateVibeArtifact(targetNode = selectedNode)
                                            }
                                            showVibeCodingDialog = true
                                        }
                                        .testTag("btn_top_vibe_coding")
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Code,
                                            contentDescription = "Vibe Coding & Scaffolder",
                                            tint = NeonEmerald,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "💻 Vibe Code",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NeonEmerald
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(4.dp))

                                // More Tools Dropdown Menu (Clean centralized tools menu)
                                Box {
                                    IconButton(
                                        onClick = { showMoreToolsMenu = true },
                                        modifier = Modifier
                                            .size(34.dp)
                                            .background(SlateCardElevated, RoundedCornerShape(9.dp))
                                            .testTag("btn_more_tools")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.MoreVert,
                                            contentDescription = "Více možností",
                                            tint = TextPrimary,
                                            modifier = Modifier.size(17.dp)
                                        )
                                    }

                                    DropdownMenu(
                                        expanded = showMoreToolsMenu,
                                        onDismissRequest = { showMoreToolsMenu = false },
                                        containerColor = SlateCard
                                    ) {
                                        // Highlight: Features Catalog
                                        DropdownMenuItem(
                                            text = {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(
                                                            imageVector = Icons.Default.Apps,
                                                            contentDescription = null,
                                                            tint = NeonCyan,
                                                            modifier = Modifier.size(18.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Column {
                                                            Text(
                                                                text = "⚡ Centrum všech funkcí & Průvodce",
                                                                color = NeonCyan,
                                                                fontSize = 12.sp,
                                                                fontWeight = FontWeight.Bold
                                                            )
                                                            Text(
                                                                text = "Podrobné info, ikony a návody ke všem nástrojům",
                                                                color = TextSecondary,
                                                                fontSize = 9.sp
                                                            )
                                                        }
                                                    }
                                                    Icon(
                                                        imageVector = Icons.Default.Info,
                                                        contentDescription = null,
                                                        tint = NeonCyan,
                                                        modifier = Modifier.size(15.dp)
                                                    )
                                                }
                                            },
                                            onClick = {
                                                showMoreToolsMenu = false
                                                showFeaturesCatalogDialog = true
                                            }
                                        )

                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(1.dp)
                                                .background(TechBorder)
                                                .padding(vertical = 4.dp)
                                        )

                                        // Category: AI & Strategie
                                        Text(
                                            text = "AI, CODING & STRATEGIE",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NeonEmerald,
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                        )

                                        DropdownMenuItem(
                                            text = {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(imageVector = Icons.Default.Code, contentDescription = null, tint = NeonEmerald, modifier = Modifier.size(17.dp))
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Column {
                                                        Text("💻 Vibe Coding & Scaffolder (Neo AI)", color = NeonEmerald, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                        Text("Kód, Cursor pravidla, DB a terminálové příkazy", color = TextSecondary, fontSize = 9.sp)
                                                    }
                                                }
                                            },
                                            onClick = {
                                                showMoreToolsMenu = false
                                                if (vibeArtifact == null) {
                                                    viewModel.generateVibeArtifact(targetNode = selectedNode)
                                                }
                                                showVibeCodingDialog = true
                                            }
                                        )

                                        DropdownMenuItem(
                                            text = {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = NeonCoral, modifier = Modifier.size(17.dp))
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text("🛡️ Hloubkový Audit & Debata", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                                }
                                            },
                                            onClick = {
                                                showMoreToolsMenu = false
                                                showAuditDebateDialog = true
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = NeonEmerald, modifier = Modifier.size(17.dp))
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text("SWOT Strategická analýza", color = TextPrimary, fontSize = 12.sp)
                                                }
                                            },
                                            onClick = {
                                                showMoreToolsMenu = false
                                                showSwotDialog = true
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(imageVector = Icons.Default.HelpOutline, contentDescription = null, tint = NeonAmber, modifier = Modifier.size(17.dp))
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text("Otázkový útok (Question Storming)", color = TextPrimary, fontSize = 12.sp)
                                                }
                                            },
                                            onClick = {
                                                showMoreToolsMenu = false
                                                viewModel.aiQuestionStorming()
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Color(0xFF4285F4), modifier = Modifier.size(17.dp))
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text("Tržní benchmarky & Konkurence", color = TextPrimary, fontSize = 12.sp)
                                                }
                                            },
                                            onClick = {
                                                showMoreToolsMenu = false
                                                viewModel.aiMarketBenchmark()
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(imageVector = Icons.Default.Description, contentDescription = null, tint = NeonAmber, modifier = Modifier.size(17.dp))
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text("Funkční Blueprint & Architektura", color = TextPrimary, fontSize = 12.sp)
                                                }
                                            },
                                            onClick = {
                                                showMoreToolsMenu = false
                                                showCoPilotSheet = true
                                                if (solutionBlueprint == null) {
                                                    viewModel.aiGenerateBlueprint()
                                                }
                                            }
                                        )

                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(1.dp)
                                                .background(TechBorder)
                                                .padding(vertical = 4.dp)
                                        )

                                        // Category: Projekt & Export
                                        Text(
                                            text = "PROJEKT & SPRÁVA",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NeonPurple,
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                        )

                                        DropdownMenuItem(
                                            text = {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(17.dp))
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text("Export & Sdílení (Markdown, Jira)", color = TextPrimary, fontSize = 12.sp)
                                                }
                                            },
                                            onClick = {
                                                showMoreToolsMenu = false
                                                showExportDialog = true
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(imageVector = Icons.Default.FileUpload, contentDescription = null, tint = NeonEmerald, modifier = Modifier.size(17.dp))
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text("Importovat zápis / poznámky", color = TextPrimary, fontSize = 12.sp)
                                                }
                                            },
                                            onClick = {
                                                showMoreToolsMenu = false
                                                showImportDialog = true
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(imageVector = Icons.Default.History, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(17.dp))
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text("Časová osa & Verze (Snapshoty)", color = TextPrimary, fontSize = 12.sp)
                                                }
                                            },
                                            onClick = {
                                                showMoreToolsMenu = false
                                                showVersionHistoryDialog = true
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(imageVector = Icons.Default.Psychology, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(17.dp))
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text("+ Vytvořit vlastní AI personu", color = TextPrimary, fontSize = 12.sp)
                                                }
                                            },
                                            onClick = {
                                                showMoreToolsMenu = false
                                                showCreatePersonaDialog = true
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(imageVector = Icons.Default.Slideshow, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(17.dp))
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text("Pitch Prezentační Režim (Slideshow)", color = TextPrimary, fontSize = 12.sp)
                                                }
                                            },
                                            onClick = {
                                                showMoreToolsMenu = false
                                                showPitchPresentation = true
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(17.dp))
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text("Smazat aktuální mapu", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                                                }
                                            },
                                            onClick = {
                                                showMoreToolsMenu = false
                                                viewModel.deleteCurrentMap()
                                            }
                                        )

                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(1.dp)
                                                .background(TechBorder)
                                                .padding(vertical = 4.dp)
                                        )

                                        // Category: Systém & Účet
                                        Text(
                                            text = "CLOUD & NASTAVENÍ",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextMuted,
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                        )

                                        DropdownMenuItem(
                                            text = {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(imageVector = Icons.Default.CloudSync, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(17.dp))
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text("Cloud synchronizace & Google účet", color = TextPrimary, fontSize = 12.sp)
                                                }
                                            },
                                            onClick = {
                                                showMoreToolsMenu = false
                                                showCloudSyncDialog = true
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(17.dp))
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Column {
                                                        Text("Modely Google AI", color = TextPrimary, fontSize = 12.sp)
                                                        Text(text = selectedGoogleModel.displayName, color = NeonCyan, fontSize = 10.sp)
                                                    }
                                                }
                                            },
                                            onClick = {
                                                showMoreToolsMenu = false
                                                showModelSelectorDialog = true
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        imageVector = Icons.Default.SystemUpdate,
                                                        contentDescription = null,
                                                        tint = if (appUpdateInfo?.hasUpdate == true) NeonEmerald else NeonCyan,
                                                        modifier = Modifier.size(17.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Column {
                                                        Text("Aktualizace aplikace (GitHub)", color = TextPrimary, fontSize = 12.sp)
                                                        Text(
                                                            text = "v${BuildConfig.VERSION_NAME}${if (appUpdateInfo?.hasUpdate == true) " • Nová v${appUpdateInfo?.latestVersion}!" else ""}",
                                                            color = if (appUpdateInfo?.hasUpdate == true) NeonEmerald else TextMuted,
                                                            fontSize = 10.sp
                                                        )
                                                    }
                                                }
                                            },
                                            onClick = {
                                                showMoreToolsMenu = false
                                                if (appUpdateInfo?.hasUpdate == true) {
                                                    viewModel.showUpdateDialogExplicitly()
                                                } else {
                                                    viewModel.checkForUpdates(isUserInitiated = true)
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Quick Persona Filter Ribbon (3 Core AI + Custom Personas + Add Button) - only when map active
                        if (currentMap != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                item {
                                    val isAll = selectedPersona == null && selectedCustomPersona == null
                                    PersonaTopChip(
                                        label = "🌟 Kulatý stůl (3 AI)",
                                        color = NeonCyan,
                                        isSelected = isAll,
                                        onClick = {
                                            viewModel.selectPersona(null)
                                            viewModel.selectCustomPersona(null)
                                        }
                                    )
                                }
                                items(AiPersona.entries) { p ->
                                    val isSelected = selectedPersona == p && selectedCustomPersona == null
                                    PersonaTopChip(
                                        label = "${p.emoji} ${p.displayName}",
                                        color = p.color,
                                        isSelected = isSelected,
                                        onClick = { viewModel.selectPersona(p) }
                                    )
                                }
                                items(customPersonas) { cp ->
                                    val isSelected = selectedCustomPersona == cp
                                    PersonaTopChip(
                                        label = "${cp.emoji} ${cp.displayName}",
                                        color = cp.color,
                                        isSelected = isSelected,
                                        onClick = { viewModel.selectCustomPersona(cp) }
                                    )
                                }
                                item {
                                    PersonaTopChip(
                                        label = "+ Vlastní AI",
                                        color = NeonPurple,
                                        isSelected = false,
                                        onClick = { showCreatePersonaDialog = true }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 4. Contextual Selected Node Toolbar (Floats above bottom dock when a node is selected)
            if (currentMap != null) {
                AnimatedVisibility(
                    visible = selectedNode != null,
                    enter = fadeIn() + slideInVertically { it },
                    exit = fadeOut() + slideOutVertically { it },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(bottom = 76.dp, start = 12.dp, end = 12.dp)
                ) {
                    if (selectedNode != null) {
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = SlateCard.copy(alpha = 0.98f),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, selectedNode.category.defaultColor),
                            shadowElevation = 12.dp,
                            modifier = Modifier.widthIn(max = 500.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                // Selected Node Title Indicator
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .weight(1f, fill = false)
                                        .padding(end = 6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(selectedNode.category.defaultColor, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = selectedNode.title,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                // 1. Edit Node
                                IconButton(
                                    onClick = { nodeToEdit = selectedNode },
                                    modifier = Modifier
                                        .size(34.dp)
                                        .background(NeonCyan.copy(alpha = 0.18f), CircleShape)
                                        .testTag("btn_toolbar_edit")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Upravit uzel",
                                        tint = NeonCyan,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                // 2. Add Child Node
                                IconButton(
                                    onClick = { viewModel.addManualChildNode(selectedNode.id) },
                                    modifier = Modifier
                                        .size(34.dp)
                                        .background(SlateCardElevated, CircleShape)
                                        .testTag("btn_toolbar_add_child")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Přidat větev",
                                        tint = TextPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                // 3. Connect (Link)
                                IconButton(
                                    onClick = {
                                        connectingSourceNodeId = selectedNode.id
                                        Toast.makeText(context, "Klepněte na druhý uzel pro spojení", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier
                                        .size(34.dp)
                                        .background(Color(0xFF38BDF8).copy(alpha = 0.18f), CircleShape)
                                        .testTag("btn_toolbar_connect")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Link,
                                        contentDescription = "Propojit s jiným uzlem",
                                        tint = Color(0xFF38BDF8),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                // 4. Process Diagram
                                IconButton(
                                    onClick = { viewModel.generateProcessDiagram(selectedNode) },
                                    modifier = Modifier
                                        .size(34.dp)
                                        .background(NeonEmerald.copy(alpha = 0.18f), CircleShape)
                                        .testTag("btn_toolbar_diagram")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountTree,
                                        contentDescription = "Procesní diagram",
                                        tint = NeonEmerald,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                // 5. Vibe Coding & Scaffold for Selected Node
                                IconButton(
                                    onClick = {
                                        viewModel.generateVibeArtifact(targetNode = selectedNode)
                                        showVibeCodingDialog = true
                                    },
                                    modifier = Modifier
                                        .size(34.dp)
                                        .background(NeonEmerald.copy(alpha = 0.25f), CircleShape)
                                        .testTag("btn_toolbar_vibe_coding")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Code,
                                        contentDescription = "Vibe Coding (Kód & Cursor)",
                                        tint = NeonEmerald,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                // 6. Delete Node
                                IconButton(
                                    onClick = { viewModel.deleteNode(selectedNode.id) },
                                    modifier = Modifier
                                        .size(34.dp)
                                        .background(MaterialTheme.colorScheme.error.copy(alpha = 0.15f), CircleShape)
                                        .testTag("btn_toolbar_delete")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Smazat uzel",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                // 6. Info & Guide for node actions
                                IconButton(
                                    onClick = {
                                        showFeaturesCatalogDialog = true
                                    },
                                    modifier = Modifier
                                        .size(34.dp)
                                        .background(NeonCyan.copy(alpha = 0.12f), CircleShape)
                                        .testTag("btn_toolbar_info")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = "Podrobné info o funkcích",
                                        tint = NeonCyan,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                // 7. Close / Deselect
                                IconButton(
                                    onClick = { viewModel.selectNode(null) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Zavřít",
                                        tint = TextMuted,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 5. Unified Bottom Action Dock (Add Node + Voice Assistant + 3-AI Chat)
            if (currentMap != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(bottom = 12.dp, start = 12.dp, end = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 600.dp)
                    ) {
                        // Quick "+ Uzel" Manual Creation Button
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = SlateCard.copy(alpha = 0.95f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, TechBorder),
                            shadowElevation = 8.dp,
                            modifier = Modifier
                                .clickable { showAddNodeDialog = true }
                                .testTag("btn_manual_add_node")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 11.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .background(NeonCyan, CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Uzel",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Voice Waveform & Transcription Assistant
                        VoiceWaveformBar(
                            voiceState = voiceState,
                            transcription = transcription,
                            audioRms = audioRms,
                            isAiProcessing = isAiProcessing,
                            onMicClick = {
                                if (voiceState == VoiceState.LISTENING) {
                                    viewModel.voiceManager.stopListening()
                                } else {
                                    val hasPermission = ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.RECORD_AUDIO
                                    ) == PackageManager.PERMISSION_GRANTED

                                    if (hasPermission) {
                                        viewModel.voiceManager.startListening { spoken ->
                                            viewModel.aiProcessVoiceOrChatInput(spoken)
                                        }
                                    } else {
                                        audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                    }
                                }
                            },
                            onStopSpeaking = { viewModel.voiceManager.stopSpeaking() },
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Open Full 3-AI Roundtable Chat Sheet Button
                        FloatingActionButton(
                            onClick = { showCoPilotSheet = true },
                            containerColor = selectedPersona?.color ?: NeonPurple,
                            contentColor = Color.White,
                            shape = CircleShape,
                            modifier = Modifier
                                .size(48.dp)
                                .shadow(8.dp, CircleShape, spotColor = selectedPersona?.color ?: NeonPurple)
                                .testTag("btn_open_copilot")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Chat,
                                contentDescription = "Otevřít 3 AI Chat",
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }

            // 7. Dialogs & Sheets
            if (showNewMapDialog) {
                NewMapDialog(
                    onDismiss = { showNewMapDialog = false },
                    onCreateMap = { title, prompt ->
                        showNewMapDialog = false
                        viewModel.createNewMap(title, prompt)
                    }
                )
            }

            if (showAddNodeDialog) {
                CreateNodeDialog(
                    nodes = nodes,
                    initialParentId = selectedNodeId,
                    onDismiss = { showAddNodeDialog = false },
                    onCreate = { title, details, category, parentId ->
                        showAddNodeDialog = false
                        viewModel.createCustomNode(title, details, category, parentId)
                    }
                )
            }

            nodeToEdit?.let { node ->
                EditNodeDialog(
                    node = node,
                    onDismiss = { nodeToEdit = null },
                    onSave = { title, details, category, diagramData ->
                        viewModel.updateNodeWithDiagram(node.id, title, details, category, diagramData)
                        nodeToEdit = null
                    }
                )
            }

            BrainstormCoPilotSheet(
                isOpen = showCoPilotSheet,
                onDismiss = { showCoPilotSheet = false },
                messages = brainstormMessages,
                solutionBlueprint = solutionBlueprint,
                isProcessing = isAiProcessing,
                selectedNode = selectedNode,
                selectedPersona = selectedPersona,
                isGoogleSearchEnabled = isGoogleSearchEnabled,
                onSelectPersona = { p -> viewModel.selectPersona(p) },
                onToggleGoogleSearch = { viewModel.toggleGoogleSearch() },
                onSendMessage = { msg -> viewModel.aiProcessVoiceOrChatInput(msg) },
                onQuickAction = { prompt -> viewModel.aiProcessVoiceOrChatInput(prompt) },
                onSpeakMessage = { text, persona ->
                    viewModel.voiceManager.speak(
                        text = text,
                        speakerName = persona?.displayName ?: "AI",
                        pitch = persona?.pitch ?: 1f,
                        speechRate = persona?.speechRate ?: 1f
                    )
                },
                onGenerateBlueprint = { viewModel.aiGenerateBlueprint() }
            )

            if (showPitchPresentation) {
                PitchPresentationOverlay(
                    nodes = nodes,
                    mapTitle = currentMap?.title ?: "Projekt",
                    onClose = { showPitchPresentation = false },
                    onSpeakNode = { node -> viewModel.speakNode(node) }
                )
            }

            if (showWhatIfDialog) {
                WhatIfSimulationDialog(
                    onDismiss = { showWhatIfDialog = false },
                    onRunScenario = { scenarioPrompt ->
                        showWhatIfDialog = false
                        viewModel.aiProcessVoiceOrChatInput(scenarioPrompt)
                    }
                )
            }

            if (showExportDialog && currentMap != null) {
                ExportShareDialog(
                    isOpen = true,
                    onDismiss = { showExportDialog = false },
                    map = currentMap,
                    nodes = nodes,
                    edges = edges
                )
            }

            if (showImportDialog) {
                ImportNotesDialog(
                    onDismiss = { showImportDialog = false },
                    onImportText = { text ->
                        showImportDialog = false
                        viewModel.aiProcessVoiceOrChatInput("Importuj tyto poznámky ze schůzky a rozpracuj je do strukturované myšlenkové mapy:\n$text")
                    }
                )
            }

            if (showCreatePersonaDialog) {
                CreatePersonaDialog(
                    onDismiss = { showCreatePersonaDialog = false },
                    onSavePersona = { customP ->
                        showCreatePersonaDialog = false
                        viewModel.addCustomPersona(customP)
                    }
                )
            }

            if (showCloudSyncDialog) {
                CloudSyncDialog(
                    currentUser = currentUser,
                    isSyncing = isCloudSyncing,
                    lastSyncTimestamp = lastCloudSyncTimestamp,
                    syncMessage = cloudSyncMessage,
                    mapTitle = currentMap?.title ?: "Projekt",
                    nodeCount = nodes.size,
                    onDismiss = { showCloudSyncDialog = false },
                    onSignInWithGoogle = {
                        viewModel.signInWithGoogle(
                            onSuccess = {
                                Toast.makeText(context, "Přihlášení úspěšné!", Toast.LENGTH_SHORT).show()
                            },
                            onError = { err ->
                                Toast.makeText(context, "Chyba přihlášení: $err", Toast.LENGTH_LONG).show()
                            }
                        )
                    },
                    onSignOut = {
                        viewModel.signOut()
                        Toast.makeText(context, "Odhlášeno", Toast.LENGTH_SHORT).show()
                    },
                    onSyncNow = {
                        viewModel.syncCurrentMapToCloud()
                    }
                )
            }

            if (showSwotDialog) {
                SwotAnalysisDialog(
                    nodes = nodes,
                    onDismiss = { showSwotDialog = false },
                    onAddSwotItemToMap = { title, details, category ->
                        viewModel.createCustomNode(title, details, category, selectedNodeId)
                        Toast.makeText(context, "Přidáno do mapy: $title", Toast.LENGTH_SHORT).show()
                    },
                    onAiGenerateSwot = {
                        viewModel.aiProcessVoiceOrChatInput("Proveď hloubkovou SWOT analýzu tohoto projektu a vygeneruj silné stránky, slabiny, příležitosti a hrozby jako nové větve mapy.")
                    }
                )
            }

            if (showVersionHistoryDialog) {
                VersionHistoryDialog(
                    snapshots = snapshots,
                    currentNodeCount = nodes.size,
                    onDismiss = { showVersionHistoryDialog = false },
                    onCreateSnapshot = { name ->
                        viewModel.createSnapshot(name)
                        Toast.makeText(context, "Snapshot '$name' uložen!", Toast.LENGTH_SHORT).show()
                    },
                    onRestoreSnapshot = { snapshot ->
                        viewModel.restoreSnapshot(snapshot)
                        showVersionHistoryDialog = false
                        Toast.makeText(context, "Verze '${snapshot.description}' obnovena!", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            if (showAuditDebateDialog) {
                AuditDebateDialog(
                    projectTitle = currentMap?.title ?: "Projekt",
                    nodeCount = nodes.size,
                    isRunning = isAuditRunning,
                    currentRound = auditCurrentRound,
                    totalRounds = auditTotalRounds,
                    speeches = auditSpeeches,
                    outdatedTools = auditOutdatedTools,
                    report = auditReport,
                    currentSpeaker = currentAuditingSpeaker,
                    onDismiss = { showAuditDebateDialog = false },
                    onStartDebate = { rounds, enableVoice ->
                        viewModel.startAuditDebate(rounds, enableVoice)
                    },
                    onStopDebate = {
                        viewModel.stopAuditDebate()
                    },
                    onClearReport = {
                        viewModel.clearAuditReport()
                    }
                )
            }

            // Floating Active Audit Status Pill (if debate is running while inspecting map)
            if (isAuditRunning && !showAuditDebateDialog) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = SlateCardElevated,
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, NeonCoral),
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 90.dp)
                        .clickable { showAuditDebateDialog = true }
                        .testTag("floating_audit_indicator")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        CircularProgressIndicator(
                            color = NeonCoral,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "🛡️ Audit probíhá: Kolo $auditCurrentRound z $auditTotalRounds • Zobrazit debatu",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonCoral
                        )
                    }
                }
            }

            if (showUpdateDialog && appUpdateInfo != null) {
                AppUpdateDialog(
                    updateInfo = appUpdateInfo!!,
                    onDismiss = { viewModel.dismissUpdateDialog() },
                    onDownloadApk = { viewModel.downloadAndInstallUpdate(context) },
                    onOpenGitHubRelease = { viewModel.openGitHubRelease(context) },
                    onRefreshCheck = { viewModel.checkForUpdates(isUserInitiated = true) },
                    onSaveSettings = { slug, token -> viewModel.updateRepoSettings(slug, token) }
                )
            }

            // Floating update notification pill if update is available and dialog closed
            if (appUpdateInfo?.hasUpdate == true && !showUpdateDialog) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = SlateCardElevated,
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, NeonEmerald),
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .statusBarsPadding()
                        .padding(top = 70.dp)
                        .clickable { viewModel.showUpdateDialogExplicitly() }
                        .testTag("floating_update_pill")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SystemUpdate,
                            contentDescription = null,
                            tint = NeonEmerald,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "🚀 Nová verze v${appUpdateInfo?.latestVersion} k dispozici • Aktualizovat",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonEmerald
                        )
                    }
                }
            }

            if (showModelSelectorDialog) {
                GoogleModelSelectorDialog(
                    currentModel = selectedGoogleModel,
                    onDismiss = { showModelSelectorDialog = false },
                    onSelectModel = { model ->
                        viewModel.selectGoogleModel(model)
                        Toast.makeText(context, "Aktivován model: ${model.displayName}", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            if (showFeaturesCatalogDialog) {
                FeaturesCatalogDialog(
                    isOpen = true,
                    onDismiss = { showFeaturesCatalogDialog = false },
                    onOpenNewMap = { showNewMapDialog = true },
                    onAddManualNode = { showAddNodeDialog = true },
                    onResetZoom = { viewModel.resetView() },
                    onAutoLayout = { viewModel.autoLayout() },
                    onOpenAiChat = { showCoPilotSheet = true },
                    onStartVoice = {
                        val hasPermission = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.RECORD_AUDIO
                        ) == PackageManager.PERMISSION_GRANTED
                        if (hasPermission) {
                            viewModel.voiceManager.startListening { spoken ->
                                viewModel.aiProcessVoiceOrChatInput(spoken)
                            }
                        } else {
                            audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    },
                    onOpenAuditDebate = { showAuditDebateDialog = true },
                    onOpenSwot = { showSwotDialog = true },
                    onRunQuestionStorming = { viewModel.aiQuestionStorming() },
                    onRunMarketBenchmark = { viewModel.aiMarketBenchmark() },
                    onOpenBlueprint = {
                        showCoPilotSheet = true
                        if (solutionBlueprint == null) {
                            viewModel.aiGenerateBlueprint()
                        }
                    },
                    onSwitchToRoadmap = { canvasViewMode = CanvasViewMode.ROADMAP },
                    onSwitchToMatrix = { canvasViewMode = CanvasViewMode.MATRIX },
                    onSwitchToCanvas = { canvasViewMode = CanvasViewMode.CANVAS },
                    onOpenPitchPresentation = { showPitchPresentation = true },
                    onOpenCloudSync = { showCloudSyncDialog = true },
                    onOpenVersionHistory = { showVersionHistoryDialog = true },
                    onOpenExport = { showExportDialog = true },
                    onOpenImport = { showImportDialog = true },
                    onOpenCreatePersona = { showCreatePersonaDialog = true },
                    onOpenModelSelector = { showModelSelectorDialog = true },
                    onCheckUpdate = {
                        if (appUpdateInfo?.hasUpdate == true) {
                            viewModel.showUpdateDialogExplicitly()
                        } else {
                            viewModel.checkForUpdates(isUserInitiated = true)
                        }
                    },
                    onOpenVibeCoding = { showVibeCodingDialog = true },
                    hasActiveMap = (currentMap != null)
                )
            }

            if (showVibeCodingDialog) {
                VibeCodingDialog(
                    isOpen = true,
                    artifact = vibeArtifact,
                    isLoading = isVibeLoading,
                    selectedStack = selectedVibeStack,
                    selectedNode = selectedNode,
                    projectTitle = currentMap?.title ?: "Projekt",
                    onDismiss = { showVibeCodingDialog = false },
                    onSelectStack = { stack -> viewModel.selectVibeStack(stack) },
                    onRegenerate = { stack -> viewModel.generateVibeArtifact(stack, selectedNode) }
                )
            }
        }
    }
}

@Composable
private fun PersonaTopChip(
    label: String,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) color.copy(alpha = 0.22f) else SlateCardElevated)
            .border(
                1.dp,
                if (isSelected) color else color.copy(alpha = 0.3f),
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 9.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) color else TextSecondary
        )
    }
}
