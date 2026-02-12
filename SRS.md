# Docstring Freshness Monitor — Software Requirements Specification (SRS)

**Verze:** 2026.2.1
**Autor:** Jakub Jirák
**Datum:** 12. února 2026
**Model:** Freemium JetBrains Plugin

---

## 1. Úvod

### 1.1 Účel
Docstring Freshness Monitor detekuje zastaralou dokumentaci v kódu. Když se kód změní, ale jeho docstring/komentář ne, plugin označí dokumentaci jako „stárnoucí" a navrhne revizi.

### 1.2 Rozsah
Plugin analyzuje vazbu mezi kódem a jeho dokumentací pomocí PSI tree change tracking a git historie. Výstupem je freshness skóre zobrazené inline v editoru.

### 1.3 Reference
- IntelliJ Platform SDK: PSI API, Git4Idea API
- Brand: Code Health Suite (společně s Dead Code & Drift Radar)

---

## 2. Celkový popis

### 2.1 Perspektiva produktu
Žádný existující plugin nedetekuje „stale" dokumentaci. Existující pluginy (Trelent, autodoc, OkDoc) generují docstringy nebo kontrolují jejich existenci, nikoli aktuálnost.

### 2.2 Uživatelské skupiny
- **Vývojáři** — chtějí udržovat dokumentaci aktuální
- **Code revieweři** — potřebují identifikovat outdated docs při review
- **QA / Tech leads** — chtějí metriku kvality dokumentace

### 2.3 Podporované jazyky
- Java (Javadoc)
- Kotlin (KDoc)
- Python (docstrings)
- JavaScript/TypeScript (JSDoc)
- Rozšiřitelné přes EP

---

## 3. Funkční požadavky

### FR-1: Freshness skóre
- **FR-1.1:** Pro každý dokumentační blok (docstring, Javadoc, KDoc, JSDoc) vypočítat freshness skóre 0–100
- **FR-1.2:** Skóre založené na:
  - Počet commitů měnících přidružený kód od posledního commitu měnícího doc
  - Počet změněných řádků v přidruženém kódu vs. doc
  - Čas od poslední úpravy doc vs. kódu
- **FR-1.3:** Freshness thresholds:
  - 🟢 80–100: Čerstvá
  - 🟡 50–79: Stárnoucí
  - 🔴 0–49: Zastaralá

### FR-2: Editor integrace
- **FR-2.1:** Gutter ikona u každého doc bloku zobrazující freshness stav (barva)
- **FR-2.2:** Inlay hint s freshness skóre (volitelné, default vypnuto)
- **FR-2.3:** Hover tooltip s detailem: poslední změna kódu, poslední změna doc, počet divergentních commitů
- **FR-2.4:** Quick-fix action: „Mark as reviewed" (resetuje freshness bez změny textu)

### FR-3: Inspection
- **FR-3.1:** Registrovaný jako IntelliJ inspection: „Stale documentation"
- **FR-3.2:** Konfigurovatelná severity (Warning / Weak Warning)
- **FR-3.3:** Scope: celý projekt, modul, nebo vybrané soubory
- **FR-3.4:** Batch mode přes Analyze → Inspect Code

### FR-4: Projekt Dashboard (Paid)
- **FR-4.1:** Tool window s přehledem freshness napříč projektem
- **FR-4.2:** Filtrování: nejhorší soubory, nejhorší moduly, trend
- **FR-4.3:** Treemap vizualizace (soubor velikost = LOC, barva = freshness)
- **FR-4.4:** Export reportu (HTML, JSON, CSV)

### FR-5: CI integrace (Paid)
- **FR-5.1:** CLI runner: `./gradlew docFreshness --threshold=60 --fail-on-stale`
- **FR-5.2:** SARIF output pro GitHub Code Scanning
- **FR-5.3:** JUnit XML output pro CI reporty

### FR-6: Konfigurace
- **FR-6.1:** Settings page: threshold hodnoty, ignorované soubory/adresáře
- **FR-6.2:** `.doc-freshness.yaml` v repo root pro team-shared config
- **FR-6.3:** Suppress per-blok: `@freshness-ignore` tag v doc

---

## 4. Nefunkční požadavky

### NFR-1: Výkon
- Freshness kalkulace probíhá asynchronně (background task)
- Inkrementální: přepočet jen změněných souborů
- Git blame cache s TTL 5 minut
- Neblokuje editor při otevření souboru

### NFR-2: Kompatibilita
- IntelliJ IDEA 2024.3+
- Všechny JetBrains IDE podporující daný jazyk

### NFR-3: Přesnost
- False positive rate < 10 %
- „Mark as reviewed" eliminuje false positives

---

## 5. Datový model

### 5.1 DocBlock
```
DocBlock {
  file: VirtualFile
  offset: Int
  language: String
  lastDocCommit: String (SHA)
  lastDocDate: Instant
  lastCodeCommit: String (SHA)
  lastCodeDate: Instant
  divergentCommits: Int
  freshnessScore: Int (0-100)
  status: Enum (FRESH, AGING, STALE)
  reviewedAt: Instant? (manual reset)
}
```

### 5.2 ProjectFreshnessReport
```
ProjectFreshnessReport {
  timestamp: Instant
  totalDocBlocks: Int
  freshCount: Int
  agingCount: Int
  staleCount: Int
  averageScore: Double
  worstFiles: List<FileFreshnessReport>
}
```

---

## 6. Rozhraní

### 6.1 Extension Points
- `com.jakubjirak.docfreshness.languageSupport` — EP pro přidání podpory dalších jazyků
- `com.jakubjirak.docfreshness.scoreCalculator` — EP pro custom skóre algoritmus

### 6.2 UI komponenty
- Gutter icon provider (LineMarkerProvider)
- Inspection (LocalInspectionTool)
- Tool window — Project Dashboard (ToolWindowFactory)
- Settings page (Configurable)

---

## 7. Freemium model

### Free tier
- Freshness skóre pro Java, Kotlin, Python, JS/TS
- Gutter ikony + hover tooltip
- Inspection (single file)
- Max 1 jazyk současně

### Paid tier
- Všechny jazyky současně
- Projekt dashboard (treemap, trend)
- CI integrace (CLI runner, SARIF, JUnit XML)
- Batch inspection
- Export reportů
- Custom skóre algoritmus (EP)
- „Mark as reviewed" audit trail
