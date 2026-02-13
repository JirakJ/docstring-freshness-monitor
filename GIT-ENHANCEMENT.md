# Docstring Freshness Monitor — Git Enhancement Specifikace

**Verze:** 2026.2.1
**Status:** Plánováno
**Závislosti:** Git4Idea API, `git log`, `git blame`

---

## Přehled

Rozšíření pluginu o pokročilou Git integraci pro přesnější detekci zastaralé dokumentace,
notifikace autorů a vizualizaci trendu stárnutí dokumentace přes Git historii.

---

## FR-GE-001: Commit-Aware Staleness

### Popis

Porovnání obsahu docstringu vs. změn kódu v Git historii. Místo jednoduchého porovnání timestampů
se analyzuje `git log` pro přesnou detekci, kdy se kód změnil bez odpovídající úpravy dokumentace.

### Funkční požadavky

- [ ] **FR-GE-001.1** — Analýza `git log --follow` pro sledování přejmenování souborů
- [ ] **FR-GE-001.2** — Porovnání diff hunks: identifikace commitů měnících kód ale ne docstring
- [ ] **FR-GE-001.3** — Sémantická analýza změn: rozlišení refactoring (rename) vs. logická změna
- [ ] **FR-GE-001.4** — Konfigurovatelné ignorování merge commitů a bot commitů
- [ ] **FR-GE-001.5** — Inkrementální analýza: pouze nové commity od posledního scanu

### Acceptance Criteria

1. Plugin správně identifikuje commity měnící kód přidružený k docstringu
2. Přejmenování souboru neovlivní staleness tracking (`--follow` support)
3. Merge commity jsou volitelně vyloučeny z analýzy
4. Inkrementální scan zpracuje pouze nové commity (performance <500ms pro 100 commitů)
5. Výsledek je konzistentní s `git log -L` výstupem pro daný range

### Datový model — rozšíření

```kotlin
data class CommitStalenessData(
    val commitHash: String,
    val commitDate: Instant,
    val author: String,
    val changedCodeLines: IntRange,
    val changedDocLines: IntRange?,
    val changeType: ChangeType // CODE_ONLY, DOC_ONLY, BOTH, REFACTOR
)

enum class ChangeType {
    CODE_ONLY,    // kód změněn, doc ne → zvyšuje staleness
    DOC_ONLY,     // doc změněn, kód ne → snižuje staleness
    BOTH,         // oboje změněno → neutrální
    REFACTOR      // rename/move → ignorováno
}

data class StalenessTimeline(
    val entries: List<CommitStalenessData>,
    val currentScore: Int,
    val trendDirection: TrendDirection // IMPROVING, STABLE, DEGRADING
)
```

### Technická implementace

- `GitLogAnalyzer.kt` — parsování `git log -L` výstupu pro line-range historii
- `CommitClassifier.kt` — klasifikace commitů (code-only, doc-only, both, refactor)
- `IncrementalStalenessTracker.kt` — cache posledního analyzovaného commitu, delta analýza
- Integrace s existujícím `FreshnessCalculator.kt` — rozšíření skóre algoritmu

---

## FR-GE-002: Author Notification

### Popis

Automatická notifikace původního autora docstringu, když se přidružený kód změní.
Využívá `git blame` pro identifikaci autora dokumentace a `git log` pro detekci změn kódu.

### Funkční požadavky

- [ ] **FR-GE-002.1** — `git blame` na docstring range → identifikace autora
- [ ] **FR-GE-002.2** — Detekce změny kódu jiným autorem → trigger notifikace
- [ ] **FR-GE-002.3** — IDE notifikace (balloon) s informací o změně a odkazem na diff
- [ ] **FR-GE-002.4** — Konfigurovatelné: notifikovat jen při significant changes (ne whitespace)
- [ ] **FR-GE-002.5** — Batch notifikace: agregace více změn do jedné notifikace

### Acceptance Criteria

1. Autor docstringu je správně identifikován přes `git blame`
2. Notifikace se zobrazí pouze aktuálnímu uživateli (porovnání s `git config user.email`)
3. Whitespace-only změny netriggerují notifikaci (pokud nakonfigurováno)
4. Batch mód: max 1 notifikace za 5 minut pro stejný soubor
5. Klik na notifikaci otevře diff view mezi posledním doc updatem a aktuálním kódem

### Datový model — rozšíření

```kotlin
data class DocAuthorInfo(
    val authorEmail: String,
    val authorName: String,
    val lastDocCommit: String,
    val lastDocDate: Instant
)

data class AuthorNotification(
    val docBlock: DocBlock,
    val authorInfo: DocAuthorInfo,
    val triggeringCommits: List<GitCommitInfo>,
    val significance: ChangeSignificance // MINOR, MODERATE, MAJOR
)

enum class ChangeSignificance {
    MINOR,     // whitespace, formatting
    MODERATE,  // parameter rename, small logic change
    MAJOR      // signature change, new parameters, logic rewrite
}
```

