# Docstring Freshness Monitor

**Keep your documentation as fresh as your code.**

---

## The Problem

Code changes constantly, but documentation gets left behind. Functions get refactored, parameters change, return types evolve — yet the docstrings stay frozen in time. Stale documentation is worse than no documentation because it actively misleads developers. Without tooling, there's no way to know which docs are trustworthy and which are outdated.

---

## Key Features

### Freshness Scoring (0-100)
Every docstring gets a freshness score based on how much the surrounding code has changed since the documentation was last updated. Higher scores mean fresher docs.

### Visual Gutter Icons
See documentation health at a glance: 🟢 Fresh (docs are up to date), 🟡 Aging (code has changed recently), 🔴 Stale (significant code changes without doc updates).

### Detailed Hover Tooltips
Hover over any gutter icon to see when the code last changed, when the doc last changed, and how many commits have diverged between them.

### Stale Doc Inspection
A built-in IDE inspection flags stale documentation with configurable severity levels. Runs automatically as you code or in batch via Analyze → Inspect Code.

### Multi-Language Support
Works across Java, Kotlin, Python, JavaScript, and TypeScript. Understands language-specific doc formats (Javadoc, KDoc, docstrings, JSDoc).

### Git-Powered Analysis
Uses git blame and git log to accurately track when code around a docstring changes versus when the documentation itself was updated.

### Project Dashboard (Pro)
A visual overview of documentation freshness across your entire project, with treemap visualization showing which areas need attention.

### Export Reports (Pro)
Generate HTML, JSON, and CSV reports for documentation health tracking, perfect for sprint reviews and team accountability.

---

## How It Works

1. **Install the plugin** — Available for all JetBrains IDEs from the Marketplace.
2. **Open any file** — Gutter icons appear automatically next to documented functions and classes.
3. **Review freshness** — Hover over icons to see freshness details or open the dashboard for project-wide view.
4. **Fix stale docs** — Update outdated documentation or use "Mark as reviewed" to acknowledge it.

---

## Pricing

| Feature | Free | Pro ($2.90/month) |
|---------|------|---------------------|
| Freshness scoring (0-100) | ✅ | ✅ |
| Gutter icons | ✅ | ✅ |
| Hover tooltips | ✅ | ✅ |
| Stale doc inspection | ✅ | ✅ |
| Multi-language support | ✅ | ✅ |
| Git-powered analysis | ✅ | ✅ |
| Mark as reviewed | ✅ | ✅ |
| Batch analysis | ✅ | ✅ |
| All languages simultaneously | ❌ | ✅ |
| Project dashboard | ❌ | ✅ |
| Treemap visualization | ❌ | ✅ |
| Trend analysis | ❌ | ✅ |
| Export reports (HTML/JSON/CSV) | ❌ | ✅ |
| CI integration | ❌ | ✅ |
| SARIF & JUnit XML output | ❌ | ✅ |

---

## Testimonials

> *"We finally have visibility into which docs are trustworthy. Game changer for onboarding new developers."*
> — **Engineering Lead** *(placeholder)*

> *"The freshness scores made documentation a first-class concern in our code reviews."*
> — **Senior Developer** *(placeholder)*

---

## FAQ

**Q: How is the freshness score calculated?**
A: It compares git history between code changes and documentation changes. When code around a docstring changes but the doc doesn't, the score decreases.

**Q: What languages are supported?**
A: Java/Kotlin (Javadoc/KDoc), Python (docstrings), and JavaScript/TypeScript (JSDoc). Free tier supports one language at a time; Pro enables all simultaneously.

**Q: Does it require a git repository?**
A: Yes. The plugin uses git blame and log to track changes, so a git repository with history is required.

**Q: Can I ignore certain files or patterns?**
A: Yes. Configure exclusions in the plugin settings or use the "Mark as reviewed" quick-fix for individual docstrings.

**Q: Does it work in CI/CD pipelines?**
A: Pro tier includes a CLI runner with SARIF and JUnit XML output for CI integration.

---

## Get Started

Install Docstring Freshness Monitor from the JetBrains Marketplace and start keeping your documentation honest.

[Install from JetBrains Marketplace](#) | [View Documentation](#) | [Report Issues](#)
