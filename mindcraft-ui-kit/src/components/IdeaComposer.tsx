import { useState } from "react";
import { Icon } from "./Icon";

export function IdeaComposer({ onSubmit }: { onSubmit?: (idea: string) => void }) {
  const [idea, setIdea] = useState("");
  const submit = () => onSubmit?.(idea);
  return <div className="composer"><div className="flex items-center gap-3"><span className="composer-icon"><Icon name="spark" size={18}/></span><input value={idea} onChange={e=>setIdea(e.target.value)} onKeyDown={e=>e.key === "Enter" && submit()} placeholder="Jaký nápad dnes vytvoříme?"/><button className="mic-button" aria-label="Zadat hlasem"><Icon name="mic" size={19}/></button></div><div className="composer-divider"/><div className="flex items-center justify-between gap-3"><div className="flex min-w-0 gap-1.5 overflow-hidden">{["Mobilní aplikace","AI produkt","Nová služba"].map((x,i)=><button key={x} onClick={()=>setIdea(x)} className={`suggestion ${i===0?"selected":""}`}>{x}</button>)}</div><button onClick={submit} className="send-button"><Icon name="arrow" size={19}/></button></div></div>;
}
