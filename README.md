# Docstring Freshness Monitor

[![Build](https://github.com/JirakJ/docstring-freshness-monitor/actions/workflows/verify.yml/badge.svg)](https://github.com/JirakJ/docstring-freshness-monitor/actions/workflows/verify.yml)
[![Version](https://img.shields.io/badge/version-2026.8.0-blue.svg)](https://github.com/JirakJ/docstring-freshness-monitor/releases)
[![License](https://img.shields.io/badge/license-Apache--2.0-blue)](LICENSE)

> **Free & open source** — every feature is unlocked for everyone, no license needed. Licensed under [Apache-2.0](LICENSE).

> Detect stale documentation by comparing code changes with documentation changes. When code around a docstring changes but the doc doesn't, it gets flagged as potentially outdated.

## ✨ Features

- **Freshness scoring** — each doc block gets a 0–100 score: 15 points off per commit that touched the following 20 lines of code since the doc last changed, plus a small penalty for the time gap
- **Stale documentation inspection** — flags stale (warning) and rotten (error) doc blocks; runs in the editor and via **Analyze → Inspect Code**
- **Multi-language** — Javadoc and KDoc (`.java`, `.kt`, `.kts`), Python docstrings, JSDoc (`.js`, `.ts`, `.jsx`, `.tsx`), and `#` comment blocks in other files
- **Git integration** — uses `git log -L` on the doc and code line ranges (Git plugin required for scores; without it every block counts as fresh)
- **Doc Freshness tool window** — scan the current file and see every doc block with its score, level and code-change count
- **Configurable thresholds** — score boundaries for fresh / aging / stale / rotten

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

1. Open a file with documentation (Javadoc, KDoc, Python docstrings or JSDoc) in a Git repository
2. Stale doc blocks are highlighted by the **Stale documentation** inspection
3. Run **Analyze → Inspect Code** to find stale documentation in the whole project
4. Open the **Doc Freshness** tool window and click **Scan Current File** for per-block scores

## ⚙️ Configuration

Settings are available at **Settings → Tools → Docstring Freshness Monitor**.

| Setting | Default | Description |
|---------|---------|-------------|
| Fresh at score ≥ | `80` | Scores at or above are fresh |
| Aging at score ≥ | `50` | Below "fresh" but at or above this: aging |
| Stale at score ≥ | `20` | Below "aging" but at or above this: stale; lower scores are rotten |

## 🎯 Usage

### Inspection
- **Stale documentation** is registered in the **Documentation freshness** group, enabled by default
- Stale blocks are reported as warnings, rotten blocks as errors; the message shows the score and the number of code changes since the doc was last updated
- Severity and scope can be adjusted under **Settings → Editor → Inspections**

### Doc Freshness tool window
- **Scan Current File** analyzes the active editor's file in the background and lists every doc block with line, score, level and code-change count

## 📋 Requirements

- IntelliJ IDEA 2023.2+ (or another IntelliJ-based IDE, build 232+)
- Git plugin and a Git repository (for freshness scores)

## 🏗️ Building from Source

```bash
git clone https://github.com/JirakJ/docstring-freshness-monitor.git
cd docstring-freshness-monitor
./gradlew build
```

## License

Apache License 2.0 — see [LICENSE](LICENSE) and [NOTICE](NOTICE). Copyright 2026 Jakub Jirák.

## Contributing

Issues and pull requests are welcome at https://github.com/JirakJ/docstring-freshness-monitor.

## 👤 Author

**Jakub Jirák** — [@JirakJ](https://github.com/JirakJ)
