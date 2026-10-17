package com.example.data.firebase

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.model.CustomPersona
import com.example.data.model.MindMap
import com.example.data.model.MindNode
import com.example.data.model.NodeCategory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirestoreSyncRepository(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _lastSyncedTimestamp = MutableStateFlow<Long?>(null)
    val lastSyncedTimestamp: StateFlow<Long?> = _lastSyncedTimestamp.asStateFlow()

    private val _syncMessage = MutableStateFlow<String?>(null)
    val syncMessage: StateFlow<String?> = _syncMessage.asStateFlow()

    suspend fun syncMapToCloud(map: MindMap, nodes: List<MindNode>): Boolean = withContext(Dispatchers.IO) {
        val user = auth.currentUser ?: run {
            _syncMessage.value = "Přihlaste se pro synchronizaci do Cloudu"
            return@withContext false
        }

        _isSyncing.value = true
        try {
            val userDocRef = firestore.collection("users").document(user.uid)
            val userProfileData = hashMapOf(
                "uid" to user.uid,
                "displayName" to (user.displayName ?: "Uživatel"),
                "email" to (user.email ?: ""),
                "photoUrl" to (user.photoUrl?.toString() ?: ""),
                "updatedAt" to System.currentTimeMillis()
            )
            userDocRef.set(userProfileData, SetOptions.merge()).await()

            // Save MindMap
            val mapDocRef = userDocRef.collection("mind_maps").document(map.id)
            val mapData = hashMapOf(
                "id" to map.id,
                "userId" to user.uid,
                "title" to map.title,
                "description" to map.description,
                "seedPrompt" to map.seedPrompt,
                "createdAt" to map.createdAt,
                "updatedAt" to System.currentTimeMillis()
            )
            mapDocRef.set(mapData, SetOptions.merge()).await()

            // Save Nodes in batch
            val nodesCollection = mapDocRef.collection("nodes")
            val batch = firestore.batch()
            for (node in nodes) {
                val nodeDoc = nodesCollection.document(node.id)
                val nodeData = hashMapOf(
                    "id" to node.id,
                    "mapId" to node.mapId,
                    "parentId" to (node.parentId ?: ""),
                    "title" to node.title,
                    "details" to node.details,
                    "category" to node.category.name,
                    "xPos" to node.xPos,
                    "yPos" to node.yPos,
                    "colorHex" to node.colorHex,
                    "isAiGenerated" to node.isAiGenerated,
                    "authorPersona" to (node.authorPersona ?: ""),
                    "diagramData" to (node.diagramData ?: ""),
                    "searchSources" to (node.searchSources ?: ""),
                    "checklistJson" to (node.checklistJson ?: ""),
                    "wireframeMockup" to (node.wireframeMockup ?: ""),
                    "clusterTag" to (node.clusterTag ?: ""),
                    "roadmapPhase" to (node.roadmapPhase ?: ""),
                    "isCompleted" to node.isCompleted,
                    "isExpanded" to node.isExpanded
                )
                batch.set(nodeDoc, nodeData, SetOptions.merge())
            }
            batch.commit().await()

            _lastSyncedTimestamp.value = System.currentTimeMillis()
            _syncMessage.value = "Mapa úspěšně uložena do Firebase Cloudu"
            true
        } catch (e: Exception) {
            Log.e("FirestoreSyncRepo", "Chyba synchronizace", e)
            _syncMessage.value = "Chyba cloudu: ${e.localizedMessage}"
            false
        } finally {
            _isSyncing.value = false
        }
    }

    suspend fun syncCustomPersonas(personas: List<CustomPersona>): Boolean = withContext(Dispatchers.IO) {
        val user = auth.currentUser ?: return@withContext false
        try {
            val personasCol = firestore.collection("users").document(user.uid).collection("custom_personas")
            val batch = firestore.batch()
            for (p in personas) {
                val doc = personasCol.document(p.id)
                val data = hashMapOf(
                    "id" to p.id,
                    "userId" to user.uid,
                    "displayName" to p.displayName,
                    "title" to p.title,
                    "roleDescription" to p.roleDescription,
                    "colorHex" to p.colorHex,
                    "emoji" to p.emoji,
                    "pitch" to p.pitch,
                    "speechRate" to p.speechRate
                )
                batch.set(doc, data, SetOptions.merge())
            }
            batch.commit().await()
            true
        } catch (e: Exception) {
            Log.e("FirestoreSyncRepo", "Chyba sync personas", e)
            false
        }
    }
}
