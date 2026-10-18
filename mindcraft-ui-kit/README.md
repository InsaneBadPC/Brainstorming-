# MindCraft UI Kit

Samostatný React + TypeScript UI balíček pro mobilní AI aplikaci. Neobsahuje externí obrázky ani ikonovou knihovnu.

## Požadavky
- React 18/19
- Tailwind CSS 4 (layout utility třídy)
- moderní prohlížeč s `backdrop-filter`, CSS mask a SVG filtry

## Integrace
1. Zkopírujte `src/components` do projektu.
2. Zkopírujte `src/mindcraft-ui.css` a importujte jej za Tailwindem.
3. Importujte prvek: `import { NeuralCanvas } from "./components"`.
4. Kompletní skladbu obrazovky najdete v `src/demo/MindCraftScreen.tsx`.

```tsx
import { useState } from "react";
import { NeuralCanvas, IdeaComposer } from "./components";
import "./mindcraft-ui.css";

export default function Screen() {
  const [loading, setLoading] = useState(false);
  return <><NeuralCanvas generating={loading}/><IdeaComposer onSubmit={() => setLoading(true)}/></>;
}
```

## Obsah
- `NeuralCanvas` — hlavní animovaný grafický objekt, síť, orbity a datové uzly.
- `IdeaComposer` — textový vstup, návrhové čipy, mikrofon a CTA.
- `ProjectCard` — dvě vizuální varianty karet.
- `BottomNav` — řízená čtyřpoložková mobilní navigace.
- `Brand` — značka s procedurální ikonou.
- `Icon` — lokální sada SVG ikon.

Podrobné rozměry a tokeny jsou v `docs/SPECIFIKACE.md`.
