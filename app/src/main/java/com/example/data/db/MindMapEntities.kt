package com.example.data.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.data.model.MindEdge
import com.example.data.model.MindMap
import com.example.data.model.MindNode
import com.example.data.model.NodeCategory

@Entity(tableName = "mind_maps")
data class MindMapEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val createdAt: Long,
    val updatedAt: Long,
    val seedPrompt: String
) {
    fun toDomain() = MindMap(
        id = id,
        title = title,
        description = description,
        createdAt = createdAt,
        updatedAt = updatedAt,
        seedPrompt = seedPrompt
    )

    companion object {
        fun fromDomain(m: MindMap) = MindMapEntity(
            id = m.id,
            title = m.title,
            description = m.description,
            createdAt = m.createdAt,
            updatedAt = m.updatedAt,
            seedPrompt = m.seedPrompt
        )
    }
}

@Entity(
    tableName = "mind_nodes",
    indices = [Index("mapId"), Index("parentId")]
)
data class MindNodeEntity(
    @PrimaryKey val id: String,
    val mapId: String,
    val parentId: String?,
    val title: String,
    val details: String,
    val category: String,
    val xPos: Float,
    val yPos: Float,
    val colorHex: String,
    val isAiGenerated: Boolean,
    val authorPersona: String? = null,
    val diagramData: String? = null,
    val searchSources: String? = null,
    val checklistJson: String? = null,
    val wireframeMockup: String? = null,
    val clusterTag: String? = null,
    val roadmapPhase: String? = null,
    val isCompleted: Boolean,
    val isExpanded: Boolean
) {
    fun toDomain() = MindNode(
        id = id,
        mapId = mapId,
        parentId = parentId,
        title = title,
        details = details,
        category = NodeCategory.fromString(category),
        xPos = xPos,
        yPos = yPos,
        colorHex = colorHex,
        isAiGenerated = isAiGenerated,
        authorPersona = authorPersona,
        diagramData = diagramData,
        searchSources = searchSources,
        checklistJson = checklistJson,
        wireframeMockup = wireframeMockup,
        clusterTag = clusterTag,
        roadmapPhase = roadmapPhase,
        isCompleted = isCompleted,
        isExpanded = isExpanded
    )

    companion object {
        fun fromDomain(n: MindNode) = MindNodeEntity(
            id = n.id,
            mapId = n.mapId,
            parentId = n.parentId,
            title = n.title,
            details = n.details,
            category = n.category.name,
            xPos = n.xPos,
            yPos = n.yPos,
            colorHex = n.colorHex,
            isAiGenerated = n.isAiGenerated,
            authorPersona = n.authorPersona,
            diagramData = n.diagramData,
            searchSources = n.searchSources,
            checklistJson = n.checklistJson,
            wireframeMockup = n.wireframeMockup,
            clusterTag = n.clusterTag,
            roadmapPhase = n.roadmapPhase,
            isCompleted = n.isCompleted,
            isExpanded = n.isExpanded
        )
    }
}

@Entity(
    tableName = "mind_edges",
    indices = [Index("mapId"), Index("fromNodeId"), Index("toNodeId")]
)
data class MindEdgeEntity(
    @PrimaryKey val id: String,
    val mapId: String,
    val fromNodeId: String,
    val toNodeId: String,
    val label: String
) {
    fun toDomain() = MindEdge(
        id = id,
        mapId = mapId,
        fromNodeId = fromNodeId,
        toNodeId = toNodeId,
        label = label
    )

    companion object {
        fun fromDomain(e: MindEdge) = MindEdgeEntity(
            id = e.id,
            mapId = e.mapId,
            fromNodeId = e.fromNodeId,
            toNodeId = e.toNodeId,
            label = e.label
        )
    }
}
