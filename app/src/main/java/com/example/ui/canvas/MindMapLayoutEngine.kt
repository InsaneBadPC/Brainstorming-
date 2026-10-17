package com.example.ui.canvas

import com.example.data.model.MindNode
import com.example.data.model.NodeCategory
import kotlin.math.max

object MindMapLayoutEngine {

    // Visual dimensions in density-independent space (DP)
    // Ensures clean, non-overlapping layouts across all Android device screen densities
    private const val CARD_APPROX_WIDTH = 260f
    private const val CARD_APPROX_HEIGHT = 140f
    private const val MIN_VERTICAL_GAP = 52f
    private const val HORIZONTAL_LEVEL_GAP = 360f

    /**
     * Re-calculates layout coordinates for all nodes in the mind map.
     * Uses a hierarchical Walker/Reingold-Tilford tree algorithm adapted for mind maps:
     * - Root is centered at (0, 0).
     * - Direct branches are balanced across Right (+X) and Left (-X) wings.
     * - Every subtree is allocated a strict non-overlapping vertical bounding box.
     * - Parents are vertically aligned to the center of their children.
     * - Orphan or disconnected nodes are safely attached or placed in an organized grid.
     */
    fun computeBalancedLayout(nodes: List<MindNode>): List<MindNode> {
        if (nodes.isEmpty()) return emptyList()
        if (nodes.size == 1) {
            return listOf(nodes.first().copy(xPos = 0f, yPos = 0f))
        }

        // 1. Identify or choose the Root node
        val nodeMap = nodes.associateBy { it.id }.toMutableMap()
        
        // Find node with parentId == null (prefer GOAL category if multiple)
        val rootsWithNullParent = nodes.filter { it.parentId == null }
        val rootNode = rootsWithNullParent.firstOrNull { it.category == NodeCategory.GOAL }
            ?: rootsWithNullParent.firstOrNull()
            ?: nodes.first()

        val rootId = rootNode.id

        // 2. Build parent-to-children hierarchy, sanitizing any missing/broken parent references
        val validIds = nodes.map { it.id }.toSet()
        val sanitizedNodes = nodes.map { node ->
            if (node.id != rootId && (node.parentId == null || !validIds.contains(node.parentId))) {
                // Attach orphan nodes as direct children of the root node so they don't get lost or overlap!
                node.copy(parentId = rootId)
            } else {
                node
            }
        }

        val childrenMap = sanitizedNodes.filter { it.id != rootId }.groupBy { it.parentId }
        val updatedMap = mutableMapOf<String, MindNode>()

        // Root at origin
        updatedMap[rootId] = rootNode.copy(xPos = 0f, yPos = 0f, parentId = null)

        val primaryChildren = childrenMap[rootId] ?: emptyList()
        if (primaryChildren.isEmpty()) {
            return sanitizedNodes.map { if (it.id == rootId) updatedMap[rootId]!! else it }
        }

        // 3. Partition primary branches into Right Wing (+1) and Left Wing (-1)
        val rightChildren = mutableListOf<MindNode>()
        val leftChildren = mutableListOf<MindNode>()

        primaryChildren.forEachIndexed { index, child ->
            if (index % 2 == 0) {
                rightChildren.add(child)
            } else {
                leftChildren.add(child)
            }
        }

        // 4. Layout Right Wing
        layoutWing(
            branches = rightChildren,
            direction = 1f,
            childrenMap = childrenMap,
            updatedMap = updatedMap
        )

        // 5. Layout Left Wing
        layoutWing(
            branches = leftChildren,
            direction = -1f,
            childrenMap = childrenMap,
            updatedMap = updatedMap
        )

        // Return the full list with updated coordinates
        return sanitizedNodes.map { updatedMap[it.id] ?: it }
    }

    /**
     * Lays out one side of the mind map (Right or Left).
     * Calculates the recursive subtree bounding height of each branch to guarantee
     * that sibling branches and their descendants never collide vertically.
     */
    private fun layoutWing(
        branches: List<MindNode>,
        direction: Float,
        childrenMap: Map<String?, List<MindNode>>,
        updatedMap: MutableMap<String, MindNode>
    ) {
        if (branches.isEmpty()) return

        // Compute subtree heights for all branches
        val subtreeHeights = branches.map { computeSubtreeHeight(it.id, childrenMap) }
        val totalWingHeight = subtreeHeights.sum()

        var currentTopY = -totalWingHeight / 2f
        val baseX = HORIZONTAL_LEVEL_GAP * direction

        branches.forEachIndexed { index, branch ->
            val branchHeight = subtreeHeights[index]
            val branchCenterY = currentTopY + branchHeight / 2f

            updatedMap[branch.id] = branch.copy(xPos = baseX, yPos = branchCenterY)

            // Recursively layout all sub-branches inside this branch's allocated vertical slice
            layoutSubtree(
                parentId = branch.id,
                parentX = baseX,
                allocatedTopY = currentTopY,
                allocatedHeight = branchHeight,
                direction = direction,
                level = 2,
                childrenMap = childrenMap,
                updatedMap = updatedMap
            )

            currentTopY += branchHeight
        }
    }

    /**
     * Recursively computes the total vertical span (height) required by a node and all of its descendants.
     */
    private fun computeSubtreeHeight(nodeId: String, childrenMap: Map<String?, List<MindNode>>): Float {
        val children = childrenMap[nodeId] ?: emptyList()
        val baseNodeHeight = CARD_APPROX_HEIGHT + MIN_VERTICAL_GAP

        if (children.isEmpty()) {
            return baseNodeHeight
        }

        val childrenTotalHeight = children.sumOf { computeSubtreeHeight(it.id, childrenMap).toDouble() }.toFloat()
        return max(baseNodeHeight, childrenTotalHeight)
    }

    /**
     * Recursively positions children inside their parent's allocated vertical bounding band.
     * Sibling subtrees never overlap because each child receives its own non-overlapping vertical window.
     */
    private fun layoutSubtree(
        parentId: String,
        parentX: Float,
        allocatedTopY: Float,
        allocatedHeight: Float,
        direction: Float,
        level: Int,
        childrenMap: Map<String?, List<MindNode>>,
        updatedMap: MutableMap<String, MindNode>
    ) {
        val children = childrenMap[parentId] ?: return
        if (children.isEmpty()) return

        val childSubtreeHeights = children.map { computeSubtreeHeight(it.id, childrenMap) }
        val totalChildrenHeight = childSubtreeHeights.sum()

        // Center children cluster within the parent's allocated height
        var currentY = allocatedTopY + (allocatedHeight - totalChildrenHeight) / 2f
        val currentX = parentX + (HORIZONTAL_LEVEL_GAP * direction)

        children.forEachIndexed { index, child ->
            val thisSubtreeHeight = childSubtreeHeights[index]
            val childCenterY = currentY + thisSubtreeHeight / 2f

            updatedMap[child.id] = child.copy(xPos = currentX, yPos = childCenterY)

            // Continue recursively to next depth level
            layoutSubtree(
                parentId = child.id,
                parentX = currentX,
                allocatedTopY = currentY,
                allocatedHeight = thisSubtreeHeight,
                direction = direction,
                level = level + 1,
                childrenMap = childrenMap,
                updatedMap = updatedMap
            )

            currentY += thisSubtreeHeight
        }
    }
}
