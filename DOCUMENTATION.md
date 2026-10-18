# 🧠 MindCraft AI — Kompletní Dokumentace Aplikace

> **MindCraft AI** je inteligentní systém pro vizuální brainstorming, strategické plánování a automatizovanou tvorbu myšlenkových map. Kombinuje multidimenzionální debatu tří specializovaných AI modelů (Kulatý stůl), pokročilé gestické plátno, obousměrnou real-time cloudovou synchronizaci mezi mobilem a PC (Webapp), Vibe Coding nástroje a volitelný hlasový režim.

---

## 📑 Obsah
1. [Architektura a technologický stack](#1-architektura-a-technologický-stack)
2. [Gestické plátno a manipulace s uzly](#2-gestické-plátno-a-manipulace-s-uzly)
3. [Kulatý stůl 3 AI (Živá multi-round debata)](#3-kulatý-stůl-3-ai-živá-multi-round-debata)
4. [AI Copilot Chat a volitelný hlasový režim](#4-ai-copilot-chat-a-volitelný-hlasový-režim)
5. [Režimy zobrazení (Mapa, Fáze, Matice 2x2)](#5-režimy-zobrazení-mapa-fáze-matice-2x2)
6. [Vibe Coding & Generátor produkčního kódu](#6-vibe-coding--generátor-produkčního-kódu)
7. [Cloudová synchronizace & Online Webapp pro PC](#7-cloudová-synchronizace--online-webapp-pro-pc)
8. [Exporty, Prezentace a Verzování](#8-exporty-prezentace-a-verzování)
9. [Průvodce komponentami a strukturou kódu](#9-průvodce-komponentami-a-strukturou-kódu)

---

## 1. Architektura a technologický stack

### Mobilní klient (Android)
- **Jazyk & UI:** 100% Kotlin s moderním deklarativním Jetpack Compose (Material Design 3).
- **Architektura:** MVVM (Model-View-ViewModel) se StateFlow a Clean Architecture vrstvami.
- **Lokální databáze:** Room SQLite s plnou offline podporou a reaktivními Flow streamy.
- **Vzdálená databáze & Sync:** Google Firebase Firestore SDK s reálným časem a offline cache.
- **AI Jádro:** Google Gemini 2.5 Flash / Pro modely s napojením na Google Search grounding.
- **Hlasový subsystém:** Android `SpeechRecognizer` (STT) a `TextToSpeech` (TTS) s personifikovanou intonací pro každou AI personu.

### Webový klient (PC / Prohlížeč)
- **Frontend:** Vanilla JS / HTML5 Canvas s nulovou závislostí na těžkých bundlerech pro okamžitý start.
- **Databáze:** Firebase Firestore SDK (Compat) pro přímou obousměrnou synchronizaci s Android aplikací.
- **Backend:** Node.js HTTP server s Nginx reverzní proxy na Google Cloud Run.

---

## 2. Gestické plátno a manipulace s uzly

### Komponenta `ZoomablePanContainer`
Plátno myšlenkové mapy využívá nízkoúrovňový `Modifier.pointerInput` pro maximální plynulost:
- **Pinch-to-zoom (Přiblížení / Oddálení):** Plynulé škálování v rozsahu **0.2× až 5.0×** s centrováním k těžišti dotyku prstů.
- **Posun plátna (Pan):** Plynulý posun jedním i dvěma prsty po neomezeném 2D prostoru.
- **Dvojité klepnutí (Double Tap):** Rychlé vycentrování pohledu a reset přiblížení na 100 %.
- **Kombinované ovládání:** Pravý postranní panel obsahuje tlačítka pro krok o krok zoom (`+` a `-`), procentuální ukazatel a tlačítko pro automatické uspořádání stromu uzlů.

### Práce s uzly a vazbami
- **Výběr uzlu:** Klepnutím na uzel se uzel zvýrazní a ve spodní části obrazovky se **bez překrývání** vysune kontextová lišta uzlu.
- **Pohyb uzlu:** Dlouhým stiskem a tažením lze libovolný uzel přesouvat po plátně.
- **Spojnice:** Vazby mezi uzly se vykreslují jako plynulé Bézierovy křivky se šipkami orientace toku myšlenek.
- **Akce na uzlu:**
  - ✏️ **Upravit:** Změna názvu, popisu, kategorie a vložených markdown poznámek.
  - ➕ **Přidat větev:** Rychlé vytvoření podřízeného potomka.
  - 🔗 **Propojit:** Klepnutím na jiný uzel se vytvoří nová orientovaná vazba.
  - 🌿 **Procesní diagram:** Převod uzlu a jeho podvětví do sekvenčního diagramu.
  - 💻 **Vibe Kód:** Okamžité vygenerování zdrojového kódu a Cursor instrukcí pro vybraný uzel.
  - 🗑️ **Smazat:** Bezpečné odstranění uzlu s možností zachování nebo smazání podřízených větví.

---

## 3. Kulatý stůl 3 AI (Živá multi-round debata)

### Princip Kulatého stolu
Kulatý stůl simuluje poradní sbor tří specializovaných AI person s protichůdnými pohledy:
1. 💡 **Nova (Kreativec & Vizionář):**
   - Hledá neotřelá řešení, virální potenciál, moderní technologie roku 2026 a netradiční inovace.
   - Barva: *Neon Cyan / Purple*.
2. ⚖️ **Vortex (Kritik & Pragmatik):**
   - Hledá skrytá rizika, bezpečnostní slabiny, pasti, zbytečné náklady a zastaralé přístupy.
   - Barva: *Neon Coral*.
3. 📈 **Atlas (Architekt & Realizátor):**
   - Syntetizuje nápady Novy a výhrady Vortexe, navrhuje kompromisy, tvoří exekuční roadmapu a rozpadá cíl do konkrétních úkolů.
   - Barva: *Neon Emerald*.

### Jak spustit debatu:
1. Klepněte na tlačítko **🌟 Kulatý stůl (3 AI)** (v horní liště, ve spodním doku nebo v menu nástrojů).
2. **Výběr parametrů:**
   - **Téma debaty:** Předvyplněno z názvu aktuální mapy nebo vybraného uzlu, možnost zadat vlastní konkrétní otázku.
   - **Počet kol:** Rychlá volba pomocí tlačítek:
     - `1 kolo`: Rychlá výměna názorů a identifikace rizik.
     - `2 kola`: Hloubková polemika s protiargumenty.
     - `3 kola (Doporučeno)`: Kompletní cyklus Vize → Kritika → Syntéza.
     - `5 kol`: Zátěžový test komplexního ekosystému.
   - **Režim:** Výchozí je **rychlý textový chat**; volitelně lze zapnout hlasové předčítání (TTS).
3. **Živý průběh debaty:**
   - Sledujte živý chat argumentů kolo za kolem s vizuálními kartami každé persony.
   - Průběžně jsou do mapy automaticky doplňovány nově navržené myšlenky a uzly.
   - Po skončení se vygeneruje celkový report s technologickým auditem projektu.

---

## 4. AI Copilot Chat a volitelný hlasový režim

### Text-first filozofie
- Spodní dok na plátně obsahuje kompaktní vyhledávací pole: `"Napište dotaz pro AI rádce..."`.
- Klepnutím se otevře textový chat (`BrainstormCoPilotSheet`), kde můžete okamžitě psát na klávesnici.
- Žádný hlasový dialog vás neobtěžuje ani automaticky nespouští mikrofon.

### Přepnutí do hlasového režimu na vyžádání:
- V textovém poli i ve spodním doku je přítomna ikonka mikrofonu **🎙️**.
- Klepnutím na mikrofon se aktivuje nahrávání s vizuálním stavem:
  - Poslech řeči uživatele (Speech-to-Text).
  - Přepis se vloží do dotazu pro AI rádce.
  - Uživatel má plnou kontrolu nad tím, kdy chce mluvit a kdy psát.
- Jednotlivé zprávy v chatu lze na přání přehrát tlačítkem reproduktoru.

---

## 5. Režimy zobrazení (Mapa, Fáze, Matice 2x2)

V horní liště lze jedním klepnutím přepínat mezi 3 perspektivami:
1. 🗺️ **Mapa (Canvas):** Volné 2D plátno s grafem uzlů, Bézierovými vazbami a neomezeným zoomem.
2. 📊 **Fáze (Roadmap / Kanban):** Zobrazení uspořádané podle fází projektu (Návrh → MVP → Produkce → Škálování).
3. 🎯 **Matice 2x2 (Prioritizace):** Eisenhowerova matice dělící úkoly podle **Dopadu (Impact)** a **Úsilí (Effort)**:
   - *Rychlé výhry (Vysoký dopad / Nízké úsilí)*
   - *Strategické projekty (Vysoký dopad / Vysoké úsilí)*
   - *Doplňkové úkoly (Nízký dopad / Nízké úsilí)*
   - *Pasti a zbytečnosti (Nízký dopad / Vysoké úsilí)*

---

## 6. Vibe Coding & Generátor produkčního kódu

- **Persona Neo (Coding Architect):**
  - Převádí abstraktní myšlenkovou mapu do konkrétního spustitelného zdrojového kódu.
  - Vytváří kompletní architekturu složek, databázová schémata (SQL / NoSQL) a API endpointy.
  - Generuje pravidla pro moderní AI editory (`.cursorrules` pro Cursor IDE, GitHub Copilot prompty).
  - Připravuje terminálové příkazy pro instalaci závislostí a spuštění projektu.

---

## 7. Cloudová synchronizace & Online Webapp pro PC

### Obousměrná real-time synchronizace
- Každá myšlenková mapa a uzel jsou synchronizovány s Google Firebase Firestore.
- Jakákoli změna provedená v mobilní aplikaci se během stovek milisekund promítne do webové aplikace a naopak.

### Webová aplikace online (Cloud Run & GitHub Pages)
- **Live Cloud Run URL:**
  `https://ais-dev-c6bgj6btma22wrfphuygha-670500173981.europe-west1.run.app`
- **GitHub Pages nasazení:**
  Automatizováno přes GitHub Actions (`.github/workflows/deploy-webapp.yml`) do větve `gh-pages` na `https://insanebadpc.github.io/Craftmind/`.
- **Funkce webové aplikace:**
  - Vykreslování interaktivních uzlů s kategoriemi a barevnými schématy.
  - Přidávání, editace a mazání uzlů přímo na počítači myší a klávesnicí.
  - Přímé napojení na stejnou Firestore databázi.

---

## 8. Exporty, Prezentace a Verzování

- **Exportní formáty:**
  - 📊 **Mermaid.js:** Pro vložení interaktivního diagramu do GitHub repozitáře nebo dokumentace.
  - 📝 **Markdown:** Kompletní strukturovaný přehled s odrážkami a poznámkami.
  - 💾 **JSON:** Záloha pro přenositelnost a import do jiných nástrojů.
  - 🖼️ **SVG / PNG:** Vektorový i rastrový grafický export plátna.
- **Pitch Prezentace:** Režim na celou obrazovku se zvukovým přednesem jednotlivých uzlů pro prezentaci investorům nebo týmu.
- **Snapshoty verzí:** Možnost uložit bod obnovy před velkými experimenty a kdykoliv se k němu vrátit.

---

## 9. Průvodce komponentami a strukturou kódu

| Soubor / Modul | Účel a hlavní funkce |
|---|---|
| `MindMapScreen.kt` | Hlavní obrazovka, správa Scaffold, koordinace horní/spodní lišty a dialogů. |
| `MindMapViewModel.kt` | Centrální ViewModel, stav StateFlow, debounce ukládání, logika debaty a AI. |
| `MindMapCanvasView.kt` | Vykreslování grafu uzlů, Bézierových křivek a ovládacích prvků zoomu. |
| `ZoomablePanContainer.kt` | Gestická vrstva pro pinch-to-zoom (0.2×–5×) a plynulý 2D pan. |
| `AuditDebateDialog.kt` | Kulatý stůl 3 AI: konfigurátor kol (1–5), téma a živý chat debaty. |
| `BrainstormCoPilotSheet.kt` | AI Copilot Chat: textový chat s tlačítkem pro přepnutí do hlasového režimu. |
| `FirestoreSyncRepository.kt`| Datová vrstva pro obousměrný Firestore real-time listener a synchronizaci. |
| `web/public/index.html` | Samostatná webová aplikace pro PC s real-time připojením k Firestore. |
| `deploy-webapp.yml` | GitHub Actions workflow pro automatické publikování webu na GitHub Pages. |
