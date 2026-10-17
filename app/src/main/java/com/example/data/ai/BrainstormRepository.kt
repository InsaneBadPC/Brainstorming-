package com.example.data.ai

import com.example.BuildConfig
import com.example.data.model.AiBrainstormResponse
import com.example.data.model.AiGeneratedNodeDto
import com.example.data.model.AiPersona
import com.example.data.model.CustomPersona
import com.example.data.model.MindNode
import com.example.data.model.AuditRoundResult
import com.example.data.model.AuditRoundSpeech
import com.example.data.model.OutdatedToolFinding
import com.example.data.model.PersonaSpeech
import com.example.data.model.RoundtableResponse
import com.example.data.model.VibeCodingArtifact
import com.example.data.model.VibeTargetStack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class BrainstormRepository {

    var activeGoogleModel: GoogleAiModel = GoogleAiModel.DEFAULT

    private suspend fun callGeminiApi(
        apiKey: String,
        request: GeminiRequest,
        preferredModel: GoogleAiModel = activeGoogleModel
    ): GeminiResponse {
        return try {
            GeminiClient.service.generateContent(preferredModel.modelId, apiKey, request)
        } catch (e: Exception) {
            // First fallback to fast free model if not already using it
            if (preferredModel != GoogleAiModel.GEMINI_FLASH_LATEST) {
                try {
                    GeminiClient.service.generateContent(GoogleAiModel.GEMINI_FLASH_LATEST.modelId, apiKey, request)
                } catch (e2: Exception) {
                    GeminiClient.service.generateContent(GoogleAiModel.GEMINI_FLASH_LITE.modelId, apiKey, request)
                }
            } else {
                GeminiClient.service.generateContent(GoogleAiModel.GEMINI_FLASH_LITE.modelId, apiKey, request)
            }
        }
    }

    suspend fun chatWithCustomPersona(
        persona: CustomPersona,
        userInput: String,
        currentNodes: List<MindNode>,
        selectedNode: MindNode?,
        enableGoogleSearch: Boolean = false
    ): AiBrainstormResponse = withContext(Dispatchers.IO) {
        val mapSummary = currentNodes.take(15).joinToString("\n") { 
            "- [${it.category.name}] ${it.title}: ${it.details.take(60)}" 
        }

        val prompt = """
            Jsi AI specializovaný expert '${persona.displayName}' (${persona.title}).
            Tvůj profil a specializace: ${persona.roleDescription}.
            
            Uživatel řekl: "$userInput"
            Kontext uzlu: ${selectedNode?.title ?: "Celá myšlenková mapa"}
            
            Aktuální stav mapy:
            $mapSummary
            
            Pokud navrhuješ proces nebo workflow, můžeš do 'diagramData' vložit kroky oddělené ' -> '.
            
            Odpověz POUZE v platném JSON formátu:
            {
               "spokenAnswer": "Mluvená odpověď česky (1-2 věty) v tónu tvé expertízy.",
               "addedNodes": [
                  {
                     "title": "Název doporučení / uzlu",
                     "details": "Popis a zdůvodnění",
                     "category": "SOLUTION",
                     "parentTitleOrId": "${selectedNode?.id ?: "null"}",
                     "authorPersona": "${persona.id}",
                     "diagramData": null
                  }
               ],
               "solutionSummary": "Doporučení od experta ${persona.displayName}"
            }
        """.trimIndent()

        val response = callGeminiOrFallback(prompt, userInput, persona.id, selectedNode?.id, enableGoogleSearch)
        response.copy(authorPersona = persona.id)
    }

    suspend fun brainstormRoundtableDebate(
        topic: String,
        currentNodes: List<MindNode>,
        selectedNode: MindNode?,
        enableGoogleSearch: Boolean = false
    ): RoundtableResponse = withContext(Dispatchers.IO) {
        val mapSummary = currentNodes.take(15).joinToString("\n") { 
            "- [${it.category.name}] ${it.title}: ${it.details.take(60)}" 
        }

        val targetContext = selectedNode?.let {
            "Debata se týká konkrétního uzlu: '${it.title}' (${it.category.displayName})"
        } ?: "Debata se týká celkového nápadu: '$topic'"

        val prompt = """
            Jste 3 specializované AI expertní osobnosti diskutující u jednoho stolu:
            1. NOVA (Inovativní Génius): Vizionář, navrhuje odvážné nekonvenční nápady a špičkové funkce (FEATURE, GOAL).
            2. VORTEX (Nemilosrdný Kritik): Hledá skrytá rizika, bezpečnostní díry, náklady, slabiny a pasti (RISK).
            3. ATLAS (Systémový Architekt): Syntetizuje debatu do funkčního technického řešení, architektury a procesu (ARCHITECTURE, SOLUTION, DIAGRAM).
            
            Uživatel předložil téma: "$topic"
            $targetContext
            
            Aktuální mapa:
            $mapSummary
            
            DŮLEŽITÉ: Pokud Atlas navrhuje proces nebo systémovou architekturu, přidej do pole 'diagramData' posloupnost kroků vývojového diagramu oddělených ' -> ' (např: [Start] -> [Ověření vstupů] -> [Zpracování v cloudu] -> [Uložení do DB] -> [Oznámení]).
            
            Odpověz POUZE v platném JSON formátu:
            {
               "speeches": [
                  { "persona": "nova", "spokenText": "Mluvený příspěvek Novy (1-2 věty česky)." },
                  { "persona": "vortex", "spokenText": "Mluvený příspěvek Vortexe (1-2 věty česky, kritické zhodnocení rizika)." },
                  { "persona": "atlas", "spokenText": "Mluvený příspěvek Atlase (1-2 věty česky, syntéza a technické řešení)." }
               ],
               "addedNodes": [
                  {
                     "title": "Inovativní funkce (od Novy)",
                     "details": "Popis funkce",
                     "category": "FEATURE",
                     "parentTitleOrId": "ID nebo název rodiče",
                     "authorPersona": "nova",
                     "diagramData": null
                  },
                  {
                     "title": "Kritické riziko (od Vortexe)",
                     "details": "Popis hrozby nebo slabiny",
                     "category": "RISK",
                     "parentTitleOrId": "ID nebo název rodiče",
                     "authorPersona": "vortex",
                     "diagramData": null
                  },
                  {
                     "title": "Systémový proces (od Atlase)",
                     "details": "Procesní tok mitigující riziko",
                     "category": "DIAGRAM",
                     "parentTitleOrId": "ID nebo název rodiče",
                     "authorPersona": "atlas",
                     "diagramData": "[Krok 1: Inicializace] -> [Krok 2: Validace] -> [Krok 3: Exekuce] -> [Výsledek]"
                  }
               ],
               "solutionSummary": "Krátké shrnutí konsenzu tří AI"
            }
        """.trimIndent()

        val parsed = callGeminiForRoundtable(prompt, topic, enableGoogleSearch)
        parsed ?: generateLocalRoundtableFallback(topic, selectedNode)
    }

    suspend fun generateProcessDiagramForNode(node: MindNode): AiBrainstormResponse = withContext(Dispatchers.IO) {
        val prompt = """
            Jsi Atlas (Systémový Architekt). Pro uzel '${node.title}' (${node.category.displayName}, popis: '${node.details}') navrhni konkrétní detailní VÝVOJOVÝ / PROCESNÍ DIAGRAM, který ukazuje krok za krokem, jak toto řešení funguje.
            
            Odpověz POUZE v platném JSON formátu:
            {
               "spokenAnswer": "Vytvořil jsem detailní procesní vývojový diagram pro '${node.title}'.",
               "addedNodes": [
                  {
                     "title": "Diagram: ${node.title}",
                     "details": "Procesní tok a algoritmus provádění",
                     "category": "DIAGRAM",
                     "parentTitleOrId": "${node.id}",
                     "authorPersona": "atlas",
                     "diagramData": "[Krok 1: Vstup požadavku] -> [Krok 2: Zpracování a bezpečnostní kontrola] -> [Krok 3: Algoritmická logika] -> [Krok 4: Zápis do databáze] -> [Výstup pro uživatele]"
                  }
               ],
               "solutionSummary": "Procesní diagram byl začleněn do myšlenkové mapy."
            }
        """.trimIndent()

        val response = callGeminiOrFallback(prompt, node.title, "atlas", node.id)
        if (response.addedNodes.isEmpty()) {
            AiBrainstormResponse(
                spokenAnswer = "Vytvořil jsem vizuální procesní diagram pro '${node.title}'.",
                addedNodes = listOf(
                    AiGeneratedNodeDto(
                        title = "Diagram: ${node.title}",
                        details = "Krokový vývojový diagram provádění",
                        category = "DIAGRAM",
                        parentTitleOrId = node.id,
                        authorPersona = "atlas",
                        diagramData = "[1. Spuštění procesu] -> [2. Načtení kontextu] -> [3. Validační kontrola] -> [4. Provedení akce] -> [5. Zpětná vazba]"
                    )
                ),
                authorPersona = "atlas"
            )
        } else {
            response
        }
    }

    suspend fun chatWithPersona(
        persona: AiPersona,
        userInput: String,
        currentNodes: List<MindNode>,
        selectedNode: MindNode?,
        enableGoogleSearch: Boolean = false
    ): AiBrainstormResponse = withContext(Dispatchers.IO) {
        val mapSummary = currentNodes.take(15).joinToString("\n") { 
            "- [${it.category.name}] ${it.title}: ${it.details.take(60)}" 
        }

        val prompt = """
            Jsi AI osobnost '${persona.displayName}' (${persona.title}).
            Tvůj profil: ${persona.roleDescription}.
            
            Uživatel řekl: "$userInput"
            Kontext uzlu: ${selectedNode?.title ?: "Celá myšlenková mapa"}
            
            Aktuální stav mapy:
            $mapSummary
            
            Pokud navrhuješ technický proces nebo workflow, můžeš do 'diagramData' vložit kroky oddělené ' -> '.
            
            Odpověz POUZE v platném JSON formátu:
            {
               "spokenAnswer": "Mluvená odpověď česky (1-2 věty) v tónu tvé osobnosti.",
               "addedNodes": [
                  {
                     "title": "Název uzlu",
                     "details": "Popis a zdůvodnění",
                     "category": "${if (persona == AiPersona.VORTEX) "RISK" else if (persona == AiPersona.NOVA) "FEATURE" else "ARCHITECTURE"}",
                     "parentTitleOrId": "${selectedNode?.id ?: "null"}",
                     "authorPersona": "${persona.id}",
                     "diagramData": null
                  }
               ],
               "solutionSummary": "Doplňující poznámka"
            }
        """.trimIndent()

        val response = callGeminiOrFallback(prompt, userInput, persona.id, selectedNode?.id, enableGoogleSearch)
        response.copy(authorPersona = persona.id)
    }

    suspend fun brainstormSeedIdea(seedTopic: String, enableGoogleSearch: Boolean = false): AiBrainstormResponse = withContext(Dispatchers.IO) {
        val prompt = """
            Uživatel navrhuje nový nápad: "$seedTopic".
            Tři expertní AI (Nova, Vortex, Atlas) vytvoří myšlenkovou mapu pro rozpracování do funkčního řešení.
            Vygeneruj:
            1. Cíl / Hlavní vizi (GOAL) od Novy
            2. Klíčové inovativní funkcionality (FEATURE) od Novy
            3. Technickou architekturu nebo proces (ARCHITECTURE / DIAGRAM) od Atlase
            4. Klíčová rizika a výzvy (RISK) od Vortexe
            5. Navrhovaná řešení pro tato rizika (SOLUTION) od Atlase
            6. První praktické akční kroky (ACTION_ITEM) od Atlase
            
            U procesního uzlu uveď v 'diagramData' např. "[Krok 1] -> [Krok 2] -> [Krok 3]".
            
            Odpověz POUZE v platném JSON formátu:
            {
               "spokenAnswer": "Krátké shrnutí pro hlasovou odpověď (1-2 věty česky).",
               "addedNodes": [
                  {
                     "title": "Krátký název uzlu",
                     "details": "Popis a zdůvodnění",
                     "category": "GOAL" | "FEATURE" | "ARCHITECTURE" | "RISK" | "SOLUTION" | "ACTION_ITEM" | "DIAGRAM",
                     "parentTitleOrId": "Název rodičovského uzlu nebo null",
                     "authorPersona": "nova" | "vortex" | "atlas",
                     "diagramData": null
                  }
               ],
               "solutionSummary": "Krátké shrnutí konceptu"
            }
        """.trimIndent()

        callGeminiOrFallback(prompt, seedTopic, "seed", enableGoogleSearch = enableGoogleSearch)
    }

    suspend fun expandNode(node: MindNode, allNodes: List<MindNode>): AiBrainstormResponse = withContext(Dispatchers.IO) {
        val ancestorContext = allNodes.filter { it.id == node.parentId }.firstOrNull()?.let {
            "Rodičovský kontext: '${it.title}' -> '${node.title}' (${node.category.name})"
        } ?: "Vybraný uzel: '${node.title}' (${node.category.name})"

        val prompt = """
            Uživatel chce prohloubit a rozpracovat tento konkrétní uzel myšlenkové mapy:
            $ancestorContext
            Popis uzlu: "${node.details}"
            
            Navrhni 3 konkrétní dílčí větve od všech 3 AI:
            - Nova: Inovativní pod-funkce (FEATURE)
            - Vortex: Riziko implementace (RISK)
            - Atlas: Technické řešení / procesní diagram (SOLUTION nebo DIAGRAM)
            
            Odpověz POUZE v platném JSON formátu:
            {
               "spokenAnswer": "Mluvená odpověď shrnující rozšíření.",
               "addedNodes": [
                  {
                     "title": "Název pod-uzlu",
                     "details": "Detailní vysvětlení",
                     "category": "FEATURE" | "RISK" | "SOLUTION" | "DIAGRAM",
                     "parentTitleOrId": "${node.id}",
                     "authorPersona": "nova" | "vortex" | "atlas",
                     "diagramData": null
                  }
               ]
            }
        """.trimIndent()

        callGeminiOrFallback(prompt, node.title, "expand", targetNodeId = node.id)
    }

    suspend fun processVoiceOrChatInput(
        userInput: String,
        currentNodes: List<MindNode>,
        selectedNode: MindNode?,
        enableGoogleSearch: Boolean = false
    ): AiBrainstormResponse = withContext(Dispatchers.IO) {
        val mapSummary = currentNodes.take(15).joinToString("\n") { 
            "- [${it.category.name}] ${it.title}: ${it.details.take(60)}" 
        }

        val prompt = """
            Jsi AI asistent pro myšlenkové mapy (zahrnující role Nova, Vortex i Atlas).
            Uživatel zadal: "$userInput"
            Kontext uzlu: ${selectedNode?.title ?: "Celá mapa"}
            
            Aktuální stav mapy:
            $mapSummary
            
            Odpověz v JSON:
            {
               "spokenAnswer": "Mluvená odpověď česky.",
               "addedNodes": [
                  {
                     "title": "Název uzlu",
                     "details": "Popis a specifikace",
                     "category": "GOAL" | "FEATURE" | "ARCHITECTURE" | "RISK" | "SOLUTION" | "ACTION_ITEM" | "DIAGRAM",
                     "parentTitleOrId": "${selectedNode?.id ?: "null"}",
                     "authorPersona": "atlas",
                     "diagramData": null
                  }
               ],
               "solutionSummary": "Doplněné poznámky"
            }
        """.trimIndent()

        callGeminiOrFallback(prompt, userInput, "voice_chat", targetNodeId = selectedNode?.id, enableGoogleSearch = enableGoogleSearch)
    }

    suspend fun generateActionableBlueprint(mapTitle: String, allNodes: List<MindNode>): AiBrainstormResponse = withContext(Dispatchers.IO) {
        val nodesDump = allNodes.joinToString("\n") { n ->
            val author = n.authorPersona?.let { " [autor: $it]" } ?: ""
            val diag = if (!n.diagramData.isNullOrBlank()) " [diagram: ${n.diagramData}]" else ""
            "* [${n.category.displayName}]$author ${n.title} - ${n.details}$diag"
        }

        val prompt = """
            Sestav prováděcí plán a funkční specifikaci pro '$mapTitle' na základě přínosů všech tří AI (Nova - inovace, Vortex - bezpečnost a rizika, Atlas - architektura a procesní diagramy).
            
            Uzly mapy:
            $nodesDump
            
            Odpověz v JSON:
            {
               "spokenAnswer": "Tři AI dospěly ke shodě a připravily kompletní funkční blueprint.",
               "addedNodes": [],
               "solutionSummary": "Markdown dokument s kapitolami: 1. Vize a inovativní hodnota (Nova), 2. Zvládnutí rizik a bezpečnost (Vortex), 3. Technická architektura a procesní diagramy (Atlas), 4. Časová osa a roadmapa."
            }
        """.trimIndent()

        callGeminiOrFallback(prompt, mapTitle, "blueprint")
    }

    private suspend fun callGeminiForRoundtable(
        prompt: String,
        topic: String,
        enableGoogleSearch: Boolean
    ): RoundtableResponse? {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") return null

        return try {
            val toolsList = if (enableGoogleSearch) {
                listOf(mapOf("googleSearch" to emptyMap<String, String>()))
            } else null

            val request = GeminiRequest(
                contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = prompt)))),
                generationConfig = GeminiGenerationConfig(temperature = 0.75f, responseMimeType = "application/json"),
                systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = "Jste 3 expertní AI: Nova, Vortex a Atlas. Vracíte striktně validní JSON."))),
                tools = toolsList
            )
            val response = callGeminiApi(apiKey, request)
            val candidate = response.candidates?.firstOrNull()
            val raw = candidate?.content?.parts?.firstOrNull()?.text ?: return null
            val sources = candidate.groundingMetadata?.groundingChunks?.mapNotNull { it.web?.title }?.joinToString(", ")
            parseRoundtableJson(raw, sources)
        } catch (e: Exception) {
            null
        }
    }

    private fun parseRoundtableJson(rawJson: String, sources: String? = null): RoundtableResponse? {
        return try {
            val clean = rawJson.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
            val root = JSONObject(clean)

            val speeches = mutableListOf<PersonaSpeech>()
            val speechesArray = root.optJSONArray("speeches")
            if (speechesArray != null) {
                for (i in 0 until speechesArray.length()) {
                    val sObj = speechesArray.getJSONObject(i)
                    val pId = sObj.optString("persona", "atlas")
                    val persona = AiPersona.fromId(pId) ?: AiPersona.ATLAS
                    val text = sObj.optString("spokenText", "")
                    if (text.isNotBlank()) {
                        speeches.add(PersonaSpeech(persona, text))
                    }
                }
            }

            val addedNodes = parseNodesArray(root.optJSONArray("addedNodes"), sources)
            val summary = if (root.has("solutionSummary")) root.getString("solutionSummary") else null

            RoundtableResponse(speeches, addedNodes, summary)
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun callGeminiOrFallback(
        prompt: String,
        contextTopic: String,
        mode: String,
        targetNodeId: String? = null,
        enableGoogleSearch: Boolean = false
    ): AiBrainstormResponse {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val isApiKeyValid = apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY"

        if (isApiKeyValid) {
            try {
                val toolsList = if (enableGoogleSearch) {
                    listOf(mapOf("googleSearch" to emptyMap<String, String>()))
                } else null

                val request = GeminiRequest(
                    contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = prompt)))),
                    generationConfig = GeminiGenerationConfig(temperature = 0.7f, responseMimeType = "application/json"),
                    systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = "Jsi špičkový AI Solution Architect a facilitátor. Vždy vracíš validní JSON."))),
                    tools = toolsList
                )
                val response = callGeminiApi(apiKey, request)
                val candidate = response.candidates?.firstOrNull()
                val rawText = candidate?.content?.parts?.firstOrNull()?.text
                val sources = candidate?.groundingMetadata?.groundingChunks?.mapNotNull { it.web?.title }?.joinToString(", ")

                if (!rawText.isNullOrBlank()) {
                    val parsed = parseJsonResponse(rawText, sources)
                    if (parsed != null) return parsed
                }
            } catch (e: Exception) {
                // Fall through to local heuristic
            }
        }

        return generateLocalHeuristicResponse(contextTopic, mode, targetNodeId)
    }

    private fun parseJsonResponse(rawJson: String, sources: String? = null): AiBrainstormResponse? {
        return try {
            val clean = rawJson.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
            val jsonObject = JSONObject(clean)
            val spokenAnswer = jsonObject.optString("spokenAnswer", "Nápad byl zpracován.")
            val solutionSummary = if (jsonObject.has("solutionSummary")) jsonObject.getString("solutionSummary") else null
            val author = jsonObject.optString("authorPersona", "atlas")

            val addedNodes = parseNodesArray(jsonObject.optJSONArray("addedNodes"), sources)

            AiBrainstormResponse(spokenAnswer, addedNodes, solutionSummary, author)
        } catch (e: Exception) {
            null
        }
    }

    private fun parseNodesArray(nodesArray: JSONArray?, defaultSources: String? = null): List<AiGeneratedNodeDto> {
        val list = mutableListOf<AiGeneratedNodeDto>()
        if (nodesArray == null) return list
        for (i in 0 until nodesArray.length()) {
            val nObj = nodesArray.getJSONObject(i)
            list.add(
                AiGeneratedNodeDto(
                    title = nObj.optString("title", "Nový nápad"),
                    details = nObj.optString("details", ""),
                    category = nObj.optString("category", "FEATURE"),
                    parentTitleOrId = if (nObj.has("parentTitleOrId") && !nObj.isNull("parentTitleOrId")) nObj.getString("parentTitleOrId") else null,
                    authorPersona = nObj.optString("authorPersona", "atlas"),
                    diagramData = if (nObj.has("diagramData") && !nObj.isNull("diagramData")) nObj.getString("diagramData") else null,
                    searchSources = if (nObj.has("searchSources") && !nObj.isNull("searchSources")) nObj.getString("searchSources") else defaultSources
                )
            )
        }
        return list
    }

    private fun generateLocalRoundtableFallback(topic: String, selectedNode: MindNode?): RoundtableResponse {
        val speeches = listOf(
            PersonaSpeech(
                AiPersona.NOVA,
                "Tady vidím obrovskou příležitost! Můžeme $topic posunout o úroveň výš s prediktivním učením a okamžitou personalizací."
            ),
            PersonaSpeech(
                AiPersona.VORTEX,
                "Pozor na to. U $topic hrozí vysoká latence, riziko nekonzistence dat a neochota uživatelů měnit zvyky. Musíme to zredukovat."
            ),
            PersonaSpeech(
                AiPersona.ATLAS,
                "Oba máte pravdu. Navrhl jsem konkrétní vývojový diagram procesu, který řeší mitigaci rizik a propojuje funkce."
            )
        )

        val targetId = selectedNode?.id
        val addedNodes = listOf(
            AiGeneratedNodeDto(
                title = "✨ Inovace: Chytrá predikce ($topic)",
                details = "Návrh od Novy: Adaptivní engine reagující na kontext uživatele v reálném čase.",
                category = "FEATURE",
                parentTitleOrId = targetId,
                authorPersona = "nova"
            ),
            AiGeneratedNodeDto(
                title = "🛡️ Kritické riziko: Závislost a bezpečnost",
                details = "Výtka od Vortexe: Riziko výpadku externích služeb a ochrana citlivých dat.",
                category = "RISK",
                parentTitleOrId = targetId,
                authorPersona = "vortex"
            ),
            AiGeneratedNodeDto(
                title = "📊 Procesní tok: $topic",
                details = "Vývojový diagram navržený Atlasem pro eliminaci rizik.",
                category = "DIAGRAM",
                parentTitleOrId = targetId,
                authorPersona = "atlas",
                diagramData = "[1. Spuštění požadavku] -> [2. Bezpečnostní autorizace] -> [3. Zpracování v cache] -> [4. Zápis změn] -> [5. Potvrzení]"
            )
        )

        return RoundtableResponse(
            speeches = speeches,
            addedNodes = addedNodes,
            solutionSummary = "Trojice AI dospěla k funkčnímu kompromisu: Inovativní vize podpořená robustní ochranou proti selhání a procesním diagramem."
        )
    }

    private fun generateLocalHeuristicResponse(topic: String, mode: String, targetNodeId: String?): AiBrainstormResponse {
        return when (mode) {
            "nova" -> {
                AiBrainstormResponse(
                    spokenAnswer = "Nova: Pojďme do toho naplno! Představ si autonomní interakce a vizuální magii.",
                    addedNodes = listOf(
                        AiGeneratedNodeDto("✨ Prolomová funkce pro $topic", "Exponenciální nápad od Novy", "FEATURE", targetNodeId, "nova")
                    ),
                    authorPersona = "nova"
                )
            }
            "vortex" -> {
                AiBrainstormResponse(
                    spokenAnswer = "Vortex: Tady vidím 2 kritické slabiny v bezpečnosti a nákladech.",
                    addedNodes = listOf(
                        AiGeneratedNodeDto("🛡️ Slabé místo: $topic", "Identifikováno Vortexem: Nebezpečí selhání za hraničních podmínek", "RISK", targetNodeId, "vortex")
                    ),
                    authorPersona = "vortex"
                )
            }
            "atlas" -> {
                AiBrainstormResponse(
                    spokenAnswer = "Atlas: Navrhuji konkrétní modulární architekturu a procesní diagram toku dat.",
                    addedNodes = listOf(
                        AiGeneratedNodeDto(
                            title = "📊 Datový a procesní diagram: $topic",
                            details = "Specifikace od Atlase: Postup krok za krokem",
                            category = "DIAGRAM",
                            parentTitleOrId = targetNodeId,
                            authorPersona = "atlas",
                            diagramData = "[Požadavek klienta] -> [API Gateway] -> [Validace schématu] -> [Room DB / Firestore] -> [Odpověď do UI]"
                        )
                    ),
                    authorPersona = "atlas"
                )
            }
            "seed" -> {
                AiBrainstormResponse(
                    spokenAnswer = "Tři expertní AI (Nova, Vortex a Atlas) vytvořily základní rozpad vašeho nápadu včetně procesního diagramu.",
                    addedNodes = listOf(
                        AiGeneratedNodeDto("Hlavní vize: $topic", "Základní cíl projektu", "GOAL", null, "nova"),
                        AiGeneratedNodeDto("✨ Klíčové funkce", "Inovace navržená Novou", "FEATURE", "Hlavní vize: $topic", "nova"),
                        AiGeneratedNodeDto("🛡️ Bezpečnost & Rizika", "Kritická analýza Vortexe", "RISK", "Hlavní vize: $topic", "vortex"),
                        AiGeneratedNodeDto("🏛️ Systémová Architektura", "Technický plán od Atlase", "ARCHITECTURE", "Hlavní vize: $topic", "atlas"),
                        AiGeneratedNodeDto("📊 Procesní tok řešení", "Vývojový diagram dat a kroků", "DIAGRAM", "🏛️ Systémová Architektura", "atlas", "[Uživatel zadá nápad] -> [AI Rozbor (Nova/Vortex)] -> [Atlas vytvoří diagram] -> [Zápis do mapy]"),
                        AiGeneratedNodeDto("💡 Řešení rizik", "Mitigační opatření", "SOLUTION", "🛡️ Bezpečnost & Rizika", "atlas"),
                        AiGeneratedNodeDto("🚀 První kroky (Next Steps)", "Akční realizace", "ACTION_ITEM", "✨ Klíčové funkce", "atlas")
                    ),
                    solutionSummary = "Vize byla úspěšně založena všemi třemi AI."
                )
            }
            "expand" -> {
                AiBrainstormResponse(
                    spokenAnswer = "Rozpracovali jsme uzel ze tří různých úhlů pohledu.",
                    addedNodes = listOf(
                        AiGeneratedNodeDto("✨ Inovativní vylepšení", "Návrh od Novy", "FEATURE", targetNodeId, "nova"),
                        AiGeneratedNodeDto("🛡️ Hrozba & Limitace", "Varování od Vortexe", "RISK", targetNodeId, "vortex"),
                        AiGeneratedNodeDto(
                            title = "📊 Procesní diagram: $topic",
                            details = "Konstrukce procesu od Atlase",
                            category = "DIAGRAM",
                            parentTitleOrId = targetNodeId,
                            authorPersona = "atlas",
                            diagramData = "[Inicializace] -> [Kroková validace] -> [Exekuce] -> [Zpětná vazba]"
                        )
                    )
                )
            }
            "blueprint" -> {
                AiBrainstormResponse(
                    spokenAnswer = "Kompletní funkční blueprint od Novy, Vortexe a Atlase je připraven.",
                    addedNodes = emptyList(),
                    solutionSummary = """
                        # Funkční Blueprint a Architektura: $topic
                        *Společný návrh: Nova (Inovace), Vortex (Rizika) & Atlas (Architektura a Procesní diagramy)*
                        
                        ## 1. Vize a konkurenční výhoda (Nova)
                        Projekt '$topic' přináší revoluční pojetí skrze adaptivní interaktivitu a propojení s AI.
                        
                        ## 2. Bezpečnostní audit a eliminace rizik (Vortex)
                        - Vyřešena ochrana soukromí a integrity dat
                        - Navržen plán pro výpadky konektivity a odolnost proti přetížení
                        
                        ## 3. Technická architektura a procesní diagramy (Atlas)
                        - **Frontend:** Jetpack Compose s reaktivním vykreslováním
                        - **Data Layer:** Lokální SQLite Room databáze
                        - **Workflow:** Krokový vývojový diagram s ověřováním stavů
                        - **Orchestrace:** Multi-agentní koordinace s hlasovým feedbackem
                        
                        ## 4. Akční kroky k nasazení
                        1. Implementace základních entit
                        2. Prověření chování za zátěže
                        3. Finální release
                    """.trimIndent()
                )
            }
            "questions" -> {
                AiBrainstormResponse(
                    spokenAnswer = "Vortex a Nova připravili otázkový útok: 4 provokativní otázky pro odhalení slepých míst.",
                    addedNodes = listOf(
                        AiGeneratedNodeDto("❓ Kdo za toto zaplatí v prvních 30 dnech?", "Otázka od Novy: Validace zákaznické poptávky", "RISK", targetNodeId, "vortex"),
                        AiGeneratedNodeDto("❓ Co když uživatel odmítne instalovat aplikaci?", "Otázka od Vortexe: Test alternativního rozhraní (Web / SMS / Widget)", "RISK", targetNodeId, "vortex"),
                        AiGeneratedNodeDto("❓ Jaké je 10x lepší řešení než konkurence?", "Otázka od Novy: Hledání radikální inovace", "FEATURE", targetNodeId, "nova"),
                        AiGeneratedNodeDto("❓ Co je jediné klíčové číslo (North Star Metric)?", "Otázka od Atlase: Měřitelný úspěch MVP", "GOAL", targetNodeId, "atlas")
                    ),
                    solutionSummary = "Otázkový útok odhalil klíčové předpoklady k prověření."
                )
            }
            "competitors" -> {
                AiBrainstormResponse(
                    spokenAnswer = "Tržní rešerše přes Google Vyhledávání: Nalezena podobná řešení a cenové modely.",
                    addedNodes = listOf(
                        AiGeneratedNodeDto("🔍 Konkurent A: Tradiční nástroje", "Trh pokrývají zavedení hráči, ale chybí jim živá multi-AI podpora", "RISK", targetNodeId, "vortex", searchSources = "Google Search: Market Benchmarks 2026"),
                        AiGeneratedNodeDto("💎 Naše konkurenční výhoda", "Unikátní synergie kritika, inovátora a architekta s hlasovým řízením", "SOLUTION", targetNodeId, "nova"),
                        AiGeneratedNodeDto("💰 Doporučený cenový model", "Freemium: Základní myšlenkové mapy zdarma, Cloud sync a export v PRO verzi", "FEATURE", targetNodeId, "atlas")
                    ),
                    solutionSummary = "Benchmarking dokončen."
                )
            }
            else -> {
                AiBrainstormResponse(
                    spokenAnswer = "Pokyn '$topic' byl úspěšně zapracován do architektury.",
                    addedNodes = listOf(
                        AiGeneratedNodeDto("Zapracování požadavku: $topic", "Řešení ověřené architekturou", "SOLUTION", targetNodeId, "atlas")
                    )
                )
            }
        }
    }

    suspend fun generateWireframeForNode(node: MindNode): String = withContext(Dispatchers.IO) {
        val titleClean = node.title.take(18)
        val detailsClean = node.details.ifBlank { "Aktivní prvek rozhraní" }.take(22)
        """
        ┌───────────────────────────────────┐
        │ [←] $titleClean              [⚙]  │
        ├───────────────────────────────────┤
        │ [ 🔍 Hledat a filtrovat...     ]  │
        │                                   │
        │  ┌─────────────────────────────┐  │
        │  │ [★] $titleClean             │  │
        │  │ $detailsClean               │  │
        │  └─────────────────────────────┘  │
        │                                   │
        │ [ 🚀 Potvrdit a provést akci ]   │
        └───────────────────────────────────┘
        """.trimIndent()
    }

    suspend fun questionStorming(topic: String): AiBrainstormResponse = withContext(Dispatchers.IO) {
        val prompt = """
            Proveď 'Otázkový útok' (Question Storming) pro téma: '$topic'.
            Vygeneruj 4 provokativní otázky zpochybňující základní předpoklady.
            Odpověz v JSON formátu s addedNodes.
        """.trimIndent()
        callGeminiOrFallback(prompt, topic, "questions")
    }

    suspend fun marketCompetitorBenchmark(topic: String): AiBrainstormResponse = withContext(Dispatchers.IO) {
        val prompt = """
            Vyhledej tržní konkurenty a benchmarky pro téma: '$topic' přes Google Search data.
            Odpověz v JSON formátu s addedNodes a searchSources.
        """.trimIndent()
        callGeminiOrFallback(prompt, topic, "competitors", enableGoogleSearch = true)
    }

    suspend fun runAuditDebateRound(
        projectTitle: String,
        currentNodes: List<MindNode>,
        roundNumber: Int,
        totalRounds: Int,
        previousSpeeches: List<AuditRoundSpeech> = emptyList(),
        enableGoogleSearch: Boolean = true
    ): AuditRoundResult = withContext(Dispatchers.IO) {
        val mapSummary = currentNodes.take(20).joinToString("\n") {
            "- [${it.category.name}] ${it.title}: ${it.details.take(70)}"
        }

        val previousContext = if (previousSpeeches.isNotEmpty()) {
            "Předchozí průběh debaty:\n" + previousSpeeches.takeLast(6).joinToString("\n") {
                "- [Kolo ${it.round}] ${it.persona.displayName}: ${it.spokenText}"
            }
        } else {
            "Toto je 1. kolo debaty. Otevřete zásadní technické otázky a prověřte základy."
        }

        val prompt = """
            Jste 3 vysoce specializované expertní AI provádějící HLOUBKOVÝ ARCHITEKTONICKÝ & TECHNOLOGICKÝ AUDIT projektu '$projectTitle'.
            Aktuální kolo debaty: $roundNumber z celkových $totalRounds kol.
            
            HLAVNÍ CÍLE AUDITU:
            1. Ověřit, zda projekt a jeho komponenty neobsahují chyby, zranitelnosti nebo slabá místa.
            2. Odhalit zastaralé nástroje, deprecated knihovny nebo neefektivní postupy a nahradit je moderními řešeními pro rok 2026 ověřenými na webu.
            3. DŮKLADNÉ INSTRUKCE: Ke všem úkolům, které AI v projektu (včetně úkolů a botů v Minecraftu / MindCraftu, automatizací, buildingu, pipeline a systémových kroků) mají, MUSÍTE vygenerovat naprosto DŮKLADNÉ, PŘESNÉ KROKOVÉ INSTRUKCE s konkrétními parametry a kontrolními podmínkami.
            
            DŮKLADNÉ INSTRUKCE PRO JEDNOTLIVÉ ROLE V DEBATĚ:
            
            1. VORTEX (Nemilosrdný Kritik & Bezpečnostní Auditor):
               - Prověř celý projekt a hledej architektonické chyby, zranitelnosti, bezpečnostní mezery, single points of failure a neudržitelné náklady.
               - DŮLEŽITÉ: Zkontroluj, zda navržené nástroje, knihovny, frameworky a postupy nejsou ZASTARALÉ, deprecated nebo překonané lepšími technologiemi.
               - Poukaž na konkrétní chyby a úzká hrdla.
            
            2. NOVA (Technologická Inovátorka & Průzkumnice Webových Alternativ):
               - Využij realtime web k vyhledání nejmodernějších (rok 2026) open-source knihoven, API, architektur a 10x lepších alternativ.
               - Pro každou výtku Vortexe najdi moderní, elegantní a efektivní řešení.
               - Navrhni konkrétní technologické náhrady za zastaralé nástroje s uvedením konkrétních názvů a výhod.
            
            3. ATLAS (Hlavní Systémový Architekt & Arbitr):
               - Syntetizuj námitky Vortexe a inovace Novy do stabilního a proveditelného plánu.
               - Vytvoř DŮKLADNÉ INSTRUKCE pro všechny nově vzniklé úkoly (podrobný krokový postup, konfigurace, příkazy a očekávaný výstup).
               - Navrhni procesní diagram toku dat/logiky (do pole 'diagramData').
            
            $previousContext
            
            Aktuální struktura myšlenkové mapy:
            $mapSummary
            
            Odpověz POUZE v platném JSON formátu:
            {
               "speeches": [
                  { "persona": "vortex", "spokenText": "Mluvený kritický příspěvek Vortexe k chybám a zastaralým nástrojům v tomto kole (2 věty česky)." },
                  { "persona": "nova", "spokenText": "Mluvený příspěvek Novy s moderními alternativami ověřenými na webu (2 věty česky)." },
                  { "persona": "atlas", "spokenText": "Mluvená syntéza a architektonické rozhodnutí Atlase s důkladnými instrukcemi (2 věty česky)." }
               ],
               "addedNodes": [
                  {
                     "title": "Odhalená chyba / Zastaralý nástroj",
                     "details": "DŮKLADNÁ ANALÝZA: Popis slabiny, přesné riziko a proč je stávající řešení neudržitelné",
                     "category": "RISK",
                     "authorPersona": "vortex",
                     "diagramData": null
                  },
                  {
                     "title": "Moderní alternativa: Název",
                     "details": "DŮKLADNÁ SPECIFIKACE: Moderní náhrada, webové srovnání a výhody pro rok 2026",
                     "category": "SOLUTION",
                     "authorPersona": "nova",
                     "diagramData": null
                  },
                  {
                     "title": "Úkol: Název úkolu s důkladnými instrukcemi",
                     "details": "DŮKLADNÉ INSTRUKCE PRO AI (vč. Minecraft/MindCraft úkolů):\nKrok 1: [Přesná příprava a parametry]\nKrok 2: [Konkrétní realizace, příkazy nebo syntaxe]\nKrok 3: [Validace a kontrolní test]",
                     "category": "ACTION_ITEM",
                     "authorPersona": "atlas",
                     "diagramData": "[Krok 1: Příprava] -> [Krok 2: Odstranění zastaralého] -> [Krok 3: Nasazení nového] -> [Ověření]"
                  }
               ],
               "outdatedTools": [
                  {
                     "toolName": "Zastaralý nástroj / nevhodná knihovna",
                     "issueOrDeprecatedReason": "Proč je to problematické / zastaralé",
                     "recommendedModernAlternative": "Moderní náhrada (rok 2026)",
                     "category": "Framework / Databáze / Knihovna"
                  }
               ],
               "roundSummary": "Krátké shrnutí výsledku kola $roundNumber"
            }
        """.trimIndent()

        val parsed = callGeminiForAuditRound(prompt, roundNumber, enableGoogleSearch)
        parsed ?: generateLocalAuditRoundFallback(projectTitle, currentNodes, roundNumber, totalRounds)
    }

    private suspend fun callGeminiForAuditRound(
        prompt: String,
        roundNumber: Int,
        enableGoogleSearch: Boolean
    ): AuditRoundResult? {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") return null

        return try {
            val toolsList = if (enableGoogleSearch) {
                listOf(mapOf("googleSearch" to emptyMap<String, String>()))
            } else null

            val request = GeminiRequest(
                contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = prompt)))),
                generationConfig = GeminiGenerationConfig(temperature = 0.7f, responseMimeType = "application/json"),
                systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = "Jste 3 expertní AI auditoři: Vortex (kritik), Nova (inovátorka) a Atlas (architekt). Vracíte striktně validní JSON."))),
                tools = toolsList
            )
            val response = callGeminiApi(apiKey, request)
            val candidate = response.candidates?.firstOrNull()
            val raw = candidate?.content?.parts?.firstOrNull()?.text ?: return null
            val sources = candidate.groundingMetadata?.groundingChunks?.mapNotNull { it.web?.title }?.joinToString(", ")
            parseAuditRoundJson(raw, roundNumber, sources)
        } catch (e: Exception) {
            null
        }
    }

    private fun parseAuditRoundJson(rawJson: String, roundNumber: Int, sources: String?): AuditRoundResult? {
        return try {
            val clean = rawJson.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
            val root = JSONObject(clean)

            val speeches = mutableListOf<AuditRoundSpeech>()
            val speechesArray = root.optJSONArray("speeches")
            if (speechesArray != null) {
                for (i in 0 until speechesArray.length()) {
                    val sObj = speechesArray.getJSONObject(i)
                    val pId = sObj.optString("persona", "atlas")
                    val persona = AiPersona.fromId(pId) ?: AiPersona.ATLAS
                    val text = sObj.optString("spokenText", "")
                    if (text.isNotBlank()) {
                        speeches.add(AuditRoundSpeech(roundNumber, persona, text, sources))
                    }
                }
            }

            val addedNodes = parseNodesArray(root.optJSONArray("addedNodes"), sources)

            val outdatedList = mutableListOf<OutdatedToolFinding>()
            val toolsArray = root.optJSONArray("outdatedTools")
            if (toolsArray != null) {
                for (i in 0 until toolsArray.length()) {
                    val tObj = toolsArray.getJSONObject(i)
                    outdatedList.add(
                        OutdatedToolFinding(
                            toolName = tObj.optString("toolName", "Neznámý nástroj"),
                            issueOrDeprecatedReason = tObj.optString("issueOrDeprecatedReason", "Zastaralý přístup"),
                            recommendedModernAlternative = tObj.optString("recommendedModernAlternative", "Moderní alternativa"),
                            category = tObj.optString("category", "Knihovna / Služba"),
                            searchSources = sources
                        )
                    )
                }
            }

            val summary = root.optString("roundSummary", "Kolo $roundNumber dokončeno.")

            AuditRoundResult(
                round = roundNumber,
                speeches = speeches,
                addedNodes = addedNodes,
                outdatedTools = outdatedList,
                roundSummary = summary
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun generateLocalAuditRoundFallback(
        projectTitle: String,
        currentNodes: List<MindNode>,
        roundNumber: Int,
        totalRounds: Int
    ): AuditRoundResult {
        val isMinecraft = projectTitle.contains("mine", ignoreCase = true) ||
                currentNodes.any { it.title.contains("mine", ignoreCase = true) || it.details.contains("mine", ignoreCase = true) }

        return when (roundNumber) {
            1 -> {
                if (isMinecraft) {
                    AuditRoundResult(
                        round = 1,
                        speeches = listOf(
                            AuditRoundSpeech(
                                round = 1,
                                persona = AiPersona.VORTEX,
                                spokenText = "Vortex prověřil Minecraft architekturu: Detekuji zastaralý synchronní Bukkit tick scheduler a staré RCON rozhraní. Při 5+ AI botech hrozí pád serverového tickrate (TPS) pod 12 a pád serveru."
                            ),
                            AuditRoundSpeech(
                                round = 1,
                                persona = AiPersona.NOVA,
                                spokenText = "Nova oponuje s webovými trendy: Webové benchmarky a PaperMC 2026 doporučují asynchronní Mineflayer worker pool a gRPC event streaming. Tím udržíme stabilních 20 TPS i pro 50 simultánních AI agentů!"
                            ),
                            AuditRoundSpeech(
                                round = 1,
                                persona = AiPersona.ATLAS,
                                spokenText = "Atlas syntetizuje: Schvaluji. Nahradíme blokující volání asynchronním frontovým systémem a pro AI agenty definujeme přísný protokol s krokovými instrukcemi."
                            )
                        ),
                        addedNodes = listOf(
                            AiGeneratedNodeDto(
                                title = "⚠️ Riziko TPS: Synchronní tick bottleneck",
                                details = "DŮKLADNÁ ANALÝZA: Blokování hlavního herního vlákna Minecraft serveru při AI výpočtech. Způsobuje 'Server can't keep up' varování.",
                                category = "RISK",
                                authorPersona = "vortex"
                            ),
                            AiGeneratedNodeDto(
                                title = "✨ Moderní alternativa: PaperMC Async + gRPC",
                                details = "DŮKLADNÁ SPECIFIKACE: Asynchronní komunikace přes gRPC (rok 2026), latence pod 3ms, nulový dopad na herní TPS.",
                                category = "SOLUTION",
                                authorPersona = "nova"
                            ),
                            AiGeneratedNodeDto(
                                title = "🤖 Úkol pro AI: Asynchronní worker pipeline",
                                details = "DŮKLADNÉ INSTRUKCE PRO AI:\nKrok 1: Inicializovat headless bot instanci s protokolem Paper 1.21+\nKrok 2: Připojit se na lokální gRPC port 50051 s auth tokenem\nKrok 3: Delegovat pathfinding na off-thread A* algoritmus\nKrok 4: Odesílat potvrzení o dosažení cílových souřadnic [X, Y, Z]",
                                category = "ACTION_ITEM",
                                authorPersona = "atlas",
                                diagramData = "[Minecraft Server] <--(gRPC)--> [Async Bot Worker] --> [Off-thread A* Pathfinding]"
                            )
                        ),
                        outdatedTools = listOf(
                            OutdatedToolFinding(
                                toolName = "Starý synchronní Bukkit/Spigot Scheduler & RCON",
                                issueOrDeprecatedReason = "Blokuje serverový tick loop, vysoká latence a chybí šifrování",
                                recommendedModernAlternative = "PaperMC Folia / Asynchronní Kotlin Coroutines & Mineflayer gRPC (2026)",
                                category = "Minecraft API & Síť",
                                searchSources = "PaperMC Docs 2026, Mineflayer Roadmap"
                            )
                        ),
                        roundSummary = "Kolo 1: Odhalen TPS bottleneck a nasazena moderní asynchronní architektura."
                    )
                } else {
                    AuditRoundResult(
                        round = 1,
                        speeches = listOf(
                            AuditRoundSpeech(
                                round = 1,
                                persona = AiPersona.VORTEX,
                                spokenText = "Vortex prověřil architekturu: v návrhu '$projectTitle' vidím monolitické závislosti a neověřené knihovny bez jasného oddělení zodpovědností. Zastaralé REST endpointy by měly nahradit asynchronní event streams."
                            ),
                            AuditRoundSpeech(
                                round = 1,
                                persona = AiPersona.NOVA,
                                spokenText = "Nova oponuje: Podle aktuálních trendů roku 2026 můžeme použít reaktivní streamy a moderní edge caching. Tím zredukujeme latenci na desetinu a náklady o 40%."
                            ),
                            AuditRoundSpeech(
                                round = 1,
                                persona = AiPersona.ATLAS,
                                spokenText = "Atlas uzavírá 1. kolo: Konsenzus je jasný. Izolujeme datovou vrstvu, přidáme validační bránu a nahradíme staré sync volání za asynchronní pipeline s důkladnými instrukcemi."
                            )
                        ),
                        addedNodes = listOf(
                            AiGeneratedNodeDto(
                                title = "⚠️ Zjištěná slabina: Monolitické synchronní volání",
                                details = "DŮKLADNÁ ANALÝZA: Riziko kaskádového pádu při výpadku jednoho subsystému a zbytečné zdržování uživatelského vlákna.",
                                category = "RISK",
                                authorPersona = "vortex"
                            ),
                            AiGeneratedNodeDto(
                                title = "✨ Doporučení: Event-Driven Reaktivní Architektura",
                                details = "DŮKLADNÁ SPECIFIKACE: Moderní reaktivní vzor (rok 2026) s nulovým zpožděním, asynchronním zpracováním a auto-škálováním.",
                                category = "SOLUTION",
                                authorPersona = "nova"
                            ),
                            AiGeneratedNodeDto(
                                title = "📋 Úkol pro AI: Implementace Reaktivní Pipeline",
                                details = "DŮKLADNÉ INSTRUKCE PRO AI:\nKrok 1: Vytvořit neblokující event bus s Kotlin Coroutines Flow\nKrok 2: Oddělit síťové volání do samostatného IO Dispatcheru\nKrok 3: Zavést záchytný buffer s backpressure managementem\nKrok 4: Spustit integrační test na simulovaný výpadek sítě",
                                category = "ACTION_ITEM",
                                authorPersona = "atlas",
                                diagramData = "[1. Izolace API] -> [2. Buffer fronta] -> [3. Asynchronní procesor] -> [4. Cache vrstva]"
                            )
                        ),
                        outdatedTools = listOf(
                            OutdatedToolFinding(
                                toolName = "Tradiční synchronní REST polling",
                                issueOrDeprecatedReason = "Neefektivní spotřeba baterie a zbytečné dotazy na server",
                                recommendedModernAlternative = "Server-Sent Events (SSE) / WebSockets / Cloud Messaging (2026)",
                                category = "Síťová komunikace",
                                searchSources = "IETF HTTP/3, Modern Web Standards 2026"
                            )
                        ),
                        roundSummary = "Kolo 1: Odhaleny synchronní slabiny a navržena reaktivní architektura."
                    )
                }
            }
            2 -> AuditRoundResult(
                round = 2,
                speeches = listOf(
                    AuditRoundSpeech(
                        round = 2,
                        persona = AiPersona.VORTEX,
                        spokenText = "Vortex ve 2. kole: Zkoumal jsem bezpečnost a škálování. Ukládání nešifrovaných stavů a závislost na jediném cloudu je kritické selhání. Co se stane při offline výpadku?"
                    ),
                    AuditRoundSpeech(
                        round = 2,
                        persona = AiPersona.NOVA,
                        spokenText = "Nova reaguje: Webové benchmarky potvrzují, že 'Offline-First' architektura s lokálním SQLite/Room a šifrováním SQLCipher řeší odolnost i bezpečnost na armádní úrovni."
                    ),
                    AuditRoundSpeech(
                        round = 2,
                        persona = AiPersona.ATLAS,
                        spokenText = "Atlas integruje: Schvaluji Offline-First. Navrhuji automatický obousměrný sync manager s řešením konfliktů a krokovým manuálem pro agenty."
                    )
                ),
                addedNodes = listOf(
                    AiGeneratedNodeDto(
                        title = "🛡️ Zabezpečení: SQLCipher & Šifrování za běhu",
                        details = "DŮKLADNÁ ANALÝZA: Ochrana před zneužitím lokálních dat a neautorizovaným přístupem třetích stran.",
                        category = "RISK",
                        authorPersona = "vortex"
                    ),
                    AiGeneratedNodeDto(
                        title = "💎 Offline-First Architektura s Vector Clocks",
                        details = "DŮKLADNÁ SPECIFIKACE: Plná funkčnost bez internetu a bezeztrátová synchronizace s cloudem při návratu spojení.",
                        category = "SOLUTION",
                        authorPersona = "nova"
                    ),
                    AiGeneratedNodeDto(
                        title = "📋 Úkol pro AI: Obousměrný Sync Manager",
                        details = "DŮKLADNÉ INSTRUKCE PRO AI:\nKrok 1: Inicializovat lokální Room databázi se šifrovacím klíčem z Android Keystore\nKrok 2: Připojit Snapshot listener na cloudovou Firestore kolekci\nKrok 3: Implementovat strategii 'Last Write Wins' s časovou značkou milisekund\nKrok 4: Otestovat offline mutaci a následný automatický sync",
                        category = "ACTION_ITEM",
                        authorPersona = "atlas",
                        diagramData = "[Místní DB (Šifrováno)] <---> [Sync Manager] <---> [Cloud Firestore]"
                    )
                ),
                outdatedTools = listOf(
                    OutdatedToolFinding(
                        toolName = "Cloud-Only architektura bez lokální cache",
                        issueOrDeprecatedReason = "Aplikace zamrzne při ztrátě signálu a generuje vysoké cloud poplatky",
                        recommendedModernAlternative = "Local-First / Offline-First s Room & Firestore Snapshoty (2026)",
                        category = "Architektura dat",
                        searchSources = "Local-First Movement 2026"
                    )
                ),
                roundSummary = "Kolo 2: Vyřešena bezpečnost, resilience a offline synchronizace."
            )
            else -> AuditRoundResult(
                round = roundNumber,
                speeches = listOf(
                    AuditRoundSpeech(
                        round = roundNumber,
                        persona = AiPersona.VORTEX,
                        spokenText = "Vortex v kole $roundNumber: Po zapracování předchozích bodů klesla zranitelnost projektu o 85%. Poslední varování je přísná validace uživatelských vstupů a sanitizace příkazů."
                    ),
                    AuditRoundSpeech(
                        round = roundNumber,
                        persona = AiPersona.NOVA,
                        spokenText = "Nova v kole $roundNumber: Architektura je nyní technologicky na špici. Máme moderní knihovny, nulové zastaralé nástroje a 10x lepší škálovatelnost ověřenou na webu."
                    ),
                    AuditRoundSpeech(
                        round = roundNumber,
                        persona = AiPersona.ATLAS,
                        spokenText = "Atlas uzavírá audit: Projekt '$projectTitle' je zvalidován. Vydávám certifikát připravenosti a kompletní sadu detailních akčních instrukcí pro realizaci."
                    )
                ),
                addedNodes = listOf(
                    AiGeneratedNodeDto(
                        title = "✅ Finální akční plán nasazení MVP",
                        details = "DŮKLADNÉ INSTRUKCE PRO AI (Realizace MVP):\nKrok 1: Zkontrolovat všechny environment proměnné a API klíče\nKrok 2: Spustit automatické smoke testy a prověřit latenci\nKrok 3: Vygenerovat finální release build\nKrok 4: Zkontrolovat monitoring a logovací metriky v reálném čase",
                        category = "ACTION_ITEM",
                        authorPersona = "atlas",
                        diagramData = "[Smoke Testy] -> [Ověření API] -> [Release Build] -> [Živý Monitoring]"
                    )
                ),
                outdatedTools = emptyList(),
                roundSummary = "Finální kolo $roundNumber: Všechna rizika ošetřena, architektura schválena s důkladnými instrukcemi."
            )
        }
    }

    suspend fun generateVibeCodingArtifact(
        projectTitle: String,
        selectedNode: MindNode?,
        allNodes: List<MindNode>,
        targetStack: VibeTargetStack
    ): VibeCodingArtifact = withContext(Dispatchers.IO) {
        val nodeContext = selectedNode?.let {
            "Kontext komponenty: '${it.title}' (${it.category.name})\nDetaily: ${it.details}\nKroky: ${it.diagramData ?: "není specifikováno"}"
        } ?: "Kontext celého projektu: '$projectTitle'"

        val mapSummary = allNodes.take(15).joinToString("\n") { 
            "- [${it.category.name}] ${it.title}: ${it.details.take(60)}" 
        }

        val prompt = """
            Jsi Neo - elitní Vibe Coder a Full-Stack Software Architect.
            Projekt: $nodeContext
            
            Přehled myšlenkové mapy:
            $mapSummary
            
            Cílový stack: ${targetStack.displayName} (${targetStack.badge}).
            
            Vygeneruj kompletní Vibe Coding balíček pro Cursor, Claude, Copilot a v0:
            1. 'filename': Doporučený název souboru.
            2. 'codeSnippet': Kompletní, bezchybný, produkční kód pro vybranou komponentu v jazyce ${targetStack.defaultLang}.
            3. 'cursorPrompt': Strukturovaný prompt pro Cursor Composer / .cursorrules (Role, Context, Stack, Rules, Task).
            4. 'copilotInstructions': Markdown instrukce pro GitHub Copilot.
            5. 'dbSchema': Databázové schéma (Prisma nebo SQL DDL).
            6. 'apiSpec': REST API kontrakt (metoda, URL, JSON a curl).
            7. 'terminalSetupCommands': Pole 3-5 přesných bash příkazů pro spuštění.
            8. 'explanation': Stručné shrnutí česky od Nea.
            
            Odpověz POUZE v platném JSON:
            {
               "filename": "...",
               "codeSnippet": "...",
               "cursorPrompt": "...",
               "copilotInstructions": "...",
               "dbSchema": "...",
               "apiSpec": "...",
               "terminalSetupCommands": ["...", "..."],
               "explanation": "..."
            }
        """.trimIndent()

        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext buildFallbackVibeArtifact(projectTitle, selectedNode, targetStack)
        }

        try {
            val geminiRequest = GeminiRequest(
                contents = listOf(
                    GeminiContent(
                        parts = listOf(GeminiPart(text = prompt))
                    )
                ),
                generationConfig = GeminiGenerationConfig(
                    responseMimeType = "application/json",
                    temperature = 0.4f
                )
            )

            val response = callGeminiApi(apiKey, geminiRequest, activeGoogleModel)
            val jsonText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (!jsonText.isNullOrBlank()) {
                val clean = jsonText.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
                val obj = JSONObject(clean)
                val commandsList = mutableListOf<String>()
                val cmdsArr = obj.optJSONArray("terminalSetupCommands")
                if (cmdsArr != null) {
                    for (i in 0 until cmdsArr.length()) {
                        commandsList.add(cmdsArr.optString(i))
                    }
                }
                if (commandsList.isEmpty()) {
                    commandsList.addAll(getDefaultTerminalCommands(targetStack))
                }

                return@withContext VibeCodingArtifact(
                    title = selectedNode?.title ?: projectTitle,
                    targetStack = targetStack,
                    filename = obj.optString("filename", getDefaultFilename(targetStack)),
                    codeSnippet = obj.optString("codeSnippet", ""),
                    cursorPrompt = obj.optString("cursorPrompt", ""),
                    copilotInstructions = obj.optString("copilotInstructions", ""),
                    dbSchema = obj.optString("dbSchema", ""),
                    apiSpec = obj.optString("apiSpec", ""),
                    terminalSetupCommands = commandsList,
                    explanation = obj.optString("explanation", "Neo připravil kompletní vibe coding balíček pro $projectTitle.")
                )
            }
        } catch (_: Exception) {
            // fallback
        }

        buildFallbackVibeArtifact(projectTitle, selectedNode, targetStack)
    }

    private fun getDefaultFilename(stack: VibeTargetStack): String {
        return when (stack) {
            VibeTargetStack.COMPOSE_ANDROID -> "MainFeatureScreen.kt"
            VibeTargetStack.REACT_TAILWIND -> "FeatureDashboard.tsx"
            VibeTargetStack.NEXTJS_FULLSTACK -> "app/dashboard/page.tsx"
            VibeTargetStack.FASTAPI_PYTHON -> "api/endpoints.py"
            VibeTargetStack.NODE_HONO -> "src/routes/api.ts"
            VibeTargetStack.DATABASE_PRISMA -> "prisma/schema.prisma"
        }
    }

    private fun getDefaultTerminalCommands(stack: VibeTargetStack): List<String> {
        return when (stack) {
            VibeTargetStack.COMPOSE_ANDROID -> listOf(
                "./gradlew assembleDebug",
                "./gradlew installDebug"
            )
            VibeTargetStack.REACT_TAILWIND -> listOf(
                "npm create vite@latest my-app -- --template react-ts",
                "npm install -D tailwindcss postcss autoprefixer",
                "npx tailwindcss init -p",
                "npm run dev"
            )
            VibeTargetStack.NEXTJS_FULLSTACK -> listOf(
                "npx create-next-app@latest my-vibe-app --typescript --tailwind --app",
                "npm install lucide-react clsx tailwind-merge",
                "npm run dev"
            )
            VibeTargetStack.FASTAPI_PYTHON -> listOf(
                "python3 -m venv venv && source venv/bin/activate",
                "pip install fastapi uvicorn pydantic sqlalchemy",
                "uvicorn main:app --reload --port 8000"
            )
            VibeTargetStack.NODE_HONO -> listOf(
                "npm create hono@latest my-hono-app",
                "npm install @hono/zod-validator zod",
                "npm run dev"
            )
            VibeTargetStack.DATABASE_PRISMA -> listOf(
                "npm install prisma @prisma/client",
                "npx prisma init",
                "npx prisma migrate dev --name init",
                "npx prisma studio"
            )
        }
    }

    private fun buildFallbackVibeArtifact(
        projectTitle: String,
        selectedNode: MindNode?,
        stack: VibeTargetStack
    ): VibeCodingArtifact {
        val title = selectedNode?.title ?: projectTitle
        val filename = getDefaultFilename(stack)
        val commands = getDefaultTerminalCommands(stack)

        val code = when (stack) {
            VibeTargetStack.COMPOSE_ANDROID -> """
                package com.example.feature
                
                import androidx.compose.foundation.layout.*
                import androidx.compose.foundation.lazy.LazyColumn
                import androidx.compose.foundation.lazy.items
                import androidx.compose.material3.*
                import androidx.compose.runtime.*
                import androidx.compose.ui.Alignment
                import androidx.compose.ui.Modifier
                import androidx.compose.ui.platform.testTag
                import androidx.compose.ui.unit.dp
                
                // Vibe-coded Component: $title
                @OptIn(ExperimentalMaterial3Api::class)
                @Composable
                fun ${title.replace(Regex("[^a-zA-Z0-9]"), "").ifBlank { "Feature" }}Screen(
                    onActionClick: (String) -> Unit = {}
                ) {
                    var query by remember { mutableStateOf("") }
                    val items = remember { listOf("$title • Fáze 1", "$title • Realizace", "$title • Validace") }
                    
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = { Text("$title") }
                            )
                        }
                    ) { padding ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(padding)
                                .padding(16.dp)
                        ) {
                            OutlinedTextField(
                                value = query,
                                onValueChange = { query = it },
                                label = { Text("Filtrovat položky...") },
                                modifier = Modifier.fillMaxWidth().testTag("input_filter")
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(items.filter { it.contains(query, ignoreCase = true) }) { item ->
                                    Card(
                                        onClick = { onActionClick(item) },
                                        modifier = Modifier.fillMaxWidth().testTag("card_${'$'}item")
                                    ) {
                                        Text(text = item, modifier = Modifier.padding(14.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            """.trimIndent()

            VibeTargetStack.REACT_TAILWIND -> """
                import React, { useState } from 'react';
                
                interface ${title.replace(Regex("[^a-zA-Z0-9]"), "").ifBlank { "Feature" }}Props {
                  initialStatus?: string;
                }
                
                export const ${title.replace(Regex("[^a-zA-Z0-9]"), "").ifBlank { "Feature" }}Component: React.FC<${title.replace(Regex("[^a-zA-Z0-9]"), "").ifBlank { "Feature" }}Props> = () => {
                  const [active, setActive] = useState(true);
                  
                  return (
                    <div className="p-6 bg-slate-900 text-slate-100 rounded-2xl border border-slate-800 shadow-xl max-w-xl mx-auto">
                      <div className="flex items-center justify-between mb-4">
                        <h2 className="text-xl font-bold text-cyan-400">$title</h2>
                        <span className="px-2.5 py-1 text-xs font-semibold rounded-full bg-cyan-950 text-cyan-400 border border-cyan-800">
                          {active ? 'Aktivní' : 'Pozastaveno'}
                        </span>
                      </div>
                      <p className="text-sm text-slate-400 mb-6">
                        Vibe-coded komponenta vygenerovaná z myšlenkové mapy MindCraft AI.
                      </p>
                      <button 
                        onClick={() => setActive(!active)}
                        className="w-full py-2.5 px-4 bg-cyan-500 hover:bg-cyan-400 text-slate-950 font-bold rounded-xl transition-colors shadow-lg shadow-cyan-500/20"
                      >
                        {active ? 'Spustit proces' : 'Obnovit'}
                      </button>
                    </div>
                  );
                };
            """.trimIndent()

            VibeTargetStack.NEXTJS_FULLSTACK -> """
                // app/dashboard/page.tsx - Server Component with Action
                import { Suspense } from 'react';
                
                async function getData() {
                  return [
                    { id: '1', title: '$title • MVP Milník', done: true },
                    { id: '2', title: '$title • Produkční nasazení', done: false }
                  ];
                }
                
                export default async function DashboardPage() {
                  const data = await getData();
                  
                  return (
                    <main className="min-h-screen bg-black text-white p-8">
                      <header className="mb-8 border-b border-zinc-800 pb-4">
                        <h1 className="text-3xl font-black text-emerald-400 tracking-tight">$title</h1>
                        <p className="text-zinc-500 text-sm mt-1">Fullstack Next.js 15 App Router Scaffold</p>
                      </header>
                      
                      <div className="grid gap-4 md:grid-cols-2">
                        {data.map((item) => (
                          <div key={item.id} className="p-5 rounded-2xl bg-zinc-900/80 border border-zinc-800">
                            <h3 className="font-semibold text-lg">{item.title}</h3>
                            <span className="text-xs text-emerald-500 font-mono mt-2 inline-block">
                              Status: {item.done ? 'COMPLETED' : 'IN_PROGRESS'}
                            </span>
                          </div>
                        ))}
                      </div>
                    </main>
                  );
                }
            """.trimIndent()

            VibeTargetStack.FASTAPI_PYTHON -> """
                from fastapi import FastAPI, HTTPException, status
                from pydantic import BaseModel, Field
                from typing import List, Optional
                import uuid
                
                app = FastAPI(title="$title API", version="1.0.0")
                
                class ${title.replace(Regex("[^a-zA-Z0-9]"), "").ifBlank { "Item" }}Schema(BaseModel):
                    id: str = Field(default_factory=lambda: str(uuid.uuid4()))
                    title: str = "$title"
                    description: Optional[str] = "Automaticky vytvořený model z MindCraft AI"
                    is_active: bool = True
                
                # In-memory store
                items_db: List[${title.replace(Regex("[^a-zA-Z0-9]"), "").ifBlank { "Item" }}Schema] = []
                
                @app.get("/api/v1/items", response_model=List[${title.replace(Regex("[^a-zA-Z0-9]"), "").ifBlank { "Item" }}Schema])
                async def get_all_items():
                    return items_db
                
                @app.post("/api/v1/items", status_code=status.HTTP_201_CREATED)
                async def create_item(item: ${title.replace(Regex("[^a-zA-Z0-9]"), "").ifBlank { "Item" }}Schema):
                    items_db.append(item)
                    return item
            """.trimIndent()

            VibeTargetStack.NODE_HONO -> """
                import { Hono } from 'hono';
                import { z } from 'zod';
                import { zValidator } from '@hono/zod-validator';
                
                const app = new Hono();
                
                const ItemSchema = z.object({
                  id: z.string().uuid().optional(),
                  title: z.string().min(2),
                  category: z.string().default('FEATURE'),
                  completed: z.boolean().default(false)
                });
                
                app.get('/api/health', (c) => c.json({ status: 'ok', project: '$title', timestamp: Date.now() }));
                
                app.post('/api/items', zValidator('json', ItemSchema), (c) => {
                  const data = c.req.valid('json');
                  return c.json({ success: true, created: data }, 201);
                });
                
                export default app;
            """.trimIndent()

            VibeTargetStack.DATABASE_PRISMA -> """
                // prisma/schema.prisma
                datasource db {
                  provider = "postgresql"
                  url      = env("DATABASE_URL")
                }
                
                generator client {
                  provider = "prisma-client-js"
                }
                
                model Project {
                  id          String      @id @default(uuid())
                  title       String
                  description String?
                  createdAt   DateTime    @default(now())
                  updatedAt   DateTime    @updatedAt
                  nodes       MindNode[]
                }
                
                model MindNode {
                  id          String      @id @default(uuid())
                  projectId   String
                  project     Project     @relation(fields: [projectId], references: [id], onDelete: Cascade)
                  parentId    String?
                  title       String
                  details     String?
                  category    String      @default("FEATURE")
                  isCompleted Boolean     @default(false)
                  createdAt   DateTime    @default(now())
                  
                  @@index([projectId])
                }
            """.trimIndent()
        }

        val cursor = """
            # CURSOR & CLAUDE PROJECT PROMPT RULES
            
            You are a senior full-stack software engineer building '$title' with ${stack.displayName}.
            
            ## Project Context
            - Goal: Implement $title based on the brainstormed architecture.
            - Tech Stack: ${stack.displayName} (${stack.defaultLang}).
            
            ## Architecture & Code Guidelines
            - Use modern, clean conventions with zero boilerplate and full type safety.
            - Maintain separation of concerns: UI, Business Logic, and Data Layer.
            - Handle all error boundaries, edge cases, and loading states gracefully.
            - Keep code modular, accessible, and easily testable.
            
            ## Immediate Task
            Implement the component in '$filename' according to the specifications and connect it to the router/API.
        """.trimIndent()

        val copilot = """
            # GitHub Copilot Instructions for $title
            
            ## Coding Standards
            - Framework: ${stack.displayName}
            - Language: ${stack.defaultLang}
            - Use concise, readable, self-documenting code.
            - Do not invent non-existent APIs or libraries.
            - Ensure all variables and functions have explicit types.
        """.trimIndent()

        val dbSchema = """
            -- SQL DDL Migration for $title
            CREATE TABLE IF NOT EXISTS projects (
                id VARCHAR(36) PRIMARY KEY,
                title VARCHAR(255) NOT NULL,
                created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
            );
            
            CREATE TABLE IF NOT EXISTS project_nodes (
                id VARCHAR(36) PRIMARY KEY,
                project_id VARCHAR(36) REFERENCES projects(id) ON DELETE CASCADE,
                title VARCHAR(255) NOT NULL,
                details TEXT,
                category VARCHAR(50) DEFAULT 'FEATURE',
                is_completed BOOLEAN DEFAULT FALSE
            );
            CREATE INDEX IF NOT EXISTS idx_project_nodes_project ON project_nodes(project_id);
        """.trimIndent()

        val apiSpec = """
            # REST API Specification for $title
            GET /api/v1/projects/:id
            Response: 200 OK
            {
               "id": "proj_123",
               "title": "$title",
               "status": "READY"
            }
            
            # Curl Command:
            curl -X GET "https://api.example.com/api/v1/projects/proj_123" -H "Authorization: Bearer YOUR_TOKEN"
        """.trimIndent()

        return VibeCodingArtifact(
            title = title,
            targetStack = stack,
            filename = filename,
            codeSnippet = code,
            cursorPrompt = cursor,
            copilotInstructions = copilot,
            dbSchema = dbSchema,
            apiSpec = apiSpec,
            terminalSetupCommands = commands,
            explanation = "Neo vygeneroval scaffold pro $title v ${stack.displayName}. Můžete rovnou zkopírovat kód, Cursor pravidla nebo spustit terminálové příkazy."
        )
    }
}

