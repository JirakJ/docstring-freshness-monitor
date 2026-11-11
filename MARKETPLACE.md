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
    <li><b>Freshness Scoring (0–100)</b> — based on the commits that touched the code below a doc block since the doc last changed, plus the time gap</li>
    <li><b>Stale Doc Inspection</b> — stale blocks as warnings, rotten blocks as errors; project-wide via Analyze → Inspect Code</li>
    <li><b>Multi-Language Support</b> — Java (Javadoc), Kotlin (KDoc), Python (docstrings), JavaScript/TypeScript (JSDoc)</li>
    <li><b>Git Integration</b> — <code>git log -L</code> on the doc and code line ranges</li>
    <li><b>Doc Freshness Tool Window</b> — per-block scores for the current file</li>
    <li><b>Configurable Thresholds</b> — your own fresh, aging, stale and rotten boundaries</li>
</ul>

<h3>Free &amp; Open Source</h3>
<p>Every feature is free for everyone — no license, no limits. The source code is available under the Apache-2.0 license at <a href="https://github.com/JirakJ/docstring-freshness-monitor">github.com/JirakJ/docstring-freshness-monitor</a>. Issues and pull requests are welcome.</p>

<h3>Getting Started</h3>
<ol>
    <li><b>Install</b> — search "Docstring Freshness Monitor" in Settings → Plugins → Marketplace</li>
    <li><b>Open a file</b> — stale Javadoc, KDoc, docstrings or JSDoc blocks are highlighted by the inspection</li>
    <li><b>Explore</b> — run Analyze → Inspect Code, or open the Doc Freshness tool window for per-block scores</li>
</ol>

<h3>Compatibility</h3>
<p>IntelliJ IDEA 2023.2+ and all compatible JetBrains IDEs. Requires a Git repository and Java 17+.</p>

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

## Screenshots

1. **Stale Doc Inspection** — editor highlighting a stale docstring with the score in the tooltip
2. **Doc Freshness tool window** — per-block scores for the current file
3. **Inspection results** — Analyze → Inspect Code listing stale documentation across the project
