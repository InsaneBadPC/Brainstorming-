export interface ProjectCardProps { index: string; title: string; meta: string; variant?: "blue" | "wire"; onClick?: () => void; }
export function ProjectCard({ index, title, meta, variant = "wire", onClick }: ProjectCardProps) {
  return <button onClick={onClick} className={`project-card ${variant === "blue" ? "project-blue" : ""}`}><span className="project-no">{index}</span><div className={`project-shape ${variant === "blue" ? "shape-one" : "shape-two"}`}/><div className="relative z-10 mt-14 text-left"><p className="text-sm font-semibold">{title}</p><p className="mt-1 text-[10px] text-white/35">{meta}</p></div></button>;
}
