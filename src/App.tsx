import { useState } from "react";
import {
  Brand,
  NeuralCanvas,
  IdeaComposer,
  ProjectCard,
  BottomNav,
  Icon,
} from "./components";

interface ProjectItem {
  id: string;
  index: string;
  title: string;
  meta: string;
  variant: "blue" | "wire";
  category: string;
  connections: number;
  description: string;
  nodes: { title: string; type: string }[];
}

const INITIAL_PROJECTS: ProjectItem[] = [
  {
    id: "p1",
    index: "01",
    title: "Future Health",
    meta: "12 propojení · dnes",
    variant: "blue",
    category: "AI produkt",
    connections: 12,
    description: "Autonomní diagnostický systém integrující biometrické senzory v reálném čase s neuronovou predikcí.",
    nodes: [
      { title: "Biometrická telemetrie", type: "Jádro" },
      { title: "Prediktivní analýza rizik", type: "Riziko" },
      { title: "Edge AI vyhodnocení", type: "Architektura" },
    ],
  },
  {
    id: "p2",
    index: "02",
    title: "Local Loop",
    meta: "7 propojení · včera",
    variant: "wire",
    category: "Mobilní aplikace",
    connections: 7,
    description: "Decentralizovaná komunitní síť pro sdílení zdrojů a lokální mikrologistiku s nulovou latencí.",
    nodes: [
      { title: "P2P Mesh protokol", type: "Architektura" },
      { title: "Geofencing zóny", type: "Funkcionalita" },
    ],
  },
  {
    id: "p3",
    index: "03",
    title: "Aura Agent",
    meta: "19 propojení · před 2 dny",
    variant: "blue",
    category: "AI produkt",
    connections: 19,
    description: "Multimodální osobní asistent s adaptivní hlasovou syntézou a kontextovou pamětí.",
    nodes: [
      { title: "Vektorová paměť", type: "Jádro" },
      { title: "Audio Waveform DSP", type: "Funkcionalita" },
    ],
  },
];

