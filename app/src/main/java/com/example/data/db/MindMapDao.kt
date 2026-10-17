package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface MindMapDao {

    @Query("SELECT * FROM mind_maps ORDER BY updatedAt DESC")
    fun getAllMaps(): Flow<List<MindMapEntity>>

    @Query("SELECT * FROM mind_maps WHERE id = :mapId LIMIT 1")
    fun getMapById(mapId: String): Flow<MindMapEntity?>

    @Query("SELECT * FROM mind_nodes WHERE mapId = :mapId")
    fun getNodesForMap(mapId: String): Flow<List<MindNodeEntity>>

    @Query("SELECT * FROM mind_edges WHERE mapId = :mapId")
    fun getEdgesForMap(mapId: String): Flow<List<MindEdgeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMap(map: MindMapEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNodes(nodes: List<MindNodeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNode(node: MindNodeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEdges(edges: List<MindEdgeEntity>)

    @Query("UPDATE mind_nodes SET xPos = :x, yPos = :y WHERE id = :nodeId")
    suspend fun updateNodePosition(nodeId: String, x: Float, y: Float)

    @Query("UPDATE mind_nodes SET isExpanded = :isExpanded WHERE id = :nodeId")
    suspend fun updateNodeExpansion(nodeId: String, isExpanded: Boolean)

    @Query("UPDATE mind_nodes SET isCompleted = :isCompleted WHERE id = :nodeId")
    suspend fun updateNodeCompletion(nodeId: String, isCompleted: Boolean)

    @Query("DELETE FROM mind_nodes WHERE id = :nodeId")
    suspend fun deleteNodeById(nodeId: String)

    @Query("DELETE FROM mind_edges WHERE fromNodeId = :nodeId OR toNodeId = :nodeId")
    suspend fun deleteEdgesForNode(nodeId: String)

    @Transaction
    suspend fun deleteNodeAndEdges(nodeId: String) {
        deleteEdgesForNode(nodeId)
        deleteNodeById(nodeId)
    }

    @Query("DELETE FROM mind_maps WHERE id = :mapId")
    suspend fun deleteMapById(mapId: String)

    @Query("DELETE FROM mind_nodes WHERE mapId = :mapId")
    suspend fun deleteNodesByMapId(mapId: String)

    @Query("DELETE FROM mind_edges WHERE mapId = :mapId")
    suspend fun deleteEdgesByMapId(mapId: String)

    @Transaction
    suspend fun deleteEntireMap(mapId: String) {
        deleteEdgesByMapId(mapId)
        deleteNodesByMapId(mapId)
        deleteMapById(mapId)
    }

    @Query("DELETE FROM mind_maps")
    suspend fun deleteAllMaps()

    @Query("DELETE FROM mind_nodes")
    suspend fun deleteAllNodes()

    @Query("DELETE FROM mind_edges")
    suspend fun deleteAllEdges()

    @Query("DELETE FROM mind_maps WHERE title LIKE '%Robot%' OR title LIKE '%Ukázk%' OR title LIKE '%Sample%' OR title LIKE '%Demo%' OR title LIKE '%Zahradní%' OR title LIKE '%Autonomní%'")
    suspend fun deleteSeedMaps()

    @Query("DELETE FROM mind_nodes WHERE mapId NOT IN (SELECT id FROM mind_maps)")
    suspend fun deleteOrphanNodes()

    @Query("DELETE FROM mind_edges WHERE mapId NOT IN (SELECT id FROM mind_maps)")
    suspend fun deleteOrphanEdges()

    @Transaction
    suspend fun purgeSeedData() {
        deleteSeedMaps()
        deleteOrphanNodes()
        deleteOrphanEdges()
    }
}
