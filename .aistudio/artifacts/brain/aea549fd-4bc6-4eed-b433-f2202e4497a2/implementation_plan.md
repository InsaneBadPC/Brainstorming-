# MindCraft AI — Interactive Brainstorming & Mind Mapping Assistant

MindCraft AI is a real-time brainstorming canvas for Android that turns raw, nascent ideas into structured, actionable solution architectures through collaborative AI co-creation, full bidirectional mind map manipulation, and hands-free voice interaction.

### User Review & Critical Decisions

> [!IMPORTANT]
> The following directions were confirmed during our initial interactive questions and will guide the build:

- **Confirmed Collaboration Mode**: Co-creator mode with smart suggestions and direct map updates. The AI can generate branches, deepen concepts, restructure nodes, assign solution tags (e.g., Problem, Feature, Architecture, Risk, Action Item), and synthesize actionable execution plans directly onto the canvas.
- **Confirmed Voice Mode**: Dual speech workflow combining on-device speech-to-text voice dictation (`SpeechRecognizer`) with spoken AI audio feedback (Android `TextToSpeech` with natural pitch/rate and live waveform visualizer) so you can brainstorm aloud.
- **Confirmed Visual Aesthetic**: Modern sleek dark canvas (`#0D1117` / `#161B22`) with luminous neon accents (electric cyan, emerald green, violet purple, amber gold) for distinct branch categories, curved bezier connectors, and smooth pinch-to-zoom/pan navigation.

---

### 1. Overview & Core Concept

- **What It Does**: Users start from a central seed idea (e.g. "On-demand local repair marketplace" or "Autonomous garden monitoring"). Users and the AI collaborate on an infinite interactive 2D canvas. The AI can brainstorm novel angles, break problems into sub-modules, detect technical risks, recommend monetization or architecture strategies, and synthesize a comprehensive "Actionable Blueprint".
- **AI Mind Map Control**: The AI is not a passive side-chat; it directly issues structured canvas commands (`ADD_NODE`, `CONNECT_NODES`, `EXPAND_BRANCH`, `REORGANIZE_LAYOUT`, `GENERATE_SOLUTION_SPECS`).
- **Target Audience**: Innovators, product builders, software architects, startup founders, and students who want to rapidly stress-test and evolve concepts into fully fleshed-out solutions.
- **Key Value**: Bridges the gap between chaotic brainstorming and structured execution, allowing thoughts to grow visually and audibly in seconds.

---

### 2. User Experience & Visual Design

#### Key User Flows
1. **Creation & Idea Seeding**: Create a new mind map session with a central topic or choose a curated brainstorming template (Tech Architecture, Business Venture, Creative Story, Product Discovery).
2. **Interactive Canvas Exploration**: Pan and zoom smoothly across the infinite canvas. Tap any node to view details, add custom children, edit text, adjust color tags, or ask the AI to "Deepen this idea", "Find challenges", or "Propose technical solutions".
3. **Voice-Powered Brainstorming**: Tap the glowing voice mic button to speak thoughts naturally. Speech is transcribed in real-time, sent to Gemini AI, and the AI speaks back its synthesis while simultaneously expanding the mind map with new structured nodes.
4. **AI Brainstorm Sheet & Co-Pilot**: Open the slide-out Brainstorming Lab to prompt Gemini freely, run automated mind map reorganizations (auto-layout hierarchy), or trigger "Generate Final Solution Blueprint" to get an exportable markdown summary of the whole map.
5. **Session Management & Persistence**: Save maps automatically to local SQLite Room storage; export as JSON, Markdown, or image snapshot.

#### Visual Identity & Theme
- **Color Tokens**:
  - Background: Deep Obsidian (`#0B0E14`) and Slate Card Surface (`#151922`)
  - Outlines / Grid Dots: Subtle Tech Matrix (`#242C38`)
  - Accent Primary: Electric Cyan (`#00F2FE` / `#4FACFE`)
  - Accent Secondary: Cyber Emerald (`#10B981` / `#00E676`)
  - Accent Caution / Risk: Neon Coral (`#FF4B72`)
  - Accent Insight / Solution: Solar Amber (`#FFB300`)
  - Text: High-contrast Crisp White (`#F3F4F6`) and Muted Tech Gray (`#94A3B8`)
- **Typography & Canvas Spatial Math**:
  - Display headers: High-contrast semi-bold typography with crisp legibility.
  - Node layout: Auto-spaced radial/hierarchical tree calculation preventing node overlaps, rendered with smooth quadratic bezier curves connecting parent to children.
