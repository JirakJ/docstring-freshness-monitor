package com.jakubjirak.docfreshness.model

import com.jakubjirak.docfreshness.settings.DocFreshnessSettings

/**
 * Classification of documentation freshness based on score thresholds.
 */
enum class FreshnessLevel(val label: String) {
    FRESH("Fresh"),
    AGING("Aging"),
    STALE("Stale"),
    ROTTEN("Rotten"),
    ;

    companion object {
        fun fromScore(score: Int): FreshnessLevel {
            val settings = try {
                DocFreshnessSettings.getInstance()
            } catch (_: Exception) {
                null
            }
            val agingThreshold = settings?.agingThreshold ?: DEFAULT_AGING_THRESHOLD
            val staleThreshold = settings?.staleThreshold ?: DEFAULT_STALE_THRESHOLD
            val rottenThreshold = settings?.rottenThreshold ?: DEFAULT_ROTTEN_THRESHOLD

            return when {
                score >= agingThreshold -> FRESH
                score >= staleThreshold -> AGING
                score >= rottenThreshold -> STALE
                else -> ROTTEN
            }
        }

        fun fromScoreWithThresholds(score: Int, aging: Int, stale: Int, rotten: Int): FreshnessLevel {
            return when {
                score >= aging -> FRESH
                score >= stale -> AGING
                score >= rotten -> STALE
                else -> ROTTEN
            }
        }

        const val DEFAULT_AGING_THRESHOLD = 80
        const val DEFAULT_STALE_THRESHOLD = 50
        const val DEFAULT_ROTTEN_THRESHOLD = 20
    }
}
