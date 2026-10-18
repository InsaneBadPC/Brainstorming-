export function Brand({ onClick }: { onClick?: () => void } = {}) {
  return (
    <button
      type="button"
      onClick={onClick}
      className="brand flex items-center gap-2.5 text-left border-0 bg-transparent p-0 cursor-pointer"
      aria-label="MindCraft domů"
    >
      <span className="brand-mark">
        <i /><i /><i /><i />
      </span>
      <span className="text-[15px] font-semibold tracking-[-0.02em] text-white">MindCraft</span>
    </button>
  );
}
