package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCoral
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple

enum class AiPersona(
    val id: String,
    val displayName: String,
    val title: String,
    val roleDescription: String,
    val colorHex: String,
    val color: Color,
    val emoji: String,
    val pitch: Float,
    val speechRate: Float
) {
    NOVA(
        id = "nova",
        displayName = "Nova",
        title = "Inovativní Génius & Vizionář",
        roleDescription = "Přináší odvážné nápady, neotřelé funkce, 10x myšlení a inovace, které boří zavedené šablony.",
        colorHex = "#D946EF",
        color = Color(0xFFD946EF),
        emoji = "✨",
        pitch = 1.25f,
        speechRate = 1.08f
    ),
    VORTEX(
        id = "vortex",
        displayName = "Vortex",
        title = "Nemilosrdný Kritik & Risk Hunter",
        roleDescription = "Hledá slabiny, skrytá rizika, bezpečnostní mezery, náklady, uživatelskou averzi a slepé uličky.",
        colorHex = "#FF4565",
        color = NeonCoral,
        emoji = "🛡️",
        pitch = 0.85f,
        speechRate = 0.95f
    ),
    ATLAS(
        id = "atlas",
        displayName = "Atlas",
        title = "Systémový Architekt & Realizátor",
        roleDescription = "Syntetizuje vize Inovátora a výtky Kritika do funkční, proveditelné architektury a akčních kroků.",
        colorHex = "#00F2FE",
        color = NeonCyan,
        emoji = "🏛️",
        pitch = 1.02f,
        speechRate = 1.02f
    ),
    NEO(
        id = "neo",
        displayName = "Neo",
        title = "Vibe Coder & Full-Stack Hacker",
        roleDescription = "Přetváří myšlenky přímo v čistý kód, prompt balíčky pro Cursor/Claude, API kontrakty a okamžitě spustitelný software.",
        colorHex = "#10B981",
        color = NeonEmerald,
        emoji = "💻",
        pitch = 1.0f,
        speechRate = 1.05f
    );

    companion object {
        fun fromId(id: String?): AiPersona? {
            if (id == null) return null
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) || it.displayName.equals(id, ignoreCase = true) }
        }
    }
}
