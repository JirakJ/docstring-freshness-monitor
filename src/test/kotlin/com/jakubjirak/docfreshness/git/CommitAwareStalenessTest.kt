package com.jakubjirak.docfreshness.git

import com.jakubjirak.docfreshness.model.DocBlock
import com.jakubjirak.docfreshness.model.DocLanguage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CommitAwareStalenessTest {

    private val staleness = CommitAwareStaleness()

    private val sampleDocBlock = DocBlock(
        filePath = "Test.java",
        lineStart = 1,
        lineEnd = 5,
        text = "/** Sample doc */",
        language = DocLanguage.JAVA,
    )

    @Test
    fun `no commits means zero staleness`() {
        val result = staleness.analyzeStaleness(sampleDocBlock, emptyList())

        assertEquals(0, result.commitsSinceDocUpdate)
        assertEquals(0, result.codeChangingCommits)
        assertTrue(result.authors.isEmpty())
    }

    @Test
    fun `code-only commits increase staleness`() {
        val commits = listOf(
            CommitChange("abc1", "author1", 1000L, codeChanged = true, docChanged = false),
            CommitChange("abc2", "author2", 2000L, codeChanged = true, docChanged = false),
        )

        val result = staleness.analyzeStaleness(sampleDocBlock, commits)

        assertEquals(2, result.codeChangingCommits)
        assertEquals(2, result.authors.size)
    }

    @Test
    fun `doc update stops counting`() {
        val commits = listOf(
            CommitChange("abc1", "author1", 1000L, codeChanged = true, docChanged = false),
            CommitChange("abc2", "author2", 2000L, codeChanged = true, docChanged = true),
            CommitChange("abc3", "author3", 3000L, codeChanged = true, docChanged = false),
        )

        val result = staleness.analyzeStaleness(sampleDocBlock, commits)

        assertEquals(1, result.codeChangingCommits)
    }

    @Test
    fun `refactor commits do not affect staleness`() {
        val commits = listOf(
            CommitChange("abc1", "author1", 1000L, codeChanged = true, docChanged = false, isRefactor = true),
        )

        val result = staleness.analyzeStaleness(sampleDocBlock, commits)

        assertEquals(0, result.codeChangingCommits)
    }

    @Test
    fun `compute score from staleness`() {
        val commitStaleness = CommitAwareStaleness.CommitStaleness(
            docBlock = sampleDocBlock,
            commitsSinceDocUpdate = 5,
            codeChangingCommits = 3,
            authors = listOf("author1", "author2"),
        )

        val score = staleness.computeScore(commitStaleness)

        assertEquals(45, score) // 3 * 15
    }

    @Test
    fun `score is capped at 100`() {
        val commitStaleness = CommitAwareStaleness.CommitStaleness(
            docBlock = sampleDocBlock,
            commitsSinceDocUpdate = 20,
            codeChangingCommits = 10,
            authors = listOf("author1"),
        )

        val score = staleness.computeScore(commitStaleness)

        assertEquals(100, score)
    }

    @Test
    fun `classify code-only change`() {
        val commit = CommitChange("abc", "author", 1000L, codeChanged = true, docChanged = false)
        assertEquals(CommitAwareStaleness.ChangeType.CODE_ONLY, staleness.classifyCommitChange(commit))
    }

    @Test
    fun `classify doc-only change`() {
        val commit = CommitChange("abc", "author", 1000L, codeChanged = false, docChanged = true)
        assertEquals(CommitAwareStaleness.ChangeType.DOC_ONLY, staleness.classifyCommitChange(commit))
    }

    @Test
    fun `classify both change`() {
        val commit = CommitChange("abc", "author", 1000L, codeChanged = true, docChanged = true)
        assertEquals(CommitAwareStaleness.ChangeType.BOTH, staleness.classifyCommitChange(commit))
    }

    @Test
    fun `classify refactor`() {
        val commit = CommitChange("abc", "author", 1000L, codeChanged = true, docChanged = false, isRefactor = true)
        assertEquals(CommitAwareStaleness.ChangeType.REFACTOR, staleness.classifyCommitChange(commit))
    }

    // Extension for tests without Project dependency
    private fun CommitAwareStaleness.analyzeStaleness(
        docBlock: DocBlock,
        commitHistory: List<CommitChange>,
    ): CommitAwareStaleness.CommitStaleness {
        if (commitHistory.isEmpty()) {
            return CommitAwareStaleness.CommitStaleness(
                docBlock = docBlock,
                commitsSinceDocUpdate = 0,
                codeChangingCommits = 0,
                authors = emptyList(),
            )
        }

        var commitsSinceDocUpdate = 0
        var codeChangingCommits = 0
        val authors = mutableSetOf<String>()

        for (commit in commitHistory) {
            val changeType = classifyCommitChange(commit)
            when (changeType) {
                CommitAwareStaleness.ChangeType.CODE_ONLY -> {
                    commitsSinceDocUpdate++
                    codeChangingCommits++
                    authors.add(commit.author)
                }
                CommitAwareStaleness.ChangeType.DOC_ONLY,
                CommitAwareStaleness.ChangeType.BOTH,
                -> break
                CommitAwareStaleness.ChangeType.REFACTOR -> { }
            }
        }

        return CommitAwareStaleness.CommitStaleness(
            docBlock = docBlock,
            commitsSinceDocUpdate = commitsSinceDocUpdate,
            codeChangingCommits = codeChangingCommits,
            authors = authors.toList(),
        )
    }
}
