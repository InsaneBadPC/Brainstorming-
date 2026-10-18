import { useState } from "react";
import { Icon } from "./Icon";

export interface IdeaComposerProps {
  onSubmit?: (idea: string) => void;
  placeholder?: string;
  suggestions?: string[];
  isListening?: boolean;
  onMicToggle?: () => void;
}

export function IdeaComposer({
  onSubmit,
  placeholder = "Jaký nápad dnes vytvoříme?",
  suggestions = ["Mobilní aplikace", "AI produkt", "Nová služba"],
  isListening = false,
  onMicToggle,
}: IdeaComposerProps) {
  const [idea, setIdea] = useState("");
  const [selectedSug, setSelectedSug] = useState(suggestions[0] || "");

  const handleSubmit = () => {
    if (!idea.trim()) return;
    onSubmit?.(idea);
    setIdea("");
  };

  const handleSelectSuggestion = (sug: string) => {
    setSelectedSug(sug);
    setIdea(sug);
  };

  return (
    <div className="composer">
      <div className="flex items-center gap-3">
        <span className="composer-icon">
          <Icon name="spark" size={18} />
        </span>
        <input
          value={idea}
          onChange={(e) => setIdea(e.target.value)}
          onKeyDown={(e) => e.key === "Enter" && handleSubmit()}
          placeholder={isListening ? "Naslouchám... mluvte prosím" : placeholder}
          aria-label="Popište svůj nápad"
          className="min-h-[44px]"
        />
        <button
          type="button"
          onClick={onMicToggle}
          className={`mic-button ${isListening ? "bg-blue-600/40 text-blue-300 border-blue-500/50 animate-pulse" : ""}`}
          aria-label={isListening ? "Zastavit nahrávání" : "Zadat hlasem"}
        >
          <Icon name="mic" size={19} />
        </button>
      </div>
      <div className="composer-divider" />
      <div className="flex items-center justify-between gap-3">
        <div className="flex min-w-0 gap-1.5 overflow-x-auto no-scrollbar py-0.5">
          {suggestions.map((sug) => (
            <button
              key={sug}
              type="button"
              onClick={() => handleSelectSuggestion(sug)}
              className={`suggestion ${selectedSug === sug ? "selected" : ""}`}
            >
              {sug}
            </button>
          ))}
        </div>
        <button
          type="button"
          onClick={handleSubmit}
          className="send-button min-w-[32px] min-h-[32px] cursor-pointer"
          aria-label="Vytvořit nápad"
        >
          <Icon name="arrow" size={19} />
        </button>
      </div>
    </div>
  );
}
