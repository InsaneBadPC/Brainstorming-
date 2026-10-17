package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCoral
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import org.json.JSONArray
import org.json.JSONObject

enum class NodeCategory(
    val displayName: String,
    val colorHex: String,
    val defaultColor: Color
) {
    GOAL("Cíl / Nápad", "#00E5FF", NeonCyan),
    FEATURE("Funkcionalita", "#B388FF", NeonPurple),
    ARCHITECTURE("Architektura & Tech", "#00E676", NeonEmerald),
    RISK("Riziko / Výzva", "#FF5252", NeonCoral),
    SOLUTION("Řešení", "#FFB300", NeonAmber),
    ACTION_ITEM("Úkol / Krok", "#FF4081", NeonPink),
    DIAGRAM("Procesní Diagram", "#38BDF8", Color(0xFF38BDF8));

    companion object {
        fun fromString(value: String?): NodeCategory {
            if (value == null) return FEATURE
            return entries.firstOrNull { 
                it.name.equals(value, ignoreCase = true) || 
                it.displayName.contains(value, ignoreCase = true) 
            } ?: FEATURE
        }
    }
}

data class ChecklistItem(
    val id: String,
    val text: String,
    val isDone: Boolean = false
)

data class MindMap(
    val id: String,
    val title: String,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val seedPrompt: String = ""
)

data class MindNode(
    val id: String,
    val mapId: String,
    val parentId: String? = null,
    val title: String,
    val details: String = "",
    val category: NodeCategory = NodeCategory.FEATURE,
    val xPos: Float = 0f,
    val yPos: Float = 0f,
    val colorHex: String = category.colorHex,
    val isAiGenerated: Boolean = false,
    val authorPersona: String? = null,
    val diagramData: String? = null,
    val searchSources: String? = null,
    val checklistJson: String? = null,
    val wireframeMockup: String? = null,
    val clusterTag: String? = null,
    val roadmapPhase: String? = null, // e.g. "Fáze 1 (MVP)", "Fáze 2 (Růst)"
    val isCompleted: Boolean = false,
    val isExpanded: Boolean = true
) {
    fun getFlowSteps(): List<String> {
        if (diagramData.isNullOrBlank()) return emptyList()
        return diagramData.split("->", ";", "\n")
            .map { it.trim().removePrefix("[").removeSuffix("]").trim() }
            .filter { it.isNotBlank() }
    }

    fun getChecklist(): List<ChecklistItem> {
        if (checklistJson.isNullOrBlank()) return emptyList()
        return try {
            val array = JSONArray(checklistJson)
            val list = mutableListOf<ChecklistItem>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    ChecklistItem(
                        id = obj.optString("id", i.toString()),
                        text = obj.optString("text", ""),
                        isDone = obj.optBoolean("isDone", false)
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }
}

data class MindEdge(
    val id: String,
    val mapId: String,
    val fromNodeId: String,
    val toNodeId: String,
    val label: String = ""
)

data class CustomPersona(
    val id: String,
    val displayName: String,
    val title: String,
    val roleDescription: String,
    val colorHex: String,
    val emoji: String,
    val pitch: Float = 1.0f,
    val speechRate: Float = 1.0f
) {
    val color: Color get() = try {
        Color(android.graphics.Color.parseColor(colorHex))
    } catch (e: Exception) {
        NeonCyan
    }
}

data class MindMapSnapshot(
    val id: String,
    val timestamp: Long = System.currentTimeMillis(),
    val description: String,
    val nodes: List<MindNode>
)

data class AiGeneratedNodeDto(
    val title: String,
    val details: String = "",
    val category: String = "FEATURE",
    val parentTitleOrId: String? = null,
    val authorPersona: String? = null,
    val diagramData: String? = null,
    val searchSources: String? = null,
    val wireframeMockup: String? = null,
    val clusterTag: String? = null,
    val roadmapPhase: String? = null,
    val checklistItems: List<String> = emptyList()
)

data class AiBrainstormResponse(
    val spokenAnswer: String,
    val addedNodes: List<AiGeneratedNodeDto> = emptyList(),
    val solutionSummary: String? = null,
    val authorPersona: String? = null
)

data class PersonaSpeech(
    val persona: AiPersona,
    val spokenText: String
)

data class RoundtableResponse(
    val speeches: List<PersonaSpeech>,
    val addedNodes: List<AiGeneratedNodeDto>,
    val solutionSummary: String? = null
)

data class AuditRoundSpeech(
    val round: Int,
    val persona: AiPersona,
    val spokenText: String,
    val searchSources: String? = null
)

data class OutdatedToolFinding(
    val toolName: String,
    val issueOrDeprecatedReason: String,
    val recommendedModernAlternative: String,
    val category: String = "Framework / Knihovna",
    val searchSources: String? = null
)

data class AuditRoundResult(
    val round: Int,
    val speeches: List<AuditRoundSpeech>,
    val addedNodes: List<AiGeneratedNodeDto>,
    val outdatedTools: List<OutdatedToolFinding> = emptyList(),
    val roundSummary: String? = null
)

data class AuditDebateReport(
    val totalRounds: Int,
    val speeches: List<AuditRoundSpeech>,
    val addedNodes: List<AiGeneratedNodeDto>,
    val healthScore: Int,
    val summary: String,
    val outdatedTools: List<OutdatedToolFinding>
)

enum class VibeTargetStack(
    val displayName: String,
    val badge: String,
    val defaultLang: String,
    val iconEmoji: String
) {
    COMPOSE_ANDROID("Android Compose (Kotlin)", "Kotlin", "kotlin", "🤖"),
    REACT_TAILWIND("React + Tailwind CSS (TS)", "React/TS", "typescript", "⚛️"),
    NEXTJS_FULLSTACK("Next.js 15 App Router", "Next.js", "typescript", "▲"),
    FASTAPI_PYTHON("Python FastAPI + Pydantic", "FastAPI", "python", "🐍"),
    NODE_HONO("Node.js / Hono Backend", "Hono/Node", "typescript", "🔥"),
    DATABASE_PRISMA("Prisma Schema & SQL DDL", "DB / SQL", "sql", "🗄️")
}

data class VibeCodingArtifact(
    val title: String,
    val targetStack: VibeTargetStack,
    val filename: String,
    val codeSnippet: String,
    val cursorPrompt: String,
    val copilotInstructions: String,
    val dbSchema: String,
    val apiSpec: String,
    val terminalSetupCommands: List<String>,
    val explanation: String
)

