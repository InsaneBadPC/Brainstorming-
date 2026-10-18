import { useState } from "react";

type IconName = "grid" | "spark" | "layers" | "user" | "arrow" | "mic" | "plus";

function Icon({ name, size = 20 }: { name: IconName; size?: number }) {
  const paths: Record<IconName, React.ReactNode> = {
    grid: <><rect x="3" y="3" width="7" height="7" rx="2" /><rect x="14" y="3" width="7" height="7" rx="2" /><rect x="3" y="14" width="7" height="7" rx="2" /><rect x="14" y="14" width="7" height="7" rx="2" /></>,
    spark: <path d="M12 2l1.7 6.3L20 10l-6.3 1.7L12 18l-1.7-6.3L4 10l6.3-1.7L12 2zm7 14 .7 2.3L22 19l-2.3.7L19 22l-.7-2.3L16 19l2.3-.7L19 16z" />,
    layers: <><path d="m12 3 9 5-9 5-9-5 9-5z" /><path d="m3 12 9 5 9-5M3 16l9 5 9-5" /></>,
    user: <><circle cx="12" cy="8" r="4" /><path d="M4 21a8 8 0 0 1 16 0" /></>,
    arrow: <><path d="M5 12h14M14 7l5 5-5 5" /></>,
    mic: <><rect x="9" y="3" width="6" height="12" rx="3" /><path d="M5 11a7 7 0 0 0 14 0M12 18v3" /></>,
    plus: <path d="M12 5v14M5 12h14" />,
  };
  return <svg width={size} height={size} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">{paths[name]}</svg>;
}

const suggestions = ["Mobilní aplikace", "AI produkt", "Nová služba"];

