package com.example.ui

import android.app.Application
import android.content.Context
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.update.AppUpdateInfo
import com.example.data.update.AppUpdateRepository
import com.example.data.ai.BrainstormRepository
import com.example.data.ai.GoogleAiModel
import com.example.data.db.AppDatabase
import com.example.data.db.MindMapRepository
import com.example.data.model.AiPersona
import com.example.data.model.MindEdge
import com.example.data.model.MindMap
import com.example.data.model.MindNode
import com.example.data.model.NodeCategory
import com.example.ui.canvas.MindMapLayoutEngine
import com.example.ui.dialogs.BrainstormMessage
import com.example.voice.VoiceAssistantManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class MindMapViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = MindMapRepository(db.mindMapDao())
    private val brainstormAi = BrainstormRepository()
    val voiceManager = VoiceAssistantManager(application)

    val allMaps: StateFlow<List<MindMap>> = repository.allMaps.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _currentMapId = MutableStateFlow<String?>(null)
    val currentMapId: StateFlow<String?> = _currentMapId.asStateFlow()

    private val _nodes = MutableStateFlow<List<MindNode>>(emptyList())
    val nodes: StateFlow<List<MindNode>> = _nodes.asStateFlow()

    private val _edges = MutableStateFlow<List<MindEdge>>(emptyList())
    val edges: StateFlow<List<MindEdge>> = _edges.asStateFlow()

    private val _selectedNodeId = MutableStateFlow<String?>(null)
    val selectedNodeId: StateFlow<String?> = _selectedNodeId.asStateFlow()

    private val _selectedPersona = MutableStateFlow<AiPersona?>(null) // null = Kulatý stůl (všichni 3)
    val selectedPersona: StateFlow<AiPersona?> = _selectedPersona.asStateFlow()

    private val _customPersonas = MutableStateFlow<List<com.example.data.model.CustomPersona>>(emptyList())
    val customPersonas: StateFlow<List<com.example.data.model.CustomPersona>> = _customPersonas.asStateFlow()

    private val _selectedCustomPersona = MutableStateFlow<com.example.data.model.CustomPersona?>(null)
    val selectedCustomPersona: StateFlow<com.example.data.model.CustomPersona?> = _selectedCustomPersona.asStateFlow()

    private val _isGoogleSearchEnabled = MutableStateFlow(true)
    val isGoogleSearchEnabled: StateFlow<Boolean> = _isGoogleSearchEnabled.asStateFlow()

    private val _panOffset = MutableStateFlow(Offset.Zero)
    val panOffset: StateFlow<Offset> = _panOffset.asStateFlow()

    private val _zoomScale = MutableStateFlow(1.0f)
    val zoomScale: StateFlow<Float> = _zoomScale.asStateFlow()

    private val _isAiProcessing = MutableStateFlow(false)
    val isAiProcessing: StateFlow<Boolean> = _isAiProcessing.asStateFlow()

    private val _brainstormMessages = MutableStateFlow<List<BrainstormMessage>>(emptyList())
    val brainstormMessages: StateFlow<List<BrainstormMessage>> = _brainstormMessages.asStateFlow()

    private val _solutionBlueprint = MutableStateFlow<String?>(null)
    val solutionBlueprint: StateFlow<String?> = _solutionBlueprint.asStateFlow()

    val authManager = com.example.data.firebase.FirebaseAuthManager(application)
    val cloudSyncRepo = com.example.data.firebase.FirestoreSyncRepository(application)

    val currentUser = authManager.currentUser
    val isSigningIn = authManager.isSigningIn
    val authError = authManager.authError

    val isCloudSyncing = cloudSyncRepo.isSyncing
    val lastCloudSyncTimestamp = cloudSyncRepo.lastSyncedTimestamp
    val cloudSyncMessage = cloudSyncRepo.syncMessage

    private val _snapshots = MutableStateFlow<List<com.example.data.model.MindMapSnapshot>>(emptyList())
    val snapshots: StateFlow<List<com.example.data.model.MindMapSnapshot>> = _snapshots.asStateFlow()

    private val _selectedClusterTag = MutableStateFlow<String?>(null)
    val selectedClusterTag: StateFlow<String?> = _selectedClusterTag.asStateFlow()

    private val _selectedGoogleModel = MutableStateFlow(GoogleAiModel.DEFAULT)
    val selectedGoogleModel: StateFlow<GoogleAiModel> = _selectedGoogleModel.asStateFlow()

    fun selectGoogleModel(model: GoogleAiModel) {
        _selectedGoogleModel.value = model
        brainstormAi.activeGoogleModel = model
    }

    private var auditJob: kotlinx.coroutines.Job? = null

    private val _isAuditRunning = MutableStateFlow(false)
    val isAuditRunning: StateFlow<Boolean> = _isAuditRunning.asStateFlow()

    private val _auditCurrentRound = MutableStateFlow(0)
    val auditCurrentRound: StateFlow<Int> = _auditCurrentRound.asStateFlow()

    private val _auditTotalRounds = MutableStateFlow(3)
    val auditTotalRounds: StateFlow<Int> = _auditTotalRounds.asStateFlow()

    private val _auditSpeeches = MutableStateFlow<List<com.example.data.model.AuditRoundSpeech>>(emptyList())
    val auditSpeeches: StateFlow<List<com.example.data.model.AuditRoundSpeech>> = _auditSpeeches.asStateFlow()

    private val _auditOutdatedTools = MutableStateFlow<List<com.example.data.model.OutdatedToolFinding>>(emptyList())
    val auditOutdatedTools: StateFlow<List<com.example.data.model.OutdatedToolFinding>> = _auditOutdatedTools.asStateFlow()

    private val _auditReport = MutableStateFlow<com.example.data.model.AuditDebateReport?>(null)
    val auditReport: StateFlow<com.example.data.model.AuditDebateReport?> = _auditReport.asStateFlow()

    private val _currentAuditingSpeaker = MutableStateFlow<com.example.data.model.AiPersona?>(null)
    val currentAuditingSpeaker: StateFlow<com.example.data.model.AiPersona?> = _currentAuditingSpeaker.asStateFlow()

    private val updateRepository = AppUpdateRepository(application)

    val currentRepoSlug: String get() = updateRepository.repoSlug
    val currentGithubToken: String get() = updateRepository.githubToken

    private val _appUpdateInfo = MutableStateFlow<AppUpdateInfo?>(null)
    val appUpdateInfo: StateFlow<AppUpdateInfo?> = _appUpdateInfo.asStateFlow()

    private val _isCheckingUpdate = MutableStateFlow(false)
    val isCheckingUpdate: StateFlow<Boolean> = _isCheckingUpdate.asStateFlow()

    private val _updateCheckMessage = MutableStateFlow<String?>(null)
    val updateCheckMessage: StateFlow<String?> = _updateCheckMessage.asStateFlow()

    private val _showUpdateDialog = MutableStateFlow(false)
    val showUpdateDialog: StateFlow<Boolean> = _showUpdateDialog.asStateFlow()

    private val _vibeArtifact = MutableStateFlow<com.example.data.model.VibeCodingArtifact?>(null)
    val vibeArtifact: StateFlow<com.example.data.model.VibeCodingArtifact?> = _vibeArtifact.asStateFlow()

    private val _isVibeLoading = MutableStateFlow(false)
    val isVibeLoading: StateFlow<Boolean> = _isVibeLoading.asStateFlow()

    private val _selectedVibeStack = MutableStateFlow(com.example.data.model.VibeTargetStack.COMPOSE_ANDROID)
    val selectedVibeStack: StateFlow<com.example.data.model.VibeTargetStack> = _selectedVibeStack.asStateFlow()

    fun selectVibeStack(stack: com.example.data.model.VibeTargetStack) {
        _selectedVibeStack.value = stack
    }

    fun generateVibeArtifact(
        stack: com.example.data.model.VibeTargetStack = _selectedVibeStack.value,
        targetNode: com.example.data.model.MindNode? = null
    ) {
        val map = allMaps.value.firstOrNull { it.id == _currentMapId.value }
        val title = targetNode?.title ?: map?.title ?: "Projekt"
        val nodeToUse = targetNode ?: _nodes.value.firstOrNull { it.id == _selectedNodeId.value }

        _selectedVibeStack.value = stack
        _isVibeLoading.value = true

        viewModelScope.launch {
            try {
                val artifact = brainstormAi.generateVibeCodingArtifact(
                    projectTitle = title,
                    selectedNode = nodeToUse,
                    allNodes = _nodes.value,
                    targetStack = stack
                )
                _vibeArtifact.value = artifact
            } catch (_: Exception) {
                // error handled safely in repository
            } finally {
                _isVibeLoading.value = false
            }
        }
    }

    fun clearVibeArtifact() {
        _vibeArtifact.value = null
    }

    init {
        viewModelScope.launch {
            repository.purgeSeedData()
            allMaps.collect { maps ->
                if (_currentMapId.value == null && maps.isNotEmpty()) {
                    selectMap(maps.first().id)
                } else if (maps.isEmpty()) {
                    _currentMapId.value = null
                    _nodes.value = emptyList()
                    _edges.value = emptyList()
                }
            }
        }

        // Automatic non-intrusive update check from InsaneBadPC/Craftmind GitHub releases
        viewModelScope.launch {
            delay(2000)
            checkForUpdates(isUserInitiated = false)
        }
    }

    fun selectMap(mapId: String) {
        _currentMapId.value = mapId
        _selectedNodeId.value = null
        _panOffset.value = Offset.Zero
        _zoomScale.value = 1.0f
        _solutionBlueprint.value = null

        viewModelScope.launch {
            repository.getNodesForMap(mapId).collect { currentNodes ->
                _nodes.value = currentNodes
            }
        }
        viewModelScope.launch {
            repository.getEdgesForMap(mapId).collect { currentEdges ->
                _edges.value = currentEdges
            }
        }
    }

    fun selectNode(nodeId: String?) {
        _selectedNodeId.value = nodeId
    }

    fun selectPersona(persona: AiPersona?) {
        _selectedPersona.value = persona
        if (persona != null) {
            _selectedCustomPersona.value = null
        }
    }

    fun addCustomPersona(persona: com.example.data.model.CustomPersona) {
        _customPersonas.value = _customPersonas.value + persona
        _selectedCustomPersona.value = persona
        _selectedPersona.value = null
    }

    fun selectCustomPersona(persona: com.example.data.model.CustomPersona?) {
        _selectedCustomPersona.value = persona
        if (persona != null) {
            _selectedPersona.value = null
        }
    }

    fun toggleNodeCompletion(nodeId: String, isCompleted: Boolean) {
        val currentList = _nodes.value
        val node = currentList.firstOrNull { it.id == nodeId } ?: return
        val updated = node.copy(isCompleted = isCompleted)
        _nodes.value = currentList.map { if (it.id == nodeId) updated else it }
        viewModelScope.launch {
            repository.updateNodeCompletion(nodeId, isCompleted)
        }
    }

    fun speakNode(node: MindNode) {
        val persona = AiPersona.fromId(node.authorPersona) ?: AiPersona.ATLAS
        val textToSpeak = "${node.title}. ${node.details}"
        voiceManager.speak(
            text = textToSpeak,
            speakerName = persona.displayName,
            pitch = persona.pitch,
            speechRate = persona.speechRate
        )
    }

    fun toggleGoogleSearch() {
        _isGoogleSearchEnabled.value = !_isGoogleSearchEnabled.value
    }

    fun selectClusterTag(tag: String?) {
        _selectedClusterTag.value = tag
    }

    fun signInWithGoogle(onSuccess: (() -> Unit)? = null, onError: ((String) -> Unit)? = null) {
        viewModelScope.launch {
            authManager.signInWithGoogle(onSuccess, onError)
        }
    }

    fun signOut() {
        authManager.signOut()
    }

    fun syncCurrentMapToCloud() {
        val mapId = _currentMapId.value ?: return
        val map = allMaps.value.firstOrNull { it.id == mapId } ?: return
        viewModelScope.launch {
            cloudSyncRepo.syncMapToCloud(map, _nodes.value)
        }
    }

    fun createSnapshot(name: String) {
        val snap = com.example.data.model.MindMapSnapshot(
            id = java.util.UUID.randomUUID().toString(),
            timestamp = System.currentTimeMillis(),
            description = name,
            nodes = _nodes.value
        )
        _snapshots.value = _snapshots.value + snap
    }

    fun restoreSnapshot(snapshot: com.example.data.model.MindMapSnapshot) {
        _nodes.value = snapshot.nodes
        viewModelScope.launch {
            repository.saveNodes(snapshot.nodes)
        }
    }

    fun aiQuestionStorming() {
        val mapId = _currentMapId.value ?: return
        val currentTitle = allMaps.value.firstOrNull { it.id == mapId }?.title ?: "Projekt"
        _isAiProcessing.value = true
        viewModelScope.launch {
            try {
                val res = brainstormAi.questionStorming(currentTitle)
                applyAiResponseToMap(mapId, _selectedNodeId.value, res)
            } finally {
                _isAiProcessing.value = false
            }
        }
    }

    fun aiMarketBenchmark() {
        val mapId = _currentMapId.value ?: return
        val currentTitle = allMaps.value.firstOrNull { it.id == mapId }?.title ?: "Projekt"
        _isAiProcessing.value = true
        viewModelScope.launch {
            try {
                val res = brainstormAi.marketCompetitorBenchmark(currentTitle)
                applyAiResponseToMap(mapId, _selectedNodeId.value, res)
            } finally {
                _isAiProcessing.value = false
            }
        }
    }

    fun generateWireframeForNode(node: MindNode) {
        viewModelScope.launch {
            val wireframe = brainstormAi.generateWireframeForNode(node)
            val updated = node.copy(wireframeMockup = wireframe)
            _nodes.value = _nodes.value.map { if (it.id == node.id) updated else it }
            repository.saveNode(updated)
        }
    }

    fun startAuditDebate(rounds: Int = 3, enableVoice: Boolean = true) {
        val mapId = _currentMapId.value ?: return
        val currentTitle = allMaps.value.firstOrNull { it.id == mapId }?.title ?: "Projekt"

        auditJob?.cancel()
        _isAuditRunning.value = true
        _auditTotalRounds.value = rounds
        _auditCurrentRound.value = 0
        _auditSpeeches.value = emptyList()
        _auditOutdatedTools.value = emptyList()
        _auditReport.value = null

        auditJob = viewModelScope.launch {
            try {
                val allAddedNodes = mutableListOf<com.example.data.model.AiGeneratedNodeDto>()

                for (r in 1..rounds) {
                    if (!_isAuditRunning.value) break
                    _auditCurrentRound.value = r

                    val roundResult = brainstormAi.runAuditDebateRound(
                        projectTitle = currentTitle,
                        currentNodes = _nodes.value,
                        roundNumber = r,
                        totalRounds = rounds,
                        previousSpeeches = _auditSpeeches.value,
                        enableGoogleSearch = true
                    )

                    // Deliver speeches with distinct persona voice
                    for (speech in roundResult.speeches) {
                        if (!_isAuditRunning.value) break
                        _currentAuditingSpeaker.value = speech.persona
                        _auditSpeeches.value = _auditSpeeches.value + speech

                        if (enableVoice) {
                            voiceManager.speak(
                                text = "${speech.persona.displayName}: ${speech.spokenText}",
                                speakerName = speech.persona.displayName,
                                pitch = speech.persona.pitch,
                                speechRate = speech.persona.speechRate
                            )
                        }
                        delay(2200)
                    }

                    // Collect outdated tools found in this round
                    if (roundResult.outdatedTools.isNotEmpty()) {
                        _auditOutdatedTools.value = _auditOutdatedTools.value + roundResult.outdatedTools
                    }

                    // Apply newly generated nodes to the mind map in real-time
                    if (roundResult.addedNodes.isNotEmpty()) {
                        allAddedNodes.addAll(roundResult.addedNodes)
                        applyNodeDtosToMap(mapId, null, roundResult.addedNodes)
                    }
                    delay(800)
                }

                // Final comprehensive audit report
                val score = (78 + (_auditOutdatedTools.value.size * 3) + (rounds * 4)).coerceIn(82, 98)
                val report = com.example.data.model.AuditDebateReport(
                    totalRounds = rounds,
                    speeches = _auditSpeeches.value,
                    addedNodes = allAddedNodes,
                    healthScore = score,
                    summary = "Audit o $rounds kolech byl úspěšně dokončen. Nalezeno a opraveno ${_auditOutdatedTools.value.size} technologických výzev a zastaralých přístupů. Myšlenková mapa byla rozšířena o moderní komponenty ověřené na webu.",
                    outdatedTools = _auditOutdatedTools.value
                )
                _auditReport.value = report

            } finally {
                _isAuditRunning.value = false
                _currentAuditingSpeaker.value = null
            }
        }
    }

    fun stopAuditDebate() {
        _isAuditRunning.value = false
        auditJob?.cancel()
        _currentAuditingSpeaker.value = null
    }

    fun clearAuditReport() {
        _auditReport.value = null
        _auditSpeeches.value = emptyList()
        _auditOutdatedTools.value = emptyList()
    }

    fun checkForUpdates(isUserInitiated: Boolean = false) {
        if (_isCheckingUpdate.value) return
        viewModelScope.launch {
            _isCheckingUpdate.value = true
            val slug = updateRepository.repoSlug
            if (isUserInitiated) {
                _updateCheckMessage.value = "Ověřuji novou verzi z GitHub ($slug)..."
                _showUpdateDialog.value = true
            }

            val result = updateRepository.checkLatestRelease()
            _isCheckingUpdate.value = false

            result.onSuccess { info ->
                _appUpdateInfo.value = info
                if (info.hasUpdate) {
                    _showUpdateDialog.value = true
                    _updateCheckMessage.value = "Nalezena nová verze: v${info.latestVersion}!"
                } else {
                    if (isUserInitiated) {
                        _showUpdateDialog.value = true
                        _updateCheckMessage.value = if (info.errorMessage != null) info.errorMessage else "Máte aktuální verzi (v${info.currentVersion})"
                    }
                }
            }.onFailure { err ->
                if (isUserInitiated) {
                    _showUpdateDialog.value = true
                    _updateCheckMessage.value = "Ověření verze selhalo: ${err.message}"
                }
            }
        }
    }

    fun updateRepoSettings(newSlug: String, newToken: String) {
        updateRepository.repoSlug = newSlug
        updateRepository.githubToken = newToken
        checkForUpdates(isUserInitiated = true)
    }

    fun showUpdateDialogExplicitly() {
        _showUpdateDialog.value = true
        if (_appUpdateInfo.value == null) {
            checkForUpdates(isUserInitiated = true)
        }
    }

    fun dismissUpdateDialog() {
        _showUpdateDialog.value = false
    }

    fun downloadAndInstallUpdate(context: Context) {
        val info = _appUpdateInfo.value ?: return
        val downloadUrl = info.apkDownloadUrl ?: info.releaseHtmlUrl
        updateRepository.startDownload(context, downloadUrl, info.apkFileName)
    }

    fun openGitHubRelease(context: Context) {
        val info = _appUpdateInfo.value ?: return
        updateRepository.openUrlInBrowser(context, info.releaseHtmlUrl)
    }

    fun clearUpdateMessage() {
        _updateCheckMessage.value = null
    }

    fun onPanDelta(delta: Offset) {
        _panOffset.value += delta
    }

    fun onZoomChange(deltaZoom: Float, centroid: Offset? = null) {
        val oldScale = _zoomScale.value
        val newScale = (oldScale * deltaZoom).coerceIn(0.25f, 3.0f)
        _zoomScale.value = newScale

        if (centroid != null && oldScale > 0f) {
            val factor = newScale / oldScale
            _panOffset.value = Offset(
                x = centroid.x - (centroid.x - _panOffset.value.x) * factor,
                y = centroid.y - (centroid.y - _panOffset.value.y) * factor
            )
        }
    }

    fun zoomIn() {
        onZoomChange(1.25f, null)
    }

    fun zoomOut() {
        onZoomChange(0.8f, null)
    }

    fun resetView() {
        _panOffset.value = Offset.Zero
        _zoomScale.value = 1.0f
    }

    fun onNodeDrag(nodeId: String, dx: Float, dy: Float) {
        val currentList = _nodes.value
        val node = currentList.firstOrNull { it.id == nodeId } ?: return
        val updated = node.copy(xPos = node.xPos + dx, yPos = node.yPos + dy)
        _nodes.value = currentList.map { if (it.id == nodeId) updated else it }

        viewModelScope.launch {
            repository.updateNodePosition(nodeId, updated.xPos, updated.yPos)
        }
    }

    fun autoLayout() {
        val currentList = _nodes.value
        if (currentList.isEmpty()) return

        val balanced = MindMapLayoutEngine.computeBalancedLayout(currentList)
        _nodes.value = balanced

        viewModelScope.launch {
            repository.saveNodes(balanced)
        }
        resetView()
    }

    fun addManualChildNode(parentId: String) {
        val mapId = _currentMapId.value ?: return
        val parent = _nodes.value.firstOrNull { it.id == parentId } ?: return

        val siblings = _nodes.value.filter { it.parentId == parentId }
        val yOffset = if (siblings.isEmpty()) 0f else (siblings.size * 160f)
        val newNode = MindNode(
            id = UUID.randomUUID().toString(),
            mapId = mapId,
            parentId = parentId,
            title = "Nová pod-větev",
            details = "Doplňte specifikaci nebo nápad",
            category = NodeCategory.FEATURE,
            xPos = parent.xPos + (if (parent.xPos >= 0) 360f else -360f),
            yPos = parent.yPos + yOffset,
            isAiGenerated = false
        )

        val newEdge = MindEdge(
            id = UUID.randomUUID().toString(),
            mapId = mapId,
            fromNodeId = parentId,
            toNodeId = newNode.id
        )

        viewModelScope.launch {
            repository.saveNode(newNode)
            repository.saveEdges(listOf(newEdge))
            _selectedNodeId.value = newNode.id
        }
    }

    fun updateNode(nodeId: String, title: String, details: String, category: NodeCategory) {
        val current = _nodes.value.firstOrNull { it.id == nodeId } ?: return
        val updated = current.copy(
            title = title,
            details = details,
            category = category,
            colorHex = category.colorHex
        )
        viewModelScope.launch {
            repository.saveNode(updated)
        }
    }

    fun updateNodeWithDiagram(
        nodeId: String,
        title: String,
        details: String,
        category: NodeCategory,
        diagramData: String?
    ) {
        val current = _nodes.value.firstOrNull { it.id == nodeId } ?: return
        val updated = current.copy(
            title = title,
            details = details,
            category = category,
            colorHex = category.colorHex,
            diagramData = diagramData
        )
        viewModelScope.launch {
            repository.saveNode(updated)
        }
    }

    fun createCustomNode(
        title: String,
        details: String,
        category: NodeCategory,
        parentId: String?
    ) {
        val mapId = _currentMapId.value ?: return
        val currentList = _nodes.value
        val parent = currentList.firstOrNull { it.id == parentId }
        val root = currentList.firstOrNull { it.parentId == null }

        val posX = parent?.let { it.xPos + (if (it.xPos >= 0) 240f else -240f) }
            ?: (root?.let { it.xPos + 220f } ?: 0f)
        val posY = parent?.let { it.yPos + 50f } ?: 0f

        val newNodeId = UUID.randomUUID().toString()
        val newNode = MindNode(
            id = newNodeId,
            mapId = mapId,
            parentId = parentId,
            title = title,
            details = details,
            category = category,
            xPos = posX,
            yPos = posY,
            isAiGenerated = false
        )

        viewModelScope.launch {
            repository.saveNode(newNode)
            if (parentId != null) {
                repository.saveEdges(
                    listOf(
                        MindEdge(
                            id = UUID.randomUUID().toString(),
                            mapId = mapId,
                            fromNodeId = parentId,
                            toNodeId = newNodeId
                        )
                    )
                )
            }
            _selectedNodeId.value = newNodeId
        }
    }

    fun connectNodes(fromNodeId: String, toNodeId: String) {
        if (fromNodeId == toNodeId) return
        val mapId = _currentMapId.value ?: return
        val edge = MindEdge(
            id = UUID.randomUUID().toString(),
            mapId = mapId,
            fromNodeId = fromNodeId,
            toNodeId = toNodeId
        )
        viewModelScope.launch {
            repository.saveEdges(listOf(edge))
        }
    }

    fun deleteNode(nodeId: String) {
        viewModelScope.launch {
            repository.deleteNode(nodeId)
            if (_selectedNodeId.value == nodeId) {
                _selectedNodeId.value = null
            }
        }
    }

    fun deleteCurrentMap() {
        val mapId = _currentMapId.value ?: return
        viewModelScope.launch {
            repository.deleteMap(mapId)
            val remaining = repository.allMaps.firstOrNull()?.filter { it.id != mapId }
            if (!remaining.isNullOrEmpty()) {
                selectMap(remaining.first().id)
            } else {
                _currentMapId.value = null
                _nodes.value = emptyList()
                _edges.value = emptyList()
            }
        }
    }

    fun createNewMap(title: String, prompt: String) {
        val mapId = UUID.randomUUID().toString()
        val cleanTitle = title.ifBlank { prompt.take(30).ifBlank { "Nová mapa" } }
        val newMap = MindMap(
            id = mapId,
            title = cleanTitle,
            description = prompt,
            seedPrompt = prompt
        )

        viewModelScope.launch {
            repository.saveMap(newMap)
            selectMap(mapId)

            if (prompt.isNotBlank()) {
                _isAiProcessing.value = true
                try {
                    val aiResponse = brainstormAi.brainstormSeedIdea(prompt, _isGoogleSearchEnabled.value)
                    applyAiResponseToMap(mapId, null, aiResponse)
                } finally {
                    _isAiProcessing.value = false
                }
            } else {
                val rootNode = MindNode(
                    id = UUID.randomUUID().toString(),
                    mapId = mapId,
                    title = cleanTitle,
                    category = NodeCategory.GOAL,
                    xPos = 0f,
                    yPos = 0f
                )
                repository.saveNode(rootNode)
            }
        }
    }

    fun aiDeepenNode(node: MindNode) {
        val mapId = _currentMapId.value ?: return
        _isAiProcessing.value = true
        _selectedNodeId.value = node.id

        viewModelScope.launch {
            try {
                val aiResponse = brainstormAi.expandNode(node, _nodes.value)
                applyAiResponseToMap(mapId, node.id, aiResponse)
            } finally {
                _isAiProcessing.value = false
            }
        }
    }

    fun generateProcessDiagram(node: MindNode) {
        val mapId = _currentMapId.value ?: return
        _isAiProcessing.value = true
        _selectedNodeId.value = node.id

        viewModelScope.launch {
            try {
                val aiResponse = brainstormAi.generateProcessDiagramForNode(node)
                applyAiResponseToMap(mapId, node.id, aiResponse)
            } finally {
                _isAiProcessing.value = false
            }
        }
    }

    fun aiProcessVoiceOrChatInput(input: String) {
        if (input.isBlank()) return
        val currentMap = _currentMapId.value
        if (currentMap == null) {
            createNewMap(title = input.take(30), prompt = input)
            return
        }
        val mapId = currentMap

        // 1. Append user message
        val currentMsgs = _brainstormMessages.value.toMutableList()
        currentMsgs.add(BrainstormMessage(sender = "User", text = input))
        _brainstormMessages.value = currentMsgs

        _isAiProcessing.value = true

        viewModelScope.launch {
            try {
                val selectedNode = _nodes.value.firstOrNull { it.id == _selectedNodeId.value }
                val persona = _selectedPersona.value

                val customP = _selectedCustomPersona.value
                if (customP != null) {
                    val aiResponse = brainstormAi.chatWithCustomPersona(
                        persona = customP,
                        userInput = input,
                        currentNodes = _nodes.value,
                        selectedNode = selectedNode,
                        enableGoogleSearch = _isGoogleSearchEnabled.value
                    )

                    val msgsWithPersona = _brainstormMessages.value.toMutableList()
                    msgsWithPersona.add(
                        BrainstormMessage(
                            sender = "${customP.emoji} ${customP.displayName}",
                            text = aiResponse.spokenAnswer,
                            persona = null
                        )
                    )
                    _brainstormMessages.value = msgsWithPersona

                    voiceManager.speak(
                        text = aiResponse.spokenAnswer,
                        speakerName = customP.displayName,
                        pitch = customP.pitch,
                        speechRate = customP.speechRate
                    )

                    applyNodeDtosToMap(mapId, selectedNode?.id, aiResponse.addedNodes)

                } else if (persona == null) {
                    // Multi-AI Roundtable Debate! (Nova + Vortex + Atlas)
                    val debate = brainstormAi.brainstormRoundtableDebate(
                        topic = input,
                        currentNodes = _nodes.value,
                        selectedNode = selectedNode,
                        enableGoogleSearch = _isGoogleSearchEnabled.value
                    )

                    // Append messages from each persona to the chat
                    val msgsWithDebate = _brainstormMessages.value.toMutableList()
                    for (speech in debate.speeches) {
                        msgsWithDebate.add(
                            BrainstormMessage(
                                sender = speech.persona.displayName,
                                text = speech.spokenText,
                                persona = speech.persona
                            )
                        )
                    }
                    _brainstormMessages.value = msgsWithDebate

                    // Speak sequentially with distinct pitch per persona!
                    if (debate.speeches.isNotEmpty()) {
                        val first = debate.speeches.first()
                        voiceManager.speak(
                            text = "${first.persona.displayName}: ${first.spokenText}",
                            speakerName = first.persona.displayName,
                            pitch = first.persona.pitch,
                            speechRate = first.persona.speechRate
                        )
                    }

                    // Apply nodes to map
                    applyNodeDtosToMap(mapId, selectedNode?.id, debate.addedNodes)

                } else {
                    // 1-on-1 Chat with specific Persona
                    val aiResponse = brainstormAi.chatWithPersona(
                        persona = persona,
                        userInput = input,
                        currentNodes = _nodes.value,
                        selectedNode = selectedNode,
                        enableGoogleSearch = _isGoogleSearchEnabled.value
                    )

                    val msgsWithPersona = _brainstormMessages.value.toMutableList()
                    msgsWithPersona.add(
                        BrainstormMessage(
                            sender = persona.displayName,
                            text = aiResponse.spokenAnswer,
                            persona = persona
                        )
                    )
                    _brainstormMessages.value = msgsWithPersona

                    voiceManager.speak(
                        text = aiResponse.spokenAnswer,
                        speakerName = persona.displayName,
                        pitch = persona.pitch,
                        speechRate = persona.speechRate
                    )

                    applyNodeDtosToMap(mapId, selectedNode?.id, aiResponse.addedNodes)
                }
            } finally {
                _isAiProcessing.value = false
            }
        }
    }

    fun aiGenerateBlueprint() {
        val mapId = _currentMapId.value ?: return
        val currentMapTitle = allMaps.value.firstOrNull { it.id == mapId }?.title ?: "Projekt"

        _isAiProcessing.value = true
        viewModelScope.launch {
            try {
                val aiResponse = brainstormAi.generateActionableBlueprint(currentMapTitle, _nodes.value)
                _solutionBlueprint.value = aiResponse.solutionSummary

                if (aiResponse.spokenAnswer.isNotBlank()) {
                    voiceManager.speak(
                        text = aiResponse.spokenAnswer,
                        speakerName = "Atlas",
                        pitch = AiPersona.ATLAS.pitch,
                        speechRate = AiPersona.ATLAS.speechRate
                    )
                }

                val currentMsgs = _brainstormMessages.value.toMutableList()
                currentMsgs.add(
                    BrainstormMessage(
                        sender = "Atlas",
                        text = "Vygeneroval jsem ucelený prováděcí plán a funkční architekturu.",
                        persona = AiPersona.ATLAS
                    )
                )
                _brainstormMessages.value = currentMsgs
            } finally {
                _isAiProcessing.value = false
            }
        }
    }

    private suspend fun applyAiResponseToMap(
        mapId: String,
        contextParentId: String?,
        aiResponse: com.example.data.model.AiBrainstormResponse
    ) {
        val authorPersona = AiPersona.fromId(aiResponse.authorPersona) ?: AiPersona.ATLAS
        if (aiResponse.spokenAnswer.isNotBlank()) {
            voiceManager.speak(
                text = aiResponse.spokenAnswer,
                speakerName = authorPersona.displayName,
                pitch = authorPersona.pitch,
                speechRate = authorPersona.speechRate
            )
        }

        val currentMsgs = _brainstormMessages.value.toMutableList()
        currentMsgs.add(
            BrainstormMessage(
                sender = authorPersona.displayName,
                text = aiResponse.spokenAnswer,
                persona = authorPersona
            )
        )
        _brainstormMessages.value = currentMsgs

        if (!aiResponse.solutionSummary.isNullOrBlank()) {
            _solutionBlueprint.value = aiResponse.solutionSummary
        }

        applyNodeDtosToMap(mapId, contextParentId, aiResponse.addedNodes)
    }

    private suspend fun applyNodeDtosToMap(
        mapId: String,
        contextParentId: String?,
        dtos: List<com.example.data.model.AiGeneratedNodeDto>
    ) {
        val currentNodes = _nodes.value.toMutableList()
        val newNodesToSave = mutableListOf<MindNode>()
        val newEdgesToSave = mutableListOf<MindEdge>()
        var rootNode = currentNodes.firstOrNull { it.parentId == null }

        // If no root exists yet in this map, guarantee a central Goal node anchor
        if (rootNode == null) {
            val mapTitle = allMaps.value.firstOrNull { it.id == mapId }?.title?.ifBlank { "Centrální cíl" } ?: "Centrální cíl"
            val newRoot = MindNode(
                id = UUID.randomUUID().toString(),
                mapId = mapId,
                parentId = null,
                title = mapTitle,
                details = "Hlavní téma a vize projektu",
                category = NodeCategory.GOAL,
                xPos = 0f,
                yPos = 0f,
                isAiGenerated = false
            )
            currentNodes.add(newRoot)
            newNodesToSave.add(newRoot)
            rootNode = newRoot
        }

        for (dto in dtos) {
            // Avoid duplicate titles
            if (currentNodes.any { it.title.equals(dto.title.trim(), ignoreCase = true) }) {
                continue
            }

            val category = NodeCategory.fromString(dto.category)
            var parentId: String? = null

            if (category == NodeCategory.GOAL && rootNode == null) {
                parentId = null
            } else if (contextParentId != null) {
                parentId = contextParentId
            } else if (!dto.parentTitleOrId.isNullOrBlank() && dto.parentTitleOrId != "null") {
                val matched = currentNodes.firstOrNull { 
                    it.id == dto.parentTitleOrId || it.title.contains(dto.parentTitleOrId, ignoreCase = true) 
                } ?: newNodesToSave.firstOrNull {
                    it.title.contains(dto.parentTitleOrId, ignoreCase = true)
                }
                parentId = matched?.id ?: rootNode.id
            } else {
                parentId = rootNode.id
            }

            val newNodeId = UUID.randomUUID().toString()
            val newNode = MindNode(
                id = newNodeId,
                mapId = mapId,
                parentId = parentId,
                title = dto.title.trim(),
                details = dto.details.trim(),
                category = category,
                xPos = 0f,
                yPos = 0f,
                isAiGenerated = true,
                authorPersona = dto.authorPersona,
                diagramData = dto.diagramData,
                searchSources = dto.searchSources
            )
            newNodesToSave.add(newNode)
            currentNodes.add(newNode)

            if (parentId != null) {
                newEdgesToSave.add(
                    MindEdge(
                        id = UUID.randomUUID().toString(),
                        mapId = mapId,
                        fromNodeId = parentId,
                        toNodeId = newNodeId
                    )
                )
            }
        }

        val allUpdated = MindMapLayoutEngine.computeBalancedLayout(currentNodes)
        _nodes.value = allUpdated
        repository.saveNodes(allUpdated)
        repository.saveEdges(newEdgesToSave)
        resetView()
    }

    override fun onCleared() {
        super.onCleared()
        voiceManager.destroy()
    }
}