### Technická implementace

- `DocAuthorResolver.kt` — `git blame` wrapper pro docstring range
- `ChangeSignificanceAnalyzer.kt` — klasifikace závažnosti změny (AST diff)
- `AuthorNotificationService.kt` — project-level service, debounce, batching
- `AuthorNotificationBalloon.kt` — UI notifikace s akcemi (View Diff, Dismiss, Mute)

---

## FR-GE-003: Documentation Debt Timeline

### Popis

Vizualizace stárnutí dokumentace přes commity jako graf/timeline. Zobrazuje trend
freshness skóre v čase, umožňuje identifikovat období zhoršení dokumentační kvality.

### Funkční požadavky

- [ ] **FR-GE-003.1** — Sběr freshness dat per commit (sampling: každý N-tý commit nebo weekly)
- [ ] **FR-GE-003.2** — Line chart: freshness skóre (Y) vs. čas/commity (X)
- [ ] **FR-GE-003.3** — Agregace per soubor, per modul, per projekt
- [ ] **FR-GE-003.4** — Trend indikátor: improving/stable/degrading s numerickým slope
- [ ] **FR-GE-003.5** — Export dat jako JSON/CSV pro externí analýzu
- [ ] **FR-GE-003.6** — Drill-down: klik na bod v grafu → zobrazí stav v daném commitu

### Acceptance Criteria

1. Timeline zobrazí minimálně posledních 100 commitů (nebo 6 měsíců)
2. Sampling je konfigurovatelný (every commit, weekly, monthly)
3. Graf je renderován v tool window panelu s interaktivním hover tooltip
4. Trend direction je vypočítán z lineární regrese posledních 30 datových bodů
5. Export produkuje validní JSON/CSV soubor
6. Performance: initial load <3s pro 1000 commitů

### Datový model — rozšíření

```kotlin
data class FreshnessTimelinePoint(
    val commitHash: String,
    val date: Instant,
    val overallScore: Int,          // 0–100, průměr přes všechny doc bloky
    val freshCount: Int,
    val agingCount: Int,
    val staleCount: Int,
    val totalDocBlocks: Int
)

data class FreshnessTimeline(
    val points: List<FreshnessTimelinePoint>,
    val trendDirection: TrendDirection,
    val trendSlope: Double,          // pozitivní = improving
    val periodStart: Instant,
    val periodEnd: Instant
)

enum class TrendDirection {
    IMPROVING,  // slope > +0.5
    STABLE,     // slope ∈ [-0.5, +0.5]
    DEGRADING   // slope < -0.5
}

data class TimelineExportData(
    val projectName: String,
    val generatedAt: Instant,
    val timeline: FreshnessTimeline,
    val perFileBreakdown: Map<String, FreshnessTimeline>?  // paid feature
)
```

### Technická implementace

- `TimelineCollector.kt` — iterace přes `git log`, checkout/analýza per commit (sampling)
- `TimelineChartPanel.kt` — JFreeChart nebo custom Canvas rendering
- `TrendCalculator.kt` — lineární regrese, slope computation
- `TimelineExporter.kt` — JSON/CSV export service
- `TimelineToolWindowTab.kt` — nový tab v existujícím tool window

---

## Společné technické požadavky

### Performance

- Všechny Git operace na background thread (nesmí blokovat EDT)
- Cache pro `git blame` a `git log` výsledky s TTL 5 minut
- Inkrementální processing: pouze nové commity od posledního scanu
- Lazy loading pro timeline data (stránkování po 50 commitech)

### Konfigurace — rozšíření `DocFreshnessSettings`

```kotlin
// Nová pole v existujícím Settings
val gitAnalysisEnabled: Boolean = true
val ignoreMergeCommits: Boolean = true
val ignoreBotCommits: Boolean = true
val botPatterns: List<String> = listOf("dependabot", "renovate", "github-actions")
val authorNotificationsEnabled: Boolean = true
val notificationDebounceMinutes: Int = 5
val timelineSamplingStrategy: SamplingStrategy = SamplingStrategy.WEEKLY
val timelineMaxCommits: Int = 500
```

### Freemium rozdělení

| Feature | Free | Paid |
|---------|------|------|
| Commit-aware staleness (basic) | ✅ | ✅ |
| Author notification | ❌ | ✅ |
| Documentation debt timeline | ❌ | ✅ |
| Per-file breakdown v timeline | ❌ | ✅ |
| Export (JSON/CSV) | ❌ | ✅ |
| Konfigurovatelný sampling | ❌ | ✅ |
