package com.example.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ViewTimeline
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCoral
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonPurple

enum class FeatureCategory(val displayName: String, val color: Color) {
    AI_STRATEGY("AI Strategické nástroje", NeonCyan),
    MAP_CREATION("Práce s plátnem a mapou", NeonEmerald),
    PROJECT_VIEWS("Pohledy na projekt", NeonPurple),
    EXPORT_DATA("Projekt & Správa dat", NeonAmber),
    CLOUD_SYSTEM("Cloud & Systém", Color(0xFF38BDF8))
}

data class AppFeatureInfo(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: FeatureCategory,
    val icon: ImageVector,
    val color: Color,
    val description: String,
    val practicalExample: String,
    val keyBenefits: List<String>,
    val actionLabel: String = "Spustit funkci"
)

object FeatureCatalog {

    val AUDIT_DEBATE = AppFeatureInfo(
        id = "audit_debate",
        title = "Hloubkový AI Audit & Debata",
        subtitle = "Multi-round debata s odhalováním rizik a zastaralých technologií",
        category = FeatureCategory.AI_STRATEGY,
        icon = Icons.Default.Security,
        color = NeonCoral,
        description = "Simuluje vícekolovou konfrontaci mezi 3 specializovanými AI (Nova, Vortex, Atlas). Vortex nemilosrdně napadá slabiny, prověřuje bezpečnostní mezery a identifikuje zastaralé nástroje nebo nevhodné postupy. Atlas reaguje konkrétní nápravou a syntézou.",
        practicalExample = "Při plánování mobilní aplikace Audit odhalí, že navržená knihovna pro přihlašování je zastaralá a navrhne moderní Credential Manager včetně plánu mitigace rizik.",
        keyBenefits = listOf(
            "Odhalení bezpečnostních a architektonických pastí před zahájením vývoje",
            "Detekce zastaralých nebo neefektivních technologií",
            "Mluvený audio report s konkrétními závěry"
        ),
        actionLabel = "Spustit audit"
    )

    val SWOT_ANALYSIS = AppFeatureInfo(
        id = "swot_analysis",
        title = "SWOT Strategická analýza",
        subtitle = "Rozbor silných/slabých stránek, příležitostí a hrozeb",
        category = FeatureCategory.AI_STRATEGY,
        icon = Icons.Default.Shield,
        color = NeonEmerald,
        description = "AI analyzuje všechny uzly a myšlenky v mapě a automaticky vygeneruje strukturovanou matici SWOT. Každý kvadrant je podrobně vysvětlen s doporučením dalších kroků.",
        practicalExample = "Vstup: 'Expanze e-shopu do zahraničí'. SWOT okamžitě identifikuje slabinu v lokální zákaznické podpoře a příležitost v nezaplněném tržním segmentu.",
        keyBenefits = listOf(
            "Ucelený strategický pohled z ptačí perspektivy",
            "Barevně odlišené kvadranty pro rychlou orientaci",
            "Možnost exportu výstupu do dokumentace"
        ),
        actionLabel = "Otevřít SWOT analýzu"
    )

    val QUESTION_STORMING = AppFeatureInfo(
        id = "question_storming",
        title = "Otázkový útok (Question Storming)",
        subtitle = "Hledání klíčových otázek před hledáním odpovědí",
        category = FeatureCategory.AI_STRATEGY,
        icon = Icons.Default.HelpOutline,
        color = NeonAmber,
        description = "Místo ukvapených odpovědí a předčasných závěrů AI zasype projekt provokativními otázkami. Pomáhá týmu objevit slepá místa, která nikdo nepředpokládal.",
        practicalExample = "'Co se stane, když server vypadne během platby?' nebo 'Kdo je skutečný uživatel, který za službu zaplatí?'",
        keyBenefits = listOf(
            "Eliminace falešných předpokladů na začátku projektu",
            "Obohacení mapy o nové důležité větve k prozkoumání",
            "Zvýšení kvality finálního rozhodování"
        ),
        actionLabel = "Spustit otázkový útok"
    )

