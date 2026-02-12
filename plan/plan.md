# Docstring Freshness Monitor — Implementační plán

**Verze:** 2026.2.1
**Status:** Připraveno k implementaci

---

## Fáze 1: Projekt scaffolding

- [ ] Inicializovat IntelliJ Platform Plugin projekt (Gradle Kotlin DSL)
  - [ ] `build.gradle.kts` — plugin metadata, IntelliJ Platform 2024.3+, JVM 17
  - [ ] `settings.gradle.kts`, `gradle.properties` (verze `2026.2.1`)
  - [ ] Gradle wrapper (8.x)
- [ ] `src/main/resources/META-INF/plugin.xml`
  - [ ] Plugin ID: `com.jirak.docfreshness`
  - [ ] Závislosti: `com.intellij.modules.platform`, `Git4Idea`
- [ ] Balíčková struktura: `com.jirak.docfreshness.*`
  - [ ] `.model` — datové třídy
  - [ ] `.core` — freshness kalkulace
  - [ ] `.git` — git blame/log služby
  - [ ] `.lang` — jazykové adaptéry
  - [ ] `.inspection` — IntelliJ inspections
  - [ ] `.ui` — gutter, inlay, tool window
  - [ ] `.settings` — konfigurace
- [ ] `src/main/resources/messages/DocFreshnessBundle.properties`
- [ ] `.gitignore`
- [ ] Ověřit `./gradlew build` projde

## Fáze 2: Datový model

- [ ] `DocBlock.kt` — data class
  - [ ] `file: VirtualFile`, `offset: Int`, `length: Int`
  - [ ] `language: String`, `elementType: DocElementType` (JAVADOC, KDOC, DOCSTRING, JSDOC)
  - [ ] `associatedCodeElement: SmartPsiElementPointer<PsiElement>`
- [ ] `FreshnessData.kt`
  - [ ] `lastDocCommit: String`, `lastDocDate: Instant`
  - [ ] `lastCodeCommit: String`, `lastCodeDate: Instant`
  - [ ] `divergentCommits: Int`
  - [ ] `freshnessScore: Int` (0–100)
  - [ ] `status: FreshnessStatus` (FRESH, AGING, STALE)
- [ ] `FreshnessStatus.kt` — enum s thresholds (FRESH ≥80, AGING ≥50, STALE <50)
- [ ] `DocElementType.kt` — enum
- [ ] `ProjectFreshnessReport.kt` — agregace pro dashboard
- [ ] Unit testy

## Fáze 3: Jazykové adaptéry

- [ ] `DocBlockExtractor.kt` — interface
  - [ ] `fun extractDocBlocks(file: PsiFile): List<DocBlock>`
  - [ ] `fun getAssociatedCodeRange(docBlock: DocBlock): TextRange?`
- [ ] `JavaDocExtractor.kt` — implementace pro Java (PsiDocComment → owner method/class)
- [ ] `KDocExtractor.kt` — implementace pro Kotlin (KDoc → owner)
- [ ] `PythonDocExtractor.kt` — implementace pro Python (docstring → funkce/třída)
- [ ] `JsDocExtractor.kt` — implementace pro JS/TS (JSDoc → funkce)
- [ ] Extension point: `com.jirak.docfreshness.languageSupport`
- [ ] Registrace adaptérů v `plugin.xml`
- [ ] Unit testy pro každý adaptér (test fixtures s ukázkovým kódem)

## Fáze 4: Git Blame Service

- [ ] `GitBlameService.kt`
  - [ ] `fun getLastModification(file: VirtualFile, lineRange: IntRange): GitCommitInfo?`
  - [ ] Volání `git log -L` nebo `git blame` přes Git4Idea API
  - [ ] Cache: `ConcurrentHashMap<FilePath, TimedCache<BlameData>>` s TTL 5 min
  - [ ] Asynchronní provádění (`Task.Backgroundable` / coroutines)
- [ ] `GitCommitInfo.kt` — data class (sha, author, date, message)
- [ ] `BlameCache.kt` — cache management, invalidace při file change
- [ ] Unit testy (mock Git4Idea)
- [ ] Integrační test s reálným git repo (test fixture)

## Fáze 5: Freshness Calculator