export default function App() {
  const [activeTab, setActiveTab] = useState("Tvořit");
  const [generating, setGenerating] = useState(false);
  const [isListening, setIsListening] = useState(false);
  const [credits, setCredits] = useState(8);
  const [projects, setProjects] = useState<ProjectItem[]>(INITIAL_PROJECTS);
  const [selectedProject, setSelectedProject] = useState<ProjectItem | null>(null);
  const [showNewProjectModal, setShowNewProjectModal] = useState(false);
  const [showCreditsModal, setShowCreditsModal] = useState(false);
  const [toastMessage, setToastMessage] = useState<string | null>(null);
  const [projectFilter, setProjectFilter] = useState("Všechny");
  const [newProjectTitle, setNewProjectTitle] = useState("");
  const [newProjectCategory, setNewProjectCategory] = useState("AI produkt");
  const [selectedAiModel, setSelectedAiModel] = useState("Claude 3.7 Sonnet");

  const showToast = (msg: string) => {
    setToastMessage(msg);
    setTimeout(() => setToastMessage(null), 3200);
  };

  const handleCreateIdea = (ideaText: string) => {
    if (!ideaText.trim()) return;
    setGenerating(true);
    if (credits > 0) setCredits((c) => c - 1);

    setTimeout(() => {
      setGenerating(false);
      const newProj: ProjectItem = {
        id: `p_${Date.now()}`,
        index: String(projects.length + 1).padStart(2, "0"),
        title: ideaText.slice(0, 24),
        meta: `3 propojení · právě teď`,
        variant: projects.length % 2 === 0 ? "blue" : "wire",
        category: "Nová vize",
        connections: 3,
        description: `Generativní koncept odvozený z podnětu: "${ideaText}". Plně namodelováno na plátně.`,
        nodes: [
          { title: "Syntetizované jádro", type: "Jádro" },
          { title: "Tržní validace", type: "Potenciál" },
          { title: "Architektonický návrh", type: "Architektura" },
        ],
      };
      setProjects((prev) => [newProj, ...prev]);
      showToast(`✨ Nápad „${newProj.title}“ syntetizován na plátně!`);
    }, 1600);
  };

  const handleCreateNewProject = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newProjectTitle.trim()) return;
    const newProj: ProjectItem = {
      id: `p_${Date.now()}`,
      index: String(projects.length + 1).padStart(2, "0"),
      title: newProjectTitle.trim(),
      meta: `1 propojení · dnes`,
      variant: projects.length % 2 === 0 ? "blue" : "wire",
      category: newProjectCategory,
      connections: 1,
      description: `Projekt vytvořený uživatelem v kategorii ${newProjectCategory}.`,
      nodes: [{ title: newProjectTitle.trim(), type: "Jádro" }],
    };
    setProjects((prev) => [newProj, ...prev]);
    setNewProjectTitle("");
    setShowNewProjectModal(false);
    showToast(`Nový projekt „${newProj.title}“ byl vytvořen.`);
  };

  const filteredProjects = projects.filter((p) => {
    if (projectFilter === "Všechny") return true;
    if (projectFilter === "Aktivní") return p.connections > 5;
    if (projectFilter === "AI Koncepty") return p.category.includes("AI");
    return true;
  });

  return (
    <main className="app-bg min-h-screen text-white select-none">
      <div className="noise" />

      {/* Floating Toast Notification */}
      {toastMessage && (
        <div className="fixed top-4 left-1/2 -translate-x-1/2 z-50 px-4 py-2.5 rounded-full bg-[#0d1629]/95 border border-[#3b82f6]/50 shadow-[0_0_25px_rgba(36,105,255,0.4)] text-[12px] font-semibold text-white backdrop-blur-md flex items-center gap-2 animate-bounce">
          <span className="w-2 h-2 rounded-full bg-[#3978ff] shadow-[0_0_8px_#3978ff]" />
          <span>{toastMessage}</span>
        </div>
      )}

      <div className="app mx-auto min-h-screen w-full max-w-[460px] overflow-hidden flex flex-col justify-between">
        <div>
          {/* Header (Specification: ~49px height, 20px padding) */}
          <header className="relative z-20 flex items-center justify-between px-5 pb-3 pt-5">
            <Brand onClick={() => setActiveTab("Tvořit")} />
            <div className="flex items-center gap-2">
              <button
                type="button"
                onClick={() => setShowCreditsModal(true)}
                className="credit-pill cursor-pointer hover:border-blue-500/40 transition-colors"
                aria-label="Stav kreditů"
              >
                <span className="credit-orb" /> {credits} kreditů
              </button>
              <button
                type="button"
                onClick={() => setActiveTab("Profil")}
                className="avatar-button cursor-pointer hover:border-blue-400 transition-colors"
                aria-label="Uživatelský profil"
              >
                MK
              </button>
            </div>
          </header>

          {/* TAB 1: TVOŘIT (AI IDEA LAB) */}
          {activeTab === "Tvořit" && (
            <div>
              {/* Hero Section */}
              <section className="relative px-5 pt-3">
                <div className="mb-4 flex items-end justify-between">
                  <div>
                    <p className="eyebrow">AI IDEA LAB / 01</p>
                    <h1 className="mt-1.5 max-w-[310px] text-[38px] font-medium leading-[1.02] tracking-[-0.055em]">
                      Dejte nápadům<br />
                      <span className="title-shine">nový rozměr.</span>
                    </h1>
                  </div>
                  <span className="live-pill">
                    <i /> Živě
                  </span>
                </div>

                {/* Neural Canvas Core */}
                <NeuralCanvas generating={generating} />
              </section>

              {/* Idea Composer (Overlaps Neural Canvas with -mt-1) */}
              <section className="relative z-10 -mt-1 px-5">
                <IdeaComposer
                  onSubmit={handleCreateIdea}
                  isListening={isListening}
                  onMicToggle={() => {
                    const next = !isListening;
                    setIsListening(next);
                    if (next) showToast("🎙️ Hlasový vstup aktivován...");
                  }}
                />
              </section>

              {/* In-Progress Worlds (Rozpracované světy) */}
              <section className="px-5 pb-28 pt-6">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="eyebrow">POKRAČOVAT</p>
                    <h2 className="mt-1 text-lg font-medium tracking-[-0.035em]">
                      Rozpracované světy
                    </h2>
                  </div>
                  <button
                    type="button"
                    onClick={() => setShowNewProjectModal(true)}
                    className="round-add cursor-pointer hover:border-blue-500/50 hover:text-white transition-all"
                    aria-label="Přidat projekt"
                  >
                    <Icon name="plus" size={18} />
                  </button>
                </div>

                <div className="mt-3 grid grid-cols-2 gap-2.5">
                  {projects.slice(0, 4).map((p) => (
                    <ProjectCard
                      key={p.id}
                      index={p.index}
                      title={p.title}
                      meta={p.meta}
                      variant={p.variant}
                      onClick={() => setSelectedProject(p)}
                    />
                  ))}
                </div>
              </section>
            </div>
          )}

          {/* TAB 2: PROJEKTY */}
          {activeTab === "Projekty" && (
            <div className="px-5 pt-3 pb-28">
              <div className="mb-4 flex items-center justify-between">
                <div>
                  <p className="eyebrow">PORTFOLIO / ARCHIV</p>
                  <h1 className="mt-1 text-2xl font-semibold tracking-[-0.04em]">
                    Všechny projekty
                  </h1>
                </div>
                <button
                  type="button"
                  onClick={() => setShowNewProjectModal(true)}
                  className="round-add cursor-pointer hover:border-blue-500/50 hover:text-white transition-all"
                  aria-label="Nový projekt"
                >
                  <Icon name="plus" size={18} />
                </button>
              </div>

              {/* Filter Chips */}
              <div className="flex gap-1.5 mb-4 overflow-x-auto no-scrollbar py-1">
                {["Všechny", "Aktivní", "AI Koncepty"].map((f) => (
                  <button
                    key={f}
                    type="button"
                    onClick={() => setProjectFilter(f)}
                    className={`suggestion cursor-pointer ${projectFilter === f ? "selected" : ""}`}
                  >
                    {f}
                  </button>
                ))}
              </div>

              {/* Projects Grid */}
              <div className="grid grid-cols-2 gap-2.5">
                {filteredProjects.map((p) => (
                  <ProjectCard
                    key={p.id}
                    index={p.index}
                    title={p.title}
                    meta={p.meta}
                    variant={p.variant}
                    onClick={() => setSelectedProject(p)}
                  />
                ))}
              </div>
            </div>
          )}

          {/* TAB 3: PŘEHLED (METRICS & NEURAL ANALYTICS) */}
          {activeTab === "Přehled" && (
            <div className="px-5 pt-3 pb-28">
              <div className="mb-4">
                <p className="eyebrow">TELEMETRIE / PŘEHLED</p>
                <h1 className="mt-1 text-2xl font-semibold tracking-[-0.04em]">
                  Neuronová aktivita
                </h1>
              </div>

              {/* 4 Metrics Grid */}
              <div className="grid grid-cols-2 gap-2.5 mb-5">
                <div className="p-3.5 rounded-2xl bg-[#090e1b] border border-white/5">
                  <span className="text-[10px] text-[#607391] font-mono block">SYNTÉZY</span>
                  <span className="text-xl font-bold text-white mt-1 block">142</span>
                  <span className="text-[9px] text-blue-400 mt-0.5 block">+18% tento týden</span>
                </div>
                <div className="p-3.5 rounded-2xl bg-[#090e1b] border border-white/5">
                  <span className="text-[10px] text-[#607391] font-mono block">SYNERGIE</span>
                  <span className="text-xl font-bold text-white mt-1 block">89%</span>
                  <span className="text-[9px] text-emerald-400 mt-0.5 block">Vysoká přesnost</span>
                </div>
                <div className="p-3.5 rounded-2xl bg-[#090e1b] border border-white/5">
                  <span className="text-[10px] text-[#607391] font-mono block">KREDITY</span>
                  <span className="text-xl font-bold text-white mt-1 block">{credits}</span>
                  <span className="text-[9px] text-[#8992a8] mt-0.5 block">z 50 dostupných</span>
                </div>
                <div className="p-3.5 rounded-2xl bg-[#090e1b] border border-white/5">
                  <span className="text-[10px] text-[#607391] font-mono block">MODELY</span>
                  <span className="text-xl font-bold text-white mt-1 block">3</span>
                  <span className="text-[9px] text-purple-400 mt-0.5 block">Kulatý stůl aktivní</span>
                </div>
              </div>

              {/* Neural Activity Graph Box */}
              <div className="p-4 rounded-2xl bg-gradient-to-b from-[#0c1428] to-[#060a14] border border-blue-500/20 mb-4">
                <div className="flex justify-between items-center mb-3">
                  <span className="text-[11px] font-semibold text-blue-200">Aktivní clustery</span>
                  <span className="text-[9px] font-mono text-[#607391]">MC-DSP 60 FPS</span>
                </div>
                <div className="h-24 flex items-end justify-between gap-1.5 px-2">
                  {[28, 45, 72, 38, 90, 64, 82, 50, 95, 78, 60, 88].map((val, i) => (
                    <div key={i} className="flex-1 flex flex-col items-center gap-1">
                      <div
                        className="w-full rounded-t bg-gradient-to-t from-blue-600 to-blue-400 shadow-[0_0_6px_#3978ff]"
                        style={{ height: `${val}%` }}
                      />
                    </div>
                  ))}
                </div>
                <div className="flex justify-between text-[8px] font-mono text-[#536079] mt-2 px-1">
                  <span>00:00</span>
                  <span>12:00</span>
                  <span>24:00</span>
                </div>
              </div>
            </div>
          )}

          {/* TAB 4: PROFIL (SETTINGS & AI MODEL CONFIG) */}
          {activeTab === "Profil" && (
            <div className="px-5 pt-3 pb-28">
              <div className="mb-4">
                <p className="eyebrow">NASTAVENÍ / PROFIL</p>
                <h1 className="mt-1 text-2xl font-semibold tracking-[-0.04em]">
                  Můj účet
                </h1>
              </div>

              {/* User Bio Card */}
              <div className="p-4 rounded-2xl bg-[#090e1b] border border-white/5 flex items-center gap-3.5 mb-4">
                <div className="w-12 h-12 rounded-xl bg-gradient-to-br from-blue-600 to-blue-900 border border-blue-400/40 flex items-center justify-center text-sm font-bold text-white shadow-[0_0_15px_rgba(36,105,255,0.4)]">
                  MK
                </div>
                <div>
                  <h3 className="text-sm font-semibold text-white">Martin Kovář</h3>
                  <p className="text-[11px] text-[#607391]">AI Lead Designer & Architect</p>
                  <p className="text-[9px] text-blue-400 font-mono mt-0.5">ID: MC-STUDIO-8443</p>
                </div>
              </div>

              {/* AI Model Selector */}
              <div className="p-4 rounded-2xl bg-[#090e1b] border border-white/5 mb-4">
                <span className="text-[11px] font-semibold text-white block mb-2">
                  Primární generativní model
                </span>
                <div className="space-y-1.5">
                  {["Claude 3.7 Sonnet", "Gemini 2.5 Flash", "GPT-4o Omnimodel"].map((mod) => (
                    <button
                      key={mod}
                      type="button"
                      onClick={() => {
                        setSelectedAiModel(mod);
                        showToast(`Aktivován model: ${mod}`);
                      }}
                      className={`w-full p-2.5 rounded-xl text-left text-xs font-medium flex items-center justify-between border transition-all ${
                        selectedAiModel === mod
                          ? "bg-blue-600/15 border-blue-500/50 text-white shadow-[0_0_12px_rgba(36,105,255,0.25)]"
                          : "bg-white/[0.02] border-white/5 text-[#8992a8] hover:text-white"
                      }`}
                    >
                      <span>{mod}</span>
                      {selectedAiModel === mod && (
                        <span className="w-2 h-2 rounded-full bg-blue-400 shadow-[0_0_6px_#60a5fa]" />
                      )}
                    </button>
                  ))}
                </div>
              </div>

              {/* Cloud Sync Status */}
              <div className="p-4 rounded-2xl bg-[#090e1b] border border-white/5 flex items-center justify-between mb-4">
                <div>
                  <span className="text-xs font-semibold text-white block">Real-time Cloud Sync</span>
                  <span className="text-[10px] text-[#607391]">Automatické zálohy myšlenkových map</span>
                </div>
                <span className="px-2.5 py-1 rounded-full text-[9px] font-mono bg-emerald-500/15 text-emerald-400 border border-emerald-500/30">
                  AKTIVNÍ
                </span>
              </div>
            </div>
          )}
        </div>

        {/* Specification BottomNav (Fixed 12px from edge, max 436px) */}
        <BottomNav active={activeTab} onChange={setActiveTab} />
      </div>

      {/* MODAL: Project Detail View */}
      {selectedProject && (
        <div className="fixed inset-0 z-50 bg-black/80 backdrop-blur-md flex items-center justify-center p-4">
          <div className="w-full max-w-[420px] rounded-[24px] bg-[#0a0f1d] border border-blue-500/30 p-5 shadow-[0_25px_60px_rgba(0,0,0,0.9),0_0_40px_rgba(36,105,255,0.25)]">
            <div className="flex items-center justify-between mb-3">
              <span className="text-[10px] font-mono text-blue-400 font-bold uppercase tracking-wider">
                Projekt #{selectedProject.index} · {selectedProject.category}
              </span>
              <button
                type="button"
                onClick={() => setSelectedProject(null)}
                className="w-7 h-7 rounded-lg bg-white/5 hover:bg-white/10 text-white flex items-center justify-center text-sm"
              >
                ✕
              </button>
            </div>

            <h2 className="text-xl font-bold text-white mb-2">{selectedProject.title}</h2>
            <p className="text-xs text-[#8992a8] leading-relaxed mb-4">
              {selectedProject.description}
            </p>

            <div className="mb-4">
              <span className="text-[10px] font-mono text-[#536079] uppercase tracking-wider block mb-2">
                Syntetizované uzly ({selectedProject.nodes.length})
              </span>
              <div className="space-y-1.5">
                {selectedProject.nodes.map((node, i) => (
                  <div
                    key={i}
                    className="p-2.5 rounded-xl bg-white/[0.03] border border-white/5 flex items-center justify-between text-xs"
                  >
                    <span className="text-white font-medium">{node.title}</span>
                    <span className="text-[9px] font-mono px-2 py-0.5 rounded bg-blue-600/20 text-blue-300 border border-blue-500/30">
                      {node.type}
                    </span>
                  </div>
                ))}
              </div>
            </div>

            <div className="flex gap-2">
              <button
                type="button"
                onClick={() => {
                  navigator.clipboard.writeText(`# ${selectedProject.title}\n${selectedProject.description}`);
                  showToast("Zkopírováno do schránky pro Cursor AI!");
                  setSelectedProject(null);
                }}
                className="flex-1 py-2.5 rounded-xl bg-blue-600 hover:bg-blue-500 text-white text-xs font-bold transition-all shadow-[0_0_15px_rgba(36,105,255,0.4)]"
              >
                Export pro Cursor AI
              </button>
              <button
                type="button"
                onClick={() => setSelectedProject(null)}
                className="px-4 py-2.5 rounded-xl bg-white/5 hover:bg-white/10 text-[#8992a8] text-xs font-semibold"
              >
                Zavřít
              </button>
            </div>
          </div>
        </div>
      )}

      {/* MODAL: New Project Creation */}
      {showNewProjectModal && (
        <div className="fixed inset-0 z-50 bg-black/80 backdrop-blur-md flex items-center justify-center p-4">
          <form
            onSubmit={handleCreateNewProject}
            className="w-full max-w-[420px] rounded-[24px] bg-[#0a0f1d] border border-blue-500/30 p-5 shadow-[0_25px_60px_rgba(0,0,0,0.9)]"
          >
            <div className="flex items-center justify-between mb-3">
              <span className="text-[10px] font-mono text-blue-400 font-bold uppercase tracking-wider">
                Nový svět myšlenek
              </span>
              <button
                type="button"
                onClick={() => setShowNewProjectModal(false)}
                className="w-7 h-7 rounded-lg bg-white/5 hover:bg-white/10 text-white flex items-center justify-center text-sm"
              >
                ✕
              </button>
            </div>

            <div className="mb-3">
              <label className="text-[10px] font-mono text-[#536079] uppercase tracking-wider block mb-1">
                Název projektu
              </label>
              <input
                type="text"
                value={newProjectTitle}
                onChange={(e) => setNewProjectTitle(e.target.value)}
                placeholder="Např. NeuroStream OS"
                className="w-full p-2.5 rounded-xl bg-white/5 border border-white/10 text-xs text-white outline-none focus:border-blue-500"
                autoFocus
              />
            </div>

            <div className="mb-4">
              <label className="text-[10px] font-mono text-[#536079] uppercase tracking-wider block mb-1">
                Kategorie
              </label>
              <select
                value={newProjectCategory}
                onChange={(e) => setNewProjectCategory(e.target.value)}
                className="w-full p-2.5 rounded-xl bg-[#0e1629] border border-white/10 text-xs text-white outline-none focus:border-blue-500"
              >
                <option value="AI produkt">AI produkt</option>
                <option value="Mobilní aplikace">Mobilní aplikace</option>
                <option value="Nová služba">Nová služba</option>
                <option value="Výzkum & Architektura">Výzkum & Architektura</option>
              </select>
            </div>

            <div className="flex gap-2">
              <button
                type="submit"
                className="flex-1 py-2.5 rounded-xl bg-blue-600 hover:bg-blue-500 text-white text-xs font-bold transition-all shadow-[0_0_15px_rgba(36,105,255,0.4)]"
              >
                Vytvořit projekt
              </button>
              <button
                type="button"
                onClick={() => setShowNewProjectModal(false)}
                className="px-4 py-2.5 rounded-xl bg-white/5 hover:bg-white/10 text-[#8992a8] text-xs font-semibold"
              >
                Zrušit
              </button>
            </div>
          </form>
        </div>
      )}

      {/* MODAL: Credits Info */}
      {showCreditsModal && (
        <div className="fixed inset-0 z-50 bg-black/80 backdrop-blur-md flex items-center justify-center p-4">
          <div className="w-full max-w-[420px] rounded-[24px] bg-[#0a0f1d] border border-blue-500/30 p-5 shadow-[0_25px_60px_rgba(0,0,0,0.9)]">
            <div className="flex items-center justify-between mb-3">
              <span className="text-[10px] font-mono text-blue-400 font-bold uppercase tracking-wider">
                Kreditní peněženka
              </span>
              <button
                type="button"
                onClick={() => setShowCreditsModal(false)}
                className="w-7 h-7 rounded-lg bg-white/5 hover:bg-white/10 text-white flex items-center justify-center text-sm"
              >
                ✕
              </button>
            </div>

            <div className="text-center py-4">
              <span className="text-4xl font-extrabold text-white block mb-1">{credits}</span>
              <span className="text-xs text-[#8992a8]">Zbývající generativní kredity</span>
            </div>

            <p className="text-xs text-[#607391] leading-relaxed mb-4">
              Každé rozvinutí nového nápadu na plátně nebo debata u kulatého stolu spotřebuje 1 kredit. V rámci beta testování se kredity obnovují denně.
            </p>

            <button
              type="button"
              onClick={() => {
                setCredits((c) => c + 10);
                showToast("🎉 Přidáno +10 testovacích kreditů!");
                setShowCreditsModal(false);
              }}
              className="w-full py-2.5 rounded-xl bg-blue-600 hover:bg-blue-500 text-white text-xs font-bold transition-all shadow-[0_0_15px_rgba(36,105,255,0.4)]"
            >
              Dobít +10 kreditů zdarma
            </button>
          </div>
        </div>
      )}
    </main>
  );
}