- **Interactive Feedback**:
  - Glowing node borders for selected / active items.
  - Pulsing circular waveform animation while recording audio.
  - Spring-animated branch unfolding and smooth camera transitions to newly generated nodes.

---

### 3. Key Product Decisions & Trade-Offs

- **Decision 1: Direct Canvas Manipulation via Structured AI Tool Commands**
  - *Chosen Approach*: Gemini API outputs structured JSON commands (`MindMapAction` including `createNode`, `linkNodes`, `batchExpand`) alongside conversational insight.
  - *Why*: Allows immediate visual co-creation where the AI acts as a visual facilitator rather than just producing text walls in a chat box.
  - *Alternatives Considered*: Chat-only suggestions requiring manual user placement were rejected because the user specifically requested full AI control of the mind map.
- **Decision 2: Voice Architecture (SpeechRecognizer + TextToSpeech)**
  - *Chosen Approach*: Native Android `SpeechRecognizer` for low-latency Czech and multi-language voice transcription paired with Android `TextToSpeech` for fluid voice playback, with permission handling and graceful fallbacks.
  - *Why*: Reliable, offline-capable voice primitives with zero lag and no extra third-party billing, seamlessly integrated into Jetpack Compose.
- **Decision 3: Local Database Persistence via Room**
  - *Chosen Approach*: Device-local Room database storing `MindMapEntity`, `NodeEntity`, and `EdgeEntity` with reactive `Flow` updates.
  - *Why*: Immediate offline availability, instant autosave on every gesture/edit, and zero login friction.

---

### 4. Technical Architecture & Data Strategy *(Technical Reference)*

#### System Architecture & Component Diagram

```
┌────────────────────────────────────────────────────────────────────────┐
│                        Jetpack Compose UI Layer                        │
├────────────────────────────────┬───────────────────────────────────────┤
│        MindMapCanvasView       │        VoiceAssistantOverlay          │
│  - Custom Pan/Zoom Canvas      │  - SpeechRecognizer speech-to-text    │
│  - Bezier branch curve painter │  - Animated Audio Waveform            │
│  - Interactive Node Cards      │  - TextToSpeech spoken playback       │
├────────────────────────────────┴───────────────────────────────────────┤
│                   BrainstormBottomSheet & Quick Actions                 │
│  - Deepen Node | Find Risks | Suggest Solutions | Export Blueprint     │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ Events & Intents
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                         MindMapViewModel                               │
│  - Canvas viewport state (offset X/Y, scale 0.3x - 3.0x)               │
│  - Active node selection & multi-touch gesture handlers                │
│  - Tree layout auto-repositioning engine (radial & tree layouts)       │
│  - Voice session coordinator (record -> transcript -> AI -> speak)    │
└───────────────────┬────────────────────────────────┬───────────────────┘
                    │                                │
                    ▼                                ▼
┌───────────────────────────────────────┐ ┌──────────────────────────────┐
│        GeminiBrainstormRepository     │ │    Room Database Repository  │
│  - Gemini 3.5 Flash REST client       │ │  - MindMapDao                │
│  - Structured JSON command parser     │ │  - MapEntity, NodeEntity     │
│  - Co-creator prompt engineer         │ │  - Auto-save & Local History │
└───────────────────────────────────────┘ └──────────────────────────────┘
```

#### Core Data Entities
- **MindMapEntity**: `id`, `title`, `description`, `createdAt`, `updatedAt`, `rootNodeId`
- **MindNode**: `id`, `mapId`, `parentId`, `title`, `details`, `category` (Concept, Feature, Risk, Architecture, Solution), `xPos`, `yPos`, `colorHex`, `isAiGenerated`, `expanded`
- **MindEdge**: `id`, `mapId`, `fromNodeId`, `toNodeId`, `label`, `style` (solid, dashed)
- **MindMapAction**: Sealed command payload parsed from Gemini containing `CreateNodesAction`, `ExpandNodeAction`, `UpdateNodeDetailsAction`, etc.

#### Key Interactive Sequences
1. **User speaks / inputs an idea**: Speech transcribed -> Viewmodel sends current map hierarchy context + user request to Gemini -> Gemini returns explanatory response + list of new nodes/connections -> Canvas updates dynamically with smooth spring animation -> Voice synthesizes the AI response aloud.
2. **Touch Drag & Pinch**: Custom `pointerInput` on Compose `Canvas` handles simultaneous pan and pinch-zoom with bounding clamps.
3. **One-Tap AI Branch Expansion**: Selecting a node and tapping "AI Expand" queries Gemini specifically with that node's ancestor chain to generate 3-5 smart divergent sub-solutions.
