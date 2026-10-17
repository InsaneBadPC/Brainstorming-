package com.example.data.db

import com.example.data.model.MindEdge
import com.example.data.model.MindMap
import com.example.data.model.MindNode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MindMapRepository(private val dao: MindMapDao) {

    val allMaps: Flow<List<MindMap>> = dao.getAllMaps().map { entities ->
        entities.map { it.toDomain() }
    }

    fun getMapById(mapId: String): Flow<MindMap?> = dao.getMapById(mapId).map { it?.toDomain() }

    fun getNodesForMap(mapId: String): Flow<List<MindNode>> = dao.getNodesForMap(mapId).map { list ->
        list.map { it.toDomain() }
    }

    fun getEdgesForMap(mapId: String): Flow<List<MindEdge>> = dao.getEdgesForMap(mapId).map { list ->
        list.map { it.toDomain() }
    }

    suspend fun saveMap(map: MindMap) {
        dao.insertMap(MindMapEntity.fromDomain(map))
    }

    suspend fun saveNode(node: MindNode) {
        dao.insertNode(MindNodeEntity.fromDomain(node))
    }

    suspend fun saveNodes(nodes: List<MindNode>) {
        dao.insertNodes(nodes.map { MindNodeEntity.fromDomain(it) })
    }

    suspend fun saveEdges(edges: List<MindEdge>) {
        dao.insertEdges(edges.map { MindEdgeEntity.fromDomain(it) })
    }

    suspend fun updateNodePosition(nodeId: String, x: Float, y: Float) {
        dao.updateNodePosition(nodeId, x, y)
    }

    suspend fun updateNodeExpansion(nodeId: String, isExpanded: Boolean) {
        dao.updateNodeExpansion(nodeId, isExpanded)
    }

    suspend fun updateNodeCompletion(nodeId: String, isCompleted: Boolean) {
        dao.updateNodeCompletion(nodeId, isCompleted)
    }

    suspend fun deleteNode(nodeId: String) {
        dao.deleteNodeAndEdges(nodeId)
    }

    suspend fun deleteMap(mapId: String) {
        dao.deleteEntireMap(mapId)
    }

    suspend fun purgeSeedData() {
        dao.purgeSeedData()
    }

    suspend fun clearAllData() {
        dao.deleteAllEdges()
        dao.deleteAllNodes()
        dao.deleteAllMaps()
    }
}
