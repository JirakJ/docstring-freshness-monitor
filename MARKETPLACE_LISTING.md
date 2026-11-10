# Docstring Freshness Monitor — JetBrains Marketplace Listing

---

## 📝 Plugin Information

| Field            | Value                                |
|------------------|--------------------------------------|
| **Name**         | Docstring Freshness Monitor          |
| **Plugin ID**    | com.jakubjirak.docfreshness          |
| **Version**      | 2026.2.9                             |
| **Category**     | Code Quality                         |
| **Tags**         | Documentation, Docstrings, Code Quality, Javadoc, Stale Docs |

---

## 🎯 Short Description

> Detects stale documentation by comparing code changes against docstring updates using git history. Freshness scores, gutter icons, and inspections keep your docs in sync with your code.

_(183 characters)_

---

## 📖 Full Description

### 📚 Your Code Changed. Did Your Docs? Now You'll Know.

**The Problem:** Documentation rot is silent and insidious. A method's behavior changes, but its docstring stays the same — misleading every developer who reads it. Code reviews catch some stale docs, but most slip through, accumulating into a documentation debt that erodes trust and slows onboarding.

**The Solution:** Docstring Freshness Monitor uses git history to compare when code last changed versus when its documentation last changed. Each docstring gets a **Freshness Score (0–100)** with visual indicators right in your editor. Stale docs surface as IDE inspections, making them impossible to ignore.

---

### ✨ Key Features

#### 🔍 Catch Stale Docs Automatically

- 📊 **Freshness Score (0–100)** — Per-docstring health score based on code vs. documentation change divergence
- 🚦 **Gutter Icons** — Visual indicators at a glance: 🟢 Fresh, 🟡 Aging, 🔴 Stale
- 💬 **Hover Tooltips** — Last code change, last doc change, number of divergent commits
- 🔍 **Stale Doc Inspection** — IDE inspection with configurable severity (default: WARNING)
- 🌐 **Multi-Language Support:**
  - Java/Kotlin (Javadoc/KDoc)
  - Python (docstrings)
  - JavaScript/TypeScript (JSDoc)
- 🔗 **Git Integration** — Powered by `git blame` and `git log` for accurate change tracking
- ✅ **"Mark as Reviewed" Quick-Fix** — Acknowledge a stale doc without changing it
- 📋 **Batch Analysis** — Run via `Analyze → Inspect Code` for project-wide scanning
- 👥 **Team Configuration** — Share settings via `.doc-freshness.yaml` in your repo
- 🏷️ **`@freshness-ignore`** — Suppress scoring for intentionally static documentation

#### 🚀 Project-wide Insight & CI

- 📊 **Project Dashboard** — Dedicated tool window with freshness scores across your entire codebase
- 🗺️ **Treemap Visualization** — File size = lines of code, color = freshness score
- 📈 **Trend Analysis** — Track documentation freshness trajectory over time
- 📤 **Export Reports** — HTML, JSON, and CSV formats for team review
- 🔧 **CI Integration** — `./gradlew docFreshness --threshold=60 --fail-on-stale`
- 📋 **SARIF Output** — Feed results into GitHub Code Scanning
- 📄 **JUnit XML Output** — Integrate with any CI dashboard
- 🧩 **Custom Score Algorithm** — Extend the scoring logic via extension point
- 📝 **"Mark as Reviewed" Audit Trail** — Track who reviewed stale docs and when

---

### 💰 Pricing Model

Free & open source (Apache-2.0) — every feature is available to everyone, no license needed.

---

### 🎯 Perfect For

- **Teams with documentation standards** who want automated enforcement
- **API developers** maintaining public-facing documentation that must stay accurate
- **Tech leads** tracking documentation health as a quality metric
- **Open-source maintainers** ensuring contributor docs stay current

---

### ⚙️ Requirements

| Requirement        | Version                          |
|---------------------|----------------------------------|
| **JetBrains IDE**  | 2024.3+ (build 232+)            |
| **Java**           | 17+                             |
| **Git**            | Recommended for full functionality |

