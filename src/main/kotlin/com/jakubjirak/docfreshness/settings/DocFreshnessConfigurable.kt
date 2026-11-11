package com.jakubjirak.docfreshness.settings

import com.intellij.openapi.options.BoundConfigurable
import com.intellij.ui.dsl.builder.bindIntValue
import com.intellij.ui.dsl.builder.panel

/**
 * Settings UI for Docstring Freshness Monitor.
 * Located at Settings → Tools → Docstring Freshness Monitor.
 */
class DocFreshnessConfigurable : BoundConfigurable("Docstring Freshness Monitor") {

    override fun createPanel() = panel {
        val settings = DocFreshnessSettings.getInstance()

        group("Freshness Thresholds") {
            row("Fresh at score ≥") {
                spinner(0..100, 1)
                    .comment("Freshness score (0–100) at or above which a doc block is fresh")
                    .bindIntValue(settings::agingThreshold)
            }
            row("Aging at score ≥") {
                spinner(0..100, 1)
                    .comment("Below the fresh threshold but at or above this score: aging")
                    .bindIntValue(settings::staleThreshold)
            }
            row("Stale at score ≥ (below: rotten)") {
                spinner(0..100, 1)
                    .comment("Below the aging threshold but at or above this score: stale; lower scores are rotten")
                    .bindIntValue(settings::rottenThreshold)
            }
        }
    }
}
