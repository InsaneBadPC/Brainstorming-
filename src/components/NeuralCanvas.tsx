export function NeuralCanvas({ generating = false }: { generating?: boolean }) {
  return <div className={`neural-stage ${generating ? "is-generating" : ""}`}>
    <div className="stage-top"><span>GENERATIVNÍ PLÁTNO</span><span className="stage-index">MC—0824</span></div>
    <svg className="network-lines" viewBox="0 0 400 310" preserveAspectRatio="none"><defs><linearGradient id="line" x1="0" y1="0" x2="1" y2="1"><stop stopColor="#0c47ff" stopOpacity=".05"/><stop offset=".5" stopColor="#69a5ff" stopOpacity=".8"/><stop offset="1" stopColor="#00d9ff" stopOpacity=".08"/></linearGradient><filter id="glow"><feGaussianBlur stdDeviation="2.2" result="b"/><feMerge><feMergeNode in="b"/><feMergeNode in="SourceGraphic"/></feMerge></filter></defs><path d="M22 78 C92 35 120 122 198 142 S300 70 382 97"/><path d="M19 230 C87 172 124 238 199 166 S311 222 388 184"/><path d="M64 20 C102 86 152 71 198 142 S265 252 337 282"/><path d="M352 24 C308 72 291 116 201 162 S95 255 42 286"/><circle cx="62" cy="64" r="3"/><circle cx="340" cy="78" r="3"/><circle cx="67" cy="241" r="3"/><circle cx="340" cy="222" r="3"/></svg>
    <div className="orbit orbit-a"/><div className="orbit orbit-b"/>
    <div className="core"><div className="core-mesh"/><div className="core-light"/><div className="core-label"><span>{generating ? "TVARUJI" : "JÁDRO"}</span><b>{generating ? "···" : "01"}</b></div></div>
    <div className="data-node node-a"><i/> PROBLÉM <b>84%</b></div><div className="data-node node-b"><i/> POTENCIÁL <b>92%</b></div><div className="data-node node-c"><i/> TRH <b>68%</b></div>
    <div className="stage-bottom"><span>3 aktivní směry</span><div className="micro-bars">{[4,8,13,7,16,10,5].map((h,i)=><i key={i} style={{height:h}}/>)}</div></div>
  </div>;
}