    val MARKET_BENCHMARK = AppFeatureInfo(
        id = "market_benchmark",
        title = "Tržní benchmarky & Konkurence",
        subtitle = "Analýza trhu, alternativ a unikátní výhody (USP)",
        category = FeatureCategory.AI_STRATEGY,
        icon = Icons.Default.Search,
        color = Color(0xFF4285F4),
        description = "Porovná váš koncept s reálnými tržními produkty a trendy. Vyhledá hlavní konkurenty a doporučí odlišující hodnotu (Unique Selling Proposition), díky které projekt uspěje.",
        practicalExample = "Pro nový fitness tracker AI srovná stávající aplikace na trhu a doporučí zaměřit se na gamifikaci pro rodiny, kde chybí silný hráč.",
        keyBenefits = listOf(
            "Okamžitý přehled o konkurenčním prostředí",
            "Definice jasné přidané hodnoty produktu",
            "Podpořeno reálnými tržními poznatky"
        ),
        actionLabel = "Analyzovat trh"
    )

    val BLUEPRINT = AppFeatureInfo(
        id = "blueprint",
        title = "Funkční Blueprint & Architektura",
        subtitle = "Kompletní technická specifikace a prováděcí plán",
        category = FeatureCategory.AI_STRATEGY,
        icon = Icons.Default.Description,
        color = NeonAmber,
        description = "Atlas ve spolupráci s Novou a Vortexem syntetizuje celou myšlenkovou mapu do profesionálního strukturovaného technického dokumentu (Markdown). Zahrnuje vizi, zvládnutí rizik, systémovou architekturu i časový plán.",
        practicalExample = "Máte mapu s 20 uzly. Blueprint vytvoří ucelený 4kapitolový dokument připravený pro předání vývojovému týmu nebo investorům.",
        keyBenefits = listOf(
            "Převedení myšlenkové mapy do exekutivního zadání",
            "Kapitoly pro vedení, architekty i programátory",
            "Možnost zkopírovat nebo exportovat do souboru"
        ),
        actionLabel = "Vytvořit Blueprint"
    )

    val CANVAS_MIND_MAP = AppFeatureInfo(
        id = "canvas_mind_map",
        title = "Interaktivní plátno (2D Myšlenková mapa)",
        subtitle = "Nekonečné plátno, plynulý zoom 2 prsty a Bezierovy křivky",
        category = FeatureCategory.PROJECT_VIEWS,
        icon = Icons.Default.Map,
        color = NeonCyan,
        description = "Základní vizuální prostředí aplikace. Podporuje plynulý zoom dvěma prsty (pinch-to-zoom), posouvání prstem, volné přetahování jednotlivých uzlů a automatické vykreslování svítících Bezierových větví podle kategorií.",
        practicalExample = "Přibližte si detailní větev uzlu dvěma prsty, přetáhněte ji na požadované místo nebo klepnutím na spojnici vytvořte vazbu mezi dvěma nesouvisejícími nápady.",
        keyBenefits = listOf(
            "Hardwarem akcelerované GPU škálování bez sekání",
            "Barevné rozlišení kategorií (Cíle, Funkce, Rizika, Řešení, Diagramy)",
            "Plovoucí panel pro krokový zoom a vycentrování"
        ),
        actionLabel = "Zobrazit plátno"
    )

    val ROADMAP_TIMELINE = AppFeatureInfo(
        id = "roadmap_timeline",
        title = "Časová osa & Fáze (Roadmapa)",
        subtitle = "Fázování projektu od MVP po škálování s checkboxy",
        category = FeatureCategory.PROJECT_VIEWS,
        icon = Icons.Default.ViewTimeline,
        color = NeonEmerald,
        description = "Převede nápady z plátna do přehledného časového harmonogramu rozděleného na Fázi 1 (Základ / MVP), Fázi 2 (Rozšíření) a Fázi 3 (Optimalizace a růst). U každého uzlu můžete sledovat a odškrtávat stav splnění.",
        practicalExample = "Místo nepřehledného seznamu vidíte přesně, které funkce patří do první verze a které počkají na další kvartál.",
        keyBenefits = listOf(
            "Okamžitý přechod z brainstormingu do realizace",
            "Interaktivní odškrtávání hotových úkolů",
            "Filtrování podle fází a zodpovědných AI person"
        ),
        actionLabel = "Otevřít časovou osu"
    )

