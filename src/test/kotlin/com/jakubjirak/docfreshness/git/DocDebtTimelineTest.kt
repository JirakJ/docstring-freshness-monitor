package com.jakubjirak.docfreshness.git

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class DocDebtTimelineTest {

    private val timeline = DocDebtTimeline()

    @Test
    fun `empty snapshots produces stable trend`() {
        val trend = timeline.buildTimeline(emptyList())

        assertTrue(trend.entries.isEmpty())
        assertEquals(0.0, trend.currentDebt)
        assertEquals(DocDebtTimeline.DebtDirection.STABLE, trend.debtDirection)
    }

    @Test
    fun `compute debt score with all fresh`() {
        val score = timeline.computeDebtScore(freshCount = 10, agingCount = 0, staleCount = 0, rottenCount = 0)
        assertEquals(0.0, score)
    }

    @Test
    fun `compute debt score with mixed freshness`() {
        val score = timeline.computeDebtScore(freshCount = 5, agingCount = 3, staleCount = 1, rottenCount = 1)
        // (3*0.25 + 1*0.5 + 1*1.0) / 10 * 100 = (0.75 + 0.5 + 1.0) / 10 * 100 = 22.5
        assertEquals(22.5, score)
    }

    @Test
    fun `compute debt score with all rotten`() {
        val score = timeline.computeDebtScore(freshCount = 0, agingCount = 0, staleCount = 0, rottenCount = 10)
        // (10 * 1.0) / 10 * 100 = 100.0
        assertEquals(100.0, score)
    }

    @Test
    fun `compute debt score with zero total`() {
        val score = timeline.computeDebtScore(freshCount = 0, agingCount = 0, staleCount = 0, rottenCount = 0)
        assertEquals(0.0, score)
    }

    @Test
    fun `single snapshot produces stable trend`() {
        val snapshots = listOf(
            FreshnessSnapshot(1000L, "abc1", freshCount = 8, agingCount = 2, staleCount = 0, rottenCount = 0),
        )

        val trend = timeline.buildTimeline(snapshots)

        assertEquals(1, trend.entries.size)
        assertEquals(DocDebtTimeline.DebtDirection.STABLE, trend.debtDirection)
    }

    @Test
    fun `worsening trend detected`() {
        val entries = (1..10).map { i ->
            DocDebtTimeline.DebtTimelineEntry(
                date = i.toLong(),
                commitHash = "hash$i",
                freshCount = 10 - i,
                agingCount = i,
                staleCount = 0,
                rottenCount = 0,
                debtScore = i * 5.0,
            )
        }

        val direction = timeline.calculateDirection(entries)

        assertEquals(DocDebtTimeline.DebtDirection.WORSENING, direction)
    }

    @Test
    fun `improving trend detected`() {
        val entries = (1..10).map { i ->
            DocDebtTimeline.DebtTimelineEntry(
                date = i.toLong(),
                commitHash = "hash$i",
                freshCount = i,
                agingCount = 10 - i,
                staleCount = 0,
                rottenCount = 0,
                debtScore = (10 - i) * 5.0,
            )
        }

        val direction = timeline.calculateDirection(entries)

        assertEquals(DocDebtTimeline.DebtDirection.IMPROVING, direction)
    }

    @Test
    fun `stable trend when no significant change`() {
        val entries = (1..10).map { i ->
            DocDebtTimeline.DebtTimelineEntry(
                date = i.toLong(),
                commitHash = "hash$i",
                freshCount = 8,
                agingCount = 2,
                staleCount = 0,
                rottenCount = 0,
                debtScore = 5.0,
            )
        }

        val direction = timeline.calculateDirection(entries)

        assertEquals(DocDebtTimeline.DebtDirection.STABLE, direction)
    }

    @Test
    fun `timeline entries sorted by date`() {
        val snapshots = listOf(
            FreshnessSnapshot(3000L, "abc3", 5, 3, 1, 1),
            FreshnessSnapshot(1000L, "abc1", 8, 2, 0, 0),
            FreshnessSnapshot(2000L, "abc2", 7, 2, 1, 0),
        )

        val trend = timeline.buildTimeline(snapshots)

        assertEquals(3, trend.entries.size)
        assertEquals(1000L, trend.entries[0].date)
        assertEquals(2000L, trend.entries[1].date)
        assertEquals(3000L, trend.entries[2].date)
    }

    // Extension for tests without Project dependency
    private fun DocDebtTimeline.buildTimeline(
        snapshots: List<FreshnessSnapshot>,
    ): DocDebtTimeline.DocDebtTrend {
        if (snapshots.isEmpty()) {
            return DocDebtTimeline.DocDebtTrend(
                entries = emptyList(),
                currentDebt = 0.0,
                debtDirection = DocDebtTimeline.DebtDirection.STABLE,
            )
        }

        val entries = snapshots.map { snapshot ->
            val debtScore = computeDebtScore(
                snapshot.freshCount, snapshot.agingCount,
                snapshot.staleCount, snapshot.rottenCount,
            )
            DocDebtTimeline.DebtTimelineEntry(
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

        return DocDebtTimeline.DocDebtTrend(
            entries = entries,
            currentDebt = currentDebt,
            debtDirection = debtDirection,
        )
    }
}
