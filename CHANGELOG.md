# Changelog

## [2026.2.5] - 2026-02-13

### Added
- Added ProGuard obfuscation for code protection


## [2026.2.4] - 2026-02-13

### Changed
- Version bump for JetBrains Marketplace publication


## [2026.2.3] - 2026-02-13

### Changed
- Expanded compatibility range: sinceBuild 232.0+ (IntelliJ 2023.2+)
- Removed upper version bound for future IDE compatibility


## [2026.2.2] - 2026-02-13

### Added
- Initial release
- Freshness scoring (0–100) for every doc block based on git history
- Stale documentation inspection with configurable severity (Warning / Weak Warning)
- Gutter icons with color-coded freshness status (🟢 Fresh, 🟡 Aging, 🔴 Stale)
- Hover tooltips showing last code change, last doc change, and divergent commit count
- "Mark as reviewed" quick-fix action to reset freshness without modifying text
- Multi-language support: Java/Kotlin (Javadoc/KDoc), Python (docstrings), JavaScript/TypeScript (JSDoc)
- Git integration using git blame/log for code vs. documentation change tracking
- Batch analysis via Analyze → Inspect Code
- Configurable freshness thresholds (aging: 80, stale: 50, rotten: 20)
- Per-language enable/disable toggles
- Ignored file patterns configuration
- `.doc-freshness.yaml` support for team-shared configuration
- `@freshness-ignore` tag for per-block suppression
- Project dashboard tool window with freshness overview (Pro)
- Treemap visualization (Pro)
- Export reports in HTML, JSON, CSV (Pro)
- CI integration with CLI runner (Pro)
- SARIF output for GitHub Code Scanning (Pro)
- JUnit XML output for CI reports (Pro)
- Custom score algorithm extension point (Pro)
