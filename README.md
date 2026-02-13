# Docstring Freshness Monitor

[![Build](https://github.com/JirakJ/docstring-freshness-monitor/actions/workflows/build.yml/badge.svg)](https://github.com/JirakJ/docstring-freshness-monitor/actions/workflows/build.yml)
[![Version](https://img.shields.io/badge/version-2026.2.2-blue.svg)](https://github.com/JirakJ/docstring-freshness-monitor/releases)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](LICENSE)

> Detect stale documentation by comparing code changes with documentation changes. When code around a docstring changes but the doc doesn't, it gets flagged as potentially outdated.

## ✨ Features

### Free Tier
- **Freshness scoring** — 0–100 score for every doc block based on git history
- **Gutter icons** — colored indicators (🟢 Fresh, 🟡 Aging, 🔴 Stale) next to each doc block
- **Hover tooltips** — last code change, last doc change, number of divergent commits
- **Stale doc inspection** — highlights outdated documentation with configurable severity
- **Multi-language support** — Java/Kotlin (Javadoc/KDoc), Python (docstrings), JavaScript/TypeScript (JSDoc)
- **Git integration** — uses git blame/log to track code vs. doc changes
- **Mark as reviewed** — quick-fix to reset freshness without changing text
- **Batch analysis** — run via Analyze → Inspect Code

### Pro Tier ($2.90/month)
- All languages enabled simultaneously (free tier: one at a time)
- **Project dashboard** — tool window with freshness scores across the entire project
- **Treemap visualization** — file size = LOC, color = freshness
- **Trend analysis** — track freshness improvements/regressions over time
- **Export reports** — HTML, JSON, CSV
- **CI integration** — CLI runner: `./gradlew docFreshness --threshold=60 --fail-on-stale`
- **SARIF output** — for GitHub Code Scanning
- **JUnit XML output** — for CI report integration
- **Custom score algorithm** — via extension point
- **"Mark as reviewed" audit trail**

## 📦 Installation

### From JetBrains Marketplace
1. Open **Settings** → **Plugins** → **Marketplace**
2. Search for **"Docstring Freshness Monitor"**
3. Click **Install** and restart the IDE

### From ZIP
1. Download the latest release from [Releases](https://github.com/JirakJ/docstring-freshness-monitor/releases)
2. Open **Settings** → **Plugins** → **⚙️** → **Install Plugin from Disk**
3. Select the downloaded ZIP file and restart the IDE

## 🚀 Quick Start

1. Install the plugin and restart your IDE
2. Open any file with documentation (Javadoc, KDoc, Python docstrings, or JSDoc)
3. Look at the **gutter icons** next to doc blocks — they show freshness status
4. Hover over a gutter icon to see detailed freshness information
5. Run **Analyze → Inspect Code** to scan the entire project for stale documentation
6. Open the **Doc Freshness** tool window (bottom panel) for a project-wide overview

## ⚙️ Configuration

Settings are available at **Settings → Tools → Docstring Freshness Monitor**.

| Setting | Default | Description |
|---------|---------|-------------|
| Aging threshold | `80` | Score below which a doc block is considered "aging" |
| Stale threshold | `50` | Score below which a doc block is considered "stale" |
| Rotten threshold | `20` | Score below which a doc block is considered "rotten" |
| Enable Java/Kotlin | `true` | Analyze Javadoc and KDoc comments |
| Enable Python | `true` | Analyze Python docstrings |
| Enable JavaScript/TypeScript | `true` | Analyze JSDoc comments |
| Ignored file patterns | *(empty)* | Glob patterns for files to exclude (one per line) |

### Team-shared Configuration

Create a `.doc-freshness.yaml` in your repository root for shared team settings. Use `@freshness-ignore` tag in a doc block to suppress warnings for individual blocks.

## 🎯 Usage

### Editor Integration
- **Gutter icons** appear next to each doc block showing freshness status
- **Hover** over the icon for details: last code change, last doc change, divergent commit count
- Use the **"Mark as reviewed"** quick-fix to acknowledge a doc block without modifying it

### Inspections
- The **"Stale documentation"** inspection is registered under the **Documentation freshness** group
- Enabled by default at **WARNING** severity
- Run batch analysis via **Analyze → Inspect Code**

### Project Dashboard
- Open the **Doc Freshness** tool window from the bottom panel
- Filter by worst files, modules, or trends
- Export reports in HTML, JSON, or CSV format (Pro)

## 📋 Requirements

- IntelliJ IDEA 2024.3+ (or any compatible JetBrains IDE)
- Git repository (for freshness score calculation)
- Java 17+

## 🏗️ Building from Source

```bash
git clone https://github.com/JirakJ/docstring-freshness-monitor.git
cd docstring-freshness-monitor
./gradlew build
```

## 📝 License

MIT — [LICENSE](LICENSE)

## 👤 Author

**Jakub Jirák** — [@JirakJ](https://github.com/JirakJ)
