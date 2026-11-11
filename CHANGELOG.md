# Changelog

## [Unreleased]

## [2026.9.0] - 2026-11-11

### Fixed
- The Doc Freshness tool window ignored Git history and showed every doc block as fresh; it now uses the same Git-based scoring as the inspection (in a background task).
- Python docstrings using both quote styles are listed in file order.
- Threshold settings were labelled as "days"; they are freshness-score boundaries (0–100).

### Removed
- The "Scan Project" button (it did nothing) and the language toggles / ignored-patterns settings (never applied).
- Unused `DocDebtTimeline`, `DocAuthorNotifier` and `CommitAwareStaleness` classes.
- Documentation of features that do not exist: gutter icons, hover tooltips, "Mark as reviewed" quick fix, dashboard treemap, trend analysis, report export, CI runner, SARIF/JUnit output, custom score extension point, `.doc-freshness.yaml` and `@freshness-ignore`.

## [2026.8.0] - 2026-11-10

### Changed
- The plugin is now completely free — all features unlocked, no license needed.
- Source code is now open source under Apache-2.0: https://github.com/JirakJ/docstring-freshness-monitor

## [2026.3.7] - 2026-04-18

### Changed
- Enabled freemium licensing via `<product-descriptor optional="true">`. Plugin remains fully functional without a license; premium features may be introduced in future minor releases.


## [2026.3.6] - 2026-04-18

### Changed
- Enabled freemium licensing via `<product-descriptor optional="true">`. Plugin remains fully functional without a license; premium features may be introduced in future minor releases.


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
- Project dashboard tool window with freshness overview
- Treemap visualization
- Export reports in HTML, JSON, CSV
- CI integration with CLI runner
- SARIF output for GitHub Code Scanning
- JUnit XML output for CI reports
- Custom score algorithm extension point
