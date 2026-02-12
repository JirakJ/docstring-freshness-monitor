package com.jakubjirak.docfreshness.analyzer

import com.jakubjirak.docfreshness.model.DocBlock
import com.jakubjirak.docfreshness.model.DocLanguage
import com.jakubjirak.docfreshness.model.FreshnessLevel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.temporal.ChronoUnit

class FreshnessAnalyzerTest {

    private val sampleDocBlock = DocBlock(
        filePath = "Test.java",
        lineStart = 1,
        lineEnd = 5,
        text = "/** Sample doc */",
        language = DocLanguage.JAVA,
    )

    @Test
    fun `zero code changes means zero staleness`() {
        val result = FreshnessAnalyzer.analyze(sampleDocBlock, 0, null, null)

        assertEquals(0, result.stalenessScore)
        assertEquals(100, result.freshnessScore)
    }

    @Test
    fun `one code change gives moderate staleness`() {
        val now = Instant.now()
        val result = FreshnessAnalyzer.analyze(
            sampleDocBlock,
            codeChangesSinceDoc = 1,
            lastCodeChange = now,
            lastDocChange = now.minus(7, ChronoUnit.DAYS),
        )

        assertTrue(result.stalenessScore > 0)
        assertTrue(result.stalenessScore <= 100)
    }

    @Test
    fun `many code changes give high staleness`() {
        val now = Instant.now()
        val result = FreshnessAnalyzer.analyze(
            sampleDocBlock,
            codeChangesSinceDoc = 7,
            lastCodeChange = now,
            lastDocChange = now.minus(180, ChronoUnit.DAYS),
        )

        assertTrue(result.stalenessScore >= 80)
    }

    @Test
    fun `staleness is capped at 100`() {
        val now = Instant.now()
        val result = FreshnessAnalyzer.analyze(
            sampleDocBlock,
            codeChangesSinceDoc = 100,
            lastCodeChange = now,
            lastDocChange = now.minus(365, ChronoUnit.DAYS),
        )

        assertEquals(100, result.stalenessScore)
        assertEquals(0, result.freshnessScore)
    }

    @Test
    fun `freshness score is inverse of staleness`() {
        val staleness = 60
        val now = Instant.now()
        val result = FreshnessAnalyzer.analyze(
            sampleDocBlock,
            codeChangesSinceDoc = 4,
            lastCodeChange = now,
            lastDocChange = now.minus(30, ChronoUnit.DAYS),
        )

        assertEquals(100 - result.stalenessScore, result.freshnessScore)
    }

    @Test
    fun `calculate staleness without timestamps`() {
        val staleness = FreshnessAnalyzer.calculateStaleness(3, null, null)

        assertEquals(45, staleness) // 3 * 15 = 45
    }

    @Test
    fun `calculate staleness with zero changes`() {
        val now = Instant.now()
        val staleness = FreshnessAnalyzer.calculateStaleness(0, now, now)

        assertEquals(0, staleness)
    }

    @Test
    fun `freshness level from score with default thresholds`() {
        assertEquals(
            FreshnessLevel.FRESH,
            FreshnessLevel.fromScoreWithThresholds(85, 80, 50, 20),
        )
        assertEquals(
            FreshnessLevel.AGING,
            FreshnessLevel.fromScoreWithThresholds(65, 80, 50, 20),
        )
        assertEquals(
            FreshnessLevel.STALE,
            FreshnessLevel.fromScoreWithThresholds(35, 80, 50, 20),
        )
        assertEquals(
            FreshnessLevel.ROTTEN,
            FreshnessLevel.fromScoreWithThresholds(10, 80, 50, 20),
        )
    }

    @Test
    fun `boundary values for freshness levels`() {
        assertEquals(
            FreshnessLevel.FRESH,
            FreshnessLevel.fromScoreWithThresholds(80, 80, 50, 20),
        )
        assertEquals(
            FreshnessLevel.AGING,
            FreshnessLevel.fromScoreWithThresholds(79, 80, 50, 20),
        )
        assertEquals(
            FreshnessLevel.AGING,
            FreshnessLevel.fromScoreWithThresholds(50, 80, 50, 20),
        )
        assertEquals(
            FreshnessLevel.STALE,
            FreshnessLevel.fromScoreWithThresholds(49, 80, 50, 20),
        )
        assertEquals(
            FreshnessLevel.STALE,
            FreshnessLevel.fromScoreWithThresholds(20, 80, 50, 20),
        )
        assertEquals(
            FreshnessLevel.ROTTEN,
            FreshnessLevel.fromScoreWithThresholds(19, 80, 50, 20),
        )
    }

    @Test
    fun `result contains correct doc block reference`() {
        val result = FreshnessAnalyzer.analyze(sampleDocBlock, 2, null, null)

        assertEquals(sampleDocBlock, result.docBlock)
        assertEquals(2, result.codeChangesSinceDocChange)
    }

    @Test
    fun `time factor contributes to staleness`() {
        val now = Instant.now()
        val recentStaleness = FreshnessAnalyzer.calculateStaleness(
            2, now, now.minus(1, ChronoUnit.DAYS),
        )
        val oldStaleness = FreshnessAnalyzer.calculateStaleness(
            2, now, now.minus(120, ChronoUnit.DAYS),
        )

        assertTrue(oldStaleness >= recentStaleness)
    }
}
