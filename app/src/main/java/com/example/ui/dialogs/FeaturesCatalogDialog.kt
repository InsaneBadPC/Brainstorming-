package com.example.ui.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Slideshow
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.ViewTimeline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCoral
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardElevated
import com.example.ui.theme.TechBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

enum class FeatureCategory(val label: String, val color: Color) {
    ALL("Všechny funkce", NeonCyan),
    CODING("💻 Vibe Coding & Dev", NeonEmerald),
    CANVAS("🗺️ Mapa & Plátno", NeonCyan),
    AI_STRATEGY("🧠 AI & Strategie", NeonPurple),
    VIEWS("📊 Pohledy & Analýzy", NeonAmber),
    CLOUD_EXPORT("💾 Cloud & Export", Color(0xFF38BDF8))
}

data class AppFeature(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val accentColor: Color,
    val category: FeatureCategory,
    val badge: String,
    val shortSummary: String,
    val detailedDescription: String,
    val howToUseSteps: List<String>,
    val proTip: String,
    val actionButtonText: String,
    val onExecute: () -> Unit
)

@Composable
fun FeaturesCatalogDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onOpenNewMap: () -> Unit,
    onAddManualNode: () -> Unit,
    onResetZoom: () -> Unit,
    onAutoLayout: () -> Unit,
    onOpenAiChat: () -> Unit,
    onStartVoice: () -> Unit,
    onOpenAuditDebate: () -> Unit,
    onOpenSwot: () -> Unit,
    onRunQuestionStorming: () -> Unit,
    onRunMarketBenchmark: () -> Unit,
    onOpenBlueprint: () -> Unit,
    onSwitchToRoadmap: () -> Unit,
    onSwitchToMatrix: () -> Unit,
    onSwitchToCanvas: () -> Unit,
    onOpenPitchPresentation: () -> Unit,
    onOpenCloudSync: () -> Unit,
    onOpenVersionHistory: () -> Unit,
    onOpenExport: () -> Unit,
    onOpenImport: () -> Unit,
    onOpenCreatePersona: () -> Unit,
    onOpenModelSelector: () -> Unit,
    onCheckUpdate: () -> Unit,
    onOpenVibeCoding: () -> Unit = {},
    hasActiveMap: Boolean
) {
    if (!isOpen) return

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(FeatureCategory.ALL) }
    var expandedFeatureId by remember { mutableStateOf<String?>("vibe_coding") }

    val features = remember(hasActiveMap) {
        listOf(
            AppFeature(
                id = "vibe_coding",
                name = "Vibe Coding & Scaffolder (Neo AI)",
                icon = Icons.Default.Code,
                accentColor = NeonEmerald,
                category = FeatureCategory.CODING,
                badge = "Vibe Coding",
                shortSummary = "Generuje funkční kód v Compose, Reactu, Next.js 15, FastAPI či Hono.",
                detailedDescription = "Přetvořte libovolný uzel nebo celou myšlenkovou mapu do produkčního kódu! Neo AI připraví plnohodnotný frontend (Compose/React), backend (FastAPI/Hono), databázové schéma i terminálové příkazy pro okamžité spuštění.",
                howToUseSteps = listOf(
                    "Otevřete Vibe Coding v horní liště nebo klepněte na '💻 Kód' v liště vybraného uzlu.",
                    "Vyberte technologický stack (Android Compose, React Tailwind, Next.js 15, FastAPI, Hono, Prisma).",
                    "Zkopírujte kód nebo terminálové příkazy a rovnou spusťte projekt."
                ),
                proTip = "V záložce 'Cursor & Prompty' najdete hotový prompt balíček pro Cursor Composer a Claude Projects.",
                actionButtonText = "Otevřít Vibe Coding Centrum",
                onExecute = {
                    onDismiss()
                    onOpenVibeCoding()
                }
            ),
            AppFeature(
                id = "cursor_rules",
                name = "Prompt Pack pro Cursor & Claude (.cursorrules)",
                icon = Icons.Default.Terminal,
                accentColor = NeonCyan,
                category = FeatureCategory.CODING,
                badge = ".cursorrules",
                shortSummary = "Systémová pravidla a prompt balíčky pro AI editory bez halucinací.",
                detailedDescription = "Získejte přesný architektonický kontext a pravidla pro Cursor, Claude, Windsurf a GitHub Copilot. Definuje rozhraní, datové modely a technologická omezení, aby AI kódovala přesně podle návrhu.",
                howToUseSteps = listOf(
                    "Ve Vibe Coding dialogu přepněte na záložku '⚡ Cursor & Prompty'.",
                    "Klepněte na 'Kopírovat pro Cursor' nebo 'Copilot'.",
                    "Vložte do souboru .cursorrules nebo do okna chatu AI editoru."
                ),
                proTip = "Ušetří až 80 % času stráveného laděním a vysvětlováním kontextu projektu.",
                actionButtonText = "Generovat pravidla pro Cursor",
                onExecute = {
                    onDismiss()
                    onOpenVibeCoding()
                }
            ),
            AppFeature(
                id = "ai_map",
                name = "Generování myšlenkové mapy (AI Mind Map)",
                icon = Icons.Default.AutoAwesome,
                accentColor = NeonCyan,
                category = FeatureCategory.AI_STRATEGY,
                badge = "Gemini AI",
                shortSummary = "Vytvoří kompletní hierarchickou mapu ze zadání s vyváženým stromem.",
                detailedDescription = "Zadejte jakýkoliv nápad, podnikatelský záměr, cíl nebo technologický koncept. Google Gemini AI automaticky navrhne kořenový cíl a logické podvětve (cíle, úkoly, rizika, nápady) s čistým hierarchickým rozložením bez překrývání.",
                howToUseSteps = listOf(
                    "Klepněte na tlačítko '+ Nová mapa' nebo 'Vytvořit s AI'.",
                    "Zadejte název projektu a podrobnější zadání (nepovinné).",
                    "Klepněte na 'Vygenerovat AI mapu' a sledujte okamžitou tvorbu stromu."
                ),
                proTip = "Můžete použít i bleskový výběr z rychlých šablon (Mobilní aplikace, SaaS Startup, E-shop expanze).",
                actionButtonText = "Vytvořit novou AI mapu",
                onExecute = {
                    onDismiss()
                    onOpenNewMap()
                }
            ),
            AppFeature(
                id = "pinch_zoom",
                name = "Zoom dvěma prsty & Plynulé plátno",
                icon = Icons.Default.CenterFocusStrong,
                accentColor = NeonCyan,
                category = FeatureCategory.CANVAS,
                badge = "Gesto 2 prsty",
                shortSummary = "Intuitivní přibližování dvěma prsty s ohniskem a posun po plátně.",
                detailedDescription = "Pracovní plátno podporuje přirozená gesta: sevřením nebo roztažením dvou prstů (pinch-to-zoom) plynule měníte měřítko od 25% do 300%. Zoomování respektuje střed mezi prsty, takže se přiblížíte přesně k místu, na které ukazujete.",
                howToUseSteps = listOf(
                    "Položte dva prsty na plátno a roztáhněte je pro přiblížení, nebo sevřete pro oddálení.",
                    "Pro posun táhněte jedním prstem po volném prostoru plátna.",
                    "Dvojitým poklepáním na prázdné plátno nebo na tlačítko '%' okamžitě obnovíte 100% zobrazení."
                ),
                proTip = "V pravém dolním rohu jsou k dispozici také tlačítka (+) a (-) pro rychlé krokové přiblížení.",
                actionButtonText = "Vycentrovat plátno",
                onExecute = {
                    onDismiss()
                    onResetZoom()
                }
            ),
            AppFeature(
                id = "auto_layout",
                name = "Automatické uspořádání (Auto-Layout)",
                icon = Icons.Default.AccountTree,
                accentColor = NeonCyan,
                category = FeatureCategory.CANVAS,
                badge = "Bez kolizí",
                shortSummary = "Rozmístí všechny uzly do přehledného a vyváženého stromu bez překrývání.",
                detailedDescription = "Pokud jste uzly ručně posouvali nebo přidali mnoho nových větví, algoritmus hierarchického uspořádání (Walker tree algorithm) přepočítá pozice celého stromu. Kořen umístí do středu a větve rovnoměrně rozprostře do stran.",
                howToUseSteps = listOf(
                    "Klepněte na plovoucí ikonu uspořádání (dvě šipky / uzel) v pravém dolním rohu plátna.",
                    "Všechny uzly se okamžitě animovaně uspořádají do symetrického stromu."
                ),
                proTip = "Algoritmus automaticky připojí i sirotčí uzly k hlavnímu kořeni projektu.",
                actionButtonText = "Spustit Auto-Layout",
                onExecute = {
                    onDismiss()
                    onAutoLayout()
                }
            ),
            AppFeature(
                id = "voice_assistant",
                name = "Hlasový asistent & Diktování",
                icon = Icons.Default.Mic,
                accentColor = NeonCoral,
                category = FeatureCategory.CANVAS,
                badge = "Hlas & Zvuk",
                shortSummary = "Diktujte myšlenky hlasem a poslouchejte mluvené odpovědi AI.",
                detailedDescription = "Umožňuje hands-free ovládání a brainstorming. Stiskněte mikrofon v dolní liště a mluvte přirozeným jazykem (např. 'Přidej riziko výpadku platební brány'). AI řeč rozpozná, provede akci v mapě a může vám odpovědět syntézou hlasu.",
                howToUseSteps = listOf(
                    "Klepněte na ikonu mikrofonu ve spodním panelu (udělte oprávnění, pokud je vyžadováno).",
                    "Řekněte svůj nápad, myšlenku nebo instrukci pro AI.",
                    "Klepnutím na mikrofon nahrávání ukončíte – AI okamžitě vytvoří uzly nebo odpoví."
                ),
                proTip = "Vizuální zvuková vlna (waveform) v dolní liště ukazuje intenzitu vašeho hlasu v reálném čase.",
                actionButtonText = "Aktivovat hlasového asistenta",
                onExecute = {
                    onDismiss()
                    onStartVoice()
                }
            ),
            AppFeature(
                id = "copilot_3ai",
                name = "Kulatý stůl 3 AI Person (CoPilot Chat)",
                icon = Icons.AutoMirrored.Filled.Chat,
                accentColor = NeonPurple,
                category = FeatureCategory.AI_STRATEGY,
                badge = "3 Experti",
                shortSummary = "Diskutujte s Kreativcem, Pragmatikem a Růstovým stratégem.",
                detailedDescription = "Kulatý stůl sdružuje 3 specializované AI rádce: 💡 Nova (Kreativec) hledá netradiční inovace, ⚖️ Vortex (Pragmatik) hledá skrytá rizika a slabiny, 📈 Atlas (Růstový stratég) plánuje škálování a monetizaci. Získáváte vyvážený pohled z více úhlů.",
                howToUseSteps = listOf(
                    "Klepněte na fialové plovoucí tlačítko chatu vpravo dole, nebo vyberte personu v horní liště.",
                    "Zadejte otázku nebo vyberte rychlou akci (např. 'Najít slabiny', 'Doporučit tech stack').",
                    "Přepínejte mezi jednotlivými experty nebo nechte odpovědět celý Kulatý stůl."
                ),
                proTip = "Každá zpráva má tlačítko reproduktoru pro předčtení hlasem s unikátním zabarvením hlasu každé persony.",
                actionButtonText = "Otevřít 3 AI CoPilot Chat",
                onExecute = {
                    onDismiss()
                    onOpenAiChat()
                }
            ),
            AppFeature(
                id = "audit_debate",
                name = "Hloubkový Audit & Debata oponentů",
                icon = Icons.Default.Security,
                accentColor = NeonCoral,
                category = FeatureCategory.AI_STRATEGY,
                badge = "Nemilosrdná prověrka",
                shortSummary = "Vícekolová debata AI expertů odhalí rizika a zastaralé technologie.",
                detailedDescription = "Simulace ostré oponentní rady. AI agenti si předávají slovo v několika kolech, prověřují proveditelnost projektu, identifikují zastaralé knihovny a postupy a sestaví závěrečný akční report s doporučeními.",
                howToUseSteps = listOf(
                    "V menu 'Více možností' (tři tečky nahoře) zvolte '🛡️ Hloubkový Audit & Debata'.",
                    "Zvolte počet kol (1 až 5) a případně zapněte hlasový přednes.",
                    "Sledujte živou argumentaci a prostudujte vygenerovaný finální auditní report."
                ),
                proTip = "Audit můžete nechat běžet na pozadí a dále pracovat s mapou – stav uvidíte v plovoucím proužku.",
                actionButtonText = "Spustit Audit projektu",
                onExecute = {
                    onDismiss()
                    onOpenAuditDebate()
                }
            ),
            AppFeature(
                id = "swot_analysis",
                name = "SWOT Strategická analýza",
                icon = Icons.Default.Shield,
                accentColor = NeonEmerald,
                category = FeatureCategory.VIEWS,
                badge = "4 Kvadranty",
                shortSummary = "Silné stránky, Slabiny, Příležitosti a Hrozby s AI generátorem.",
                detailedDescription = "Kompletní strategický rozbor projektu rozdělený do čtyř přehledných kvadrantů (Strengths, Weaknesses, Opportunities, Threats). Můžete položky zadávat ručně nebo nechat AI vygenerovat kompletní analýzu jedním kliknutím.",
                howToUseSteps = listOf(
                    "V horním menu zvolte 'SWOT Strategická analýza'.",
                    "Klepněte na 'Vygenerovat SWOT přes AI' pro automatickou analýzu.",
                    "Klepnutím na libovolný řádek můžete daný bod rovnou vložit jako uzel do myšlenkové mapy."
                ),
                proTip = "Ideální pro přípravu prezentace pro investory nebo vedení.",
                actionButtonText = "Otevřít SWOT analýzu",
                onExecute = {
                    onDismiss()
                    onOpenSwot()
                }
            ),
            AppFeature(
                id = "question_storming",
                name = "Otázkový útok (Question Storming)",
                icon = Icons.Default.HelpOutline,
                accentColor = NeonAmber,
                category = FeatureCategory.AI_STRATEGY,
                badge = "Inovační technika",
                shortSummary = "Generuje klíčové otázky a odhaluje slepá místa projektu.",
                detailedDescription = "Namísto předčasného hledání odpovědí generuje Question Storming nejprovokativnější a nejzásadnější otázky, na které musíte znát odpověď dříve, než investujete čas do realizace.",
                howToUseSteps = listOf(
                    "Otevřete horní menu nástrojů a klepněte na 'Otázkový útok (Question Storming)'.",
                    "AI vygeneruje sérii kritických otázek přímo do mapy jako nové zkoumací větve."
                ),
                proTip = "Použijte tuto funkci hned na začátku projektu, kdy je koncept ještě tvárný.",
                actionButtonText = "Spustit Question Storming",
                onExecute = {
                    onDismiss()
                    onRunQuestionStorming()
                }
            ),
            AppFeature(
                id = "market_benchmark",
                name = "Tržní benchmarky & Konkurence",
                icon = Icons.Default.Search,
                accentColor = Color(0xFF4285F4),
                category = FeatureCategory.AI_STRATEGY,
                badge = "Analýza trhu",
                shortSummary = "Prozkoumá konkurenční prostředí, tržní trendy a alternativy.",
                detailedDescription = "Vyhledá a shrne existující tržní lídry, jejich výhody a nevýhody a pomůže vám formulovat unikátní hodnotovou nabídku (USP) vašeho řešení.",
                howToUseSteps = listOf(
                    "V menu zvolte 'Tržní benchmarky & Konkurence'.",
                    "AI analyzuje obor projektu a přidá do mapy srovnání s trhem a příležitosti pro diferenciaci."
                ),
                proTip = "Vhodné kombinovat s Google Search přepínačem pro aktuální data.",
                actionButtonText = "Spustit analýzu trhu",
                onExecute = {
                    onDismiss()
                    onRunMarketBenchmark()
                }
            ),
            AppFeature(
                id = "blueprint",
                name = "Funkční Blueprint & Architektura",
                icon = Icons.Default.Description,
                accentColor = NeonAmber,
                category = FeatureCategory.AI_STRATEGY,
                badge = "Specifikace",
                shortSummary = "Kompletní technická specifikace, datový model a architektura.",
                detailedDescription = "Přetvoří myšlenkovou mapu do uceleného architektonického dokumentu: technologický stack, datové entity, bezpečnostní zásady, API rozhraní a milníky vývoje.",
                howToUseSteps = listOf(
                    "V menu zvolte 'Funkční Blueprint & Architektura'.",
                    "V otevřeném panelu CoPilotu můžete blueprint procházet a kopírovat do schránky."
                ),
                proTip = "Dokonalý podklad pro vývojový tým i pro zadání do Jira nebo GitHubu.",
                actionButtonText = "Zobrazit Blueprint",
                onExecute = {
                    onDismiss()
                    onOpenBlueprint()
                }
            ),
            AppFeature(
                id = "view_roadmap",
                name = "Pohled Fáze & Časová osa (Roadmap)",
                icon = Icons.Default.ViewTimeline,
                accentColor = NeonEmerald,
                category = FeatureCategory.VIEWS,
                badge = "Lineární pohled",
                shortSummary = "Přehled všech úkolů a milníků rozdělených do realizačních fází.",
                detailedDescription = "Přepne z nelineární myšlenkové mapy do strukturovaného přehledu fází (Příprava, Vývoj, Testování, Spuštění). Vidíte jasný časový postup a stav dokončení jednotlivých úkolů.",
                howToUseSteps = listOf(
                    "V horní liště přepněte přepínač na '📊 Fáze'.",
                    "Procházejte jednotlivé etapy projektu a odškrtávejte hotové položky."
                ),
                proTip = "Každý uzel s kategorií ÚKOL se automaticky zařadí do příslušné fáze.",
                actionButtonText = "Přepnout na Fáze (Roadmap)",
                onExecute = {
                    onDismiss()
                    onSwitchToRoadmap()
                }
            ),
            AppFeature(
                id = "view_matrix",
                name = "Pohled Matice priorit 2x2",
                icon = Icons.Default.GridOn,
                accentColor = NeonPurple,
                category = FeatureCategory.VIEWS,
                badge = "Prioritizace",
                shortSummary = "Rozdělení nápadů podle Dopadu (Impact) a Úsilí (Effort).",
                detailedDescription = "Klasická matice 2x2: Rychlé výhry (Vysoký dopad / Malé úsilí), Velké sázky (Vysoký dopad / Velké úsilí), Výplně (Nízký dopad / Malé úsilí) a Černé díry (Nízký dopad / Velké úsilí).",
                howToUseSteps = listOf(
                    "V horní liště přepněte na '🎯 Matice'.",
                    "Prohlédněte si, na které nápady se zaměřit nejdříve a které raději vynechat."
                ),
                proTip = "Pomáhá celému týmu shodnout se na prioritách pro první sprint.",
                actionButtonText = "Přepnout na Matici priorit",
                onExecute = {
                    onDismiss()
                    onSwitchToMatrix()
                }
            ),
            AppFeature(
                id = "pitch_mode",
                name = "Prezentační Pitch Režim",
                icon = Icons.Default.Slideshow,
                accentColor = NeonCyan,
                category = FeatureCategory.VIEWS,
                badge = "Slideshow",
                shortSummary = "Převede mapu do čisté celoobrazovkové prezentace slide po slidu.",
                detailedDescription = "Pro prezentaci projektu před klientem nebo týmem nemusíte tvořit PowerPoint. Režim Pitch provede publikum jednotlivými větvemi mapy jako čistými snímky s možností mluveného doprovodu.",
                howToUseSteps = listOf(
                    "V menu nástrojů zvolte 'Pitch Prezentační Režim'.",
                    "Šipkami procházejte snímky nebo klepněte na tlačítko reproduktoru pro předčtení."
                ),
                proTip = "Obsahuje indikátor postupu a možnost okamžitého skoku na libovolnou část.",
                actionButtonText = "Spustit Pitch prezentaci",
                onExecute = {
                    onDismiss()
                    onOpenPitchPresentation()
                }
            ),
            AppFeature(
                id = "node_linking",
                name = "Vzájemné propojování uzlů (Spojnice)",
                icon = Icons.Default.Link,
                accentColor = Color(0xFF38BDF8),
                category = FeatureCategory.CANVAS,
                badge = "Vazby & Zájmy",
                shortSummary = "Propojte libovolné dvě myšlenky zakřivenou vizuální spojnicí.",
                detailedDescription = "Kromě základního stromového větvení můžete vytvářet křížové vazby mezi nesouvisejícími větvemi (např. propojit riziko s konkrétním úkolem řešení). Spojnice se dynamicky vykreslují jako plynulé křivky.",
                howToUseSteps = listOf(
                    "Klepněte na první uzel – ve spodním panelu se objeví akční lišta uzlu.",
                    "Klepněte na ikonu řetězu (Propojit).",
                    "Klepněte na druhý uzel, se kterým chcete vazbu vytvořit."
                ),
                proTip = "Horní modrý proužek vás informuje, že režim propojování je aktivní.",
                actionButtonText = "Zpět na mapu",
                onExecute = {
                    onDismiss()
                    onSwitchToCanvas()
                }
            ),
            AppFeature(
                id = "manual_nodes",
                name = "Ruční tvorba uzlů & Úpravy",
                icon = Icons.Default.Add,
                accentColor = NeonEmerald,
                category = FeatureCategory.CANVAS,
                badge = "Úplná kontrola",
                shortSummary = "Přidávejte vlastní uzly, poduzly a upravujte jejich text a kategorie.",
                detailedDescription = "Plná tvůrčí svoboda. Každý uzel může mít název, podrobný popis, přiřazeného AI autora a kategorii (Cíl, Úkol, Riziko, Nápad, Poznámka). Uzly lze libovolně přetahovat po plátně.",
                howToUseSteps = listOf(
                    "Klepněte na tlačítko '+ Uzel' ve spodní liště pro nový hlavní uzel.",
                    "Nebo vyberte existující uzel a klepněte na '+' v plovoucí liště pro přidání potomka.",
                    "Klepnutím na ikonu tužky otevřete editor textu a parametrů uzlu."
                ),
                proTip = "Změna kategorie okamžitě změní barvu obrysu uzlu pro snadnou orientaci.",
                actionButtonText = "Přidat uzel",
                onExecute = {
                    onDismiss()
                    onAddManualNode()
                }
            ),
            AppFeature(
                id = "cloud_sync",
                name = "Cloud synchronizace & Google účet",
                icon = Icons.Default.CloudSync,
                accentColor = NeonCyan,
                category = FeatureCategory.CLOUD_EXPORT,
                badge = "Firestore",
                shortSummary = "Zálohujte své myšlenkové mapy do bezpečného cloudu.",
                detailedDescription = "Přihlaste se přes Google účet a vaše myšlenkové mapy se budou automaticky synchronizovat do databáze Google Firestore. O své nápady nikdy nepřijdete ani při změně zařízení.",
                howToUseSteps = listOf(
                    "V menu zvolte 'Cloud synchronizace & Google účet'.",
                    "Klepněte na 'Přihlásit se přes Google' a potvrďte svůj účet.",
                    "Klepněte na 'Synchronizovat nyní do cloudu'."
                ),
                proTip = "V aplikaci vidíte čas posledního uložení a stav spojení s cloudem.",
                actionButtonText = "Otevřít Cloud synchronizaci",
                onExecute = {
                    onDismiss()
                    onOpenCloudSync()
                }
            ),
            AppFeature(
                id = "version_history",
                name = "Historie verzí & Snapshoty",
                icon = Icons.Default.History,
                accentColor = NeonPurple,
                category = FeatureCategory.CLOUD_EXPORT,
                badge = "Časová osa",
                shortSummary = "Ukládejte si záchytné body projektu a kdykoliv se vraťte v čase.",
                detailedDescription = "Nebojte se experimentovat. Před velkými změnami si uložte pojmenovaný snímek (snapshot). Kdykoliv se můžete jedním klepnutím vrátit k předchozímu stavu projektu.",
                howToUseSteps = listOf(
                    "V menu zvolte 'Časová osa & Verze (Snapshoty)'.",
                    "Zadejte název verze (např. 'Před schůzkou s investorem') a uložte ji.",
                    "V seznamu verzí můžete libovolnou verzi ihned obnovit."
                ),
                proTip = "Při každém obnovení se stávající stav nejprve automaticky zazálohuje.",
                actionButtonText = "Otevřít historii verzí",
                onExecute = {
                    onDismiss()
                    onOpenVersionHistory()
                }
            ),
            AppFeature(
                id = "export_share",
                name = "Export & Sdílení (Markdown, Jira)",
                icon = Icons.Default.Share,
                accentColor = NeonCyan,
                category = FeatureCategory.CLOUD_EXPORT,
                badge = "Export",
                shortSummary = "Exportujte mapu do Markdownu, Jira formátu, JSONu nebo textu.",
                detailedDescription = "Převeďte myšlenkovou mapu do formátů připravených pro váš tým: strukturovaný Markdown pro GitHub/Notion, formátování pro Jira tickety, surový JSON nebo čistý text k odeslání e-mailem.",
                howToUseSteps = listOf(
                    "V menu zvolte 'Export & Sdílení'.",
                    "Zvolte požadovaný formát (Markdown, Jira, JSON, Text).",
                    "Klepněte na 'Kopírovat do schránky' nebo 'Sdílet přes Android share sheet'."
                ),
                proTip = "Jira export automaticky převede úkoly do formátu User Stories s akceptačními kritérii.",
                actionButtonText = "Otevřít dialog exportu",
                onExecute = {
                    onDismiss()
                    onOpenExport()
                }
            ),
            AppFeature(
                id = "import_notes",
                name = "Import poznámek ze schůzky",
                icon = Icons.Default.FileUpload,
                accentColor = NeonEmerald,
                category = FeatureCategory.CLOUD_EXPORT,
                badge = "Text na mapu",
                shortSummary = "Vložte surové zápisky ze schůzky a AI je přetvoří v myšlenkovou mapu.",
                detailedDescription = "Máte neuspořádaný zápis ze schůzky, e-mail nebo poznámky z bloku? Vložte text sem – Gemini AI extrahuje klíčové pilíře, akční kroky a rizika a vygeneruje z nich strukturovanou mapu.",
                howToUseSteps = listOf(
                    "V menu zvolte 'Importovat zápis / poznámky'.",
                    "Vložte nebo napište text ze schůzky.",
                    "Klepněte na 'Zpracovat AI' – nové uzly se automaticky připojí k mapě."
                ),
                proTip = "Ušetří desítky minut ručního přepisování poznámek z brainstormingu.",
                actionButtonText = "Importovat poznámky",
                onExecute = {
                    onDismiss()
                    onOpenImport()
                }
            ),
            AppFeature(
                id = "custom_personas",
                name = "Tvorba vlastních AI person",
                icon = Icons.Default.Psychology,
                accentColor = NeonPurple,
                category = FeatureCategory.AI_STRATEGY,
                badge = "Personalizace",
                shortSummary = "Vytvořte si vlastního AI experta (Právník, Bezpečák, Investor...).",
                detailedDescription = "Potřebujete specifický pohled na projekt? Vytvořte si vlastní personu: definujte jméno, emoji, barvu, tón komunikace, specializaci a zabarvení hlasu pro syntézu řeči.",
                howToUseSteps = listOf(
                    "V menu zvolte '+ Vytvořit vlastní AI personu'.",
                    "Vyplňte jméno (např. 'Cyber Security Expert'), vyberte emoji a systémový prompt.",
                    "Uložte personu – ihned se objeví v horní liště i v Kulatém stole."
                ),
                proTip = "Vlastní personu můžete kdykoliv aktivovat klepnutím na její štítek v horní liště.",
                actionButtonText = "Vytvořit novou personu",
                onExecute = {
                    onDismiss()
                    onOpenCreatePersona()
                }
            ),
            AppFeature(
                id = "google_models",
                name = "Výběr Google AI Modelu",
                icon = Icons.Default.Bolt,
                accentColor = NeonAmber,
                category = FeatureCategory.AI_STRATEGY,
                badge = "Gemini",
                shortSummary = "Přepínejte mezi modely Gemini 2.5 Flash, Pro a 2.0 Flash.",
                detailedDescription = "Vyberte si optimální model pro své potřeby: Gemini 2.5 Flash pro bleskurychlou odezvu při interaktivní tvorbě, nebo Gemini 2.5 Pro pro hloubkové strategické úvahy a složité audity.",
                howToUseSteps = listOf(
                    "Klepněte na štítek modelu v pravé horní části obrazovky (např. 'Gemini 2.5 Flash').",
                    "Zvolte model ze seznamu s přehledem jeho parametrů a silných stránek."
                ),
                proTip = "Všechny modely jsou v rámci Google AI Studia optimalizované pro nízkou latenci.",
                actionButtonText = "Změnit AI model",
                onExecute = {
                    onDismiss()
                    onOpenModelSelector()
                }
            ),
            AppFeature(
                id = "app_updates",
                name = "Aktualizace aplikace (OTA z GitHubu)",
                icon = Icons.Default.SystemUpdate,
                accentColor = NeonEmerald,
                category = FeatureCategory.CLOUD_EXPORT,
                badge = "GitHub",
                shortSummary = "Kontrola nových verzí a instalace přímo ze zařízení.",
                detailedDescription = "Aplikace sama kontroluje dostupnost nových sestavení a aktualizací na GitHubu. Můžete si přímo stáhnout a nainstalovat nejnovější verzi s opravami a novými funkcemi.",
                howToUseSteps = listOf(
                    "V menu zvolte 'Aktualizace aplikace (GitHub)'.",
                    "Pokud je k dispozici nová verze, zobrazí se seznam změn a tlačítko ke stažení."
                ),
                proTip = "Při nalezení nové verze se v horní části obrazovky zobrazí zelený notifikační štítek.",
                actionButtonText = "Zkontrolovat aktualizace",
                onExecute = {
                    onDismiss()
                    onCheckUpdate()
                }
            )
        )
    }

    val filteredFeatures = remember(features, searchQuery, selectedCategory) {
        features.filter { feat ->
            val matchesCategory = selectedCategory == FeatureCategory.ALL || feat.category == selectedCategory
            val matchesQuery = searchQuery.isBlank() ||
                feat.name.contains(searchQuery, ignoreCase = true) ||
                feat.shortSummary.contains(searchQuery, ignoreCase = true) ||
                feat.detailedDescription.contains(searchQuery, ignoreCase = true) ||
                feat.badge.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = SlateCard,
            border = BorderStroke(1.dp, TechBorder),
            shadowElevation = 20.dp,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .testTag("features_catalog_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header with glowing icon and close button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(44.dp)
                                .background(NeonCyan.copy(alpha = 0.15f), CircleShape)
                                .border(1.5.dp, NeonCyan.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Centrum funkcí & Průvodce",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Klepnutím na libovolnou funkci rozbalíte podrobné info",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .background(SlateCardElevated, CircleShape)
                            .testTag("btn_close_features_catalog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Zavřít",
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = "Hledat funkci (např. zoom, audit, export, hlas...)",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Vymazat",
                                    tint = TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = TechBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("input_search_features")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Category Filter Pills
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(FeatureCategory.entries) { cat ->
                        val isSelected = selectedCategory == cat
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) cat.color.copy(alpha = 0.22f) else SlateCardElevated,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) cat.color else TechBorder
                            ),
                            modifier = Modifier.clickable { selectedCategory = cat }
                        ) {
                            Text(
                                text = cat.label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) cat.color else TextSecondary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Feature Count summary banner
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SlateCardElevated.copy(alpha = 0.6f),
                    border = BorderStroke(0.8.dp, TechBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Nalezeno ${filteredFeatures.size} funkcí • Klepnutím na kartu rozbalíte návod a akce",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Expandable Features List
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("list_features")
                ) {
                    items(filteredFeatures, key = { it.id }) { feat ->
                        val isExpanded = expandedFeatureId == feat.id
                        FeatureCardItem(
                            feature = feat,
                            isExpanded = isExpanded,
                            onToggleExpand = {
                                expandedFeatureId = if (isExpanded) null else feat.id
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FeatureCardItem(
    feature: AppFeature,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isExpanded) SlateCardElevated else SlateCard,
        border = BorderStroke(
            if (isExpanded) 1.5.dp else 1.dp,
            if (isExpanded) feature.accentColor else TechBorder
        ),
        shadowElevation = if (isExpanded) 8.dp else 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onToggleExpand() }
            .testTag("feature_card_${feature.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Icon + Title + Category Badge + Expand Chevron
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Feature Icon in accented round badge
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(38.dp)
                            .background(feature.accentColor.copy(alpha = 0.16f), CircleShape)
                            .border(1.dp, feature.accentColor.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(
                            imageVector = feature.icon,
                            contentDescription = feature.name,
                            tint = feature.accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = feature.name,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isExpanded) feature.accentColor else TextPrimary,
                                maxLines = if (isExpanded) 2 else 1
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = feature.accentColor.copy(alpha = 0.15f),
                                modifier = Modifier.padding(end = 6.dp)
                            ) {
                                Text(
                                    text = feature.badge,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = feature.accentColor,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = feature.shortSummary,
                                fontSize = 11.sp,
                                color = TextMuted,
                                maxLines = 1
                            )
                        }
                    }
                }

                // Expand indicator
                IconButton(
                    onClick = onToggleExpand,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Sbalit info" else "Rozbalit podrobné info",
                        tint = if (isExpanded) feature.accentColor else TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Expandable Detailed Information Section
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    // Divider
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(TechBorder)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 1. Detailed Description
                    Text(
                        text = "📖 Podrobný popis funkce:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = feature.accentColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = feature.detailedDescription,
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 2. Step-by-step How to Use
                    Text(
                        text = "🛠️ Jak funkci použít:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = feature.accentColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    feature.howToUseSteps.forEachIndexed { idx, step ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                        ) {
                            Text(
                                text = "${idx + 1}. ",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = feature.accentColor
                            )
                            Text(
                                text = step,
                                fontSize = 11.sp,
                                color = TextPrimary,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 3. Pro Tip Box
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = feature.accentColor.copy(alpha = 0.08f),
                        border = BorderStroke(0.8.dp, feature.accentColor.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                        ) {
                            Text(text = "💡", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = feature.proTip,
                                fontSize = 11.sp,
                                color = TextPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 4. Action Button to Execute
                    Button(
                        onClick = feature.onExecute,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = feature.accentColor,
                            contentColor = if (feature.accentColor == NeonCyan || feature.accentColor == NeonEmerald || feature.accentColor == NeonAmber) Color.Black else Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("btn_execute_${feature.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = feature.actionButtonText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
