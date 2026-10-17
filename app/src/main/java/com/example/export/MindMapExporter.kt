package com.example.export

import com.example.data.model.MindEdge
import com.example.data.model.MindMap
import com.example.data.model.MindNode
import org.json.JSONArray
import org.json.JSONObject

object MindMapExporter {

    fun exportToMarkdown(map: MindMap, nodes: List<MindNode>, edges: List<MindEdge>): String {
        val sb = StringBuilder()
        sb.append("# ${map.title}\n\n")
        if (map.description.isNotBlank()) {
            sb.append("> ${map.description}\n\n")
        }

        val rootNodes = nodes.filter { it.parentId == null }
        val childrenMap = nodes.groupBy { it.parentId }

        fun appendBranch(node: MindNode, indent: Int) {
            val prefix = "  ".repeat(indent) + "- "
            val check = if (node.isCompleted) "[x]" else "[ ]"
            val author = node.authorPersona?.let { " *(@$it)*" } ?: ""
            val cluster = node.clusterTag?.let { " `[$it]`" } ?: ""
            sb.append("$prefix$check **[${node.category.displayName}]** ${node.title}$author$cluster\n")

            if (node.details.isNotBlank()) {
                sb.append("  ".repeat(indent + 1) + "${node.details}\n")
            }

            if (!node.diagramData.isNullOrBlank()) {
                sb.append("  ".repeat(indent + 1) + "📊 Diagram toku: `${node.diagramData}`\n")
            }

            val checklist = node.getChecklist()
            if (checklist.isNotEmpty()) {
                checklist.forEach { item ->
                    val cMark = if (item.isDone) "[x]" else "[ ]"
                    sb.append("  ".repeat(indent + 1) + "  $cMark ${item.text}\n")
                }
            }

            childrenMap[node.id]?.forEach { child ->
                appendBranch(child, indent + 1)
            }
        }

        if (rootNodes.isNotEmpty()) {
            rootNodes.forEach { appendBranch(it, 0) }
        } else {
            nodes.forEach { appendBranch(it, 0) }
        }

        return sb.toString()
    }

    fun exportToJiraOrGitHub(map: MindMap, nodes: List<MindNode>): String {
        val sb = StringBuilder()
        sb.append("### Project Tasks: ${map.title}\n\n")
        sb.append("Transformed from MindCraft AI mind map.\n\n")

        val actionNodes = nodes.filter { it.category.name == "ACTION_ITEM" || it.getChecklist().isNotEmpty() }
        if (actionNodes.isEmpty()) {
            nodes.forEach { n ->
                val state = if (n.isCompleted) "[x]" else "[ ]"
                sb.append("- $state **${n.title}** (${n.category.displayName})\n  ${n.details}\n\n")
            }
        } else {
            actionNodes.forEach { n ->
                val state = if (n.isCompleted) "[x]" else "[ ]"
                val author = n.authorPersona?.let { " (Assignee: $it)" } ?: ""
                sb.append("- $state **${n.title}**$author\n")
                if (n.details.isNotBlank()) {
                    sb.append("  > ${n.details}\n")
                }
                n.getChecklist().forEach { sub ->
                    val subState = if (sub.isDone) "[x]" else "[ ]"
                    sb.append("  - $subState ${sub.text}\n")
                }
                sb.append("\n")
            }
        }
        return sb.toString()
    }

    fun exportToInteractiveHtml(map: MindMap, nodes: List<MindNode>, edges: List<MindEdge>): String {
        val nodeItemsHtml = nodes.joinToString("\n") { n ->
            val color = n.colorHex
            val steps = n.getFlowSteps().joinToString(" &rarr; ") { "<span class='step'>$it</span>" }
            val diagramPart = if (steps.isNotBlank()) "<div class='diagram'>$steps</div>" else ""
            val checkItems = n.getChecklist().joinToString("") {
                "<li><input type='checkbox' ${if (it.isDone) "checked" else ""} disabled/> ${it.text}</li>"
            }
            val checkPart = if (checkItems.isNotBlank()) "<ul class='checklist'>$checkItems</ul>" else ""

            """
            <div class='node-card' style='border-left: 4px solid $color;'>
                <div class='badge' style='color:$color;'>${n.category.displayName} ${n.authorPersona?.let { "• @$it" } ?: ""}</div>
                <h3>${n.title}</h3>
                <p>${n.details}</p>
                $diagramPart
                $checkPart
            </div>
            """.trimIndent()
        }

        return """
        <!DOCTYPE html>
        <html lang="cs">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>${map.title} - MindCraft AI</title>
            <style>
                body { background: #0A0E17; color: #F1F5F9; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; padding: 24px; margin: 0; }
                h1 { color: #00E5FF; margin-bottom: 4px; }
                .subtitle { color: #94A3B8; margin-bottom: 24px; }
                .grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 16px; }
                .node-card { background: #131923; border: 1px solid #253041; border-radius: 12px; padding: 16px; box-shadow: 0 4px 12px rgba(0,0,0,0.5); }
                .badge { font-size: 11px; font-weight: bold; text-transform: uppercase; margin-bottom: 6px; }
                h3 { margin: 0 0 6px 0; font-size: 16px; }
                p { color: #94A3B8; font-size: 13px; line-height: 1.4; margin: 0; }
                .diagram { margin-top: 10px; padding: 8px; background: #1C2433; border-radius: 8px; font-size: 11px; color: #38BDF8; }
                .step { background: #0A0E17; padding: 2px 6px; border-radius: 4px; border: 1px solid #38BDF8; display: inline-block; margin: 2px 0; }
                .checklist { margin: 10px 0 0 0; padding-left: 18px; font-size: 12px; color: #CBD5E1; }
            </style>
        </head>
        <body>
            <h1>${map.title}</h1>
            <div class="subtitle">${map.description.ifBlank { "Exportováno z MindCraft AI" }}</div>
            <div class="grid">
                $nodeItemsHtml
            </div>
        </body>
        </html>
        """.trimIndent()
    }

    fun exportToJson(map: MindMap, nodes: List<MindNode>, edges: List<MindEdge>): String {
        val root = JSONObject()
        root.put("mapId", map.id)
        root.put("title", map.title)
        root.put("description", map.description)

        val nodesArr = JSONArray()
        nodes.forEach { n ->
            val nObj = JSONObject()
            nObj.put("id", n.id)
            nObj.put("parentId", n.parentId)
            nObj.put("title", n.title)
            nObj.put("details", n.details)
            nObj.put("category", n.category.name)
            nObj.put("authorPersona", n.authorPersona)
            nObj.put("diagramData", n.diagramData)
            nObj.put("clusterTag", n.clusterTag)
            nObj.put("roadmapPhase", n.roadmapPhase)
            nodesArr.put(nObj)
        }
        root.put("nodes", nodesArr)

        val edgesArr = JSONArray()
        edges.forEach { e ->
            val eObj = JSONObject()
            eObj.put("from", e.fromNodeId)
            eObj.put("to", e.toNodeId)
            edgesArr.put(eObj)
        }
        root.put("edges", edgesArr)

        return root.toString(2)
    }
}
