# Docstring Freshness Monitor — JetBrains Marketplace Listing

## Plugin Name

Docstring Freshness Monitor

## Tagline

Your documentation is aging — this plugin tells you exactly where.

## Short Description

Scores every docstring 0–100 for freshness using git history. Spots stale docs when code changes but documentation doesn't. Java, Kotlin, Python, JS/TS.

## Full Description

```html
<h2>Docstring Freshness Monitor</h2>

<p>Code changes. Documentation doesn't. Over time, your docstrings silently become lies —
describing behavior that no longer exists. <b>Docstring Freshness Monitor</b> uses git history
to detect when code around a docstring changes but the doc stays the same, assigning a 0–100
freshness score to every documentation block in your project.</p>

<h3>Key Features</h3>
<ul>
    <li><b>Freshness Scoring (0–100)</b> — every doc block gets a score based on commits since last doc change, changed lines, and time elapsed</li>
    <li><b>Gutter Icons</b> — see freshness at a glance: 🟢 Fresh (80–100), 🟡 Aging (50–79), 🔴 Stale (0–49)</li>
    <li><b>Hover Tooltips</b> — last code change, last doc change, and divergent commit count</li>
    <li><b>Stale Doc Inspection</b> — registered IDE inspection with configurable severity (Warning/Weak Warning)</li>
    <li><b>Multi-Language Support</b> — Java (Javadoc), Kotlin (KDoc), Python (docstrings), JavaScript/TypeScript (JSDoc)</li>
    <li><b>Git Integration</b> — uses git blame and log to track code vs. documentation changes</li>
    <li><b>"Mark as Reviewed" Quick-Fix</b> — reset freshness score without editing the doc text</li>
    <li><b>Batch Analysis</b> — run via Analyze → Inspect Code for project-wide scanning</li>
    <li><b>Configurable Thresholds</b> — set your own aging, stale, and rotten score boundaries</li>
    <li><b>Suppress Per-Block</b> — add <code>@freshness-ignore</code> to any doc block to exclude it</li>
    <li><b>Project Dashboard</b> — tool window with project-wide freshness overview, worst files, and worst modules</li>
    <li><b>Treemap Visualization</b> — file size = lines of code, color = freshness score</li>
    <li><b>Trend Analysis</b> — track how documentation freshness improves or declines over time</li>
    <li><b>Export Reports</b> — HTML, JSON, and CSV output for reviews and audits</li>
    <li><b>CI Integration</b> — <code>./gradlew docFreshness --threshold=60 --fail-on-stale</code></li>
    <li><b>SARIF Output</b> — integrate with GitHub Code Scanning</li>
    <li><b>JUnit XML Output</b> — plug into any CI reporting pipeline</li>
    <li><b>Custom Score Algorithm</b> — extend via extension point for domain-specific scoring</li>
    <li><b>"Mark as Reviewed" Audit Trail</b> — track who reviewed what and when</li>
</ul>

<h3>Free &amp; Open Source</h3>
<p>Every feature is free for everyone — no license, no limits. The source code is available under the Apache-2.0 license at <a href="https://github.com/JirakJ/docstring-freshness-monitor">github.com/JirakJ/docstring-freshness-monitor</a>. Issues and pull requests are welcome.</p>

<h3>Getting Started</h3>
<ol>
    <li><b>Install</b> — search "Docstring Freshness Monitor" in Settings → Plugins → Marketplace</li>
    <li><b>Open a file</b> — look for freshness gutter icons next to Javadoc, KDoc, docstrings, or JSDoc blocks</li>
    <li><b>Explore</b> — hover over icons for details, or open the Doc Freshness tool window for the full picture</li>
</ol>

<h3>Compatibility</h3>
<p>IntelliJ IDEA 2024.3+ and all compatible JetBrains IDEs. Requires a Git repository and Java 17+.</p>

<h3>Part of Developer Productivity Suite</h3>
<p>Docstring Freshness Monitor is part of the <b>Code Health Suite</b>, working alongside
<b>Dead Code &amp; Drift Radar</b> for comprehensive code quality metrics.</p>
```

## Tags

`documentation`, `code-quality`, `git`, `inspection`, `code-health`

## Category

Code Quality

## Pricing

Free & open source (Apache-2.0) — every feature is available to everyone, no license needed.

## Screenshots Needed

1. **Gutter Icons in Editor** — Java/Python file showing 🟢🟡🔴 freshness icons next to docstrings with hover tooltip visible
2. **Project Dashboard** — Doc Freshness tool window with treemap visualization colored by freshness score
3. **Stale Doc Inspection** — editor highlighting a stale docstring with the "Mark as Reviewed" quick-fix popup
4. **Trend Analysis** — chart showing documentation freshness improving over time
5. **CI Integration** — terminal output of `./gradlew docFreshness` with pass/fail results