    val PRIORITIZATION_MATRIX = AppFeatureInfo(
        id = "prioritization_matrix",
        title = "Prioritizační Matice 2x2",
        subtitle = "Rozdělení nápadů podle Dopadu vs. Náročnosti",
        category = FeatureCategory.PROJECT_VIEWS,
        icon = Icons.Default.GridOn,
        color = NeonPurple,
        description = "Zobrazuje uzly ve 4 kvadrantech grafu (Dopad na svislé ose, Náročnost/Úsilí na vodorovné ose): 1. Rychlé výhry (Quick Wins), 2. Strategické priority, 3. Doplňky, 4. Pasti na čas (vysoká pracnost, malý užitek).",
        practicalExample = "Okamžitě vidíte, které 3 funkce přinesou uživatelům největší hodnotu s minimálním úsilím a vyhnete se pastem na čas.",
        keyBenefits = listOf(
            "Objektivní výběr priorit pro vývoj",
            "Jasná vizuální orientace pro celý tým",
            "Barevné štítky kvadrantů s okamžitou odezvou"
        ),
        actionLabel = "Otevřít matici"
    )

    val AUTO_LAYOUT = AppFeatureInfo(
        id = "auto_layout",
        title = "Automatické uspořádání (Auto-Layout)",
        subtitle = "Vyrovnání celé mapy do symetrického stromu bez překryvů",
        category = FeatureCategory.MAP_CREATION,
        icon = Icons.Default.AutoAwesome,
        color = NeonCyan,
        description = "Využívá pokročilý hierarchický Walkerův stromový algoritmus. Rekurzivně vypočítá výšku a rozestupy všech pod-větví a seřadí uzly do symetrických křídel (vpravo i vlevo) s garantovaným nulovým překrýváním.",
        practicalExample = "Když po intenzivním brainstormingu přibude 15 nových uzlů, jedno klepnutí na kouzelnou hůlku celou mapu bleskově vyčistí a uhladí.",
        keyBenefits = listOf(
            "Žádné ruční zarovnávání uzlů po jednom",
            "Matematicky vyloučená kolize větví",
            "Automatické vycentrování mapy na displeji"
        ),
        actionLabel = "Uspořádat mapu"
    )

    val VOICE_ASSISTANT = AppFeatureInfo(
        id = "voice_assistant",
        title = "Hlasový asistent & Diktování",
        subtitle = "Ovládání myšlenkové mapy mluveným slovem v češtině",
        category = FeatureCategory.MAP_CREATION,
        icon = Icons.Default.Mic,
        color = NeonCyan,
        description = "Stiskněte mikrofon a mluvte. Asistent v reálném čase přepisuje váš hlas, pochopí kontext aktuálně vybraného uzlu, odpoví syntetickým hlasem a automaticky založí nové relevantní větve přímo do mapy.",
        practicalExample = "Vyberete uzel 'Marketing' a řeknete: 'Přidej 3 nápady na propagaci na sociálních sítích s minimálním rozpočtem'. Mapa se okamžitě rozroste o 3 pod-uzly.",
        keyBenefits = listOf(
            "Přirozený brainstorming bez psaní na klávesnici",
            "Mluvené hlasové shrnutí od expertních AI person",
            "Vizuální animovaná vlnovka hlasu pro přehled o stavu"
        ),
        actionLabel = "Vyzkoušet hlas"
    )

    val ROUNDTABLE_CHAT = AppFeatureInfo(
        id = "roundtable_chat",
        title = "Kulatý stůl 3 AI (Nova, Vortex, Atlas)",
        subtitle = "Multi-agentní brainstorming tří odlišných myšlenkových směrů",
        category = FeatureCategory.AI_STRATEGY,
        icon = Icons.AutoMirrored.Filled.Chat,
        color = NeonPurple,
        description = "Spolupráce tří AI expertů u jednoho stolu: Nova (vizionář přinášející inovace a prolomové funkce), Vortex (nemilosrdný kritik hledající bezpečnostní slabiny, pasti a náklady) a Atlas (systémový architekt syntetizující debatu do procesů).",
        practicalExample = "Zadáte téma a sledujete, jak se Nova nadchne pro AI automatizaci, Vortex upozorní na GDPR a latenci a Atlas navrhne bezpečné workflow pro obojí.",
        keyBenefits = listOf(
            "Pohled na problém z více úhlů pohledu současně",
            "Každá persona má svůj hlas, barvu a emoji",
            "Přímý zápis doporučení jednotlivých AI do mapy"
        ),
        actionLabel = "Otevřít 3-AI Chat"
    )

