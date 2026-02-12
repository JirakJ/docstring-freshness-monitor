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
- **Multi-lang:** Podpora jazyků přes EP `com.jakubjirak.docfreshness.languageSupport`

## Konvence
- Balíčky: `com.jakubjirak.docfreshness.*`
- Freshness skóre: 0–100, thresholds: FRESH (80+), AGING (50–79), STALE (0–49)
- Git operace vždy asynchronní (background thread, `Task.Backgroundable`)
- Cache TTL: 5 minut pro git blame data
- Texty v `messages/DocFreshnessBundle.properties`
- Verzování: `2026.2.X`

## Production-grade požadavky
- Všechny I/O operace na background threadech (`Task.Backgroundable`, coroutines)
- Read/Write actions správně (`ReadAction.run {}`, `WriteAction.run {}`)
- `Disposable` pattern pro listeners a services
- Exception handling: žádné unhandled exceptions, graceful degradation
- Logging: `Logger.getInstance(MyClass::class.java)` v každé třídě
- EDT nesmí být blokován — žádné síťové/git/file operace na EDT
- Smart mode check (`DumbService.isDumb()`) kde potřeba

## Freemium architektura
- `FreemiumGate` centrální guard: `FreemiumGate.isPaidFeatureAvailable("feature.id")`
- JetBrains Marketplace Licensing API přes `<product-descriptor>` v `plugin.xml`
- Paid UI elementy: skryté nebo disabled ve free tieru (ne crash/error)
- Trial: 30 dní plný přístup, pak downgrade na free tier
- Upgrade prompt: max 1× denně, non-intrusive balloon

## CI/CD
- GitHub Actions: `./gradlew check`, `verifyPlugin`, `runPluginVerifier`
- Detekt pro statickou analýzu Kotlin kódu
- Tag-based release → `publishPlugin` na Marketplace
- Kotlin coding conventions (JetBrains official style)

## Ikona
- `src/main/resources/META-INF/pluginIcon.svg` (40×40, light)
- `src/main/resources/META-INF/pluginIcon_dark.svg` (40×40, dark)
- Action ikony: 16×16 + 13×13 SVG (light + dark varianty)

## Freemium hranice
- Free: freshness skóre, gutter ikony, 1 jazyk
- Paid: všechny jazyky, projekt dashboard, CI integrace, export
