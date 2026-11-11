package com.jakubjirak.docfreshness.model

import com.jakubjirak.docfreshness.settings.DocFreshnessSettings
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ModelTest {

    @Test
    fun `language detection is case-insensitive and falls back to generic`() {
        assertEquals(DocLanguage.JAVA, DocLanguage.fromExtension("KT"))
        assertEquals(DocLanguage.JAVA, DocLanguage.fromExtension("kts"))
        assertEquals(DocLanguage.PYTHON, DocLanguage.fromExtension("py"))
        assertEquals(DocLanguage.JAVASCRIPT, DocLanguage.fromExtension("tsx"))
        assertEquals(DocLanguage.GENERIC, DocLanguage.fromExtension("rb"))
        assertEquals(DocLanguage.GENERIC, DocLanguage.fromExtension(""))
    }

    @Test
    fun `custom thresholds classify scores`() {
        assertEquals(FreshnessLevel.FRESH, FreshnessLevel.fromScoreWithThresholds(90, 90, 60, 30))
        assertEquals(FreshnessLevel.AGING, FreshnessLevel.fromScoreWithThresholds(89, 90, 60, 30))
        assertEquals(FreshnessLevel.STALE, FreshnessLevel.fromScoreWithThresholds(30, 90, 60, 30))
        assertEquals(FreshnessLevel.ROTTEN, FreshnessLevel.fromScoreWithThresholds(29, 90, 60, 30))
    }

    @Test
    fun `fromScore without an application uses default thresholds`() {
        assertEquals(FreshnessLevel.FRESH, FreshnessLevel.fromScore(FreshnessLevel.DEFAULT_AGING_THRESHOLD))
        assertEquals(FreshnessLevel.AGING, FreshnessLevel.fromScore(FreshnessLevel.DEFAULT_STALE_THRESHOLD))
        assertEquals(FreshnessLevel.STALE, FreshnessLevel.fromScore(FreshnessLevel.DEFAULT_ROTTEN_THRESHOLD))
        assertEquals(FreshnessLevel.ROTTEN, FreshnessLevel.fromScore(0))
    }

    @Test
    fun `freshness score clamps out-of-range staleness`() {
        val block = DocBlock("f.kt", 1, 2, "/** x */")
        assertEquals(0, FreshnessResult(block, stalenessScore = 250, codeChangesSinceDocChange = 9).freshnessScore)
        assertEquals(100, FreshnessResult(block, stalenessScore = -5, codeChangesSinceDocChange = 0).freshnessScore)
        assertEquals(FreshnessLevel.ROTTEN, FreshnessResult(block, 100, 7).level)
    }

    @Test
    fun `settings defaults and loadState`() {
        val settings = DocFreshnessSettings()
        assertEquals(FreshnessLevel.DEFAULT_AGING_THRESHOLD, settings.agingThreshold)
        assertEquals(FreshnessLevel.DEFAULT_STALE_THRESHOLD, settings.staleThreshold)
        assertEquals(FreshnessLevel.DEFAULT_ROTTEN_THRESHOLD, settings.rottenThreshold)
        settings.loadState(DocFreshnessSettings().apply { agingThreshold = 95; rottenThreshold = 5 })
        assertEquals(95, settings.agingThreshold)
        assertEquals(5, settings.rottenThreshold)
    }
}