---

### 🚀 Quick Start

1. **Install** — Search "Docstring Freshness Monitor" in `Settings → Plugins → Marketplace`
2. **Open a file** — Navigate to any Java, Kotlin, Python, or JS/TS file with docstrings
3. **Look for gutter icons** — 🟢 Fresh, 🟡 Aging, 🔴 Stale indicators appear next to documented elements
4. **Hover for details** — Mouse over icons to see last code/doc change dates and divergent commit count
5. **Configure** — Adjust thresholds in `Settings → Tools → Docstring Freshness Monitor`

---

## 📸 Screenshots & Media

| #  | Screenshot Description                                                                 |
|----|----------------------------------------------------------------------------------------|
| 1  | **Gutter icons in Java file** — Shows 🟢🟡🔴 freshness indicators next to Javadoc comments |
| 2  | **Hover tooltip detail** — Expanded tooltip showing last code change, last doc change, and divergent commits |
| 3  | **Stale doc inspection warning** — IDE inspection highlighting a stale docstring with suggested quick-fix |
| 4  | **"Mark as Reviewed" quick-fix** — Quick-fix popup allowing acknowledgment of reviewed stale documentation |
| 5  | **Project dashboard** — Tool window showing freshness scores for all documented elements across the project |
| 6  | **Treemap visualization** — Color-coded treemap where file size = LOC and color = freshness score |
| 7  | **Settings panel** — Configuration page with aging/stale/rotten thresholds and language toggles |
| 8  | **CI integration output** — Terminal showing `./gradlew docFreshness --threshold=60` results |

---

## 🏷️ Marketplace Categories

| Type          | Category                |
|---------------|-------------------------|
| **Primary**   | Code Quality            |
| **Secondary** | Documentation           |

---

## 🔍 Keywords (SEO)

`documentation`, `docstring`, `javadoc`, `kdoc`, `jsdoc`, `stale documentation`, `doc freshness`, `documentation quality`, `code comments`, `documentation drift`, `git blame`, `documentation review`, `api docs`, `code quality`, `inspection`, `python docstring`

---

## 📋 Compatibility

### Compatible JetBrains IDEs

| IDE                        | Compatible |
|----------------------------|:----------:|
| IntelliJ IDEA Community    | ✅          |
| IntelliJ IDEA Ultimate     | ✅          |
| PyCharm                    | ✅          |
| WebStorm                   | ✅          |
| GoLand                     | ✅          |
| PhpStorm                   | ✅          |
| Rider                      | ✅          |
| CLion                      | ✅          |
| RubyMine                   | ✅          |
| Android Studio             | ✅          |

**Minimum IDE Version:** 2024.3 (build 232)

---

## 📞 Vendor Information

| Field        | Value                              |
|--------------|------------------------------------|
| **Vendor**   | Jakub Jirak                        |
| **Email**    | jirak@jakubjirak.com               |
| **Website**  | https://jakubjirak.com             |
| **Support**  | support@jakubjirak.com             |

---

## ✅ Pre-Launch Checklist

- [ ] Plugin icon meets JetBrains Marketplace guidelines (SVG, 40x40 & 80x80)
- [ ] Short description is under 255 characters
- [ ] Full description renders correctly in Marketplace preview
- [ ] Screenshots are high-resolution (1280×800 or higher), PNG format
- [ ] Plugin ID matches `gradle.properties` and `plugin.xml`
- [ ] `plugin.xml` vendor email and URL are correct
- [ ] Product code `PDOCFRESHNESS` is registered
- [ ] License agreement / EULA uploaded
- [ ] Compatibility range tested on minimum supported IDE version (2024.3)
- [ ] Plugin verified with Plugin Verifier (`runPluginVerifier` Gradle task)
- [ ] CHANGELOG.md is up to date for the release version
- [ ] Support email is monitored and responsive
- [ ] Privacy policy URL is set
- [ ] Plugin signed with JetBrains Marketplace certificate
