import { Icon, type IconName } from "./Icon";
const items: [string, IconName][] = [["Tvořit","spark"],["Projekty","layers"],["Přehled","grid"],["Profil","user"]];
export function BottomNav({ active, onChange }: { active: string; onChange: (item: string) => void }) {
  return <nav className="bottom-nav">{items.map(([label,icon])=><button key={label} onClick={()=>onChange(label)} className={active===label?"active":""}><Icon name={icon} size={20}/><span>{label}</span></button>)}</nav>;
}