export default function App() {
  const [idea, setIdea] = useState("");
  const [active, setActive] = useState("Tvořit");
  const [generating, setGenerating] = useState(false);

  const createIdea = () => {
    setGenerating(true);
    window.setTimeout(() => setGenerating(false), 1800);
  };

  return (
    <main className="app-bg min-h-screen text-white">
      <div className="noise" />
      <div className="app mx-auto min-h-screen w-full max-w-[460px] overflow-hidden">
        <header className="relative z-20 flex items-center justify-between px-5 pb-3 pt-5">
          <button className="brand flex items-center gap-2.5" aria-label="MindCraft domů">
            <span className="brand-mark"><i /><i /><i /><i /></span>
            <span className="text-[15px] font-semibold tracking-[-0.02em]">MindCraft</span>
          </button>
          <div className="flex items-center gap-2">
            <button className="credit-pill"><span className="credit-orb" /> 8 kreditů</button>
            <button className="avatar-button">MK</button>
          </div>
        </header>

        <section className="relative px-5 pt-5">
          <div className="mb-5 flex items-end justify-between">
            <div>
              <p className="eyebrow">AI IDEA LAB / 01</p>
              <h1 className="mt-2 max-w-[310px] text-[38px] font-medium leading-[1.02] tracking-[-0.055em]">
                Dejte nápadům<br />
                <span className="title-shine">nový rozměr.</span>
              </h1>
            </div>
            <span className="live-pill"><i /> Živě</span>
          </div>

          <div className={`neural-stage ${generating ? "is-generating" : ""}`}>
            <div className="stage-top">
              <span>GENERATIVNÍ PLÁTNO</span>
              <span className="stage-index">MC—0824</span>
            </div>

            <svg className="network-lines" viewBox="0 0 400 310" preserveAspectRatio="none">
              <defs>
                <linearGradient id="line" x1="0" y1="0" x2="1" y2="1">
                  <stop stopColor="#0c47ff" stopOpacity=".05" />
                  <stop offset=".5" stopColor="#69a5ff" stopOpacity=".8" />
                  <stop offset="1" stopColor="#00d9ff" stopOpacity=".08" />
                </linearGradient>
                <filter id="glow"><feGaussianBlur stdDeviation="2.2" result="b" /><feMerge><feMergeNode in="b" /><feMergeNode in="SourceGraphic" /></feMerge></filter>
              </defs>
              <path d="M22 78 C92 35 120 122 198 142 S300 70 382 97" />
              <path d="M19 230 C87 172 124 238 199 166 S311 222 388 184" />
              <path d="M64 20 C102 86 152 71 198 142 S265 252 337 282" />
              <path d="M352 24 C308 72 291 116 201 162 S95 255 42 286" />
              <circle cx="62" cy="64" r="3" /><circle cx="340" cy="78" r="3" />
              <circle cx="67" cy="241" r="3" /><circle cx="340" cy="222" r="3" />
            </svg>

            <div className="orbit orbit-a" />
            <div className="orbit orbit-b" />
            <div className="core">
              <div className="core-mesh" />
              <div className="core-light" />
              <div className="core-label">
                <span>{generating ? "TVARUJI" : "JÁDRO"}</span>
                <b>{generating ? "···" : "01"}</b>
              </div>
            </div>

            <div className="data-node node-a"><i /> PROBLÉM <b>84%</b></div>
            <div className="data-node node-b"><i /> POTENCIÁL <b>92%</b></div>
            <div className="data-node node-c"><i /> TRH <b>68%</b></div>

            <div className="stage-bottom">
              <span>3 aktivní směry</span>
              <div className="micro-bars">{[4, 8, 13, 7, 16, 10, 5].map((h, i) => <i key={i} style={{ height: h }} />)}</div>
            </div>
          </div>
        </section>

        <section className="relative z-10 -mt-1 px-5">
          <div className="composer">
            <div className="flex items-center gap-3">
              <span className="composer-icon"><Icon name="spark" size={18} /></span>
              <input
                value={idea}
                onChange={(event) => setIdea(event.target.value)}
                onKeyDown={(event) => event.key === "Enter" && createIdea()}
                placeholder="Jaký nápad dnes vytvoříme?"
                aria-label="Popište svůj nápad"
              />
              <button className="mic-button" aria-label="Zadat hlasem"><Icon name="mic" size={19} /></button>
            </div>
            <div className="composer-divider" />
            <div className="flex items-center justify-between gap-3">
              <div className="flex min-w-0 gap-1.5 overflow-hidden">
                {suggestions.map((item, index) => (
                  <button key={item} onClick={() => setIdea(item)} className={`suggestion ${index === 0 ? "selected" : ""}`}>{item}</button>
                ))}
              </div>
              <button onClick={createIdea} className="send-button" aria-label="Vytvořit nápad"><Icon name="arrow" size={19} /></button>
            </div>
          </div>
        </section>

        <section className="px-5 pb-28 pt-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="eyebrow">POKRAČOVAT</p>
              <h2 className="mt-1 text-lg font-medium tracking-[-0.035em]">Rozpracované světy</h2>
            </div>
            <button className="round-add"><Icon name="plus" size={18} /></button>
          </div>
          <div className="mt-3 grid grid-cols-2 gap-2.5">
            <button className="project-card project-blue">
              <span className="project-no">01</span>
              <div className="project-shape shape-one" />
              <div className="relative z-10 mt-14 text-left">
                <p className="text-sm font-semibold">Future Health</p>
                <p className="mt-1 text-[10px] text-blue-200/60">12 propojení · dnes</p>
              </div>
            </button>
            <button className="project-card">
              <span className="project-no">02</span>
              <div className="project-shape shape-two" />
              <div className="relative z-10 mt-14 text-left">
                <p className="text-sm font-semibold">Local Loop</p>
                <p className="mt-1 text-[10px] text-white/35">7 propojení · včera</p>
              </div>
            </button>
          </div>
        </section>

        <nav className="bottom-nav">
          {[
            ["Tvořit", "spark" as IconName],
            ["Projekty", "layers" as IconName],
            ["Přehled", "grid" as IconName],
            ["Profil", "user" as IconName],
          ].map(([label, icon]) => (
            <button key={label} onClick={() => setActive(label)} className={active === label ? "active" : ""}>
              <Icon name={icon} size={20} />
              <span>{label}</span>
            </button>
          ))}
        </nav>
      </div>
    </main>
  );
}
