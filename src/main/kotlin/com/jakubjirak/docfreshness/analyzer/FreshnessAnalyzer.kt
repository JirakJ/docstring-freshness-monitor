package com.jakubjirak.docfreshness.analyzer

import com.intellij.openapi.diagnostic.Logger
import com.jakubjirak.docfreshness.model.DocBlock
import com.jakubjirak.docfreshness.model.FreshnessResult
import java.time.Duration
import java.time.Instant

/**
 * Core analysis engine that calculates freshness scores for documentation blocks.
 *
 * Staleness score formula:
 * - Base: number of code changes since last doc change (weighted)
 * - Time factor: days since last doc change vs last code change
 * - Score clamped to 0–100 range
 */
object FreshnessAnalyzer {

    private val LOG = Logger.getInstance(FreshnessAnalyzer::class.java)

    private const val COMMIT_WEIGHT = 15
    private const val TIME_DECAY_WEIGHT = 5
    private const val MAX_STALENESS = 100
    private const val DAYS_DIVISOR = 30L

    /**
     * Calculate the freshness result for a documentation block.
     *
     * @param docBlock the documentation block to analyze
     * @param codeChangesSinceDoc number of code commits since the doc was last changed
     * @param lastCodeChange timestamp of the last code change near this doc
     * @param lastDocChange timestamp of the last doc change
     */
    fun analyze(
        docBlock: DocBlock,
        codeChangesSinceDoc: Int,
        lastCodeChange: Instant?,
        lastDocChange: Instant?,
    ): FreshnessResult {
        val staleness = calculateStaleness(codeChangesSinceDoc, lastCodeChange, lastDocChange)
        LOG.debug("Staleness for ${docBlock.filePath}:${docBlock.lineStart} = $staleness (changes=$codeChangesSinceDoc)")

        return FreshnessResult(
            docBlock = docBlock,
            stalenessScore = staleness,
            codeChangesSinceDocChange = codeChangesSinceDoc,
            lastCodeChange = lastCodeChange,
            lastDocChange = lastDocChange,
        )
    }

    /**
     * Calculate staleness score from raw metrics.
     * Higher score = more stale. Range: 0–100.
     */
    fun calculateStaleness(
        codeChangesSinceDoc: Int,
        lastCodeChange: Instant?,
        lastDocChange: Instant?,
    ): Int {
        if (codeChangesSinceDoc == 0) return 0
        if (lastCodeChange == null || lastDocChange == null) {
            return (codeChangesSinceDoc * COMMIT_WEIGHT).coerceAtMost(MAX_STALENESS)
        }

        val commitScore = codeChangesSinceDoc * COMMIT_WEIGHT
        val timeDelta = Duration.between(lastDocChange, lastCodeChange)
        val daysSinceDocChange = timeDelta.toDays().coerceAtLeast(0)
        val timeScore = (daysSinceDocChange / DAYS_DIVISOR * TIME_DECAY_WEIGHT).toInt()

        return (commitScore + timeScore).coerceIn(0, MAX_STALENESS)
    }
}
