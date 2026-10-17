package com.example.data.ai

enum class GoogleAiModel(
    val modelId: String,
    val displayName: String,
    val description: String,
    val badge: String,
    val isFreeTier: Boolean = true
) {
    GEMINI_FLASH_LATEST(
        modelId = "gemini-flash-latest",
        displayName = "Gemini Flash (Doporučeno)",
        description = "Bleskový a stabilní bezplatný model od Google pro brainstorming a uzly.",
        badge = "FREE / FAST",
        isFreeTier = true
    ),
    GEMINI_FLASH_LITE(
        modelId = "gemini-3.1-flash-lite-preview",
        displayName = "Gemini Flash Lite",
        description = "Ultra lehký bezplatný model s minimální latencí pro okamžité reakce.",
        badge = "FREE / LITE",
        isFreeTier = true
    ),
    GEMINI_35_FLASH(
        modelId = "gemini-3.5-flash",
        displayName = "Gemini 3.5 Flash",
        description = "Nejnovější inteligentní multimodální model pro pokročilou syntézu a architekturu.",
        badge = "FREE TIER",
        isFreeTier = true
    ),
    GEMINI_PRO(
        modelId = "gemini-3.1-pro-preview",
        displayName = "Gemini Pro Reasoning",
        description = "Hloubkové uvažování pro komplexní technické audity a vícekrokové debaty.",
        badge = "REASONING",
        isFreeTier = true
    );

    companion object {
        val DEFAULT = GEMINI_FLASH_LATEST

        fun fromModelId(id: String?): GoogleAiModel {
            return entries.firstOrNull { it.modelId == id } ?: DEFAULT
        }
    }
}
