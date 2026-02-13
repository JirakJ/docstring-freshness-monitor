package com.jakubjirak.docfreshness.git

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project

/**
 * Tracks documentation freshness over time using git history.
 * Generates timeline data points for visualization.
 */
class DocDebtTimeline {

    /**
     * A single point in the documentation debt timeline.
     */
    data class DebtTimelineEntry(
        val date: Long,
        val commitHash: String,
        val freshCount: Int,
        val agingCount: Int,
        val staleCount: Int,
        val rottenCount: Int,
        val debtScore: Double,
    )

    /**
     * Overall trend of documentation debt.
     */
    data class DocDebtTrend(
        val entries: List<DebtTimelineEntry>,
        val currentDebt: Double,
        val debtDirection: DebtDirection,
    )

    /**
     * Direction of debt trend.
     */
    enum class DebtDirection {
        IMPROVING,
        STABLE,
        WORSENING,
    }

    /**
     * Builds a documentation debt timeline from freshness snapshots.
     */
    @Suppress("UNUSED_PARAMETER")
    fun buildTimeline(
        project: Project,
        snapshots: List<FreshnessSnapshot>,
    ): DocDebtTrend {
        if (snapshots.isEmpty()) {
            return DocDebtTrend(
                entries = emptyList(),
                currentDebt = 0.0,
                debtDirection = DebtDirection.STABLE,
            )
        }

        val entries = snapshots.map { snapshot ->
            val debtScore = computeDebtScore(
                snapshot.freshCount, snapshot.agingCount,
                snapshot.staleCount, snapshot.rottenCount,
            )
            DebtTimelineEntry(
                date = snapshot.date,
                commitHash = snapshot.commitHash,
                freshCount = snapshot.freshCount,
                agingCount = snapshot.agingCount,
                staleCount = snapshot.staleCount,
                rottenCount = snapshot.rottenCount,
                debtScore = debtScore,
            )
        }.sortedBy { it.date }

        val currentDebt = entries.last().debtScore
        val debtDirection = calculateDirection(entries)

        LOG.info("Built debt timeline with ${entries.size} entries, current debt: $currentDebt")

        return DocDebtTrend(
            entries = entries,
            currentDebt = currentDebt,
            debtDirection = debtDirection,
        )
    }

    /**
     * Computes a debt score from freshness counts.
     * Higher score = more debt (worse).
     */
    fun computeDebtScore(freshCount: Int, agingCount: Int, staleCount: Int, rottenCount: Int): Double {
        val total = freshCount + agingCount + staleCount + rottenCount
        if (total == 0) return 0.0

        val weightedSum = (agingCount * AGING_WEIGHT) +
            (staleCount * STALE_WEIGHT) +
            (rottenCount * ROTTEN_WEIGHT)
        return (weightedSum / total) * SCORE_MULTIPLIER
    }

    /**
     * Calculates the trend direction from timeline entries.
     */
    internal fun calculateDirection(entries: List<DebtTimelineEntry>): DebtDirection {
        if (entries.size < MIN_ENTRIES_FOR_TREND) return DebtDirection.STABLE

        val recentEntries = entries.takeLast(TREND_WINDOW_SIZE.coerceAtMost(entries.size))
        val firstHalf = recentEntries.take(recentEntries.size / 2)
        val secondHalf = recentEntries.drop(recentEntries.size / 2)

        val firstAvg = firstHalf.map { it.debtScore }.average()
        val secondAvg = secondHalf.map { it.debtScore }.average()

        val delta = secondAvg - firstAvg
        return when {
            delta < -TREND_THRESHOLD -> DebtDirection.IMPROVING
            delta > TREND_THRESHOLD -> DebtDirection.WORSENING
            else -> DebtDirection.STABLE
        }
    }

    companion object {
        private val LOG = Logger.getInstance(DocDebtTimeline::class.java)
        private const val AGING_WEIGHT = 0.25
        private const val STALE_WEIGHT = 0.5
        private const val ROTTEN_WEIGHT = 1.0
        private const val SCORE_MULTIPLIER = 100.0
        private const val MIN_ENTRIES_FOR_TREND = 3
        private const val TREND_WINDOW_SIZE = 10
        private const val TREND_THRESHOLD = 2.0
    }
}

/**
 * A snapshot of documentation freshness state at a point in time.
 */
data class FreshnessSnapshot(
    val date: Long,
    val commitHash: String,
    val freshCount: Int,
    val agingCount: Int,
    val staleCount: Int,
    val rottenCount: Int,
)
