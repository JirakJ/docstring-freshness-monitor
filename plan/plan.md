# Docstring Freshness Monitor — Implementační plán

**Verze:** 2026.2.1
**Status:** Připraveno k implementaci

---

## Fáze 0: Plugin Ikona & Branding

- [ ] Navrhnout plugin ikonu (SVG + PNG)
  - [ ] Koncept: 📊 Dokument s freshness metrem
  - [ ] Hlavní ikona: 40×40 SVG pro Marketplace listing
  - [ ] Toolbar ikona: 16×16 + 13×13 SVG (light + dark varianta)
  - [ ] Tool window ikona: 13×13 SVG
  - [ ] `src/main/resources/META-INF/pluginIcon.svg` (40×40, light theme)
  - [ ] `src/main/resources/META-INF/pluginIcon_dark.svg` (40×40, dark theme)
  - [ ] Action ikony pro toolbar/menu (16×16 SVG, light + dark)
- [ ] Barevná paleta konzistentní s Code Health Suite brandem
- [ ] Marketplace banner obrázek (1280×800)
- [ ] Screenshoty pro Marketplace listing (min 3, 1280×800 PNG)

## Fáze 1: Projekt scaffolding

- [ ] Inicializovat IntelliJ Platform Plugin projekt (Gradle Kotlin DSL)
  - [ ] `build.gradle.kts` — plugin metadata, IntelliJ Platform 2024.3+, JVM 17
  - [ ] `settings.gradle.kts`, `gradle.properties` (verze `2026.2.1`)
  - [ ] Gradle wrapper (8.x)
- [ ] `src/main/resources/META-INF/plugin.xml`
  - [ ] Plugin ID: `com.jakubjirak.docfreshness`
  - [ ] Závislosti: `com.intellij.modules.platform`, `Git4Idea`
- [ ] Balíčková struktura: `com.jakubjirak.docfreshness.*`
  - [ ] `.model` — datové třídy
  - [ ] `.core` — freshness kalkulace
  - [ ] `.git` — git blame/log služby
  - [ ] `.lang` — jazykové adaptéry
  - [ ] `.inspection` — IntelliJ inspections
  - [ ] `.ui` — gutter, inlay, tool window
  - [ ] `.settings` — konfigurace
- [ ] `src/main/resources/messages/DocFreshnessBundle.properties`
- [ ] `.gitignore` (build/, .idea/, .gradle/, .kotlin/, out/)
- [ ] `gradle/libs.versions.toml` — version catalog
- [ ] `LICENSE` (Apache-2.0)
- [ ] `.editorconfig` (Kotlin style conventions)
- [ ] Detekt konfigurace (`detekt.yml`)
- [ ] GitHub Actions CI workflow (`.github/workflows/build.yml`)
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
- [ ] Extension point: `com.jakubjirak.docfreshness.languageSupport`
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

## Fáze 10: Freemium & Licensing Infrastructure

- [ ] Integrace JetBrains Marketplace Licensing API
  - [ ] `LicenseChecker.kt` — ověření licence přes `com.intellij.ide.plugins.PluginManager` a Marketplace API
  - [ ] `LicenseStatus.kt` — enum: FREE, TRIAL, PAID, EXPIRED
  - [ ] `FreemiumGate.kt` — centrální guard pro paid features
    - [ ] `fun isPaidFeatureAvailable(featureId: String): Boolean`
    - [ ] Graceful degradation — paid UI elementy skryté/disabled ve free tieru
  - [ ] Trial period: 30 dní plný přístup
- [ ] `plugin.xml` — `<product-descriptor>` element pro Marketplace licensing
  - [ ] `code` atribut (unique product code)
  - [ ] `release-date`, `release-version`
- [ ] Nag screen / upgrade prompt
  - [ ] Non-intrusive balloon notification při pokusu o paid feature
  - [ ] „Upgrade to Pro" action → Marketplace stránka
  - [ ] Max 1× denně (ne spam)
- [ ] Feature flags
  - [ ] `FeatureRegistry.kt` — definice free vs. paid features
  - [ ] Každý paid feature má `@PaidFeature("feature.id")` anotaci nebo guard check
- [ ] Unit testy pro licensing logic (mock Marketplace API)

## Fáze 11: Production-Grade Quality

- [ ] Error Handling & Resilience
  - [ ] Globální exception handler (`ErrorReportSubmitter` implementace)
  - [ ] Graceful degradation — plugin nesmí crashnout IDE
  - [ ] Try-catch na všech EP entry pointech
  - [ ] Timeout na všech I/O operacích (network, git, file system)
  - [ ] Rate limiting na API volání
- [ ] Logging
  - [ ] `Logger` instance v každé třídě (IntelliJ `Logger.getInstance()`)
  - [ ] Structured logging: DEBUG pro vývoj, INFO pro operace, WARN/ERROR pro problémy
  - [ ] Žádné citlivé údaje v logech
- [ ] Telemetrie (opt-in, anonymní)
  - [ ] `PluginUsageTracker.kt` — FUS (Feature Usage Statistics) integrace
  - [ ] Metriky: aktivace featury, error rate, latence
  - [ ] Registrace FUS group v `plugin.xml`
- [ ] Performance
  - [ ] Profiling na velkých projektech (10k+ souborů)
  - [ ] Background thread pro všechny I/O operace (nesmí blokovat EDT)
  - [ ] Read/write action správně (ReadAction, WriteAction, smart mode check)
  - [ ] Disposal pattern: `Disposable` pro všechny listeners a services
  - [ ] Memory leak check (WeakReference kde potřeba)
- [ ] Testing — production úroveň
  - [ ] Unit testy: ≥80 % coverage core logiky
  - [ ] Integrační testy: `BasePlatformTestCase` pro PSI, inspections, EP
  - [ ] UI testy: `HeavyPlatformTestCase` pro tool windows, dialogy
  - [ ] Performance testy: benchmark na velkých souborech
- [ ] CI/CD Pipeline
  - [ ] GitHub Actions workflow:
    - [ ] `./gradlew check` — kompilace + testy
    - [ ] `./gradlew verifyPlugin` — plugin strukturální validace
    - [ ] `./gradlew verifyPluginProjectConfiguration` — metadata validace
    - [ ] `./gradlew runPluginVerifier` — binární kompatibilita s target IDE verzemi
    - [ ] Qodana scan (JetBrains code quality)
  - [ ] Release workflow:
    - [ ] Tag-based release (push tag → build → publish)
    - [ ] `./gradlew publishPlugin` — automatický upload na Marketplace
    - [ ] Changelog generace z git commitů
  - [ ] Dependabot / Renovate pro dependency updates
- [ ] Kódová kvalita
  - [ ] Kotlin coding conventions (official JetBrains style)
  - [ ] Detekt nebo ktlint pro statickou analýzu
  - [ ] Žádné deprecated API (IntelliJ Platform compatibility)
  - [ ] `@ApiStatus.Experimental` / `@ApiStatus.Internal` anotace
- [ ] Dokumentace
  - [ ] KDoc pro všechny public API
  - [ ] README.md: instalace, konfigurace, screenshoty, FAQ
  - [ ] CHANGELOG.md (Keep a Changelog format)
  - [ ] CONTRIBUTING.md (pokud open source)

## Fáze 12: Polish & Release

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