    val PROCESS_DIAGRAM = AppFeatureInfo(
        id = "process_diagram",
        title = "Vizuální procesní diagram (Flowchart)",
        subtitle = "Krokový vývojový diagram zobrazený přímo v těle uzlu",
        category = FeatureCategory.MAP_CREATION,
        icon = Icons.Default.AccountTree,
        color = NeonEmerald,
        description = "Atlas dokáže jakýkoliv uzel rozpracovat do přehledného krokového vývojového diagramu spojeného šipkami (např. [1. Zadání] -> [2. Validace] -> [3. Schválení] -> [4. Zápis]).",
        practicalExample = "U uzlu 'Registrace uživatele' vygeneruje kroky od odeslání formuláře přes e-mailové ověření až po uvítací tutoriál.",
        keyBenefits = listOf(
            "Jasná vizualizace algoritmu nebo byznysového procesu",
            "Kompaktní zobrazení přímo na kartě uzlu bez nutnosti otevírat jiné okno",
            "Snadná editace jednotlivých kroků"
        ),
        actionLabel = "Generovat diagram"
    )

    val EXPORT_SHARE = AppFeatureInfo(
        id = "export_share",
        title = "Export & Sdílení (Markdown, Jira, HTML)",
        subtitle = "Export projektu do vývojářských i kancelářských formátů",
        category = FeatureCategory.EXPORT_DATA,
        icon = Icons.Default.Share,
        color = NeonCyan,
        description = "Převede mapu do formátu dle vašeho výběru: Markdown dokument, formátované úkoly pro import do Jira / Trello / Asana, HTML interaktivní soubor pro web nebo textový soubor pro sdílení.",
        practicalExample = "Po brainstormingu jedním klikem vygenerujete Jira backlog a nasdílíte ho vývojářům do Slacku.",
        keyBenefits = listOf(
            "Podpora Jira, Confluence, Trello i Notion",
            "Kopírování do schránky i odeslání přes systémové sdílení",
            "Žádná nutnost ručního přepisování nápadů"
        ),
        actionLabel = "Otevřít export"
    )

    val IMPORT_NOTES = AppFeatureInfo(
        id = "import_notes",
        title = "Import textových zápisů a poznámek",
        subtitle = "Převod nestrukturovaného textu na myšlenkovou mapu",
        category = FeatureCategory.EXPORT_DATA,
        icon = Icons.Default.FileUpload,
        color = NeonEmerald,
        description = "Vložte textový zápis z porady, chaotické poznámky nebo odrážky z e-mailu. AI text zanalyzuje, extrahuje hlavní myšlenku a automaticky z něj vytvoří větve myšlenkové mapy.",
        practicalExample = "Vložíte 10 řádků zápisu z pondělní schůzky a za 3 vteřiny máte přehlednou mapu s cíli a zodpovědnými kroky.",
        keyBenefits = listOf(
            "Okamžitá digitalizace meeting notes",
            "Automatické rozpoznání kategorií (úkoly, rizika, nápady)",
            "Úspora desítek minut ručního překreslování"
        ),
        actionLabel = "Importovat poznámky"
    )

    val VERSION_HISTORY = AppFeatureInfo(
        id = "version_history",
        title = "Časová osa & Historie verzí (Snapshoty)",
        subtitle = "Ukládání milníků a návrat k dřívějšímu stavu mapy",
        category = FeatureCategory.EXPORT_DATA,
        icon = Icons.Default.History,
        color = NeonPurple,
        description = "Umožňuje ukládat pojmenované snímky mapy v čase (Snapshoty). Můžete volně experimentovat, mazat a zkoušet divoké nápady s jistotou, že se kdykoliv můžete vrátit k uložené verzi.",
        practicalExample = "Před velkou reorganizací vytvoříte snímek 'Stav před revizí'. Pokud se nový směr neosvědčí, jedním klepnutím obnovíte původní rozložení.",
        keyBenefits = listOf(
            "Bezpečné experimentování bez obav ze ztráty práce",
            "Časové razítko a popis u každého snímku",
            "Okamžitá obnova celé struktury včetně pozic uzlů"
        ),
        actionLabel = "Zobrazit historii"
    )

