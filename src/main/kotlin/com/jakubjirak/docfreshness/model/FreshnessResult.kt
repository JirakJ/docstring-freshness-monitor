package com.jakubjirak.docfreshness.model

import java.time.Instant

/**
 * Result of freshness analysis for a single documentation block.
 */
data class FreshnessResult(
    val docBlock: DocBlock,
    val stalenessScore: Int,
    val codeChangesSinceDocChange: Int,
    val lastCodeChange: Instant? = null,
    val lastDocChange: Instant? = null,
) {
    /**
     * Freshness score is the inverse of staleness (100 - staleness).
     */
    val freshnessScore: Int get() = MAX_SCORE - stalenessScore.coerceIn(0, MAX_SCORE)

    val level: FreshnessLevel get() = FreshnessLevel.fromScore(freshnessScore)

    companion object {
        const val MAX_SCORE = 100
    }
}
