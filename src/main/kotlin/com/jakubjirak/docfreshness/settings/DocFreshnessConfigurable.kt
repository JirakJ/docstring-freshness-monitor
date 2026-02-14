package com.jakubjirak.docfreshness.settings

import com.intellij.openapi.options.BoundConfigurable
import com.intellij.ui.dsl.builder.bindIntValue
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel

/**
 * Settings UI for Docstring Freshness Monitor.
 * Located at Settings → Tools → Docstring Freshness Monitor.
 */
class DocFreshnessConfigurable : BoundConfigurable("Docstring Freshness Monitor") {

    override fun createPanel() = panel {
        val settings = DocFreshnessSettings.getInstance()

        group("Freshness Thresholds") {
            row("Aging threshold (days):") {
                spinner(1..1000, 1)
                    .comment("Documents older than this many days since last code change are marked as aging")
                    .bindIntValue(settings::agingThreshold)
            }
            row("Stale threshold (days):") {
                spinner(1..1000, 1)
                    .comment("Documents older than this many days since last code change are marked as stale")
                    .bindIntValue(settings::staleThreshold)
            }
            row("Rotten threshold (days):") {
                spinner(1..1000, 1)
                    .comment("Documents older than this many days since last code change are marked as rotten")
                    .bindIntValue(settings::rottenThreshold)
            }
        }

        group("Language Support") {
            row {
                checkBox("Java / Kotlin (Javadoc, KDoc)")
                    .comment("Analyze freshness of Javadoc and KDoc comments")
                    .bindSelected(settings::enableJavaKotlin)
            }
            row {
                checkBox("Python (docstrings)")
                    .comment("Analyze freshness of Python docstrings")
                    .bindSelected(settings::enablePython)
            }
            row {
                checkBox("JavaScript / TypeScript (JSDoc)")
                    .comment("Analyze freshness of JSDoc comments")
                    .bindSelected(settings::enableJavaScript)
            }
        }

        group("Filtering") {
            row("Ignored patterns:") {
                expandableTextField()
                    .comment("Comma-separated glob patterns to exclude from analysis (e.g., */test/*, */generated/*)")
                    .bindText(settings::ignoredPatterns)
            }
        }
    }
}