    val CUSTOM_PERSONAS = AppFeatureInfo(
        id = "custom_personas",
        title = "Vlastní AI persony a experti",
        subtitle = "Tvorba specializovaných rolí dle vašich potřeb",
        category = FeatureCategory.AI_STRATEGY,
        icon = Icons.Default.Psychology,
        color = NeonPurple,
        description = "Kromě 3 základních AI (Nova, Vortex, Atlas) si můžete vytvořit vlastní experty na míru: např. 'Právník pro GDPR', 'Kopírajtr', 'Finanční kontrolor' nebo 'Zkušený investor'. Nastavíte jim jméno, roli, emoji a tón hlasu.",
        practicalExample = "Vytvoříte personu 'UX Designér', která se v debatě zaměří výhradně na jednoduchost a přívětivost pro běžné uživatele.",
        keyBenefits = listOf(
            "Neomezené přizpůsobení vašemu oboru podnikání",
            "Vlastní barva, emoji i parametry syntézy hlasu",
            "Možnost vést debatu 1 na 1 i zapojit personu do mapy"
        ),
        actionLabel = "Vytvořit personu"
    )

    val CLOUD_SYNC = AppFeatureInfo(
        id = "cloud_sync",
        title = "Cloud synchronizace & Google účet",
        subtitle = "Ukládání do Firebase Firestore a záloha projektů",
        category = FeatureCategory.CLOUD_SYSTEM,
        icon = Icons.Default.CloudSync,
        color = NeonCyan,
        description = "Propojení s cloudovou databází Firebase Firestore přes oficiální Google Sign-In (Credential Manager). Vaše projekty jsou v bezpečí, zálohovány a přístupné po přihlášení.",
        practicalExample = "Vytvoříte brainstorming na mobilu a po přihlášení na tabletu máte okamžitě k dispozici celou mapu v aktuálním stavu.",
        keyBenefits = listOf(
            "Bezpečné jednotné přihlášení přes Google účet",
            "Automatická záloha a ochrana proti ztrátě dat",
            "Indikátor stavu synchronizace přímo v aplikaci"
        ),
        actionLabel = "Otevřít synchronizaci"
    )

    val GOOGLE_AI_MODELS = AppFeatureInfo(
        id = "google_ai_models",
        title = "Bezplatné modely Google AI",
        subtitle = "Přepínání mezi modely rodiny Gemini",
        category = FeatureCategory.CLOUD_SYSTEM,
        icon = Icons.Default.Bolt,
        color = NeonCyan,
        description = "Umožňuje volit optimální AI model pro vaši práci: Gemini 2.5 Flash (extrémně rychlý, bleskové reakce), Gemini 2.5 Flash Lite (minimální latence) nebo Gemini 2.5 Pro (hluboká analýza a komplexní uvažování).",
        practicalExample = "Pro rychlé generování uzlů použijete Flash, pro náročný bezpečnostní audit přepnete na Gemini Pro.",
        keyBenefits = listOf(
            "Oficiální přístup k nejnovějším modelům Gemini",
            "Rychlý přepínač dostupný přímo z horní lišty",
            "Optimalizováno pro rychlost odezvy na mobilním zařízení"
        ),
        actionLabel = "Vybrat model"
    )

    val allFeatures = listOf(
        AUDIT_DEBATE,
        SWOT_ANALYSIS,
        QUESTION_STORMING,
        MARKET_BENCHMARK,
        BLUEPRINT,
        CANVAS_MIND_MAP,
        ROADMAP_TIMELINE,
        PRIORITIZATION_MATRIX,
        AUTO_LAYOUT,
        VOICE_ASSISTANT,
        ROUNDTABLE_CHAT,
        PROCESS_DIAGRAM,
        EXPORT_SHARE,
        IMPORT_NOTES,
        VERSION_HISTORY,
        CUSTOM_PERSONAS,
        CLOUD_SYNC,
        GOOGLE_AI_MODELS
    )

    fun getById(id: String): AppFeatureInfo? = allFeatures.firstOrNull { it.id == id }
}
