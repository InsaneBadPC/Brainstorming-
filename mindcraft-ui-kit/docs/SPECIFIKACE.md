# Specifikace rozložení a prvků

## Základ obrazovky
- Referenční viewport: **390 × 844 px**; podporováno od 320 px.
- Maximální šířka aplikace: **460 px**.
- Horizontální okraj obsahu: **20 px**.
- Základní pozadí: `#03050B`; povrch: `#080D19`.
- Primární modrá: `#2469FF`; světlý akcent: `#8DB5FF`.
- Písmo: Manrope, technické popisky DM Mono.

## Header
- Výška přibližně 49 px, odsazení 20 px.
- Logo 30 × 30 px; avatar 34 × 34 px; credit pill výška 31 px.
- Doporučená bezpečná zóna nahoře: `env(safe-area-inset-top)` + 12 px v nativním wrapperu.

## Hero nadpis
- 38 px / line-height 1.02 / tracking -5.5 %.
- Eyebrow 8 px, uppercase, tracking 0.18 em.

## NeuralCanvas
- Rozměr: šířka kontejneru × **320 px**.
- Radius: **30 px**; border 1 px.
- Jádro: 114 × 114 px, střed `left: 50%; top: 52%`.
- Orbit A: 226 × 215 px; Orbit B: 186 × 250 px.
- Datové uzly: 6 px mono text, padding 6 × 8 px.
- Stav `generating` zrychlí morphing a spustí světelný burst.
- Síť je inline SVG ve viewBoxu 400 × 310, takže se plynule škáluje.

## IdeaComposer
- Překrývá spodní hranu plátna pomocí záporného marginu v rodiči.
- Radius 22 px; vnitřní odsazení 12 px.
- Hlavní ikona 35 × 35 px; mikrofon 34 × 34 px; CTA 32 × 32 px.
- Input má min. doporučenou dotykovou oblast 44 px při nasazení do nativní aplikace.
- `onSubmit(idea)` vrací aktuální text rodiči.

## ProjectCard
- Grid 2 sloupce, mezera 10 px.
- Minimální výška 126 px; radius 19 px; padding 12 px.
- `variant="blue"` vykreslí zářící organický objekt, `wire` technickou drátěnou variantu.

## BottomNav
- Fixní, 12 px od bočních hran, max. 436 px.
- Spodní odsazení 10 px; pro iOS použijte `calc(10px + env(safe-area-inset-bottom))`.
- Radius 21 px; blur 22 px.
- 4 položky po 70 px; aktivní stav má horní 24 × 2 px indikátor.

## Vrstvy
1. Pozadí aplikace
2. Dekorativní síť a grid
3. Orbity
4. AI jádro
5. Datové uzly
6. Composer
7. Fixní navigace (`z-index: 30`)
8. Noise textura (`z-index: 50`, pointer-events none)

## Animace
- `morph`: 7 s, infinite — organická deformace jádra.
- `orbit`: 9 s; `orbitb`: 12 s — prostorová rotace.
- `dash`: 8 s — tok dat po SVG cestách.
- `pulse`: 1.8 s — stavový bod.
- Respektuje `prefers-reduced-motion`.

## Přizpůsobení
Barvy lze centralizovat do CSS custom properties. Při změně primary barvy upravte `#2469FF`, `#3978FF`, `#8DB5FF` a odpovídající `rgba` stíny. Nepoužívejte glow na běžný text; patří jen k aktivním a generativním prvkům.

## Přístupnost
- Ikony jsou dekorativní (`aria-hidden`).
- Ikonová tlačítka musí mít `aria-label`.
- Kontrast hlavních textů je vyšší než 4.5:1.
- V produkci zvětšete malé vizuální ovladače transparentním paddingem na dotykovou oblast alespoň 44 × 44 px.