- [ ] `FreshnessCalculator.kt`
  - [ ] Input: `DocBlock` + git data pro doc range + git data pro code range
  - [ ] Algoritmus:
    1. Najdi poslední commit měnící doc blok → `lastDocDate`
    2. Najdi poslední commit měnící přidružený kód → `lastCodeDate`
    3. Spočítej divergentní commity (změnily kód ale ne doc)
    4. Skóre = f(časový rozdíl, divergentní commity, rozsah změn)
  - [ ] Konfigurovatelné váhy
  - [ ] Handle edge cases: nový soubor, soubor bez git historie, doc bez kódu
- [ ] `FreshnessService.kt` — project-level service, orchestruje kalkulaci pro soubor/projekt
  - [ ] Inkrementální: přepočet jen změněných souborů
  - [ ] Registrace jako `ProjectService` v `plugin.xml`
- [ ] Unit testy pro skóre kalkulaci (boundary values, edge cases)

## Fáze 6: Editor integrace — Gutter ikony

- [ ] `FreshnessLineMarkerProvider.kt` implements `LineMarkerProvider`
  - [ ] Registrace v `plugin.xml`
  - [ ] Pro každý doc blok: ikona dle stavu (🟢/🟡/🔴)
  - [ ] Lazy loading: skóre se počítá na pozadí, ikona se aktualizuje
- [ ] `FreshnessGutterIcon.kt` — custom ikony (SVG: fresh, aging, stale)
- [ ] Hover tooltip: `FreshnessTooltipProvider`
  - [ ] Poslední změna kódu: „3 commity od poslední doc úpravy"
  - [ ] Datum poslední doc změny
  - [ ] Freshness skóre
- [ ] Click action: „Mark as reviewed" (resetuje freshness)

## Fáze 7: Inspection

- [ ] `StaleDocInspection.kt` extends `LocalInspectionTool`
  - [ ] Registrace v `plugin.xml`: `<localInspection>`
  - [ ] Highlight: doc bloky se STALE skóre
  - [ ] Severity: konfigurovatelná (Warning / Weak Warning)
  - [ ] Quick-fix: „Mark as reviewed"
  - [ ] Quick-fix: „Open diff — code vs. doc changes"
- [ ] `StaleDocInspection` description HTML soubor
- [ ] Inspection testy (`InspectionTestCase`)

## Fáze 8: Konfigurace

- [ ] `DocFreshnessSettings.kt` implements `PersistentStateComponent`
  - [ ] Thresholds (FRESH, AGING, STALE hranice)
  - [ ] Ignorované soubory/adresáře (glob patterns)
  - [ ] Enabled jazyky
  - [ ] Cache TTL
- [ ] `DocFreshnessConfigurable.kt` implements `Configurable`
  - [ ] Settings → Tools → Docstring Freshness Monitor
  - [ ] UI: threshold slidery, ignore patterns editor, language checkboxes
- [ ] `TeamConfigLoader.kt`
  - [ ] Načtení `.doc-freshness.yaml` z repo root
  - [ ] Merge s user settings (team config má prioritu pro thresholds)
- [ ] Unit testy

## Fáze 9: Mark as Reviewed

- [ ] `ReviewMarker.kt`
  - [ ] Přidá `@freshness-reviewed: 2026-02-12` tag do doc bloku
  - [ ] Alternativa: uložení do `.idea/doc-freshness-reviewed.xml` (per-user)
  - [ ] Reset freshness skóre na 100 bez změny obsahu doc
- [ ] Integration s gutter ikonou a inspection quick-fix
- [ ] Unit testy

## Fáze 10: Polish & Release

- [ ] `plugin.xml` — finální popis, changelog
- [ ] README.md — instalace, konfigurace, screenshots
- [ ] `./gradlew verifyPlugin`
- [ ] `./gradlew buildPlugin`
- [ ] Manuální testování: Java, Kotlin, Python, JS/TS soubory
- [ ] Cross-IDE test (IntelliJ, PyCharm, WebStorm)
- [ ] Marketplace listing
- [ ] Git tag `v2026.2.1`

---

## Budoucí fáze (Paid)

- [ ] Projekt dashboard (treemap vizualizace, trend)
- [ ] CI integrace (CLI runner, SARIF, JUnit XML output)
- [ ] Batch inspection (Analyze → Inspect Code)
- [ ] Multi-jazyk simultánně (free = 1 jazyk)
- [ ] Export reportů (HTML, JSON, CSV)
- [ ] Marketplace licensing API
