# Copilot Instructions — Docstring Freshness Monitor

## Projekt
JetBrains IDE plugin (IntelliJ Platform) — detekuje zastaralou dokumentaci porovnáním git historie kódu a jeho docstringů.

## Technický stack
- **Jazyk:** Kotlin (JVM 17+)
- **Build:** Gradle (Kotlin DSL) s `org.jetbrains.intellij.platform` plugin 2.x
- **Platform:** IntelliJ Platform SDK 2024.3+
- **Testování:** JUnit 5 + `BasePlatformTestCase`

## Build a testy
```bash
./gradlew build
./gradlew test
./gradlew test --tests "*.FreshnessScoreTest.testStaleDetection"
./gradlew runIde
./gradlew verifyPlugin
```

## Architektura
- **Core:** `FreshnessCalculator` — porovnává git blame timestamps doc bloků vs. přidruženého kódu
- **PSI integration:** `DocBlockVisitor` prochází PSI strom a identifikuje doc bloky (Javadoc, KDoc, docstring, JSDoc)
- **Git layer:** `GitBlameService` — cachovaný git blame pro freshness výpočet
- **UI:** `LineMarkerProvider` (gutter ikony), `LocalInspectionTool` (inspection), `ToolWindowFactory` (dashboard)
- **Multi-lang:** Podpora jazyků přes EP `com.jirak.docfreshness.languageSupport`

## Konvence
- Balíčky: `com.jirak.docfreshness.*`
- Freshness skóre: 0–100, thresholds: FRESH (80+), AGING (50–79), STALE (0–49)
- Git operace vždy asynchronní (background thread, `Task.Backgroundable`)
- Cache TTL: 5 minut pro git blame data
- Texty v `messages/DocFreshnessBundle.properties`
- Verzování: `2026.2.X`

## Freemium hranice
- Free: freshness skóre, gutter ikony, 1 jazyk
- Paid: všechny jazyky, projekt dashboard, CI integrace, export
